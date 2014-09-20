package nslc.src;/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

//
// nslc.src.NslCompiler.java
//
//////////////////////////////////////////////////////////////////////

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

public class NslCompiler
{

    private static String moduleName, /*inputFileName, outputFileName, currentDirectory,*/
            fullModuleName;
    public static String libraryDir=System.getProperty("user.dir");

    private static boolean generateClass = true;
    private static boolean generateJavaCode = false;
    private static boolean generateXMLCode = false;
    private static boolean cleanFiles = false;
    private static boolean deprecation = false;

    public static boolean verbose = false;

    private static Vector<String> classPath;
    private static Vector<NslScope> parsedFiles;
    private static Vector<String> visitedFiles;

    private static Stack<String> fileNames;

    public static void main(String args[])
    {
        try
        {
            fileNames = new Stack<String>();

            parseArguments(args);

            if (cleanFiles)
            {
                cleanFilesInDirectory();
                return;
            }

            initClassPath();

            parsedFiles = new Vector<NslScope>(10);
            visitedFiles = new Vector<String>(10);

            NslParser.error = false;
            NslParser.errorCount = 0;

            // Add String class as default
            parseType("String", libraryDir, null);

            moduleName = "";
            parseType(fullModuleName, libraryDir, null);

            if (!NslParser.error)
            {
                String[] fileNames = null;
                if (generateClass || generateJavaCode)
                {
                    fileNames = dumpJavaFiles();
                }

                if (generateXMLCode)
                {
                    dumpXMLFiles();
                }
                else if (!NslParser.error && generateClass && fileNames != null)
                {
                    parseJavaFiles(fileNames);
                }

                if (NslParser.error)
                {
                    System.err.println(NslParser.errorCount + " error" + ((NslParser.errorCount > 1) ? "s" : ""));
                }
            }
            else
            {
                System.err.println(NslParser.errorCount + " error" + ((NslParser.errorCount > 1) ? "s" : ""));
            }

        }
        catch (Exception argumentError)
        {
            argumentError.printStackTrace();
            System.err.println();
            System.err.println("Usage:   java nslc.src.NslCompiler [Options] \"ModelName\"");
            System.err.println("   or:   java nslc.src.NslCompiler -clean");
            System.err.println();
            System.err.println("Options: -java");
            System.err.println("         -xml");
            System.err.println("         -deprecation");
            System.err.println("         -verbose");

        }
    }

    /**
     * Parses command line arguments such as:
     * <p/>
     * java, xml, verbose
     *
     * @param argv The vector containing the command line arguments.
     * @throws Exception This exception is thrown if the user
     *                   does not provide enough or correct information in the command
     *                   line.
     */

    private static void parseArguments(String argv[])
            throws Exception
    {

        if (argv.length < 1 || (argv.length == 1 && (argv[0].compareTo("-?") == 0 || argv[0].compareTo("-h") == 0)))
        {
            throw new Exception();
        }

        if (argv[argv.length - 1].equals("-clean"))
        {
            cleanFiles = true;
            return;
        }

        fullModuleName = moduleName = argv[argv.length - 1];

        for (int i = 0; i < argv.length - 1; i++)
        {

            if (argv[i].compareTo("-java") == 0)
            {

                generateJavaCode = true;
                generateXMLCode = false;
                generateClass = false;

            }
            else if (argv[i].compareTo("-xml") == 0)
            {

                generateJavaCode = false;
                generateXMLCode = true;
                generateClass = false;

            }
            else if (argv[i].compareTo("-verbose") == 0)
            {
                verbose = true;
            }
            else if (argv[i].compareTo("-deprecation") == 0)
            {

                deprecation = true;
            }
            else if (argv[i].equals("-libraryDir"))
            {
                if (i == argv.length)
                {
                    throw new Exception();
                }
                else
                {
                    i++;
                    if (i == argv.length)
                    {
                        throw new Exception();
                    }
                    else
                    {
                        libraryDir = argv[i];
                    }
                }
            }
            else
            {
                throw new Exception();
            }

        }
    }

    public static void addVisitedFile(String fileName)
    {
        visitedFiles.addElement(fileName);
    }

    public static void addNslScope(NslScope scope)
    {
        parsedFiles.addElement(scope);
    }

    public static Vector getParsedFiles()
    {
        return parsedFiles;
    }

    public static String getTypeName(String type)
    {
        if (type != null && type.indexOf(".") >= 0)
        {
            type = type.substring(type.lastIndexOf(".") + 1);
        }
        return type;
    }

    public static boolean wasThisFileVisited(String fileName)
    {
        Enumeration e = visitedFiles.elements();

        String tmpFileName;

        while (e.hasMoreElements())
        {
            tmpFileName = (String) e.nextElement();
            if (fileName.equals(tmpFileName))
            {
                return true;
            }
        }

        return false;
    }

    public static NslScope getNslScope(String type)
    {
        Enumeration e = parsedFiles.elements();

        NslScope scopeClassMatch=null;
        NslScope scopeFullMatch=null;
        NslScope scopeTmp;
        String typeName;

        while (e.hasMoreElements())
        {
            scopeTmp = (NslScope) e.nextElement();
            typeName = scopeTmp.getClassName();

            if(typeName.equals(type))
            {
                scopeFullMatch=scopeTmp;
            }
            else if (getTypeName(typeName).equals(getTypeName(type)))
            {
                scopeClassMatch=scopeTmp;
            }
        }
        if(scopeFullMatch!=null)
            return scopeFullMatch;
        else if(scopeClassMatch!=null && !type.contains("."))
            return scopeClassMatch;
        else
            return null;
    }

    public static String[] dumpJavaFiles()
    {
        Enumeration e = parsedFiles.elements();

        NslScope scopeTmp;
        NslParserVisitor visitor;
        String fileName;//, fullFileName;
        int[] pos = new int[parsedFiles.size()];
        int numFiles = 0, index = 0;
        while (e.hasMoreElements())
        {
            scopeTmp = (NslScope) e.nextElement();
            if (scopeTmp.shouldGenerateCode())
            {
                ASTCompilationUnit ast = scopeTmp.getAST();

                fullModuleName = scopeTmp.getFullFileName();
                moduleName = scopeTmp.getFileName();
                fileName = new StringBuilder("").append(moduleName).append(".java").toString();
                visitor = new ModifyVisitor();
                ast.jjtAccept(visitor, scopeTmp);

                generateCode(ast, fileName);
                pos[numFiles++] = index;
            }
            index++;
        }
        if(numFiles>0)
        {
            String[] fileNames = new String[numFiles];
            for (int i = 0; i < numFiles; i++)
            {
                scopeTmp = parsedFiles.elementAt(pos[i]);
                fileNames[i] = scopeTmp.getFileName();
            }
            return fileNames;
        }
        return null;
    }

    public static String[] dumpXMLFiles()
    {
        Enumeration e = parsedFiles.elements();

        NslScope scopeTmp;
        NslParserVisitor visitor;
        String fileName;
        int[] pos = new int[parsedFiles.size()];
        int numFiles = 0, index = 0;
        while (e.hasMoreElements())
        {
            scopeTmp = (NslScope) e.nextElement();
            if (scopeTmp.shouldGenerateCode())
            {
                ASTCompilationUnit ast = scopeTmp.getAST();

                fileName = new StringBuilder("\"").append(scopeTmp.getFileName()).append(".xml\"").toString();

                visitor = new ComputeTypeVisitor();
                ast.jjtAccept(visitor, scopeTmp);

                generateXMLCode(ast, fileName);
                pos[numFiles++] = index;
            }
            index++;
        }
        if(numFiles>0)
        {
            String[] fileNames = new String[numFiles];
            for (int i = 0; i < numFiles; i++)
            {
                scopeTmp = parsedFiles.elementAt(pos[i]);
                fileNames[i] = scopeTmp.getFileName();
            }
            return fileNames;
        }
        return null;
    }

    public static String javaToNslType(String type)
    {
        int dim;
        if ((dim = type.lastIndexOf("[")) >= 0)
        {
            dim++;
            StringBuilder temp = new StringBuilder("");
            for (int i = 0; i < dim; i++)
            {
                temp.append("[]");
            }
            String kind = type.substring(dim, dim + 1);
            if (kind.equals("L"))
            {
                type = type.substring(dim + 1, type.length() - 1) + temp.toString();
            }
            else if (kind.equals("B"))
            {
                type = "byte" + temp.toString();
            }
            else if (kind.equals("C"))
            {
                type = "char" + temp.toString();
            }
            else if (kind.equals("D"))
            {
                type = "double" + temp.toString();
            }
            else if (kind.equals("F"))
            {
                type = "float" + temp.toString();
            }
            else if (kind.equals("I"))
            {
                type = "int" + temp.toString();
            }
            else if (kind.equals("J"))
            {
                type = "long" + temp.toString();
            }
            else if (kind.equals("S"))
            {
                type = "short" + temp.toString();
            }
            else if (kind.equals("Z"))
            {
                type = "boolean" + temp.toString();
            }
        }
        return type;
    }

    public static NslScope createNslScopeFromClass(String className, String path)
    {
        NslScope scope = null;

        try
        {
            Class object;
            try
            {
                object = URLClassLoader.newInstance(new URL[]{(new File(path)).toURI().toURL()}).loadClass(className);
            }
            catch (Exception e)
            {
                object = Class.forName(className);
            }

            scope = new NslScope();

            //int mod;

            String modifiers,
                    packageName = ((object.getPackage() != null) ? object.getPackage().getName() : null),
                    superClassName = ((object.getSuperclass() != null) ? object.getSuperclass().getName() : null),
                    kind = (object.isInterface() ? "interface" : "class"),
                    name;
            Modifier.toString(object.getModifiers());

            Class type;

            scope.setPackage(packageName);
            scope.setClassKind(kind);
            scope.setClassName(className);

            scope.setSuperClassName(superClassName);

            addNslScope(scope);

            Class interfaces[] = object.getInterfaces();

            for (Class aInterface : interfaces)
            {
                scope.addInterface(parseType(aInterface.getName(), libraryDir, null));
            }

            if (superClassName != null)
            {
                scope.setSuperClassScope(parseType(superClassName, libraryDir, null));
            }

            NslVariable variable;
            Field[] fields = object.getDeclaredFields();
            for (Field field : fields)
            {
                type = field.getType();
                name = field.getName();
                modifiers = Modifier.toString(field.getModifiers());
                variable = new NslVariable(modifiers, javaToNslType(type.getName()), name);
                scope.addLocalVar(variable);
            }

            NslMethod method;
            Method[] methods = object.getDeclaredMethods();
            Class[] parameters;
            String[] formals;
            for (Method method1 : methods)
            {
                type = method1.getReturnType();
                name = method1.getName();
                modifiers = Modifier.toString(method1.getModifiers());
                parameters = method1.getParameterTypes();
                formals = new String[parameters.length];
                for (int j = 0; j < formals.length; j++)
                {
                    formals[j] = javaToNslType(parameters[j].getName());
                }
                method = new NslMethod(modifiers, javaToNslType(type.getName()), name, formals);
                scope.addMethod(method);
            }

            Class[] subClasses = object.getDeclaredClasses();
            for (Class subClass : subClasses)
            {
                scope.addSubClass(createNslScopeFromClass(subClass.getName(), path));
            }
            //addNslScope(scope);
        }
        catch (Exception ex)
        {
            System.err.println("nslc.src.NslCompiler [Error]: Compiler Bug, class " + className + " was not found");
            if (verbose)
            {
                ex.printStackTrace();
            }
        }

        return scope;
    }

    public static NslParser parseFile(String fileName)
    {
        NslParser parser = null;
        try
        {
            NslFile.loadFile(fileName);
            FileInputStream inputFile = new FileInputStream(fileName);
            parser = new NslParser(inputFile);
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
            System.err.println("nslc.src.NslCompiler [Error]: \"" + ioException.getMessage() + "\" while opening source file " + fileName);
        }
        catch (Exception parseException)
        {

            System.err.println("nslc.src.NslCompiler [Error]: Compiler bug, a  parser exception was occured in " + fileName);
            parseException.printStackTrace();
        }
        return parser;
    }

    /*public static NslScope parseType(String importName, Vector importList)
    {
        return parseType(importName, null, importList);
    }*/

    public static NslScope parseType(String importName, String path, Vector importList)
    {

        NslScope scope = getNslScope(importName);

        if (scope != null)
        {
            return scope;
        }

        // Avoid cycles
        if (moduleName.equals(importName))
        {
            return null;
        }

        if (verbose)
        {
            System.err.println("nslc.src.NslCompiler [Verbose]: Parsing class: " + importName);
        }

        String importPath = importName.replace(".", File.separator).replace("/", File.separator);
        if (path != null)
        {
            StringBuilder importPathBuilder = new StringBuilder(path);
            /*if (path.charAt(1) == ':' && path.charAt(2) == File.separatorChar)
            {
                importPathBuilder = new StringBuilder(path.substring(2));
            }*/
            if (!path.endsWith(File.separator))
            {
                importPathBuilder.append(File.separatorChar);
            }
            importPathBuilder.append(importName.replace(".", File.separator).replace("/", File.separator));
            importPath = importPathBuilder.toString();
        }
        String srcFileName = findFile(importPath + ".mod", importList),
                xmlFileName = findFile(importPath + ".xml", importList),
                javaFileName = findFile(importPath + ".java", importList),
                classFileName = findFile(importPath + ".class", importList);

        if (srcFileName != null)
        {
            File srcFile = new File(srcFileName);
            if (verbose)
            {
                System.err.println("nslc.src.NslCompiler [Verbose]: Parsing file: " + srcFileName);
            }

            // A mod file exists, should create a nslc.src.NslScope from it.
            NslParser parser = parseFile(srcFileName);
            if ((generateClass &&
                    (classFileName == null ||
                            (srcFile.lastModified() > (new File(classFileName)).lastModified()))) ||
                    (generateJavaCode &&
                            (javaFileName == null ||
                                    (srcFile.lastModified() > (new File(javaFileName)).lastModified()))) ||
                    (generateXMLCode &&
                            (xmlFileName == null ||
                                    (srcFile.lastModified() > (new File(xmlFileName)).lastModified()))))
            {
                fileNames.push(moduleName);
                fileNames.push(fullModuleName);
                moduleName = importName;
                fullModuleName = srcFileName;
                if (parser != null && !NslParser.error)
                {
                    try
                    {
                        ASTCompilationUnit ast = parser.CompilationUnit();

                        if (!NslParser.error)
                        {
                            scope = new NslScope();
                            scope.setFullFileName(srcFileName);
                            scope.setFileName(importPath);
                            scope.setAST(ast);
                            scope.setClassName(importName);
                            NslParserVisitor visitor = new SymbolTableClassVisitor(scope);
                            ast.jjtAccept(visitor, null);

                            // Avoid cycles
                            if (!wasThisFileVisited(srcFileName))
                            {
                                addNslScope(scope);
                                addVisitedFile(srcFileName);
                            }

                            if (!NslParser.error)
                            {
                                visitor = new SymbolTableVisitor(scope);
                                ast.jjtAccept(visitor, null);
                            }

                            // Check if the java/xml file and/or the class file should be generated


                                if (verbose)
                                {
                                    System.err.println("nslc.src.NslCompiler [Verbose]: Scheduling " + srcFileName + " for later code generation");
                                }
    //			    scope.setFullFileName(srcFileName);
    //		            scope.setFileName(importPath);
                                scope.setGenerateCode(true);


    //		        addNslScope(scope);
                        }
                        else if (verbose)
                        {
                            System.err.println("nslc.src.NslCompiler [Verbose]: Syntax errors were found while compiling " + srcFileName);
                        }
                    }
                    catch (ParseException parseException)
                    {
                        System.err.println("nslc.src.NslCompiler [Error]: Compiler bug, a  parser exception was occured in " + srcFileName);
                        if (verbose)
                        {
                            parseException.printStackTrace();
                        }
                    }

                }
                else if (verbose)
                {
                    System.err.println("nslc.src.NslCompiler [Error]: Compiler Bug. Couldn't get AST of " + srcFileName);
                }
                try
                {
                    fullModuleName = fileNames.pop();
                    moduleName = fileNames.pop();
                }
                catch (EmptyStackException stackException)
                {
                    System.err.println("nslc.src.NslCompiler [Error]: Compiler Bug. Empty file name list.");
                    throw stackException;
                }
            }
            else if (parser != null && !NslParser.error)
            {
                try
                {
                    ASTCompilationUnit ast = parser.CompilationUnit();

                    if (!NslParser.error)
                    {
                        scope = new NslScope();
                        scope.setFullFileName(srcFileName);
                        scope.setFileName(importPath);
                        scope.setAST(ast);
                        scope.setClassName(importName);
                        NslParserVisitor visitor = new SymbolTableClassVisitor(scope);
                        ast.jjtAccept(visitor, null);
                        
                        // Avoid cycles
                        if (!wasThisFileVisited(srcFileName))
                        {
                            addNslScope(scope);
                            addVisitedFile(srcFileName);
                        }
                    }
                }
                catch (ParseException parseException)
                {
                    System.err.println("nslc.src.NslCompiler [Error]: Compiler bug, a  parser exception was occured in " + srcFileName);
                    if (verbose)
                    {
                        parseException.printStackTrace();
                    }
                }
            }
        }
        else
        {

            if (javaFileName != null)
            {
                if (verbose)
                {
                    System.err.println("nslc.src.NslCompiler [Verbose]: The file " + javaFileName + " exists");
                }
                if (classFileName == null || ((new File(javaFileName)).lastModified() > (new File(classFileName)).lastModified()))
                {
                    // Update class file from it's java file
                    String[] fileNames = new String[1];
                    fileNames[0] = javaFileName.substring(0, javaFileName.lastIndexOf("."));

                    if (parseJavaFiles(fileNames))
                    {
                        classFileName = javaFileName.substring(0, javaFileName.lastIndexOf(".")) + ".class";
                    }
                    else
                    {
                        classFileName = null;
                    }
                }
            }
            if (classFileName != null)
            {
                String fullName = findClassInPackages(importName, path, importList);
                if (verbose)
                {
                    System.err.println("nslc.src.NslCompiler [Verbose]: The file " + classFileName + " exists in package " + fullName);
                }
                scope = createNslScopeFromClass(fullName, path);
            }
            else
            {
                // See if it is a System class
                String fullName = findClassInPackages(importName, path, importList);
//		String fullName = findClassInSystem(importName);
                if (fullName != null)
                {
                    scope = createNslScopeFromClass(fullName, path);
                }
                else
                {
                    //printError("nslc.src.NslCompiler", "Couldn't find class "+importName);
                }
            }
        }
        return scope;
    }

    static String findClass(String name)
    {
        try
        {
            Class.forName(name);
            return name;
        }
        catch (Exception ex)
        {
        }
        return null;
    }

    static String findClassInSystem(String name)
    {
        String fullName = findClass(name);
        if (fullName == null)
        {
            Package packages[] = Package.getPackages();
            for (Package aPackage : packages)
            {
                try
                {
                    fullName = new StringBuilder("").append(aPackage.getName()).append('.').append(name).toString();
                    Class.forName(fullName);
                    return fullName;
                }
                catch (Exception ex)
                {
                }
            }
            return null;
        }

        return fullName;
    }

    static String findClassInPackages(String name, String path, Vector importList)
    {
        String fullName = findClassInSystem(name);
        if (fullName == null && importList != null)
        {
            Enumeration e = importList.elements();
            String packageName;
            while (e.hasMoreElements())
            {
                packageName = (String) e.nextElement();
                fullName = new StringBuilder("").append(packageName.replace(File.separator, ".").replace("/", ".")).append('.').append(name).toString();
                try
                {
                    URLClassLoader.newInstance(new URL[]{(new File(path)).toURI().toURL()}).loadClass(fullName);
                    return fullName;
                }
                catch (Exception ex1)
                {
                    try
                    {
                        fullName = new StringBuilder("").append(packageName).append('.').append(name).toString();
                        Class.forName(fullName);
                        return fullName;
                    }
                    catch (Exception ex2)
                    {
                    }
                }
            }
            return null;
        }
        return fullName;
    }

    /*
     *
     * Compiles a java file and genetares
     * a class file
     *
     */

    public static boolean parseJavaFiles(String[] fileNames)
    {

        boolean result = true;

        // Compile the Java File

        //String classpath = System.getProperty("java.class.path");
        StringBuilder classpath = new StringBuilder("");
        if (libraryDir != null && libraryDir.length() > 0)
        {
            classpath.append(libraryDir);
        }
        for (String fileName : fileNames)
        {
            String dir;
            int endIndex = fileName.lastIndexOf(File.separatorChar);
            if (endIndex != -1)
            {
                dir = fileName.substring(0, endIndex);
            }
            else
            {
                dir = "";
            }
            if (!dir.equals(System.getProperty("user.dir")) && dir.length() > 0)
            {
                classpath.append(File.pathSeparatorChar);
                classpath.append(dir);
            }
        }
        for (String aClassPath : classPath)
        {
            classpath.append(File.pathSeparatorChar);
            classpath.append(aClassPath);
        }
        StringBuilder cp = new StringBuilder(classpath.toString());
        if (cp.indexOf(" ") > -1)
        {
            if (System.getProperty("os.name").toLowerCase().indexOf("win") > -1)
            {
                cp.insert(0,'\"');
                cp.append('\"');
            }
            else
            {
                cp = new StringBuilder(cp.toString().replace(" ", "\\ "));
            }
        }
        int offset = (deprecation ? 4 : 3);

        String[] parameters = new String[offset + fileNames.length];
        parameters[0] = "javac";
        parameters[1] = "-classpath";
        parameters[2] = cp.toString();
        if (deprecation)
        {
            parameters[3] = "-deprecation";
        }
        for (int i = 0; i < fileNames.length; i++)
        {
            String javaFileName=new StringBuilder(fileNames[i]).append(".java").toString();
            if (javaFileName.indexOf(" ") > -1)
            {
                if (System.getProperty("os.name").toLowerCase().indexOf("win") > -1)
                {
                    javaFileName="\""+javaFileName+"\"";
                }
                else
                {
                    javaFileName = javaFileName.replace(" ", "\\ ");
                }
            }
            parameters[offset + i] = javaFileName;
        }

        try
        {
            Process process = (Runtime.getRuntime()).exec(parameters);
            InputStream errStr = process.getErrorStream();
            byte[] errbyte = new byte[100];

            int i = 0, element = 0;

            while (element >= 0)
            {
                errbyte[i++] = (byte) (element = errStr.read());

                if (element == -1 && i > 1)
                {
                    System.err.write(errbyte, 0, i - 1);
                }
                else if (i == 100)
                {
                    System.err.write(errbyte, 0, 100);
                    i = 0;
                }
            }

            errStr.close();

            process.waitFor();

            if (process.exitValue() != 0)
            {
                for (String fileName : fileNames)
                {
                    if (findFileInClassPath(new StringBuilder("").append(fileName).append(".class").toString()) == null)
                    {
                        System.err.println(new StringBuilder("").append("nslc.src.NslCompiler [Error]: The class ").append(fileName).append(" was not created").toString());
                    }
                }
                result = false;
            }
            else if (verbose)
            {
                for (String fileName : fileNames)
                {
                    System.err.println(new StringBuilder("").append("nslc.src.NslCompiler [Verbose]: The class ").append(fileName).append(" was created").toString());
                }
            }
        }
        catch (Exception processException)
        {
            System.err.println("nslc.src.NslCompiler [Error]: Process exception while compiling java file");
            System.err.println("nslc.src.NslCompiler [Error]: " + processException.getMessage());
            if (verbose)
            {
                processException.printStackTrace();
            }
            result = false;
        }
        return result;
    }

    /*
     *
     *  Searches for the file fileName in all the class path
     *  Returns the file name if it actually found it
     *  or null if it didn't find it.
     *
     */

    private static String findFileInClassPath(String fileName)
    {
        String tryName = fileName;
        if (fileName.indexOf(' ') > -1)
        {
            if (System.getProperty("os.name").toLowerCase().indexOf("win") > -1)
                tryName = "\"" + fileName + '\"';
            else
                tryName = tryName.replace(" ", "\\ ");
        }
        if ((new File(tryName)).exists())
            return fileName;
        else if((new File(fileName)).exists())
            return fileName;
        String fname = fileName;
        if (fname.charAt(1) == ':')
        {
            fname = fname.substring(2);
        }
        if (fname.startsWith(File.separator))
        {
            fname = fname.substring(1);
        }

        Enumeration classPathEnumeration = classPath.elements();
        while (classPathEnumeration.hasMoreElements())
        {
            String cpElem = classPathEnumeration.nextElement().toString();
            StringBuilder cpath = new StringBuilder(cpElem);
            if (!cpElem.endsWith(File.separator))
            {
                cpath.append(File.separatorChar);
            }
            cpath.append(fname);

            if (cpath.indexOf(" ") > -1)
            {
                if (System.getProperty("os.name").toLowerCase().indexOf("win") > -1)
                {
                    cpath.insert(0,'\"');
                    cpath.append('\"');
                }
                else
                {
                    cpath = new StringBuilder(cpath.toString().replace(" ", "\\ "));
                }
            }
            if (new File(cpath.toString()).exists())
            {
                return fileName;
            }
        }

        return null;
    }

    /*
     *
     *  Searches for the file fileName in the current directory or in all the class path
     *  Returns the file name if it actually found it
     *  or null if it didn't find it.
     *
     */

    private static String findFile(String fileName, Vector importList)
    {
        String result = findFileInClassPath(fileName);

        if (result == null && importList != null)
        {
            String newFilename = fileName;
            if (newFilename.charAt(1) == ':')
            {
                newFilename = newFilename.substring(2);
            }
            if (newFilename.startsWith(File.separator))
            {
                newFilename = newFilename.substring(1);
            }
            // Look into the imported directories
            Enumeration importEnumeration = importList.elements();
            while (importEnumeration.hasMoreElements())
            {
                String importName = (String) importEnumeration.nextElement();
                importName = new StringBuilder("").append(importName.replace('.', File.separatorChar)).append(File.separatorChar).append(newFilename).toString();
                result = findFileInClassPath(importName);
                if (result != null)
                {
                    return result;
                }
            }
        }
        return result;
    }

    /*
    *
    * Finds the user directory
    *
    */
    /*private static String getCurrentDirectory(String fileName)
    {
        String directory;
        int index;
        if ((index = fileName.lastIndexOf(System.getProperty("file.separator"))) >= 0)
        {
            directory = fileName.substring(0, index);
        }
        else
        {
            directory = System.getProperty("user.dir");
        }

        if (verbose)
        {
            System.err.println("nslc.src.NslCompiler [Verbose]: Current Directory is " + directory);
        }

        return directory;
    }*/

    /*
     *
     * Lists all files in dirname and searches fname there.
     * Returns the complete file name (dirname + fname)
     * if success.
     *
     */

    private static boolean cleanFilesInDirectory()
    {
        StringBuilder directoryName = new StringBuilder(System.getProperty("user.dir"));

        if (!directoryName.toString().endsWith(File.separator))
        {
            directoryName.append(File.separatorChar);
        }

        File searchedDirectory = new File(directoryName.toString());

        // do exception handling here.
        if (searchedDirectory.exists())
        {
            String[] files = searchedDirectory.list();

            int fileIndex = files.length;
            String fileName, javaFileName, classFileName, xmlFileName;
            File javaFile, classFile, xmlFile;
            while (--fileIndex > -1)
            {
                fileName = files[fileIndex];
                if (fileName.endsWith(".mod"))
                {
                    xmlFileName = new StringBuilder("").append(fileName.substring(0, fileName.lastIndexOf("."))).append(".xml").toString();
                    javaFileName = new StringBuilder("").append(fileName.substring(0, fileName.lastIndexOf("."))).append(".java").toString();
                    classFileName = new StringBuilder("").append(fileName.substring(0, fileName.lastIndexOf("."))).append(".class").toString();
                    xmlFile = new File(new StringBuilder("\"").append(xmlFileName).append('\"').toString());
                    javaFile = new File(new StringBuilder("\"").append(javaFileName).append('\"').toString());
                    classFile = new File(new StringBuilder("\"").append(classFileName).append('\"').toString());
                    if (xmlFile.exists())
                    {
                        xmlFile.delete();
                    }
                    if (javaFile.exists())
                    {
                        javaFile.delete();
                    }
                    if (classFile.exists())
                    {
                        classFile.delete();
                    }
                }
            }
        }
        else
        {
            return false;
        }

        return true;
    }

    /*
     * Save all directories of the classpath into the classPath vector
     */

    private static void initClassPath()
    {
        //currentDirectory = getCurrentDirectory(moduleName);

        String path = System.getProperty("java.class.path");
        classPath = new Vector<String>(10);

        // Save all directories of the classpath into the classPath vector
        int index;
        while ((index = path.indexOf(File.pathSeparator)) > 0)
        {
            classPath.addElement(path.substring(0, index));
            path = path.substring(index + 1);
        }
    }

    public static void generateCode(SimpleNode node, String outputFileName)
    {
        try
        {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));

            Token t1 = node.getFirstToken();
            Token t = new Token();
            t.next = t1;

            while (t != node.getLastToken())
            {
                t = t.next;
                print(t, out);
            }

            if (verbose)
            {
                System.err.println("nslc.src.NslCompiler [Verbose]: The java file " + outputFileName + " was created");
            }
            out.close();
        }
        catch (Exception ioException)
        {
            System.err.println("nslc.src.NslCompiler [Error]: \"" + ioException.getMessage() + "\" while generating java file");
        }
    }

    public static void generateXMLCode(SimpleNode node, String outputFileName)
    {
        try
        {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));

            // Print the xml file header:

            out.println("<?xml version=\"1.0\"?>");
            out.println("<?xml-stylesheet type=\"text/xsl\" href=\"nsl.xsl\"?>");
            out.println();
            out.println("<!-- XML code generated by nslc.src.NslCompiler (c) 2002 USC -->");
            out.println();
            node.dumpXML("", out);

            if (verbose)
            {
                System.err.println("nslc.src.NslCompiler [Verbose]: The xml file " + outputFileName + " was created");
            }
            out.close();
        }
        catch (Exception ioException)
        {
            System.err.println("nslc.src.NslCompiler [Error]: \"" + ioException.getMessage() + "\" while generating XML file");
        }
    }

    private static void print(Token t, PrintStream out)
    {
        Token tt = t.specialToken;
        if (tt != null)
        {
            while (tt.specialToken != null)
            {
                tt = tt.specialToken;
            }
            while (tt != null)
            {
                out.print(addUnicodeEscapes(tt.image));
                tt = tt.next;
            }
        }
        out.print(addUnicodeEscapes(t.image));
    }

    private static String addUnicodeEscapes(String str)
    {
        StringBuilder retval = new StringBuilder("");
        char ch;
        for (int i = 0; i < str.length(); i++)
        {
            ch = str.charAt(i);
            if ((ch < 0x20 || ch > 0x7e) &&
                    ch != '\t' && ch != '\n' && ch != '\r' && ch != '\f')
            {
                String s = new StringBuilder("").append("0000").append(Integer.toString(ch, 16)).toString();
                retval.append("\\u");
                retval.append(s.substring(s.length() - 4, s.length()));
            }
            else
            {
                retval.append(ch);
            }
        }
        return retval.toString();
    }

    public static void printError(String reporter, String description, int line, int col)
    {
        System.err.println(moduleName + ".mod:" + line + ": " + description);
        if (verbose)
        {
            System.err.println("Reported by: " + reporter);
        }
        System.err.println(NslFile.getLine(fullModuleName, line));
        StringBuilder spaces = new StringBuilder("");
        for (int i = 0; i < (col - 1) % 80; i++)
        {
            spaces.append(' ');
        }
        spaces.append('^');
        System.err.println(spaces.toString());
        NslParser.error = true;
        NslParser.errorCount++;
    }

    public static void printError(String reporter, String description)
    {
        System.err.println(moduleName + ".mod:" + description);
        if (verbose)
        {
            System.err.println("Reported by: " + reporter);
        }
        NslParser.error = true;
        NslParser.errorCount++;
    }

    public static String translateNslFunctionName(String functionName)
    {
        if (!functionName.startsWith("nsl"))
        {
            return functionName;
        }

        if (functionName.equals("nslPrint")) return "system.nslPrint";
        if (functionName.equals("nslPrintln")) return "system.nslPrintln";
        if (functionName.equals("nslprint")) return "system.nslPrint";
        if (functionName.equals("nslprintln")) return "system.nslPrintln";
        if (functionName.equals("NslPrint")) return "system.nslPrint";
        if (functionName.equals("NslPrintln")) return "system.nslPrintln";

        if (functionName.equals("nslCallMethod")) return "system.nslCallMethod";
        
        if (functionName.equals("nslRelabel")) return "nslConnect";

        if (functionName.equals("nslAdd")) return "NslAdd.eval";
        if (functionName.equals("nslConv")) return "NslConv.eval";
        if (functionName.equals("nslConvzero")) return "NslConvZero.eval";
        if (functionName.equals("nslConvW")) return "NslConvW.eval";
        if (functionName.equals("nslConvC")) return "NslConvC.eval";
        if (functionName.equals("nslCross")) return "NslCross.eval";
        if (functionName.equals("nslDiff")) return "system.nsldiff.eval";
        if (functionName.equals("nslFillColumns")) return "NslFillColumns.eval";
        if (functionName.equals("nslFillRows")) return "NslFillRows.eval";
        if (functionName.equals("nslMath")) return "system.nslmath.eval";
        if (functionName.equals("nslMatlabScalar")) return "nslj.src.system.NslMatlab.evalScalar";
        if (functionName.equals("nslMatlabVector")) return "nslj.src.system.NslMatlab.evalVector";
        if (functionName.equals("nslMatlabMatrix")) return "nslj.src.system.NslMatlab.evalMatrix";
        if (functionName.equals("nslMax")) return "NslMax.eval";
        if (functionName.equals("nslMaxValue")) return "NslMaxValue.eval";
        if (functionName.equals("nslMinValue")) return "NslMinValue.eval";
        if (functionName.equals("nslProd")) return "NslProd.eval";
        if (functionName.equals("nslProduct")) return "NslProd.eval";
        if (functionName.equals("nslRamp")) return "NslRamp.eval";
        if (functionName.equals("nslSaturation")) return "NslSaturation.eval";
        if (functionName.equals("nslSigmoid")) return "NslSigmoid.eval";
        if (functionName.equals("nslSigmoid2")) return "NslSigmoid2.eval";
        if (functionName.equals("nslStep")) return "NslStep.eval";
        if (functionName.equals("nslSub")) return "NslSub.eval";
        if (functionName.equals("nslTrans")) return "NslTrans.eval";
        if (functionName.equals("nslSum")) return "NslSum.eval";
        if (functionName.equals("nslSumRows")) return "NslSumRows.eval";
        if (functionName.equals("nslSumColumns")) return "NslSumColumns.eval";
        if (functionName.equals("nslMaxElem")) return "NslMaxElem.eval";
        if (functionName.equals("nslMinElem")) return "NslMinElem.eval";
        if (functionName.equals("nslMax")) return "NslMax.eval";
        if (functionName.equals("nslMin")) return "NslMin.eval";
        if (functionName.equals("nslGetSector")) return "NslGetSector.eval";
        if (functionName.equals("nslSetSector")) return "NslSetSector.eval";
        if (functionName.equals("nslElemMult")) return "NslElemMult.eval";
        if (functionName.equals("nslElemDiv")) return "NslElemDiv.eval";
        if (functionName.equals("nslConcatenateRows")) return "NslConcatenateRows.eval";
        if (functionName.equals("nslConcatenateColumns")) return "NslConcatenateColumns.eval";
        if (functionName.equals("nslGetRow")) return "NslGetRow.eval";
        if (functionName.equals("nslGetColumn")) return "NslGetColumn.eval";
        if (functionName.equals("nslRandom")) return "NslRandom.eval";
        if (functionName.equals("nslGaussian")) return "NslGaussian.eval";
        if (functionName.equals("nslAll")) return "NslAll.eval";
        if (functionName.equals("nslSome")) return "NslSome.eval";
        if (functionName.equals("nslNone")) return "NslNone.eval";
        if (functionName.equals("nslBound")) return "NslBound.eval";
        if (functionName.equals("nslDotProd")) return "NslDotProd.eval";

        // NslOperations
        if (functionName.equals("nslRint")) return "NslOperator.rint.eval";
        if (functionName.equals("nslAbs")) return "NslOperator.abs.eval";
        if (functionName.equals("nslPow")) return "NslOperator.pow.eval";
        if (functionName.equals("nslFloor")) return "NslOperator.floor.eval";
        if (functionName.equals("nslExp")) return "NslOperator.exp.eval";
        if (functionName.equals("nslLog")) return "NslOperator.log.eval";
        if (functionName.equals("nslMaxMerge")) return "NslOperator.max.eval";
        if (functionName.equals("nslMinMerge")) return "NslOperator.min.eval";
        if (functionName.equals("nslCos")) return "NslOperator.cos.eval";
        if (functionName.equals("nslSin")) return "NslOperator.sin.eval";
        if (functionName.equals("nslTan")) return "NslOperator.tan.eval";
        if (functionName.equals("nslArcCos")) return "NslOperator.acos.eval";
        if (functionName.equals("nslArcSin")) return "NslOperator.asin.eval";
        if (functionName.equals("nslArcTan")) return "NslOperator.atan.eval";
        if (functionName.equals("nslArcTan2")) return "NslOperator.atan2.eval";
        if (functionName.equals("nslSqrt")) return "NslOperator.sqrt.eval";
        if (functionName.equals("nslDistance")) return "NslOperator.distance.eval";
        if (functionName.equals("nslNorm")) return "NslOperator.norm.eval";

        return functionName;
    }
}

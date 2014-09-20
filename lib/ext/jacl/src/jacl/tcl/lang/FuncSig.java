/*
 * FuncSig.java --
 *
 *	This class implements the internal representation of a Java
 *	method or constructor signature.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: FuncSig.java,v 1.1.1.1 1998/10/14 21:09:14 cvsadmin Exp $
 *
 */

package jacl.tcl.lang;

import jacl.tcl.lang.reflect.PkgInvoker;

import java.lang.reflect.*;
import java.util.*;

/*
 * This class implements the internal representation of a Java method
 * or constructor signature. Because methods and constructors are very
 * similar to each other, the operations on method signatures and
 * constructor signatures are limped in this class of "function
 * signature."
 */

class FuncSig extends InternalRep
{

/*
 * The class that a method signature is used against. In the case of a
 * static method call by java::call, targetCls is given by the <class>
 * argument to java::call. In the case of an instance method call,
 * targetCls is the class of the instance. targetCls is used to test
 * the validity of a cached FuncSig internal rep for method
 * signatures.
 *
 * targetCls is not used for class signatures.
 */

    Class targetCls;

/*
 * The PkgInvoker used to access the constructor or method.
 */

    PkgInvoker pkgInvoker;

/*
 * The constructor or method given by the field signature. You need to
 * apply the instanceof operator to determine whether it's a
 * Constructor or a Method.
 *
 * func may be a public, protected, package protected or private
 * member of the given class. Attempts to access func is subject to
 * the Java language access control rules. Public members can always
 * be accessed. Protected and package protected members can be
 * accessed only if a proper TclPkgInvoker class exists. Private
 * members can never be accessed.
 *
 * If the signature is a method signature and the specified method has
 * been overloaded, then func will point to the "most public" instance
 * of that method. The order of public-ness is ranked as the following
 * (a larger number means more public):
 *
 * RANK		     METHOD ACCESS TYPE      CLASS ACCESS TYPE	
 *   0			private			any
 *   1			package protected	protected
 *   1			protected		protected
 *   1			public			protected
 *   1			package protected	public
 *   1			protected		public
 *   2			public			public
 */

    Object func;

/*
 * Stores all of the declared methods of each Java class.
 */

    static Hashtable allDeclMethTable = new Hashtable();

    
/*
 *----------------------------------------------------------------------
 *
 * FuncSig --
 *
 *	Creates a new FuncSig instance.
 *
 * Side effects:
 *	Member fields are initialized.
 *
 *----------------------------------------------------------------------
 */

    FuncSig(
            Class cls,            // Initial value for targetCls.
            PkgInvoker p,        // Initial value for pkgInvoker.
            Object f)            // Initial value for func.
    {
        targetCls = cls;
        pkgInvoker = p;
        func = f;
    }

    
/*
 *----------------------------------------------------------------------
 *
 * duplicate --
 *
 *	Make a copy of an object's internal representation.
 *
 * Results:
 *	Returns a newly allocated instance of the appropriate type.
 *
 * Side effects:
 *	None.
 *
 *----------------------------------------------------------------------
 */

    protected InternalRep
            duplicate()
    {
        return new FuncSig(targetCls, pkgInvoker, func);
    }

    
/*
 *----------------------------------------------------------------------
 *
 * get --
 *
 *	Returns the FuncSig internal representation of the constructor
 *	or method that matches with the signature and the parameters.
 *
 * Results:
 *	The FuncSig given by the signature.
 *
 * Side effects:
 *	When successful, the internalRep of the signature object is
 *	converted to FuncSig.
 *
 *----------------------------------------------------------------------
 */

    static FuncSig
            get(
            Interp interp,        // Current interpreter.
            Class cls,            // If null, we are looking for a constructor
            // in signature. If non-null, we are looking
            // for a method of this class in signature.
            TclObject signature,    // Method/constructor signature.
            TclObject[] argv,        // Arguments.
            int startIdx,        // Index of the first argument in argv
            int count)            // Number of arguments to pass to the
        // constructor.
            throws
            TclException
    {
        boolean isConstructor = (cls == null);

        /* FIXME: we comment out this code because it causes
         * the AmbiguousSignature-2.1 test to fail under Tcl Blend.
         * We need to implement a new signature cache system to fix this.
        InternalRep rep = signature.getInternalRep();

        // If a valid FuncSig internal rep is already cached, return it
        // right away.

        if (rep instanceof FuncSig) {
        FuncSig tmp = (FuncSig)rep;
        Object func = tmp.func;

        if (isConstructor) {
            if ((func instanceof Constructor)
                && (((Constructor)func).getParameterTypes().length
                    == count)) {
            return tmp;
            }
        } else {
            if ((func instanceof Method) && (tmp.targetCls == cls)
                && (((Method)func).getParameterTypes().length == count)) {
            return tmp;
            }
        }
        }
        */

        // Look up the constructor or method using the string rep of the
        // signature object.

        Object match;
        int sigLength = TclList.getLength(interp, signature);
        String methodName = null;

        if (sigLength == 0)
        {
            throw new TclException(interp, "bad signature \"" + signature + "\"");
        }

        if (isConstructor)
        {
            cls = JavaInvoke.getClassByName(interp,
                    TclList.index(interp, signature, 0).toString());
        }
        else
        {
            methodName = TclList.index(interp, signature, 0).toString();
        }

        if ((sigLength > 1) || (sigLength == 1 && count == 0))
        {
            /*
        * We come to here if one of the following two cases in true:
        *
        *     [1] (sigLength > 1): A signature has been given.
        *     [2] (sigLength == 1 && count == 0): A signature of no
        *         parameters is implied.
        *
        * In both cases, we search for a method that matches exactly
        * with the signature.
        */

            int sigNumArgs = sigLength - 1;
            Class[] paramTypes = new Class[sigNumArgs];

            for (int i = 0; i < sigNumArgs; i++)
            {
                String clsName = TclList.index(interp, signature, i + 1).toString();
                paramTypes[i] = JavaInvoke.getClassByName(interp, clsName);
            }

            if (isConstructor)
            {
                try
                {
                    match = cls.getDeclaredConstructor(paramTypes);
                }
                catch (NoSuchMethodException e)
                {
                    if (sigLength > 1)
                    {
                        throw new TclException(interp, "no such constructor \"" +
                                signature + "\"");
                    }
                    else
                    {
                        throw new TclException(interp, "can't find constructor with " +
                                count + " argument(s) for class \"" + cls.getName() +
                                "\"");
                    }
                }
            }
            else
            {
                match = lookupMethod(interp, cls, methodName, paramTypes, signature);
            }
        }
        else
        {
            match = matchSignature(interp, cls, signature, methodName,
                    isConstructor, argv, startIdx, count);
        }

        FuncSig sig = new FuncSig(cls, PkgInvoker.getPkgInvoker(cls), match);
        signature.setInternalRep(sig);

        return sig;
    }

//lookupMethod attempts to find an Exact for the method name
//mand method arguments (Class objects) given

//it will raise a TclException if a matching Method can not be found


    static Method
            lookupMethod(
            Interp interp,           //the tcl interpreter
            Class cls,               //the Java objects class
            String methodName,       //name of method
            Class[] paramTypes,      //the Class object arguments
            TclObject signature     //used for error reporting
    )
            throws TclException
    {
        Method methods[] = getAllDeclaredMethods(cls);
        boolean foundSameName = false;

//the search through ALL the methods is really slow

        for (int i = 0; i < methods.length; i++)
        {
            if (! methodName.equals(methods[i].getName()))
            {
                continue;
            }

            foundSameName = true;

            Class pt[] = methods[i].getParameterTypes();
            if (pt.length != paramTypes.length)
            {
                continue;
            }

            boolean good = true;
            for (int j = 0; j < pt.length; j ++)
            {
                if (pt[j] != paramTypes[j])
                {
                    good = false;
                    break;
                }
            }
            if (good)
            {
                return methods[i];
            }
        }


        if (paramTypes.length > 0 || !foundSameName)
        {
            throw new TclException(interp,
                    "no such method \"" + signature + "\" in class " +
                            cls.getName());
        }
        else
        {
            throw new TclException(interp,
                    "can't find method \"" + signature + "\" with " +
                            paramTypes.length + " argument(s) for class \"" + cls.getName()
                            + "\"");
        }
    }

//this method will attempt to find a match for a signature
//if an exact match can not be found then it will use
//the types of the argument objects to "guess" what
//method was intended by the user
//if match is not found it will raise a TclException


    static Object
            matchSignature(
            Interp interp,           //the tcl interpreter
            Class cls,               //the Java objects class
            TclObject signature,    //used for error reporting
            String methodName,       //name of method, can be null
            boolean isConstructor,   //duh
            TclObject[] argv,        //argument to Method or Constructor
            int startIdx,         // Index of the first argument in argv
            int argv_count
    )
            throws TclException
    {
        Object funcs[];
        boolean foundSameName = false;
        Vector match_vector = new Vector();
        int i, j;

        if (isConstructor)
        {
            funcs = cls.getDeclaredConstructors();
        }
        else
        {
            funcs = getAllDeclaredMethods(cls);
        }

        for (i = 0; i < funcs.length; i++)
        {
            Class[] paramTypes;
            if (isConstructor)
            {
                paramTypes = ((Constructor) funcs[i]).getParameterTypes();
            }
            else
            {
                Method method = (Method) funcs[i];
                if (! methodName.equals(method.getName()))
                {
                    continue;
                }
                foundSameName = true;

                paramTypes = method.getParameterTypes();
            }

            if (paramTypes.length == argv_count)
            {
                match_vector.addElement(funcs[i]);
            }
        }

        //return now if we have a single match

        if (match_vector.size() == 1)
        {
            return match_vector.elementAt(0);
        }
        else if (match_vector.size() > 1)
        {

            Class[] argv_classes = new Class[argv_count];
            Class[] match_classes;

            //get the object types for the method arguments in argv
            for (i = 0; i < argv_count; i++)
            {
                TclObject tobj = argv[startIdx + i];
                InternalRep ir = tobj.getInternalRep();

                if (ir instanceof ReflectObject)
                {
                    argv_classes[i] = ReflectObject.getClass(interp, argv[startIdx + i]);
                }
                else
                {
//should this use internal rep for conversion??
                    argv_classes[i] = String.class;
                }
            }

            /*

            //debug print argv types

            System.out.println("multiple matches for method " + methodName);

            System.out.print("argv    is ");

            for (i=0; i < argv_count; i++) {
              Class c = argv_classes[i];
              System.out.print( c.getName() );
              System.out.print(" ");
            }
            System.out.println();


            //debug print match types

            for (i=0; i < match_vector.size(); i++) {

              if (isConstructor) {
            match_classes = ((Constructor) match_vector.elementAt(i)).getParameterTypes();
              } else {
            match_classes = ((Method) match_vector.elementAt(i)).getParameterTypes();
              }

              System.out.print("match " + i + " is ");

              for (j=0; j < match_classes.length; j++) {
            Class c = match_classes[j];
            System.out.print( c.getName() );
            System.out.print(" ");
              }

              System.out.println();
            }

            */

            //try to match the argument types and the match
            //types exactly by comparing Class objects

            for (i = 0; i < match_vector.size(); i++)
            {

                if (isConstructor)
                {
                    match_classes = ((Constructor) match_vector.elementAt(i)).getParameterTypes();
                }
                else
                {
                    match_classes = ((Method) match_vector.elementAt(i)).getParameterTypes();
                }

                boolean exact = true;
                for (j = 0; j < argv_count; j++)
                {
                    if (match_classes[j] != argv_classes[j])
                    {
                        exact = false;
                        break;
                    }
                }

                if (exact)
                {
                    //System.out.println("exact match at " + i);
                    return match_vector.elementAt(i);
                }
            }

            //loop from the end of the Vector to the begining and
            //remove those signatures that are not assignable

            for (i = match_vector.size() - 1; i >= 0; i--)
            {

                if (isConstructor)
                {
                    match_classes = ((Constructor) match_vector.elementAt(i)).getParameterTypes();
                }
                else
                {
                    match_classes = ((Method) match_vector.elementAt(i)).getParameterTypes();
                }

                //test for assignability if the item is not an exact match

                for (j = 0; j < argv_count; j++)
                {
                    if (match_classes[j] != argv_classes[j] &&
                            !match_classes[j].isAssignableFrom(argv_classes[j]))
                    {

                        //if this argument is totally incompatible with the signature
                        //then remove this signature from the possible matches

                        match_vector.removeElementAt(i);
                        break;
                    }
                }

            }

            /*

            //debug print match_vector

            if (match_vector.size() > 0) {
              System.out.println("isAssignableFrom() matches");
            }

            for (i=0; i < match_vector.size(); i++) {

              if (isConstructor) {
            match_classes = ((Constructor) match_vector.elementAt(i)).getParameterTypes();
              } else {
            match_classes = ((Method) match_vector.elementAt(i)).getParameterTypes();
              }

              System.out.print("match " + i + " is ");

              for (j=0; j < argv_count; j++) {
            Class c = match_classes[j];
            System.out.print( c.getName() );
            System.out.print(" ");
              }

              System.out.println();
            }

            */

            //if we have filtered the methods down to a single method return it

            if (match_vector.size() == 1)
            {
                return match_vector.elementAt(0);
            }

            //at this point match_vector should have only those signatures
            //that can take the argument types from argv with widining conversion

            //to figure out which method we should call we need to determine
            //which signatures are "better" then others where "better" is defined
            //as the shortest number of steps up the inheritance tree

            if (match_vector.size() > 1)
            {

                //the first thing we need to do is get the inheritance info
                //from the Java classes of the arguments to the method.
                //from this we will create an array of Class objects that
                //we will then use to match againsts the possible Method
                //objects that we could invoke with this class name

                //as an example if we invoked a Method that took one
                //String argument this argv_classes_lookup array would
                //end up as a 1 x 4 array with this array as index 0
                //the Class objects representing these objects
                //and including any interface Class objects

                // {String,Serializable,null,Object}


                Class[][] argv_classes_lookup = new Class[argv_count][];

                //we use a vector to store up all of the Class objects
                //that make up the inheritance tree for a particular class

                Vector class_vector = new Vector();

                //for each argument to the method we loop up the inheritance tree
                //to find out if there is a superclass argv class that exactly matches

                for (i = 0; i < argv_count; i++)
                {

                    Class c = argv_classes[i]; //start with class type of argument

                    //if we have alredy built this Class object lookup array
                    //then dont do it again.

                    //loop over the first elements of the argv_classes_lookup to find out
                    //if we have already looked up this class object

                    //... add impl here

                    //loop up the inheritance tree starting from c

                    while (c != null)
                    {
                        //add a null to the front of the vector
                        class_vector.addElement(null);

                        //add this Class and its Interfaces to the vector
                        addInterfaces(c, class_vector);

                        c = c.getSuperclass();
                    }

                    //now remove the first element of the vector (it is null)
                    class_vector.removeElementAt(0);

                    Class[] classes = new Class[class_vector.size()];

                    class_vector.copyInto(classes);

                    argv_classes_lookup[i] = classes;

                    class_vector.removeAllElements();
                }

                /*

                //debug print the argv_classes_lookup array

                System.out.println("argv_classes_lookup array");


                for (i=0; i < argv_count; i++) {

              Class[] classes = argv_classes_lookup[i];

              System.out.print("{ ");

              for (j=0; j < classes.length; j++) {

                if (classes[j] == null) {
                  System.out.print("null");
                } else {
                  System.out.print(classes[j].getName());
                }

                System.out.print(' ');
              }

              System.out.println("}");

                }

                */



                int[] super_steps = new int[match_vector.size()];
                int[] total_steps = new int[match_vector.size()];
                boolean[] trim_matches = new boolean[match_vector.size()];
                int min_super_step;
                int min_total_step;
                Class min_class;

                //iterate over the arguments then the Methods
                //as opposed to Methods then over the arguments

                for (j = 0; j < argv_count; j++)
                {

                    //we need to keep track of the smallest # of jumps up the
                    //inheritance tree as well as the total min for the one
                    //special case where an implemented interface inherits
                    //from another interface that also matches the signature

                    min_super_step = Integer.MAX_VALUE;
                    min_total_step = Integer.MAX_VALUE;

                    //define min_class as base object before we loop
                    min_class = Object.class;

                    //iterate over the matched methods to find the
                    //minimum steps for this argument

                    for (i = 0; i < match_vector.size(); i++)
                    {

                        if (isConstructor)
                        {
                            match_classes = ((Constructor) match_vector.elementAt(i)).getParameterTypes();
                        }
                        else
                        {
                            match_classes = ((Method) match_vector.elementAt(i)).getParameterTypes();
                        }

                        Class match_to = match_classes[j];

                        //Class objects we will compare the match_to Class against
                        //the index (j) gives us the Class array for argv[j]
                        Class[] arg_classes = argv_classes_lookup[j];

                        Class c;
                        int super_step = 0;
                        int total_step = 0;

                        for (; total_step < arg_classes.length; total_step++)
                        {
                            c = arg_classes[total_step];

                            if (c == null)
                            {
                                super_step++; //null means we have gone up to the superclass
                            }
                            else if (c == match_to)
                            {
                                super_steps[i] = super_step; //# of super classes up
                                total_steps[i] = total_step; //total # of visible classes

                                //when we define the min for an argument we must make
                                //sure that three precidence rules are followed
                                //1: an interface can replace another interface as the min
                                //2: an interface can replace the class Object
                                //3: a class can replace an interface or a class

                                //thus if we have already found a non Object min_class
                                //it can not be replaced by an interface

                                if (super_step <= min_super_step)
                                {

                                    if (!c.isInterface() ||
                                            min_class == Object.class ||
                                            min_class.isInterface())
                                    {

                                        /*
                    System.out.println("redefing min");
                    System.out.println("min_class was " + min_class);
                    System.out.println("min_class is now " + c);
                    */

                                        min_class = c;

                                        min_super_step = super_step;

                                        //check min_total_step only AFTER a min_super_step
                                        //or equal to min_super_step has been found

                                        if (total_step < min_total_step)
                                        {
                                            min_total_step = total_step;
                                        }
                                    }
                                }

                                break;
                            }
                        }
                    }

                    /*

         //debug print the super_step array and the total_step array

         System.out.println("step arrays for argument " + j);

         for (int loop=0; loop < match_vector.size(); loop++) {
           System.out.println("(" + super_steps[loop] + "," + total_steps[loop] + ")");
         }

         System.out.println("min_super_step = " + min_super_step);
         System.out.println("min_total_step = " + min_total_step);

         */

                    //from the step info we know the minumum so we can
                    //remove those values that are "worse" then the min

                    for (i = match_vector.size() - 1; i >= 0; i--)
                    {

                        if (super_steps[i] > min_super_step ||
                                (super_steps[i] == min_super_step &&
                                        total_steps[i] > min_total_step))
                        {
                            //System.out.println("will trim method " + i);
                            trim_matches[i] = true; //trim this match # later
                        }

                    }

                    //we should be able to short circut this so that we do
                    //not waste loops when they are not needed

                    //if all the methods have been trimmed then we do not
                    //need to loop to the next argument

                }

                //remove and methods that were marked for deletion

                for (i = match_vector.size() - 1; i >= 0; i--)
                {
                    if (trim_matches[i])
                    {
                        match_vector.removeElementAt(i);
                    }
                }

                /*

                System.out.println("after super steps trim");

                for (i=0; i < match_vector.size(); i++) {

              if (isConstructor) {
                match_classes = ((Constructor) match_vector.elementAt(i)).getParameterTypes();
              } else {
                match_classes = ((Method) match_vector.elementAt(i)).getParameterTypes();
              }

              System.out.print("match " + i + " is ");

              for (j=0; j < argv_count; j++) {
                Class c = match_classes[j];
                System.out.print( c.getName() );
                System.out.print(" ");
              }

              System.out.println();
                }
                */


            } //end if (match_vector.size() > 1)

            //if there is only one item left in the match_vector return it

            if (match_vector.size() == 1)
            {
                return match_vector.elementAt(0);
            }
            else
            {
                //if we have 0 or >1 remaining matches then
                //we were unable to find the "best" match so raise an error

                throw new TclException(interp, "ambiguous " +
                        (isConstructor ? "constructor" : "method") +
                        " signature \"" + signature + "\"");
            }

        } //end else if (match_vector.size() > 1)

        //if we got to here then we could not find a matching method so raise error

        if (isConstructor)
        {
            throw new TclException(interp, "can't find constructor with " +
                    argv_count + " argument(s) for class \"" +
                    cls.getName() + "\"");
        }
        else
        {

            if (!foundSameName)
            {
                throw new TclException(interp,
                        "no such method \"" + signature + "\" in class " +
                                cls.getName());
            }
            else
            {
                throw new TclException(interp,
                        "can't find method \"" + signature + "\" with " +
                                argv_count + " argument(s) for class \"" +
                                cls.getName() + "\"");
            }
        }
    }

//this function recursively adds interfaces of a class to the vector

    static void addInterfaces(Class cls, Vector v)
    {
        v.addElement(cls);

        Class[] interfaces = cls.getInterfaces();

        for (int i = 0; i < interfaces.length; i++)
        {
            addInterfaces(interfaces[i], v);
        }
    }


    
/*
 *----------------------------------------------------------------------
 *
 * getAllDeclaredMethods --
 *
 *	Returns all the methods declared by the class or superclasses
 *	of the class.
 *
 * Results:
 *	An array of all the methods declared by the class and the
 *	superclasses of this class. If overloaded methods, only the
 *	"most public" instance of that method is included in the
 *	array. See comments above the "func" member variable for more
 *	details.
 *
 * Side effects:
 *	The array of methods are saved in a hashtable for faster access
 *	in the future.
 *
 *----------------------------------------------------------------------
 */

    static Method[]
            getAllDeclaredMethods(
            Class cls)                // The class to query.
    {
        Method methods[] = (Method[]) allDeclMethTable.get(cls);
        if (methods != null)
        {
            return methods;
        }

        Vector vec = new Vector();

        for (Class c = cls; c != null; c = c.getSuperclass())
        {
            mergeMethods(c, c.getDeclaredMethods(), vec);

            Class interfaces[] = c.getInterfaces();
            for (int i = 0; i < interfaces.length; i++)
            {
                mergeMethods(interfaces[i], interfaces[i].getMethods(), vec);
            }
        }

        methods = new Method[vec.size()];
        vec.copyInto(methods);
        allDeclMethTable.put(cls, methods);

        return methods;
    }

    
/*
 *----------------------------------------------------------------------
 *
 * mergeMethods --
 *
 *	Add the methods declared by a super-class or an interface to
 *	the list of declared methods of a class.
 *
 * Results:
 *	None.
 *
 * Side effects:
 *	Elements of methods[] are added to vec. If an instance of
 *	an overloaded method is already in vec, it will be replaced
 *	by a new instance only if the new instance has a higher rank.
 *
 *----------------------------------------------------------------------
 */

    static void
            mergeMethods(
            Class c,
            Method methods[],
            Vector vec)
    {
        for (int i = 0; i < methods.length; i++)
        {
            boolean sameSigExists = false;
            Method newMeth = methods[i];

            for (int j = 0; j < vec.size(); j++)
            {
                Method oldMeth = (Method) vec.elementAt(j);

                if (methodSigEqual(oldMeth, newMeth))
                {
                    sameSigExists = true;

                    Class oldCls = oldMeth.getDeclaringClass();
                    int newRank = getMethodRank(c, newMeth);
                    int oldRank = getMethodRank(oldCls, oldMeth);

                    if (newRank > oldRank)
                    {
                        vec.setElementAt(newMeth, j);
                    }
                    break;
                }
            }

            if (!sameSigExists)
            {
                /*
             * We copy a method into the vector only if no method
             * with the same signature is already in the
             * vector. Otherwise the matching routine in the get()
             * procedure may run into "ambiguous method signature"
             * errors when it sees instances of overloaded
             * methods.
             */

                vec.addElement(newMeth);
            }
        }
    }

    
/*
 *----------------------------------------------------------------------
 *
 * methodSigEqual --
 *
 *	Returns whether the two methods have the same signature.
 *
 * Results:
 *	True if the method names and arguments are the same. False
 *	otherwise
 *
 * Side effects:
 *	None.
 *
 *----------------------------------------------------------------------
 */

    static boolean
            methodSigEqual(
            Method method1,
            Method method2)
    {
        if (!method1.getName().equals(method2.getName()))
        {
            return false;
        }

        Class param1[] = method1.getParameterTypes();
        Class param2[] = method2.getParameterTypes();

        if (param1.length != param2.length)
        {
            return false;
        }

        for (int i = 0; i < param1.length; i++)
        {
            if (param1[i] != param2[i])
            {
                return false;
            }
        }

        return true;
    }

    
/*
 *----------------------------------------------------------------------
 *
 * getMethodRank --
 *
 *	Returns the rank of "public-ness" of the method. See comments
 *	above the "func" member variable for more details on public-ness
 *	ranking.
 *
 * Results:
 *	The rank of "public-ness" of the method.
 *
 * Side effects:
 *	None.
 *
 *----------------------------------------------------------------------
 */

    static int
            getMethodRank(
            Class declaringCls,        // The class that declares the method.
            Method method)        // Return the rank of this method.
    {
        int methMod = method.getModifiers();

        if (Modifier.isPrivate(methMod))
        {
            return 0;
        }

        int clsMod = declaringCls.getModifiers();

        if (Modifier.isPublic(methMod) && Modifier.isPublic(clsMod))
        {
            return 2;
        }

        return 0;
    }

} // end FuncSig.


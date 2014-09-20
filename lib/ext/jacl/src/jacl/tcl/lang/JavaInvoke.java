/*
 * JavaInvoke.java --
 *
 *	This class implements the common routines used by the java::*
 *	commands to access the Java Reflection API.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: JavaInvoke.java,v 1.1.1.1 1998/10/14 21:09:14 cvsadmin Exp $
 *
 */

package jacl.tcl.lang;


import jacl.tcl.lang.reflect.PkgInvoker;

import java.lang.reflect.*;

/*
 * This class implements the common routines used by the java::*
 * commands to create Java objects, call Java methods and access fields
 * and properties. It also has auxiliary routines for converting between
 * TclObject's and Java Object's.
 */

class JavaInvoke
{

/*
 * We need to use empty array Object[0] a lot. We keep a static copy
 * and re-use it to avoid garbage collection.
 */

    static private Object EMPTY_ARGS[] = new Object[0];

    
/*
 *-----------------------------------------------------------------------------
 *
 * newInstance --
 *
 *	Call the specified constructor.
 *
 * Results:
 *	When successful, the object created by the constructor.
 *
 * Side effects:
 *	The constructor can cause arbitrary side effects.
 *
 *-----------------------------------------------------------------------------
 */

    static TclObject
            newInstance(
            Interp interp,              // Current interpreter.
            TclObject signature,    // Constructor signature.
            TclObject argv[],        // Arguments.
            int startIdx,        // Index of the first argument in argv[] to
            // pass to the constructor.
            int count)            // Number of arguments to pass to the
        // constructor.
            throws
            TclException        // Standard Tcl exception.
    {
        FuncSig sig = FuncSig.get(interp, null, signature, argv, startIdx, count);

        Object javaObj = call(interp, sig.pkgInvoker, signature, sig.func,
                null, argv, startIdx, count);

        return ReflectObject.newInstance(interp, sig.targetCls, javaObj);
    }

    
/*
 *-----------------------------------------------------------------------------
 *
 * callMethod --
 *
 *	Call the specified instance or static method of the given object.
 *
 * Results:
 *      When successful, this method returns the Java object that the
 *      Java method would have returned. If the Java method has a void
 *      return type the an empty Tcl string object is returned.
 *
 * Side effects:
 *	The method can cause arbitrary side effects.
 *
 *-----------------------------------------------------------------------------
 */

    static TclObject
            callMethod(
            Interp interp,              // Current interpreter.
            TclObject reflectObj,    // The object whose method to invoke.
            TclObject signature,    // Method signature.
            TclObject argv[],        // Arguments.
            int startIdx,        // Index of the first argument in argv[] to
            // pass to the method.
            int count,            // Number of arguments to pass to the
            // method.
            boolean convert)        // Whether the value should be converted
        // into Tcl objects of the closest types.
            throws
            TclException
    {
        Object javaObj = ReflectObject.get(interp, reflectObj);
        Class javaCl = ReflectObject.getClass(interp, reflectObj);
        FuncSig sig = FuncSig.get(interp, javaCl, signature, argv,
                startIdx, count);
        Method method = (Method) sig.func;

        Object result = call(interp, sig.pkgInvoker, signature, method, javaObj,
                argv, startIdx, count);

        if (method.getReturnType() == Void.TYPE)
        {
            return TclString.newInstance("");
        }
        else
        {
            return wrap(interp, method.getReturnType(), result, convert);
        }
    }

    
/*
 *-----------------------------------------------------------------------------
 *
 * callStaticMethod --
 *
 *	Call the specified static method of the given object.
 *
 * Results:
 *	When successful, returns an array: Object result[2]. result[0]
 *	is the object returned by the method; result[1] is the return
 *	type of the method.
 *
 * Side effects:
 *	The method can cause arbitrary side effects.
 *
 *-----------------------------------------------------------------------------
 */

    static TclObject
            callStaticMethod(
            Interp interp,        // Current interpreter.
            TclObject classObj,        // Class whose static method to invoke.
            TclObject signature,    // Method signature.
            TclObject argv[],        // Arguments.
            int startIdx,        // Index of the first argument in argv[] to
            // pass to the method.
            int count,            // Number of arguments to pass to the
            // method.
            boolean convert)        // Whether the value should be converted
        // into Tcl objects of the closest types.
            throws
            TclException
    {
        Class cls = ClassRep.get(interp, classObj);
        FuncSig sig = FuncSig.get(interp, cls, signature, argv,
                startIdx, count);

        Method method = (Method) sig.func;
        if (!Modifier.isStatic(method.getModifiers()))
        {
            throw new TclException(interp, "\"" + signature +
                    "\" is not a static method of class \"" + classObj + "\"");
        }

        Object result = call(interp, sig.pkgInvoker, signature, method, null,
                argv, startIdx, count);

        if (method.getReturnType() == Void.TYPE)
        {
            return TclString.newInstance("");
        }
        else
        {
            return wrap(interp, method.getReturnType(), result, convert);
        }
    }

    
/*
 *-----------------------------------------------------------------------------
 *
 * call --
 *
 *	Call the constructor, instance method, or static method with
 *	the given parameters. Check the parameter types and perform
 *	TclObject to JavaObject conversion.
 *
 * Results:
 *	The object created by the constructor, or the return value
 *	of the method call.
 *
 * Side effects:
 *	The constructor/method call may have arbitrary side effects.
 *
 *-----------------------------------------------------------------------------
 */

    static Object
            call(
            Interp interp,
            PkgInvoker invoker,        // The PkgInvoked used to invoke the
            // method or constructor.
            TclObject signature,    // For formatting error message.
            Object func,        // The Constructor or Method to call.
            Object obj,            // The object associated with an instace
            // method call. Should be null for
            // constructor calls and static method
            // calls.
            TclObject argv[],        // Argument list.
            int startIdx,        // Index of the first argument in argv[] to
            // pass to the method or constructor.
            int count)            // Number of arguments to pass to the
        // method or constructor.
            throws
            TclException        // Standard Tcl exception.
    {
        Class paramTypes[];
        Constructor cons = null;
        Method method = null;
        int i;
        boolean isConstructor = (func instanceof Constructor);

        if (isConstructor)
        {
            cons = (Constructor) func;
            paramTypes = cons.getParameterTypes();
        }
        else
        {
            method = (Method) func;
            paramTypes = method.getParameterTypes();
        }

        if (count != paramTypes.length)
        {
            throw new TclException(interp, "wrong # args for calling " +
                    (isConstructor ? "constructor" : "method") +
                    " \"" + signature + "\"");
        }

        Object args[];

        if (count == 0)
        {
            args = EMPTY_ARGS;
        }
        else
        {
            args = new Object[count];
            for (i = 0; i < count; i++)
            {
                args[i] = convertTclObject(interp, paramTypes[i],
                        argv[i + startIdx]);
            }
        }

        try
        {
            if (isConstructor)
            {
                return invoker.invokeConstructor(cons, args);
            }
            else
            {
                return invoker.invokeMethod(method, obj, args);
            }
        }
        catch (Exception e)
        {
            throw new ReflectException(interp, e);
        }
    }

    
/*
 *-----------------------------------------------------------------------------
 *
 * getField --
 *
 *	Returns the value of a field in the given object.
 *
 * Results:
 *	When successful, returns an array: Object result[2]. result[0]
 *	is the value of the field; result[1] is the type of the field.
 *
 * Side effects:
 *	None.
 *
 *-----------------------------------------------------------------------------
 */

    static  TclObject
            getField(
            Interp interp,        // Current interpreter.
            TclObject classOrObj,    // Class or object whose field to get.
            TclObject signature,    // Signature of the field.
            boolean convert)        // Whether the value should be converted
        // into Tcl objects of the closest types.
            throws
            TclException        // Standard Tcl exception.
    {
        return getsetField(interp, classOrObj, signature, null, convert, true);
    }

    
/*
 *-----------------------------------------------------------------------------
 *
 * setField --
 *
 *	Sets the value of a field in the given object.
 *
 * Results:
 *	None.
 *
 * Side effects:
 *	When successful, the field is set to the given value.
 *
 *-----------------------------------------------------------------------------
 */

    static void
            setField(
            Interp interp,        // Current interpreter.
            TclObject classOrObj,    // Class or object whose field to get.
            TclObject signature,    // Signature of the field.
            TclObject value)        // New value for the field.
            throws
            TclException        // Standard Tcl exception.
    {
        getsetField(interp, classOrObj, signature, value, false, false);
    }

    
/*
 *-----------------------------------------------------------------------------
 *
 * getsetField --
 *
 *	Gets or sets the field in the given object.
 *
 * Results:
 *	None.
 *
 * Side effects:
 *	When successful, the field is set to the given value if isget
 *	is false.
 *
 *-----------------------------------------------------------------------------
 */

    static TclObject
            getsetField(
            Interp interp,        // Current interpreter.
            TclObject classOrObj,    // Class or object whose field to get.
            TclObject signature,    // Signature of the field.
            TclObject value,        // New value for the field.
            boolean convert,        // Whether the value should be converted
            // into Tcl objects of the closest types.
            boolean isget)
            throws
            TclException        // Standard Tcl exception.
    {
        Class cls = null;
        Object obj = null;
        boolean isStatic = false;

        try
        {
            obj = ReflectObject.get(interp, classOrObj);
        }
        catch (TclException e)
        {
            try
            {
                cls = ClassRep.get(interp, classOrObj);
            }
            catch (TclException e1)
            {
                throw new TclException(interp, "unknown class or object \"" +
                        classOrObj + "\"");
            }
            isStatic = true;
        }

        if (!isStatic)
        {
            if (obj == null)
            {
                throw new TclException(interp,
                        "can't access fields in a null object reference");
            }
            cls = ReflectObject.getClass(interp, classOrObj);
        }

        FieldSig sig = FieldSig.get(interp, signature, cls);
        Field field = sig.field;
        if (isStatic && (!(Modifier.isStatic(field.getModifiers()))))
        {
            throw new TclException(interp,
                    "can't access an instance field without an object");
        }

        try
        {
            if (isget)
            {
                return wrap(interp, field.getType(),
                        sig.pkgInvoker.getField(field, obj), convert);
            }
            else
            {
                Object javaValue = convertTclObject(interp, field.getType(),
                        value);
                sig.pkgInvoker.setField(field, obj, javaValue);
                return null;
            }
        }
        catch (TclException e1)
        {
            throw e1;
        }
        catch (IllegalAccessException e2)
        {
            throw new TclException(interp, "can't access field \"" +
                    signature + "\": " + e2);
        }
        catch (Exception e)
        {
            throw new ReflectException(interp, e);
        }
    }

    
/*
 *-----------------------------------------------------------------------------
 *
 * getProperty --
 *
 *	Returns the value of a property in the given object.
 *
 * Results:
 *	When successful, returns a the value of the property inside
 *	a TclObject
 *
 * Side effects:
 *	None.
 *
 *-----------------------------------------------------------------------------
 */

    static TclObject
            getProperty(
            Interp interp,        // Current interpreter.
            TclObject reflectObj,    // The object whose property to query.
            TclObject propName,        // The name of the property to query.
            boolean convert)        // Whether the value should be converted
        // into Tcl objects of the closest types.
            throws
            TclException        // A standard Tcl exception.
    {
        Object javaObj = ReflectObject.get(interp, reflectObj);
        if (javaObj == null)
        {
            throw new TclException(interp, "can't get property from null object");
        }

        Class javaClass = ReflectObject.getClass(interp, reflectObj);
        PropertySig sig = PropertySig.get(interp, javaClass, propName);

        Method readMethod = sig.desc.getReadMethod();

        if (readMethod == null)
        {
            throw new TclException(interp, "can't get write-only property \"" +
                    propName + "\"");
        }

        try
        {
            return wrap(interp, readMethod.getReturnType(),
                    sig.pkgInvoker.invokeMethod(readMethod, javaObj,
                            EMPTY_ARGS), convert);
        }
        catch (Exception e)
        {
            throw new ReflectException(interp, e);
        }
    }

    
/*
 *-----------------------------------------------------------------------------
 *
 * setProperty --
 *
 *	Returns the value of a property in the given object.
 *
 * Results:
 *	None.
 *
 * Side effects:
 *	When successful, the property will have the new value.
 *
 *-----------------------------------------------------------------------------
 */

    static void
            setProperty(
            Interp interp,        // Current interpreter.
            TclObject reflectObj,    // The object whose property to query.
            TclObject propName,        // The name of the property to query.
            TclObject value)        // Whether the value should be converted
        // into Tcl objects of the closest types.
            throws
            TclException        // A standard Tcl exception.
    {
        Object javaObj = ReflectObject.get(interp, reflectObj);
        if (javaObj == null)
        {
            throw new TclException(interp, "can't set property in null object");
        }

        Class javaClass = ReflectObject.getClass(interp, reflectObj);
        PropertySig sig = PropertySig.get(interp, javaClass, propName);


        Method writeMethod = sig.desc.getWriteMethod();
        Class type = sig.desc.getPropertyType();

        if (writeMethod == null)
        {
            throw new TclException(interp, "can't set read-only property \"" +
                    propName + "\"");
        }

        Object args[] = new Object[1];
        args[0] = convertTclObject(interp, type, value);

        try
        {
            sig.pkgInvoker.invokeMethod(writeMethod, javaObj, args);
        }
        catch (Exception e)
        {
            throw new ReflectException(interp, e);
        }
    }

    
/*
 *-----------------------------------------------------------------------------
 *
 * getClassByName --
 *
 *	Returns Class object identified by the string name. We allow
 *	abbreviation of the java.lang.* class if there is no ambiguity:
 *	e.g., if there is no class whose fully qualified name is "String",
 *	then "String" means java.lang.String.
 *
 * Results:
 *	If successful, The Class object identified by the string name.
 *
 * Side effects:
 *	None.
 *
 *-----------------------------------------------------------------------------
 */

    static Class
            getClassByName(
            Interp interp,                      // Interp used by TclClassLoader
            String clsName)            // String name of the class.
            throws
            TclException            // If the class cannot be found.
    {
        Class result = null;
        int dimension;

        /*
        * If the string is of the form className[][]..., strip out the trailing
        * []s and record the dimension of the array.
        */

        String prefix = "";
        String suffix = "";
        for (dimension = 0; clsName.endsWith("[]"); dimension++)
        {
            int newLen = clsName.length() - 2;
            clsName = clsName.substring(0, newLen);
            prefix = prefix + "[";
        }

        try
        {
            if (clsName.indexOf('.') == -1)
            {
                if (dimension > 0)
                {
                    if (clsName.equals("int"))
                    {
                        return Class.forName(prefix + "I");
                    }
                    else if (clsName.equals("boolean"))
                    {
                        return Class.forName(prefix + "Z");
                    }
                    else if (clsName.equals("long"))
                    {
                        return Class.forName(prefix + "J");
                    }
                    else if (clsName.equals("float"))
                    {
                        return Class.forName(prefix + "F");
                    }
                    else if (clsName.equals("double"))
                    {
                        return Class.forName(prefix + "D");
                    }
                    else if (clsName.equals("byte"))
                    {
                        return Class.forName(prefix + "B");
                    }
                    else if (clsName.equals("short"))
                    {
                        return Class.forName(prefix + "S");
                    }
                    else if (clsName.equals("char"))
                    {
                        return Class.forName(prefix + "C");
                    }
                    else
                    {
                        prefix = prefix + "L";
                        suffix = ";";
                    }
                }
                else
                {
                    if (clsName.equals("int"))
                    {
                        return Integer.TYPE;
                    }
                    else if (clsName.equals("boolean"))
                    {
                        return Boolean.TYPE;
                    }
                    else if (clsName.equals("long"))
                    {
                        return Long.TYPE;
                    }
                    else if (clsName.equals("float"))
                    {
                        return Float.TYPE;
                    }
                    else if (clsName.equals("double"))
                    {
                        return Double.TYPE;
                    }
                    else if (clsName.equals("byte"))
                    {
                        return Byte.TYPE;
                    }
                    else if (clsName.equals("short"))
                    {
                        return Short.TYPE;
                    }
                    else if (clsName.equals("char"))
                    {
                        return Character.TYPE;
                    }
                }

                TclClassLoader tclClassLoader = new TclClassLoader(interp, null);

                try
                {
                    result = tclClassLoader.loadClass(prefix + clsName + suffix);
                }
                catch (ClassNotFoundException e)
                {
                    /*
              * The code below should really be in a java::import command.
              * Since we dont have one yet, do a simple check for the class
              * in java.lang.  If the system class loader cannot resolve the
              * class, than a SecurityException is thrown, catch this and
              * throw the standard error.
              */

                    try
                    {
                        result = tclClassLoader.loadClass(prefix + "java.lang." +
                                clsName + suffix);
                    }
                    catch (SecurityException e2)
                    {
                        result = null;
                    }
                }
            }
            else
            {
                TclClassLoader tclClassLoader = new TclClassLoader(interp, null);

                if (dimension > 0)
                {
                    clsName = prefix + "L" + clsName + ";";
                }
                result = tclClassLoader.loadClass(clsName);
            }
        }
        catch (ClassNotFoundException e)
        {
            result = null;
        }
        catch (SecurityException e)
        {
            throw new TclException(interp,
                    "cannot load new class into java or tcl package");
        }

        if (result == null)
        {
            throw new TclException(interp, "unknown class \"" + clsName + "\"");
        }

        return result;
    }

    
/*
 *----------------------------------------------------------------------
 *
 *  convertJavaObject --
 *
 *	Converts the java.lang.Object into a Tcl object and set the
 *	interpreter's result with this Tcl object. Primitive data
 *	types are converted into primitive Tcl data types. Otherwise,
 *	a ReflectObject wrapper is created for the object so that it
 *	can be later accessed with the Reflection API.
 *
 * Results:
 *	The TclObject representation of the Java object.
 *
 * Side effects:
 *	None.
 *
 *----------------------------------------------------------------------
 */

    static TclObject
            convertJavaObject(
            Interp interp,    // Current interpreter.
            Class cls,        // The class of the Java Object (we can't use
            // javaObj.getClass because javaObj may be null.)
            Object javaObj)    // The java.lang.Object to convert to a TclObject.
            throws TclException
    {
        if (javaObj == null)
        {
            if (cls == String.class)
            {
                return TclString.newInstance("");
            }
            else
            {
                return JavaNullCmd.getNullObj();
            }
        }
        else if (javaObj instanceof Integer)
        {
            return TclInteger.newInstance(((Integer) javaObj).intValue());

        }
        else if (javaObj instanceof Long)
        {
            return TclInteger.newInstance(((Long) javaObj).intValue());

        }
        else if (javaObj instanceof Short)
        {
            return TclInteger.newInstance(((Short) javaObj).intValue());

        }
        else if (javaObj instanceof Byte)
        {
            return TclInteger.newInstance(((Byte) javaObj).intValue());

        }
        else if (javaObj instanceof Double)
        {
            return TclDouble.newInstance(((Double) javaObj).doubleValue());

        }
        else if (javaObj instanceof Float)
        {
            return TclDouble.newInstance(((Float) javaObj).doubleValue());

        }
        else if (javaObj instanceof Boolean)
        {
            return TclBoolean.newInstance(((Boolean) javaObj).booleanValue());

        }
        else if (javaObj instanceof Character)
        {
            return TclString.newInstance(((Character) javaObj).toString());

        }
        else if (javaObj instanceof String)
        {
            return TclString.newInstance((String) javaObj);

        }
        else
        {
            return ReflectObject.newInstance(interp, cls, javaObj);
        }
    }

    
/*
 *-----------------------------------------------------------------------------
 *
 * convertTclObject --
 *
 *	Converts a Tcl object to a Java Object of the required type.
 *
 * Results:
 *	An Object of the required type.
 *
 * Side effects:
 *	None.
 *
 *-----------------------------------------------------------------------------
 */

    static final Object
            convertTclObject(
            Interp interp,        // Current interpreter.
            Class type,            // Convert to this type.
            TclObject tclObj)        // From this Tcl object.
            throws
            TclException        // If conversion fails.
    {
        Object javaObj = null;
        boolean isReflectObj = false;

        try
        {
            javaObj = ReflectObject.get(interp, tclObj);
            isReflectObj = true;
        }
        catch (TclException e)
        {
            interp.resetResult();
        }


        if (!isReflectObj)
        {
            /*
        * tclObj a Tcl "primitive" value. We try convert it to the
        * corresponding primitive value in Java.
        *
        * To optimize performance, the following "if" statements are
        * arranged according to (my guesstimation of) the frequency
        * that a certain type is used.
        */

            if (type == String.class)
            {
                return tclObj.toString();

            }
            else if (type == Object.class)
            {
                return tclObj.toString();

            }
            else if ((type == Integer.TYPE) || (type == Integer.class))
            {
                return new Integer(TclInteger.get(interp, tclObj));

            }
            else if ((type == Boolean.TYPE) || (type == Boolean.class))
            {
                return new Boolean(TclBoolean.get(interp, tclObj));

            }
            else if ((type == Long.TYPE) || (type == Long.class))
            {
                return new Long((long) TclInteger.get(interp, tclObj));

            }
            else if ((type == Float.TYPE) || (type == Float.class))
            {
                return new Float((float) TclDouble.get(interp, tclObj));

            }
            else if ((type == Double.TYPE) || (type == Double.class))
            {
                return new Double(TclDouble.get(interp, tclObj));

            }
            else if ((type == Byte.TYPE) || (type == Byte.class))
            {
                int i = TclInteger.get(interp, tclObj);
                if ((i < -128) || (i > 127))
                {
                    throw new TclException(interp,
                            "integer value too large to represent in a byte");
                }
                return new Byte((byte) i);

            }
            else if ((type == Short.TYPE) || (type == Short.class))
            {
                int i = TclInteger.get(interp, tclObj);
                if ((i < -32768) || (i > 32767))
                {
                    throw new TclException(interp,
                            "integer value too large to represent in a short");
                }
                return new Short((short) i);

            }
            else if ((type == Character.TYPE) || (type == Character.class))
            {
                String str = tclObj.toString();

                if (str.length() == 1)
                {
                    return new Character(str.charAt(0));
                }
                else
                {
                    throw new TclException(interp, "expected character but got \""
                            + tclObj + "\"");
                }
            }
            else
            {
                throw new TclException(interp, "\"" + tclObj +
                        "\" is not an object handle of class \"" + type.getName() +
                        "\"");
            }
        }
        else
        {
            /*
        * The TclObject is a ReflectObject that contains javaObj. We
        * check to see if javaObj can be converted to the required
        * type.  If javaObj is an numberical type, we attempt to
        * widen it to the required type.
        */

            if ((javaObj == null) || type.isInstance(javaObj))
            {
                return javaObj;
            }

            throw new TclException(interp, "expected object of type " +
                    type.getName() + " but got \"" + tclObj + "\" (" +
                    javaObj.getClass().getName() + ")");
        }
    }

    
/*
 *-----------------------------------------------------------------------------
 *
 * wrap --
 *
 *	Wraps a Java Object into a TclObject according to whether the
 *	convert flag is set.
 *
 * Results:
 *	The TclObject that wraps the Java Object.
 *
 * Side effects:
 *	None.
 *
 *-----------------------------------------------------------------------------
 */

    private static final TclObject
            wrap(
            Interp interp,    // Current interpreter.
            Class cls,        // The class of the Java Object (we can't use
            // javaObj.getClass because javaObj may be null.)
            Object javaObj,    // The Java Object to wrap.
            boolean convert)    // Whether the value should be converted
        // into Tcl objects of the closest types.
            throws TclException
    {
        if (convert)
        {
            return convertJavaObject(interp, cls, javaObj);
        }
        else
        {
            return ReflectObject.newInstance(interp, cls, javaObj);
        }
    }

} // end JavaInvoke


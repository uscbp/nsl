package jacl.tcl.lang;

/**
 * Created by IntelliJ IDEA.
 * User: jbonaiuto
 * Date: Mar 27, 2007
 * Time: 10:35:30 AM
 * To change this template use File | Settings | File Templates.
 */
class ArraySig extends InternalRep {

/*
 * The base component type of the array. For example, the component
 * type for int[][][] is int.
 */

    Class componentType;

/*
 * The number of dimensions specified by the signature. For example,
 * int[][][] has a dimension of 3.
 */

    int dimensions;

    
/*
 *----------------------------------------------------------------------
 *
 * ArraySig --
 *
 *	Creates a new ArraySig instance.
 *
 * Side effects:
 *	Member fields are initialized.
 *
 *----------------------------------------------------------------------
 */

    ArraySig(
            Class cType,        // Initial value for componentType.
            int n)            // Initial value for dimensions.
    {
        componentType = cType;
        dimensions = n;
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
        return new ArraySig(componentType, dimensions);
    }

    
/*
 *----------------------------------------------------------------------
 *
 * looksLikeArraySig --
 *
 *	This method quickly determines whether a TclObject can be
 *	interpreted as an array signature or a constructor signature.
 *
 * Results:
 *	True if the object looks like an array signature, false
 *	otherwise.
 *
 * Side effects:
 *	None.
 *
 *----------------------------------------------------------------------
 */

    static boolean
            looksLikeArraySig(
            Interp interp,        // Current interpreter.
            TclObject signature)    // TclObject to check.
            throws
            TclException {
        InternalRep rep = signature.getInternalRep();

        if (rep instanceof FuncSig)
        {
            /*
        * The string rep of FuncSig can never represent an ArraySig,
        * so we know for sure that signature doesn't look like an
        * ArraySig.
        */

            return false;
        }
        if (rep instanceof ArraySig)
        {
            return true;
        }

        if (TclList.getLength(interp, signature) < 1)
        {
            return false;
        }

        String clsName = TclList.index(interp, signature, 0).toString();
        if (clsName.endsWith("[]") || clsName.startsWith("["))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    
/*
 *----------------------------------------------------------------------
 *
 * get --
 *
 *	Returns the ArraySig internal representation of the constructor
 *	or method that matches with the signature and the parameters.
 *
 * Results:
 *	The ArraySig given by the signature.
 *
 * Side effects:
 *	When successful, the internalRep of the signature object is
 *	converted to ArraySig.
 *
 *----------------------------------------------------------------------
 */

    static ArraySig
    get(
            Interp interp,        // Current interpreter. Stores error message
            // if signature doesn't contain an array sig.
            TclObject signature)    // The TclObject to convert.
            throws
            TclException        // Standard Tcl exception.
    {
        InternalRep rep = signature.getInternalRep();
        if ((rep instanceof ArraySig))
        {
            /*
        * The cached internal rep is a valid array signature, return it.
        */

            return (ArraySig) rep;
        }

        trying:
        {
            if (TclList.getLength(interp, signature) != 1)
            {
                break trying;
            }

            String clsName = TclList.index(interp, signature, 0).toString();
            if (!(clsName.endsWith("[]")) && !(clsName.startsWith("[")))
            {
                break trying;
            }
            Class componentType = JavaInvoke.getClassByName(interp, clsName);
            int dimensions = 0;

            if (clsName.charAt(0) == '[')
            {
                /*
             * If the string begins with '[', count the leading '['s.
             */

                String tmp = clsName;
                while (tmp.charAt(++dimensions) == '[')
                {
                }

            }
            else
            {
                /*
             * If the string is of the form className[][]..., count
             * the trailing "[]"s.
             */

                String tmp = clsName;
                for (; tmp.endsWith("[]"); dimensions++)
                {
                    tmp = tmp.substring(0, tmp.length() - 2);
                }
            }

            ArraySig sigRep = new ArraySig(componentType, dimensions);

            signature.setInternalRep(sigRep);
            return sigRep;
        }

        throw new TclException(interp, "bad array signature \"" + signature
                + "\"");
    }

} // end ArraySig

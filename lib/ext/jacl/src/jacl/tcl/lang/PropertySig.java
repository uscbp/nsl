/*
 * PropertySig.java --
 *
 *	This class implements the internal representation of a Java
 *	property signature.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: PropertySig.java,v 1.1.1.1 1998/10/14 21:09:14 cvsadmin Exp $
 *
 */

package jacl.tcl.lang;

import java.beans.*;

import jacl.tcl.lang.reflect.PkgInvoker;

/*
 * This class implements the internal representation of a Java Property
 * signature.
 */

class PropertySig extends InternalRep
{

/*
 * The class that the property signature is used against. It is the
 * class of the java object specified in the java::prop command.
 * targetCls is used to test the validity of a cached PropertySig
 * internal rep.
 */

    Class targetCls;

/*
 * Property descriptor of the property.
 */

    PropertyDescriptor desc;

/*
 * The PkgInvoker used to access the property. 
 */

    PkgInvoker pkgInvoker;

    
/*
 *----------------------------------------------------------------------
 *
 * PropertySig --
 *
 *	Creates a new PropertySig instance.
 *
 * Side effects:
 *	Member fields are initialized.
 *
 *----------------------------------------------------------------------
 */

    PropertySig(
            Class c,            // Initial value for targetCls.
            PkgInvoker i,        // Initial value for pkgInvoker.
            PropertyDescriptor d)    // Initial value for desc.
    {
        targetCls = c;
        pkgInvoker = i;
        desc = d;
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
        return new PropertySig(targetCls, pkgInvoker, desc);
    }

    
/*
 *----------------------------------------------------------------------
 *
 * get --
 *
 *	Returns the FieldSig internal rep given by the field signature.
 *
 * Results:
 *	The FieldSig given by the signature.
 *
 * Side effects:
 *	When successful, the internalRep of the signature object is
 *	converted to FieldSig.
 *
 *----------------------------------------------------------------------
 */

    static PropertySig
            get(
            Interp interp,        // Current interpreter
            Class targetCls,        // The target class of the property signature.
            TclObject signature)    // The TclObject that holds a prop. signature.
            throws
            TclException        // Standard Tcl exception.
    {
        InternalRep rep = signature.getInternalRep();

        if ((rep instanceof PropertySig) &&
                (targetCls == ((PropertySig) rep).targetCls))
        {
            /*
        * The cached internal rep is a valid property signature for
        * the given targetCls. Return it.
        */

            return (PropertySig) rep;
        }

        String propName = signature.toString();
        PropertyDescriptor desc = null;

        search_prop :
        {
            BeanInfo beanInfo;

            try
            {
                beanInfo = Introspector.getBeanInfo(targetCls);
            }
            catch (IntrospectionException e)
            {
                break search_prop;
            }

            PropertyDescriptor descriptors[] = beanInfo.getPropertyDescriptors();

            if (descriptors == null)
            {
                break search_prop;
            }

            /*
        * Search for a property that has the same name as propName.
        */

            for (int i = 0; i < descriptors.length; i++)
            {
                if (descriptors[i].getName().equals(propName))
                {
                    if (descriptors[i] instanceof IndexedPropertyDescriptor)
                    {
                        throw new TclException(interp,
                                "can't access indexed property \"" +
                                        propName + "\": not implemented");
                    }
                    desc = descriptors[i];
                    break;
                }
            }
        }

        if (desc == null)
        {
            throw new TclException(interp, "unknown property \"" + propName +
                    "\"");
        }

        PropertySig sig = new PropertySig(targetCls, PkgInvoker.getPkgInvoker(
                targetCls), desc);
        signature.setInternalRep(sig);

        return sig;
    }

} // end PropertySig


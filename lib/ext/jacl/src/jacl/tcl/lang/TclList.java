/*
 * TclList.java
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: TclList.java,v 1.1.1.1 1998/10/14 21:09:20 cvsadmin Exp $
 *
 */

package jacl.tcl.lang;

import java.util.*;

/**
 * This class implements the list object type in Tcl.
 */
public class TclList extends InternalRep
{

    /**
     * Internal representation of a list value.
     */
    private Vector vector;

    /**
     * Create a new empty Tcl List.
     */
    private TclList()
    {
        vector = new Vector();
    }

    /**
     * Create a new empty Tcl List, with the vector pre-allocated to
     * the given size.
     *
     * @param size the number of slots pre-allocated in the vector.
     */
    private TclList(int size)
    {
        vector = new Vector(size);
    }

    /**
     * Called to free any storage for the type's internal rep.
     *
     * @param obj the TclObject that contains this internalRep.
     */
    protected void dispose()
    {
        int size = vector.size();
        for (int i = 0; i < size; i++)
        {
            ((TclObject) vector.elementAt(i)).release();
        }
    }

    /**
     * Returns a dupilcate of the current object.
     *
     * @param obj the TclObject that contains this internalRep.
     */
    protected InternalRep duplicate()
    {
        int size = vector.size();
        TclList newList = new TclList(size);

        for (int i = 0; i < size; i++)
        {
            TclObject tobj = (TclObject) vector.elementAt(i);

            /*
            * takeExclusive() is necessary. See comments inside
            * append().
            */
            tobj.preserve();
            newList.vector.addElement(tobj.takeExclusive());
        }

        return newList;
    }

    /**
     * Called to query the string representation of the Tcl object. This
     * method is called only by TclObject.toString() when
     * TclObject.stringRep is null.
     *
     * @return the string representation of the Tcl object.
     */
    public String toString()
    {
        StringBuffer sbuf = new StringBuffer();
        int size = vector.size();

        try
        {
            for (int i = 0; i < size; i++)
            {
                Object elm = vector.elementAt(i);
                if (elm != null)
                {
                    Util.appendElement(null, sbuf, elm.toString());
                }
                else
                {
                    Util.appendElement(null, sbuf, "");
                }
            }
        }
        catch (TclException e)
        {
            throw new TclRuntimeError("unexpected TclException: " + e);
        }

        return sbuf.toString();
    }

    /**
     * Creates a new instance of a TclObject with a TclList internal
     * rep.
     *
     * @return the TclObject with the given list value.
     */

    public static TclObject newInstance()
    {
        return new TclObject(new TclList());
    }

    /**
     * Called to convert the other object's internal rep to list.
     *
     * @param interp current interpreter.
     * @param tobj   the TclObject to convert to use the List internal rep.
     * @throws TclException if the object doesn't contain a valid list.
     */
    static void setListFromAny(Interp interp, TclObject tobj)
            throws TclException
    {
        InternalRep rep = tobj.getInternalRep();

        if (!(rep instanceof TclList))
        {
            TclList tlist = new TclList();
            splitList(interp, tlist.vector, tobj.toString());
            tobj.setInternalRep(tlist);
        }
    }

    /**
     * Splits a list (in string rep) up into its constituent fields.
     *
     * @param interp current interpreter.
     * @param v      store the list elements in this vector.
     * @param s      the string to convert into a list.
     * @throws TclException if the object doesn't contain a valid list.
     */
    private static final void splitList(Interp interp, Vector v, String s)
            throws TclException
    {
        int len = s.length();
        int i = 0;

        while (i < len)
        {
            FindElemResult res = Util.findElement(interp, s, i, len);
            if (res == null)
            {
                break;
            }
            else
            {
                TclObject tobj = TclString.newInstance(res.elem);
                tobj.preserve();
                v.addElement(tobj);
            }
            i = res.elemEnd;
        }
    }


    /**
     * Appends a list element to a TclObject object. This method is
     * equivalent to Tcl_ListObjAppendElement in Tcl 8.0.
     *
     * @param interp  current interpreter.
     * @param tobj    the TclObject to append an element to.
     * @param elemObj the element to append to the object.
     * @throws TclException if tobj cannot be converted into a list.
     */
    public static final void append(Interp interp, TclObject tobj,
                                    TclObject elemObj)
            throws TclException
    {
        setListFromAny(interp, tobj);
        tobj.invalidateStringRep();

        TclList tlist = (TclList) tobj.getInternalRep();

        /*
       * Note: takeExclusive() is necessary to avoid circular references
       * like the following:
       *
       *	set x [list 1 2 3]
       *	lappend y $x
       *	lappend x $y
       */
        elemObj.preserve();
        tlist.vector.addElement(elemObj.takeExclusive());
    }

    /**
     * Queries the length of the list. If tobj is not a list object,
     * an attempt will be made to convert it to a list.
     *
     * @param interp current interpreter.
     * @param tobj   the TclObject to use as a list.
     * @return the length of the list.
     * @throws TclException if tobj is not a valid list.
     */
    public static final int getLength(Interp interp, TclObject tobj)
            throws TclException
    {
        setListFromAny(interp, tobj);

        TclList tlist = (TclList) tobj.getInternalRep();
        return tlist.vector.size();
    }

    /**
     * Returns a TclObject array of the elements in a list object.  If
     * tobj is not a list object, an attempt will be made to convert
     * it to a list. <p>
     * <p/>
     * The objects referenced by the returned array should be treated
     * as readonly and their ref counts are _not_ incremented; the
     * caller must do that if it holds on to a reference.
     *
     * @param interp the current interpreter.
     * @param tobj   the list to sort.
     * @return a TclObject array of the elements in a list object.
     * @throws TclException if tobj is not a valid list.
     */
    public static TclObject[] getElements(Interp interp, TclObject tobj)
            throws TclException
    {
        setListFromAny(interp, tobj);
        TclList tlist = (TclList) tobj.getInternalRep();

        int size = tlist.vector.size();
        TclObject objArray[] = new TclObject[size];
        for (int i = 0; i < size; i++)
        {
            objArray[i] = (TclObject) tlist.vector.elementAt(i);
        }
        return objArray;
    }

    /**
     * This procedure returns a pointer to the index'th object from
     * the list referenced by tobj. The first element has index
     * 0. If index is negative or greater than or equal to the number
     * of elements in the list, a null is returned. If tobj is not a
     * list object, an attempt will be made to convert it to a list.
     *
     * @param interp current interpreter.
     * @param tobj   the TclObject to use as a list.
     * @param index  the index of the requested element.
     * @return the the requested element.
     * @throws TclException if tobj is not a valid list.
     */
    public static final TclObject index(Interp interp, TclObject tobj,
                                        int index) throws TclException
    {
        setListFromAny(interp, tobj);

        TclList tlist = (TclList) tobj.getInternalRep();
        if (index < 0 || index >= tlist.vector.size())
        {
            return null;
        }
        else
        {
            return (TclObject) tlist.vector.elementAt(index);
        }
    }

    /**
     * This procedure inserts the elements in elements[] into the list at
     * the given index. If tobj is not a list object, an attempt will
     * be made to convert it to a list.
     *
     * @param interp   current interpreter.
     * @param tobj     the TclObject to use as a list.
     * @param index    the starting index of the insertion operation. <=0 means
     *                 the beginning of the list. >= TclList.getLength(tobj) means
     *                 the end of the list.
     * @param elements the element(s) to insert.
     * @param from     insert elements starting from elements[from] (inclusive)
     * @param to       insert elements up to elements[to] (inclusive)
     * @throws TclException if tobj is not a valid list.
     */
    static final void insert(Interp interp, TclObject tobj, int index,
                             TclObject elements[], int from, int to)
            throws TclException
    {
        replace(interp, tobj, index, 0, elements, from, to);
    }

    /**
     * This procedure replaces zero or more elements of the list
     * referenced by tobj with the objects from an TclObject array.
     * If tobj is not a list object, an attempt will be made to
     * convert it to a list.
     *
     * @param interp   current interpreter.
     * @param tobj     the TclObject to use as a list.
     * @param index    the starting index of the replace operation. <=0 means
     *                 the beginning of the list. >= TclList.getLength(tobj) means
     *                 the end of the list.
     * @param count    the number of elements to delete from the list. <=0 means
     *                 no elements should be deleted and the operation is equivalent to
     *                 an insertion operation.
     * @param elements the element(s) to insert.
     * @param from     insert elements starting from elements[from] (inclusive)
     * @param to       insert elements up to elements[to] (inclusive)
     * @throws TclException if tobj is not a valid list.
     */
    public static final void replace(Interp interp, TclObject tobj, int index,
                                     int count, TclObject elements[], int from, int to)
            throws TclException
    {
        setListFromAny(interp, tobj);
        tobj.invalidateStringRep();
        TclList tlist = (TclList) tobj.getInternalRep();

        int size = tlist.vector.size();
        int i;

        if (index >= size)
        {
            /*
            * Append to the end of the list. There is no need for deleting
            * elements.
            */
            index = size;
        }
        else
        {
            if (index < 0)
            {
                index = 0;
            }
            if (count > size - index)
            {
                count = size - index;
            }
            for (i = 0; i < count; i++)
            {
                TclObject obj = (TclObject) tlist.vector.elementAt(index);
                obj.release();
                tlist.vector.removeElementAt(index);
            }
        }
        for (i = from; i <= to; i++)
        {
            elements[i].preserve();
            tlist.vector.insertElementAt(elements[i].takeExclusive(), index++);
        }
    }

    /**
     * Sorts the list according to the sort mode and (optional) sort command.
     * If tobj is not a list object, an attempt will be made to
     * convert it to a list.
     *
     * @param interp         the current interpreter.
     * @param tobj           the list to sort.
     * @param sortMode       the sorting mode.
     * @param sortIncreasing true if to sort the elements in increasing order.
     * @param command        the command to compute the order of two elements.
     * @throws TclException if tobj is not a valid list.
     */

    static void sort(Interp interp, TclObject tobj, int sortMode,
                     int sortIndex, boolean sortIncreasing, String command)
            throws TclException
    {
        setListFromAny(interp, tobj);
        tobj.invalidateStringRep();
        TclList tlist = (TclList) tobj.getInternalRep();

        int size = tlist.vector.size();

        if (size <= 1)
        {
            return;
        }

        TclObject objArray[] = new TclObject[size];
        for (int i = 0; i < size; i++)
        {
            objArray[i] = (TclObject) tlist.vector.elementAt(i);
        }

        QSort s = new QSort();
        s.sort(interp, objArray, sortMode, sortIndex, sortIncreasing, command);

        for (int i = 0; i < size; i++)
        {
            tlist.vector.setElementAt(objArray[i], i);
            objArray[i] = null;
        }
    }
}

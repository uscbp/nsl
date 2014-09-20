/*  SCCS - @(#)NslDouble1.java	1.16 - 09/20/99 - 19:19:26 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.
////////////////////////////////////////////////////////////////////////////////
// NslDouble1.java

/**
 * NslDouble1 - double precision floating point one dimensional array
 */
package nslj.src.lang;

public class NslDouble1 extends NslNumeric1
{
    public double[] _data;
//  public String _name;
//  boolean visibility = true;
//  NslHierarchy module;

    ////////////////////////////////////////////////////////////////////
    // constructors


    /**
     * Constructor, copy the contain of array <tt>d</tt> to
     * the new number
     *
     * @param d - the value of new number
     */

    public NslDouble1(double[] d)
    {
        super();
        _data = new double[d.length];
        set(d);
        // _name = null;
    }

    /**
     * Constructor, initialize the value to the same as another <tt>NslNumeric1</tt>
     *
     * @param n - a 1-D array
     */

    public NslDouble1(NslNumeric1 n)
    {
        super();

        _data = new double[n.getSize()];
        set(n.getdouble1());
        //_name = null;
    }

    /**
     * Constructor, initialize the number to be size <tt>size</tt> 1-D array
     *
     * @param size - size of the new array
     */
    public NslDouble1(int size)
    {
        super();

        _data = new double[size];
        //_name = null;
    }

    /**
     * Constructor - initialize the reference of the number to be null
     *
     * @see NslPort, NslInport
     */
    public NslDouble1()
    {
        super();

        _data = null;
        //_name = null;
    }

    /**
     * This constructs a number with specified name
     *
     * @param name - name of the variable
     */
    public NslDouble1(String name)
    {
        super(name);

        _data = null;
        _name = name;
    }

    public NslDouble1(String name, NslHierarchy curParent)
    {
        super(name, curParent, curParent.nslGetAccess());

        _data = null;
        _name = name;
        //module = curParent;
        //visibility = module.getVisibility();
        //module.enableAccess(this);
    }

    /**
     * This constructs a number with specified name
     *
     * @param name - name of the variable
     * @param size - size of the array
     */
    public NslDouble1(String name, int size)
    {
        super(name);
        _data = new double[size];
        //_name = name;
    }

    public NslDouble1(String name, NslHierarchy curParent, int size)
    {
        super(name, curParent, curParent.nslGetAccess());
        _data = new double[size];
        //_name = name;
        //module = curParent;
        //visibility = module.getVisibility();
        //module.enableAccess(this);
    }

    /**
     * This constructs a number with specified name
     *
     * @param name - name of the variable
     * @param n    - initialized values
     */
    public NslDouble1(String name, NslHierarchy curParent, NslNumeric1 n)
    {
        super(name, curParent, curParent.nslGetAccess());
        _data = new double[n.getSize()];
        set(n.getdouble1());
        //_name = name;
        // module = curParent;
        //visibility = module.getVisibility();
        //module.enableAccess(this);

    }

    public NslDouble1(String name, NslNumeric1 n)
    {
        super(name);
        _data = new double[n.getSize()];
        set(n.getdouble1());
        //_name = name;

    }

    /**
     * This constructs a number with specified name
     *
     * @param name - name of the variable
     * @param d    - initialized values
     */
    // aa
    public NslDouble1(String name, double[] d)
    {
        super(name);
        _data = new double[d.length];
        set(d);
        //_name = name;
    }

    public void nslMemAlloc(int size)
    {
        _data = new double[size];
    }

//-----gets-----------------------------------

    /**
     * Get the contain of 1D array
     * in double precision floating point
     *
     * @return array - in double precision pointing point
     */
    public double[] get()
    {
        return _data;
    }

    public double get(int pos)
    {
        return _data[pos];
    }


    /**
     * Get the array
     * in double precision floating point
     *
     * @return array - in double precision pointing point
     */
    public double[] getdouble1()
    {
        return _data;
    }

    /**
     * Get the array
     * in single precision floating point
     *
     * @return array - in single precision pointing point
     */
    public float[] getfloat1()
    {
        float[] floatdata = new float[_data.length];
        int i;

        for (i = 0; i < _data.length; i++)
        {
            floatdata[i] = (float) _data[i];
        }

        return floatdata;
    }

    /**
     * Get the array
     * in integer
     *
     * @return array - in integer
     */
    public int[] getint1()
    {
        int[] intdata = new int[_data.length];
        int i;

        for (i = 0; i < _data.length; i++)
        {
            intdata[i] = (int) _data[i];
        }

        return intdata;
    }

    /**
     * Get the value of <tt>pos</tt>th element in this number
     * in double precision floating point
     *
     * @param pos - position of the array
     * @return value - in double precision pointing point
     */
    public double getdouble(int pos)
    {
        return _data[pos];
    }

    /**
     * Get the value of <tt>pos</tt>th element in this number
     * in single precision floating point
     *
     * @param pos - position of the array
     * @return value - in single precision pointing point
     */
    public float getfloat(int pos)
    {
        return (float) _data[pos];
    }


    /**
     * Get the value of <tt>pos</tt>th element in this number
     * in integer
     *
     * @param pos - position of the array
     * @return value - in integer
     */
    public int getint(int pos)
    {
        return (int) _data[pos];
    }

    /**
     * Get a <tt>NslNumeric1</tt> array with the same value as this array
     *
     * @return array - in NslNumeric1
     */
    public NslNumeric1 getNslNumeric1()
    {
        return this;
    }


    /**
     * Get a <tt>NslDouble1</tt> array with the same value as this array
     *
     * @return array - in NslDouble1
     */
    public NslDouble1 getNslDouble1()
    {
        return this;
    }

    /**
     * Create a <tt>NslFloat1</tt> array with the same value as this array
     *
     * @return array - in NslFloat1
     */
    public NslFloat1 getNslFloat1()
    {
        return (new NslFloat1(getfloat1()));
    }

    /**
     * Create a <tt>NslInt1</tt> array with the same value as this array
     *
     * @return array - in NslInt1
     */
    public NslInt1 getNslInt1()
    {
        return (new NslInt1(getint1()));
    }

    /**
     * Create an array that captures element <tt>start</tt> to <tt>end</tt>
     * if <tt>start</tt> is smaller than 0, <tt>start</tt> is default as 0;
     * if <tt>end</tt> is greater than the length of the array,
     * <tt>end</tt> is default as the length of the array
     *
     * @param start - the element number start the capture
     * @param end   - the element number ends the capture
     * @return a section of the original array
     */
    public double[] getSector(int start, int end)
    {
        int i;
        int j;
        int length;
        double[] doubledata;
        if (start < 0)
        {
            start = 0;
        }
        if (end > _data.length)
        {
            end = _data.length;
        }
        length = end - start + 1;
        doubledata = new double[length];
        i = start;
        for (j = 0; j < length; j++, i++)
        {
            doubledata[j] = _data[i];
        }
        return doubledata;
    }
//-----sets----------------------------------


    /**
     * set the value of this object to one-dimensional array <tt>value</tt>
     *
     * @param value - value to be defined.
     */
    public void _set(double[] value)
    {
        set(value);
    }

    /**
     * set the value of this object to one-dimensional array <tt>value</tt>
     *
     * @param value - value to be defined.
     */
    public void _set(float[]  value)
    {
        set(value);
    }

    /**
     * set the value of this object to one-dimensional array <tt>value</tt>
     *
     * @param value - value to be defined.
     */
    public void _set(int[] value)
    {
        set(value);
    }

    /**
     * set the value of <tt>pos</tt>th element of this object to <tt>value</tt>
     *
     * @param pos   - position of the element to be defined
     * @param value - value to be defined.
     */
    public void _set(int pos, double value)
    {
        set(pos, value);
    }

    /**
     * set the value of <tt>pos</tt>th element of this object to <tt>value</tt>
     *
     * @param pos   - position of the element to be defined
     * @param value - value to be defined.
     */
    public void _set(int pos, float value)
    {
        set(pos, value);
    }

    /**
     * set the value of <tt>pos</tt>th element of this object to <tt>value</tt>
     *
     * @param pos   - position of the element to be defined
     * @param value - value to be defined.
     */
    public void _set(int pos, int value)
    {
        set(pos, value);
    }

    // changing all inside the array

    /**
     * set the value of all elements of this object to <tt>value</tt>
     *
     * @param value - value to be defined.
     */
    public void _set(double value)
    {
        set(value);
    }

    /**
     * set the value of all elements of this object to <tt>value</tt>
     *
     * @param value - value to be defined.
     */
    public void _set(float value)
    {
        set(value);
    }

    /**
     * set the value of all elements of this object to <tt>value</tt>
     *
     * @param value - value to be defined.
     */
    public void _set(int value)
    {
        set(value);
    }

    /**
     * Set the value of this object to be <tt>value</tt>
     *
     * @param value - in any of <tt>NslNumeric1</tt> type.
     */
    public void _set(NslNumeric1 value)
    {
        set(value);
    }

    // changing all inside the array

    /**
     * Set the value of all elements of this object to be <tt>value</tt>
     */
    public void _set(NslNumeric0 value)
    {
        set(value);
    }

    /**
     * set the value of <tt>pos</tt>th element of this object to <tt>value</tt>
     *
     * @param pos   - position of the element to be defined
     * @param value - value to be defined.
     */
    public void _set(int pos, NslNumeric0 value)
    {
        set(pos, value);
    }

    /**
     * Set the value of this array to <tt>value</tt>
     *
     * @param value
     */

    public double[] set(double[] value)
    {
        int i;

        if (_data.length != value.length)
        {
            System.out.println("NslDouble1: array length not match");
            return _data;
        }

        for (i = 0; i < _data.length; i++)
        {
            _data[i] = value[i];
        }
        return _data;
    }


    /**
     * Set the value of this array to <tt>value</tt>
     *
     * @param value
     */
    public double[] set(float[] value)
    {
        int i;

        if (_data.length != value.length)
        {
            System.out.println("NslDouble1: array length not match");
            return _data;
        }

        for (i = 0; i < _data.length; i++)
        {
            _data[i] = (double) value[i];
        }
        return _data;
    }


    /**
     * Set the value of this array to <tt>value</tt>
     *
     * @param value
     */
    public double[] set(int[] value)
    {
        int i;

        if (_data.length != value.length)
        {
            System.out.println("NslDouble1: array length not match");
            return _data;
        }

        for (i = 0; i < _data.length; i++)
        {
            _data[i] = (double) value[i];
        }
        return _data;
    }

    /**
     * Set the <tt>pos</tt>th element of this array to <tt>value</tt>
     *
     * @param pos
     * @param value
     */
    public double set(int pos, double value)
    {
        _data[pos] = value;
        return _data[pos];
    }

    /**
     * Set the <tt>pos</tt>th element of this array to <tt>value</tt>
     *
     * @param pos
     * @param value
     */
    public double set(int pos, float value)
    {
        _data[pos] = (double) value;
        return _data[pos];
    }

    /**
     * Set the <tt>pos</tt>th element of this array to <tt>value</tt>
     *
     * @param pos
     * @param value
     */
    public double set(int pos, int value)
    {
        _data[pos] = (double) value;
        return _data[pos];
    }

    /**
     * Set all elements of this array to <tt>value</tt>
     *
     * @param value
     */
    public double[] set(double value)
    {
        int i;
        for (i = 0; i < _data.length; i++)
        {
            _data[i] = value;
        }
        return _data;
    }

    /**
     * Set all elements of this array to <tt>value</tt>
     *
     * @param value
     */
    public double[] set(float value)
    {
        int i;

        for (i = 0; i < _data.length; i++)
        {
            _data[i] = (double) value;
        }
        return _data;
    }

    /**
     * Set all elements of this array to <tt>value</tt>
     *
     * @param value
     */
    public double[] set(int value)
    {
        int i;

        for (i = 0; i < _data.length; i++)
        {
            _data[i] = (double) value;
        }
        return _data;
    }


    /**
     * Set the value of this array to <tt>value</tt>
     *
     * @param value
     */
    public double[] set(NslNumeric1 value)
    {
        if (_data == null)
        {
            System.out.println("NslDouble1: data is null");
            return _data;
        }
        else if (value == null)
        {
            System.out.println("NslDouble1: value is null");
            return _data;
        }
        else if (_data.length != value.getSize())
        {
            System.out.println("NslDouble1: array size not match");
            return _data;
        }
        // if error let system crash
        set(value.getdouble1());

        return _data;
    }

    /**
     * Set all elements of this array to <tt>value</tt>
     *
     * @param value
     */
    public double[] set(NslNumeric0 value)
    {
        int i;
        for (i = 0; i < _data.length; i++)
        {
            _data[i] = value.getdouble();
        }
        return _data;
    }

    /**
     * Set the <tt>pos</tt>th element of this array to <tt>value</tt>
     *
     * @param pos
     * @param value
     */
    public double set(int pos, NslNumeric0 value)
    {
        _data[pos] = value.getdouble();
        return _data[pos];
    }


    /**
     * Set the value of the array from <tt>startpos</tt> to d
     * If the array <tt>d</tt> longer than this array, those
     * out of array scope elements are ignored.
     *
     * @param d - object 1-D array
     * @param startpos - the element number to start copying
     */
    public void setSector(double[] d, int startpos)
    {
        int endpos = d.length + startpos;
        int i;
        int j = 0;

        if (startpos > _data.length)
        {
            return;
        }
        if (endpos > _data.length)
        {
            endpos = _data.length;
        }
        for (i = startpos; i < endpos; i++, j++)
        {
            _data[i] = d[j];
        }
    }

//--various------------------------------------

    /**
     * get the size of this array
     *
     * @return size of this array
     */
    public int getSize()
    {
        return (_data == null ? 0 : _data.length);
    }

    /**
     * get the size of this array and put it into <tt>size</tt>
     *
     * @param size
     */
    public void getNslSize(NslInt0 size)
    {
        size.set((_data == null ? 0 : _data.length));
    }

    /**
     * get the size of this array and put it into <tt>size</tt>
     *
     * @return size = return vector of 4 elements
     */

    public int[] getSizes()
    {
        int[]size = new int[4];
        size[0] = (_data == null ? 0 : _data.length);
        size[1] = 0;
        size[2] = 0;
        size[3] = 0;
        return (size);
    }

    /**
     * get the size of this array at the first dimension
     *
     * @return size of this array
     */
    public int getSize1()
    {
        return (_data == null ? 0 : _data.length);
    }
//----various------------------------------------


    /**
     * Duplicating data from <tt>_new</tt> data of double
     * buffered port to <tt>_old</tt> data object
     *
     * @param n - number with <i>new</i> value
     */
    public void duplicateData(NslData n)
    {
        try
        {
            /* Here we assume that the passed parameter is originally a
        NslInt1 class. Otherwise, it will force a ClassCastException
        and notify the NslSystem.
        */
            set(((NslDouble1) n)._data);
        }
        catch (ClassCastException e)
        {
            System.out.println("Class exception is caught in data duplication");
            System.out.println("between two copies of buffer.");
            System.out.println("Please check NslPort arrangement");
            throw e;
        }
    }

    /**
     * Copy itself
     *
     * @return a copy of this array, casted to NslNumeric
     * @see NslOutport
     */
    public NslData duplicateThis()
    {
        if (isDataSet())
        {
            return new NslDouble1(this._data);
        }
        else
        {
            return new NslDouble1();
        }
    }

    /**
     * Reset the reference of <tt>_data</tt> to a number object
     *
     * @param n number to be reference
     */
    public NslData setReference(NslData n)
    {
        try
        {
            _data = ((NslDouble1) n)._data;
        }
        catch (ClassCastException e)
        {
            System.out.println("$$$ Class exception is caught in reference setting");
            System.out.println("between two copies of buffer.");
            System.out.println("Please check NslPort arrangement");
            throw e;
        }
        return this;
    }

    /**
     * Set the reference to the wrapped data of <tt>n</tt>
     * It is used in double buffered ports, to make the the ports
     * reference different number object at different time.
     *
     * @param value
     */

    public NslDouble1 setReference(double[] value)
    {
        _data = value;
        return this;
    }

    /**
     * Check if the reference is well-defined
     *
     * @return true - if the reference is defined; false - if reference is null
     */
    public boolean isDataSet()
    {
        return (_data != null);
    }

    /**
     * Reset the reference pointer to null
     */
    public void resetData()
    {
        _data = null;
    }

    //<CHANGED>
    public void print()
    {
        System.out.print(toString());
    }

    public String toString()
    {
        StringBuilder strbuf = new StringBuilder("");
        int i;

        //strbuf.append("{ ");
        for (i = 0; i < _data.length; i++)
        {
            strbuf.append(_data[i]);
            strbuf.append(' ');
        }
        //strbuf.append("}");

        return strbuf.toString();
    }

    /**
     * sum all elements of the array
     * @return the sum
     */
/*
  public double sum() {
    double sum = 0;
    int i;

    for (i=0; i<_data.length; i++) {
      sum+=_data[i];
    }
    return sum;
  }
*/
    /**
     * Get the element with maximum value
     * @param max_elem_pos - the position of the element with maximum value
     * @return the maximum value
     */
/*
  public double maxElem(NslInt0 max_elem_pos) {
    double value = java.lang.Double.NEGATIVE_INFINITY;
    int i;
    int pos = -1;

    for (i=0; i<_data.length; i++) {
      if (_data[i]>value) {
	pos = i;
	value = _data[i];
      }
    }
    max_elem_pos.set(pos);
    return value;
  }
*/
    /**
     * Get the maximum value of this array
     * @return the maximum value
     */
/*
  public double maxValue() {
    return maxElem(new NslInt0(0));
  }
*/
    /**
     * Get the element with minimum value
     * @param min_elem_pos - the position of the element with minimum value
     * @return the minimum value
     */
/*
  public double minElem(NslInt0 min_elem_pos) {
    double value = java.lang.Double.POSITIVE_INFINITY;
    int i;
    int pos =-1;

    for (i=0; i<_data.length; i++) {
      if (_data[i]<value) {
	pos = i;
	value = _data[i];
      }
    }
    min_elem_pos.set(pos);
    return value;
  }
*/
    /**
     * Get the minimum value of this array
     * @return the minimum value
     */
/*
  public double minValue() {
    return minElem(new NslInt0(0));
  }
*/



}

// NslDouble1.java
////////////////////////////////////////////////////////////////////////////////



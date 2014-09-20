/*  SCCS - @(#)NslDouble2.java	1.15 - 09/20/99 - 19:19:27 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

////////////////////////////////////////////////////////////////////////////////
// NslDouble2.java
package nslj.src.lang;
//import tcl.lang.*;
//import nslj.src.math.NslSigmoid;

public class NslDouble2 extends NslNumeric2
{
// <CHANGED>
    public double[][] _data;
    //public String _name;
    //boolean visibility = true;
    //NslHierarchy module;


    public NslDouble2(double[][] d)
    {
        super();
        _data = new double[d.length][d[0].length];
        set(d);
        //_name = null;
    }

    public NslDouble2(NslNumeric2 n)
    {
        super();
        _data = new double[n.getSize1()][n.getSize2()];
        set(n.getdouble2());
        //_name = null;
    }

    public NslDouble2(int size1, int size2)
    {
        super();
        _data = new double[size1][size2];
        //_name = null;
    }

    public NslDouble2()
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
    public NslDouble2(String name)
    {
        super(name);
        _data = null;
        //_name = name;
    }

    public NslDouble2(String name, NslHierarchy curParent)
    {
        super(name, curParent, curParent.nslGetAccess());
        _data = null;
        //_name = name;
        //module = curParent;
        //visibility = module.getVisibility();
        //module.enableAccess(this);
    }

    /**
     * This constructs a number with specified name
     *
     * @param name  - name of the variable
     * @param size1 - size of the array 1st-Dimension
     * @param size2 - size of the array 2nd-Dimension
     */
    public NslDouble2(String name, int size1, int size2)
    {
        super(name);
        _data = new double[size1][size2];
        //_name = name;
    }

    public NslDouble2(String name, NslHierarchy curParent, int size1, int size2)
    {
        super(name, curParent, curParent.nslGetAccess());
        _data = new double[size1][size2];
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
    public NslDouble2(String name, NslHierarchy curParent, NslNumeric2 n)
    {
        super(name, curParent, curParent.nslGetAccess());
        _data = new double[n.getSize1()][n.getSize2()];
        set(n.getdouble2());
        //_name = name;
        // module = curParent;
        //visibility = module.getVisibility();
        //module.enableAccess(this);

    }

    public NslDouble2(String name, NslNumeric2 n)
    {
        super(name);
        _data = new double[n.getSize1()][n.getSize2()];
        set(n.getdouble2());
        //_name = name;

    }

    /**
     * This constructs a number with specified name
     *
     * @param name - name of the variable
     * @param d    - initialized values
     */
    // aa
    public NslDouble2(String name, double[][] d)
    {
        super(name);
        _data = new double[d.length][d[0].length];
        set(d);
        //_name = name;
    }

    public void nslMemAlloc(int size1, int size2)
    {
        _data = new double[size1][size2];
    }

//------------------gets-------------------

    public double[][] get()
    {
        return _data;
    }

    public double[] get(int pos1)
    {
        return _data[pos1];
    }

    public double get(int pos1, int pos2)
    {
        return _data[pos1][pos2];
    }

    public double[][] getdouble2()
    {
        return _data;
    }

    public float[][] getfloat2()
    {
        float[][] floatdata;
        int i;
        int j;
        int size1 = _data.length;
        int size2 = _data[0].length;
        floatdata = new float[size1][size2];
        for (i = 0; i < size1; i++)
        {
            for (j = 0; j < size2; j++)
            {
                floatdata[i][j] = (float) _data[i][j];
            }
        }

        return floatdata;
    }

    public int[][] getint2()
    {
        int[][] intdata;
        int i;
        int j;
        int size1 = _data.length;
        int size2 = _data[0].length;
        intdata = new int[size1][size2];
        for (i = 0; i < size1; i++)
        {
            for (j = 0; j < size2; j++)
            {
                intdata[i][j] = (int) _data[i][j];
            }
        }

        return intdata;
    }

    public double[] getdouble1(int pos)
    {
        return _data[pos];
    }

    public float[] getfloat1(int pos)
    {
        int i;
        int size2 = _data[0].length;
        float[] tmp = new float[size2];
        for (i = 0; i < size2; i++)
        {
            tmp[i] = (float) _data[pos][i];
        }
        return tmp;
    }

    public int[] getint1(int pos)
    {
        int i;
        int size2 = _data[0].length;
        int tmp[] = new int[size2];
        for (i = 0; i < size2; i++)
        {
            tmp[i] = (int) _data[pos][i];
        }
        return tmp;
    }

    public double getdouble(int pos1, int pos2)
    {
        return _data[pos1][pos2];
    }

    public float getfloat(int pos1, int pos2)
    {
        return (float) _data[pos1][pos2];
    }

    public int getint(int pos1, int pos2)
    {
        return (int) _data[pos1][pos2];
    }


    public double[][] getSector(int start1, int start2, int end1, int end2)
    {
        int i1;
        int i2;
        int j1;
        int j2;

        int length1;
        int length2;

        double[][] doubledata;
        if (start1 < 0)
        {
            start1 = 0;
        }
        if (start2 < 0)
        {
            start2 = 0;
        }

        if (end1 > _data.length)
        {
            end1 = _data.length;
        }
        if (end2 > _data[0].length)
        {
            end2 = _data[0].length;
        }
        length1 = end1 - start1 + 1;
        length2 = end2 - start2 + 1;
        doubledata = new double[length1][length2];
        i1 = start1;
        for (j1 = 0; j1 < length1; j1++, i1++)
        {
            i2 = start2;
            for (j2 = 0; j2 < length2; j2++, i2++)
            {
                doubledata[j1][j2] = _data[i1][i2];
            }
        }
        return doubledata;
    }

    public NslDouble2 getNslDouble2()
    {
        return this;
    }

    public NslFloat2 getNslFloat2()
    {
        return (new NslFloat2(getfloat2()));
    }

    public NslInt2 getNslInt2()
    {
        return (new NslInt2(getint2()));
    }

//----------sets------------------------


    public void _set(double[][] value)
    {
        set(value);
    }

    /**
     * set the value of this object to <tt>value</tt>
     *
     * @param value - two dimension array
     */
    public void _set(float[][]  value)
    {
        set(value);
    }

    /**
     * set the value of this object to <tt>value</tt>
     *
     * @param value - two dimension array
     */
    public void _set(int[][]    value)
    {
        set(value);
    }

    /**
     * set the value of an element in this object to <tt>value</tt>
     *
     * @param pos1  - the row number of the element
     * @param pos2  - the column number of the element
     * @param value - scalar in double
     */
    public void _set(int pos1, int pos2, double value)
    {
        set(pos1, pos2, value);
    }

    /**
     * set the value of an element in this object to <tt>value</tt>
     *
     * @param pos1  - the row number of the element
     * @param pos2  - the column number of the element
     * @param value - scalar in float
     */
    public void _set(int pos1, int pos2, float value)
    {
        set(pos1, pos2, value);
    }

    /**
     * set the value of an element in this object to <tt>value</tt>
     *
     * @param pos1  - the row number of the element
     * @param pos2  - the column number of the element
     * @param value - scalar in int
     */
    public void _set(int pos1, int pos2, int value)
    {
        set(pos1, pos2, value);
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
     * @param value - in any of <tt>NslNumeric2</tt> type.
     */
    public void _set(NslNumeric2 value)
    {
        set(value);
    }

    // changing all inside the array

    /**
     * set the value of all elements of this object to <tt>value</tt>
     *
     * @param value - value to be defined.
     */
    public void _set(NslNumeric0 value)
    {
        set(value);
    }

    /**
     * set the value of an element in this object to <tt>value</tt>
     *
     * @param pos1  - the row number of the element
     * @param pos2  - the column number of the element
     * @param value - scalar in NslNumeric0
     */
    public void _set(int pos1, int pos2, NslNumeric0 value)
    {
        set(pos1, pos2, value);
    }


    public double[][] set(double[][] value)
    {
        int i;
        int size1 = _data.length;
        int size2 = _data[0].length;

        if (size1 != value.length || size2 != value[0].length)
        {
            System.out.println("NslDouble2: array size not equal");
            return _data;
        }

        for (i = 0; i < size1; i++)
        {
            System.arraycopy(value[i], 0, _data[i], 0, size2);
        }
        return _data;
    }

    public double[][] set(float[][] value)
    {
        int i;
        int j;
        int size1 = _data.length;
        int size2 = _data[0].length;

        if (size1 != value.length || size2 != value[0].length)
        {
            System.out.println("NslDouble2:  array size not equal");
            return _data;
        }
        for (i = 0; i < size1; i++)
        {
            for (j = 0; j < size2; j++)
            {
                _data[i][j] = (double) value[i][j];
            }
        }

        return _data;
    }

    public double[][] set(int[][] value)
    {
        int i;
        int j;
        int size1 = _data.length;
        int size2 = _data[0].length;

        if (size1 != value.length || size2 != value[0].length)
        {
            System.out.println("NslDouble2: internal data length not equal set value length. set() ignored");
            return _data;
        }

        for (i = 0; i < size1; i++)
        {
            for (j = 0; j < size2; j++)
            {
                _data[i][j] = (double) value[i][j];
            }
        }
        return _data;
    }

    public double[] set(int pos, double[] value)
    {

        if (_data[0].length != value.length)
        {
            System.out.println("NslDouble2: array size not match");
            return _data[pos];
        }

        System.arraycopy(value, 0, _data[pos], 0, _data[0].length);

        return _data[pos];
    }

    /**
     * Set the value of this number to <tt>value</tt>
     *
     * @param value
     */

    public double[] set(int pos, float[] value)
    {

        if (_data[0].length != value.length)
        {
            System.out.println("NslDouble2: array size not match");
            return _data[pos];
        }

        System.arraycopy(value, 0, _data[pos], 0, _data[0].length);

        return _data[pos];
    }

    /**
     * Set the value of this number to <tt>value</tt>
     *
     * @param value
     */

    public double[] set(int pos, int[] value)
    {

        if (_data[0].length != value.length)
        {
            System.out.println("NslDouble1: array size not match");
            return _data[pos];
        }

        System.arraycopy(value, 0, _data[pos], 0, _data[0].length);

        return _data[pos];
    }

    public double set(int pos1, int pos2, double value)
    {
        _data[pos1][pos2] = value;
        return _data[pos1][pos2];
    }

    public double set(int pos1, int pos2, float value)
    {
        _data[pos1][pos2] = (double) value;
        return _data[pos1][pos2];
    }

    public double set(int pos1, int pos2, int value)
    {
        _data[pos1][pos2] = (double) value;
        return _data[pos1][pos2];
    }

    public double[][] set(double value)
    {
        int i, j;
        int size1 = _data.length;
        int size2 = _data[0].length;
        for (i = 0; i < size1; i++)
        {
            for (j = 0; j < size2; j++)
            {
                _data[i][j] = value;
            }
        }
        return _data;
    }

    public double[][] set(float value)
    {
        set((double) value);
        return _data;
    }

    public double[][] set(int value)
    {
        set((double) value);
        return _data;
    }

    public double[][] set(NslNumeric2 n)
    {
        //   NslInt0 size1 = new NslInt0(0);
        //   NslInt0 size2 = new NslInt0(0);

        //  value.getSizes(size1, size2);
        if (_data.length != n.getSize1() || _data[0].length != n.getSize2())
        {
            System.out.println("NslDouble1: array size not eqaul");
            return _data;
        }
        return set(n.getdouble2());
    }

    public double[][] set(NslNumeric0 n)
    {
        return set(n.getdouble());
    }

    public double[] set(int pos1, NslNumeric1 n)
    {
        return set(pos1, n.getdouble1());
    }

    public double set(int pos1, int pos2, NslNumeric0 value)
    {
        return set(pos1, pos2, value.getdouble());
    }


    public void setSector(double[][] d, int startpos1, int startpos2)
    {
        int endpos1 = d.length + startpos1;
        int endpos2 = d[0].length + startpos2;
        int i1, i2;
        int j1, j2;

        if (startpos1 > _data.length)
        {
            return;
        }
        if (startpos2 > _data[0].length)
        {
            return;
        }
        if (endpos1 > _data.length)
        {
            endpos1 = _data.length;
        }
        if (endpos2 > _data[0].length)
        {
            endpos2 = _data[0].length;
        }

        for (i1 = startpos1, j1 = 0; i1 < endpos1; i1++, j1++)
        {
            j2 = 0;
            for (i2 = startpos2; i2 < endpos2; i2++, j2++)
            {
                _data[i1][i2] = d[j1][j2];
            }
        }
    }

    public void setSector(int[][] d, int startpos1, int startpos2)
    {
        int endpos1 = d.length + startpos1;
        int endpos2 = d[0].length + startpos2;
        int i1, i2;
        int j1, j2;

        if (startpos1 > _data.length)
        {
            return;
        }
        if (startpos2 > _data[0].length)
        {
            return;
        }
        if (endpos1 > _data.length)
        {
            endpos1 = _data.length;
        }
        if (endpos2 > _data[0].length)
        {
            endpos2 = _data[0].length;
        }

        for (i1 = startpos1, j1 = 0; i1 < endpos1; i1++, j1++)
        {
            j2 = 0;
            for (i2 = startpos2; i2 < endpos2; i2++, j2++)
            {
                _data[i1][i2] = d[j1][j2];
            }
        }
    }
//-------various-------------------------------

    public int[] getSizes()
    {
        int[] size = new int[4];
        size[0] = (_data == null ? 0 : _data.length);
        size[1] = (_data == null ? 0 : _data[0].length);
        size[2] = (0);
        size[3] = (0);

        return size;
    }

    public void getNslSizes(NslInt0 size1, NslInt0 size2)
    {
        size1.set((_data == null ? 0 : _data.length));
        size2.set((_data == null ? 0 : _data[0].length));
    }


    public int getSize1()
    {
        return (_data == null ? 0 : _data.length);
    }

    public int getSize2()
    {
        return (_data == null ? 0 : _data[0].length);
    }

/*
     Duplicating data between buffers in double buffering port model.
     Since we cannot ensure the copy is the original copy created
     in instantiation, this code is to make a security check and
     to make sure the program runs correctly in the latter step.
     */

    public void duplicateData(NslData n)
    {
        try
        {
            /* Here we assume that the passed parameter is originally a
        NslInt1 class. Otherwise, it will force a ClassCastException
        and notify the NslSystem.
        */
            set(((NslDouble2) n)._data);

        }
        catch (ClassCastException e)
        {
            System.out.println("Class exception is caught in data duplication");
            System.out.println("between two copies of buffer.");
            System.out.println("Please check NslPort arrangement");
            throw e;
        }
    }

    public NslData duplicateThis()
    {
        if (isDataSet())
        {
            return new NslDouble2(this._data);
        }
        else
        {
            return new NslDouble2();
        }
    }

    public NslData setReference(NslData n)
    {

        try
        {
            _data = ((NslDouble2) n)._data;
        }
        catch (ClassCastException e)
        {
            System.out.println("Class exception is caught in reference setting");
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

    public NslDouble2 setReference(double[][] value)
    {
        _data = value;
        return this;
    }


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

    public void print()
    {
        System.out.print(toString());

    }

    public String toString()
    {
        StringBuilder strbuf = new StringBuilder("");
        int i, j;
        int size1 = _data.length;
        int size2 = _data[0].length;
        //System.out.println(" ");
        //strbuf.append(" ");
        for (i = 0; i < size1; i++)
        {
            strbuf.append("{ ");
            for (j = 0; j < size2; j++)
            {
                strbuf.append(_data[i][j]);
                strbuf.append(' ');
            }
            strbuf.append("} ");
        }
        //strbuf.append("}");
        return strbuf.toString();
    }

/*
public double sum() {
    double sum = 0;
    int i, j;
    int size1 = _data.length;
    int size2 = _data[0].length;

    for (i=0; i<size1; i++) {
        for(j=0; j<size2; j++)
          sum+=_data[i][j];
    }
    return sum;
  }

  public double maxElem(NslInt0 max_elem_pos1, NslInt0 max_elem_pos2) {
    double value = java.lang.Double.NEGATIVE_INFINITY;
    int i, j;
    int size1 = _data.length;
    int size2 = _data[0].length;

    int pos1 = -1;
    int pos2 = -1;

    for (i=0; i<size1; i++) {
        for(j=0; j<size2; j++) {
          if (_data[i][j]>value) {
            pos1 = i; pos2 = j;
            value = _data[i][j];
          }
      }
    }
    max_elem_pos1.set(pos1);
    max_elem_pos2.set(pos2);
    return value;
  }

  public double max() {
    return maxElem(new NslInt0(0), new NslInt0(0));
  }


  public double minElem(NslInt0 max_elem_pos1, NslInt0 max_elem_pos2) {
    double value = java.lang.Double.POSITIVE_INFINITY;
    int i, j;
    int size1 = _data.length;
    int size2 = _data[0].length;

    int pos1 = -1;
    int pos2 = -1;

    for (i=0; i<size1; i++) {
        for(j=0; j<size2; j++) {
          if (_data[i][j]<value) {
            pos1 = i; pos2 = j;
            value = _data[i][j];
          }
      }
    }
    max_elem_pos1.set(pos1);
    max_elem_pos2.set(pos2);
    return value;
  }

  public double min() {
    return minElem(new NslInt0(0), new NslInt0(0));
  }

*/
/*  public TclObject toList(Interp interp) 
  	throws TclException {    
    int i, j;
    int size1 = _data.length;
    int size2 = _data[0].length;
    TclObject list1 = TclList.newInstance();
    for(i=0; i<size1; i++) {
    	TclObject list2 = TclList.newInstance();
        for(j=0; j<size2; j++) {
           String s = _data[i][j] + "";
          TclList.append(interp, list2, TclString.newInstance(s));
        }
        TclList.append(interp, list1, list2);
    }
    
    return list1;
  }*/

/** Threshold functions */
    /* public double[][] sigmoid(){
    return nslj.src.math.NslSigmoid.eval(this);
  }


  public double[][] sigmoid(double kx1, double kx2, double ky1, double ky2 ) {
    return nslj.src.math.NslSigmoid.eval(this, kx1, kx2, ky1, ky2);
  }  */

}

// NslDouble2.java
////////////////////////////////////////////////////////////////////////////////



/*  SCCS - @(#)NslConv.java	1.5 - 09/01/99 - 00:18:05 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

/*
 * $Log: NslConv.java,v $
 * Revision 1.1  1997/07/30 21:19:29  erhan
 * nsl3.0
 *
 * Revision 1.1.1.1  1997/03/12 22:52:20  nsl
 * new dir structure
 *
 * Revision 1.1.1.1  1997/02/08 00:40:40  nsl
 *  Imported the Source directory
 *
*/
////////////////////////////////////////////////////////////
//
// Standard convolution routines
//
//

/**
 Standard convolution routines.
 There are two basic format for the evaluation method in
 this routine:
 1, eval(a, b) -> c
 a, b are the parameter of the evaluation function to do
 a convolutes b and the result is passed out as c
 2. eval(dest, a, b) -> c
 a, b are the parameter of the evaluation function and
 <tt>dest</tt> is the temporary space to hold the result.
 The method returns the reference to <tt>dest</tt>.

 */

package nslj.src.math;

import nslj.src.lang.*;

public final class NslConv
{


    public static int eval(int a, int b)
    {
        return a * b;
    }

    public static float eval(float a, float b)
    {
        return a * b;
    }

    public static double eval(double a, double b)
    {
        return a * b;
    }

    /**
     * *********************
     */

    public static int[] eval(int a, int[] b)
    {
        int[] tmp = new int[1];
        tmp[0] = a;
        return eval(tmp, b);
    }

    public static float[] eval(float a, float[] b)
    {
        float[] tmp = new float[1];
        tmp[0] = a;
        return eval(tmp, b);
    }

    public static double[] eval(double a, double[] b)
    {
        double[] tmp = new double[1];
        tmp[0] = a;
        return eval(tmp, b);
    }

    /**
     * ************************
     */

    public static int[] eval(int[] a, int b)
    {
        int[] tmp = new int[1];
        tmp[0] = b;
        return eval(b, tmp);
    }

    public static float[] eval(float[] a, float b)
    {
        float[] tmp = new float[1];
        tmp[0] = b;
        return eval(b, tmp);
    }

    public static double[] eval(double[] a, double b)
    {
        double[] tmp = new double[1];
        tmp[0] = b;
        return eval(b, tmp);
    }

    /**
     * ***************************
     */

    public static int[] eval(int[] a, int[] b)
    {
        int[] dest;

        dest = new int[b.length];

        return eval(dest, a, b);
    }

    public static int[] eval(int[] dest, int[] a, int[] b)
    {
        int sa = a.length; // size of a
        int sb = b.length;
        int i;
        int j;
        int[] tmp = new int[sa + sb - 1];
        int sum;

        if (dest.length != sb)
        {
            dest = new int[sb];
        }

        j = sa >> 1;
        for (i = 0; i < sb; i++)
        {
            tmp[i + j] = b[i]; // tmp[i+a.length]= b[i];
        }

        for (i = 0; i < sb; i++)
        {
            sum = 0;
            for (j = 0; j < sa; j++)
            {
                sum += (a[j] * tmp[i + j]);
            }
            dest[i] = sum;
        }

        return dest;
    }

    /**
     * ***************************
     */

    public static float[] eval(float[] a, float[] b)
    {
        float[] dest;
        dest = new float[b.length];
        return eval(dest, a, b);
    }

    public static float[] eval(float[] dest, float[] a, float[] b)
    {
        int sa = a.length; // size of a
        int sb = b.length;
        int i;
        int j;
        float[] tmp = new float[sa + sb - 1];
        float sum;

        if (dest.length != sb)
        {
            dest = new float[sb];
        }

        j = sa >> 1;
        for (i = 0; i < sb; i++)
        {
            tmp[i + j] = b[i]; // tmp[i+a.length]= b[i];
        }

        for (i = 0; i < sb; i++)
        {
            sum = 0;
            for (j = 0; j < sa; j++)
            {
                sum += (a[j] * tmp[i + j]);
            }
            dest[i] = sum;
        }
        return dest;
    }

    /**
     * *******************************
     */

    public static double[] eval(double[] a, double[] b)
    {
        double[] dest;
        dest = new double[b.length];
        return eval(dest, a, b);
    }

    public static double[] eval(double[] a, int[] b)
    {
        double[] dest;
        dest = new double[b.length];
        return eval(dest, a, b);
    }

    public static double[] eval(int[] a, double[] b)
    {
        double[] dest;
        dest = new double[b.length];
        return eval(dest, a, b);
    }

    public static double[] eval(double[] dest, double[] a, int[] b)
    {
        int sa = a.length; // size of a
        int sb = b.length;
        int i;
        int j;
        double[] tmp = new double[sa + sb - 1];
        double sum;

        if (dest.length != sb)
        {
            dest = new double[sb];
        }

        j = sa >> 1;
        for (i = 0; i < sb; i++)
        {
            tmp[i + j] = b[i]; // tmp[i+a.length]= b[i];
        }

        for (i = 0; i < sb; i++)
        {
            sum = 0;
            for (j = 0; j < sa; j++)
            {
                sum += (a[j] * tmp[i + j]);
            }
            dest[i] = sum;
        }

        return dest;
    }

    public static double[] eval(double[] dest, int[] a, double[] b)
    {
        int sa = a.length; // size of a
        int sb = b.length;
        int i;
        int j;
        double[] tmp = new double[sa + sb - 1];
        double sum;

        if (dest.length != sb)
        {
            dest = new double[sb];
        }

        j = sa >> 1;
        for (i = 0; i < sb; i++)
        {
            tmp[i + j] = b[i]; // tmp[i+a.length]= b[i];
        }

        for (i = 0; i < sb; i++)
        {
            sum = 0;
            for (j = 0; j < sa; j++)
            {
                sum += (a[j] * tmp[i + j]);
            }
            dest[i] = sum;
        }

        return dest;
    }

    public static double[] eval(double[] dest, double[] a, double[] b)
    {
        int sa = a.length; // size of a
        int sb = b.length;
        int i;
        int j;
        double[] tmp = new double[sa + sb - 1];
        double sum;

        if (dest.length != sb)
        {
            dest = new double[sb];
        }

        j = sa >> 1;
        for (i = 0; i < sb; i++)
        {
            tmp[i + j] = b[i]; // tmp[i+a.length]= b[i];
        }

        for (i = 0; i < sb; i++)
        {
            sum = 0;
            for (j = 0; j < sa; j++)
            {
                sum += (a[j] * tmp[i + j]);
            }
            dest[i] = sum;
        }

        return dest;
    }

    /**
     * *******************************
     */

    public static double[][] eval(double[][] a, double[][] b)
    {
        double[][] dest;
        dest = new double[b.length][b[0].length];
        return eval(dest, a, b);
    }

    public static double[][] eval(NslDouble2 dest, double[][] a, double[][] b)
    {
        int sai = a.length; // size of a
        int saj = a[0].length;
        int sbi = b.length;
        int sbj = b[0].length;

        int si = sai + sbi - 1;
        int sj = saj + sbj - 1;

        int i;
        int j;
        int m, n;
        double val;

        dest = new NslDouble2(si, sj);
        double[][] dv = dest.getdouble2();

        dest.setSector(b, sai >> 1, saj >> 1);

        for (i = 0; i < sbi; i++)
        {
            for (j = 0; j < sbj; j++)
            {
                val = 0.0;
                for (m = 0; m < sai; m++)
                {
                    for (n = 0; n < saj; n++)
                    {
                        val += (a[m][n] * dv[i + m][j + n]);
                    }
                }
                dest.set(i,j, val); // c[i][j]=val;
            }
        }
        return dest.get(); // return c;
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    public static double[][] eval(double[][] dest, double[][] a, double[][] b)
    {
        int sai = a.length; // size of a
        int saj = a[0].length;
        int sbi = b.length;
        int sbj = b[0].length;

        int si = sai + sbi - 1;
        int sj = saj + sbj - 1;

        int i;
        int j;
        int m, n;
        double val;

        NslDouble2 d = new NslDouble2(si, sj);

        double[][] dv = d.getdouble2();

        d.setSector(b, sai >> 1, saj >> 1);
        if (dest.length != sbi || dest[0].length != sbj)
        {
            dest = new double[sbi][sbj];
        }

        for (i = 0; i < sbi; i++)
        {
            for (j = 0; j < sbj; j++)
            {
                val = 0.0;
                for (m = 0; m < sai; m++)
                {
                    for (n = 0; n < saj; n++)
                    {
                        val += (a[m][n] * dv[i + m][j + n]);
                    }
                }
                dest[i][j] = val; // c[i][j]=val;
            }
        }
        return dest; // return c;
    }

    public static double[][] eval(int[][] a, double[][] b)
    {
        double[][] dest;
        dest = new double[b.length][b[0].length];
        return eval(dest, a, b);
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    /**
     * Takes a 2D array of grey-levels and a kernel and applies the convolution
     * over the area of the image specified by width and height.
     *
     * @param dest the 2D double array representing the output
     * @param a the 2D double array representing the output
     * @param b the 2D array representing the kernel
     * @return the 2D array representing the new image
     */
    /*public static double [][] eval(double[][] dest, int [][] a, double [][] b)
    {
        int width = dest.length; // size of a
        int height = dest[0].length;
        int kernelWidth = b.length;
        int kernelHeight = b[0].length;
        //int smallWidth = width - kernelWidth + 1;
        //int smallHeight = height - kernelHeight + 1;
        for(int i=0;i<width;++i)
        {          
            for(int j=0;j<height;++j)
            {
                dest[i][j]=0;
            }
        }
        for(int i=0;i<width;++i)
        {
            for(int j=0;j<height;++j)
            {

                dest[i][j] = singlePixelConvolution(a,i,j,b,kernelWidth,kernelHeight);
            }
        }
        return dest;
    }

    *//**
     * Takes an image (grey-levels) and a kernel and a position,
     * applies the convolution at that position and returns the
     * new pixel value.
     *
     * @param input The 2D double array representing the image.
     * @param x The x coordinate for the position of the convolution.
     * @param y The y coordinate for the position of the convolution.
     * @param k The 2D array representing the kernel.
     * @param kernelWidth The width of the kernel.
     * @param kernelHeight The height of the kernel.
     * @return The new pixel value after the convolution.
     *//*
    public static double singlePixelConvolution(int [][] input, int x, int y, double [][] k, int kernelWidth,
                                                int kernelHeight)
    {
        double output = 0;
        for(int i=0;i<kernelWidth;++i)
        {
            for(int j=0;j<kernelHeight;++j)
            {
                output = output + (input[x+i][y+j] * k[i][j]);
            }
        }
        return output;
    }*/

    public static double[][] eval(double[][] dest, int[][] b, double[][] a)
    {
        int sai = a.length; // size of a
        int saj = a[0].length;
        int sbi = b.length;
        int sbj = b[0].length;

        int si = sai + sbi - 1;
        int sj = saj + sbj - 1;

        int i;
        int j;
        int m, n;
        double val;

        NslDouble2 d = new NslDouble2(si, sj);

        double[][] dv = d.getdouble2();

        d.setSector(b, sai >> 1, saj >> 1);
        if (dest.length != sbi || dest[0].length != sbj)
        {
            dest = new double[sbi][sbj];
        }

        for (i = 0; i < sbi; i++)
        {
            for (j = 0; j < sbj; j++)
            {
                val = 0.0;
                for (m = 0; m < sai; m++)
                {
                    for (n = 0; n < saj; n++)
                    {
                        val += (a[m][n] * dv[i + m][j + n]);
                    }
                }
                dest[i][j] = val; // c[i][j]=val;
            }
        }
        return dest; // return c;
    }

    /**
     * *******************************
     */

    public static double[][][] eval(double[][][] a, double[][][] b)
    {
        double[][][] dest;
        dest = new double[b.length][b[0].length][b[0][0].length];
        return eval(dest, a, b);
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    public static double[][][] eval(double[][][] dest, double[][][] a, double[][][] b)
    {
        int sai = a.length; // size of a
        int saj = a[0].length;
        int sak = a[0][0].length;
        int sbi = b.length;
        int sbj = b[0].length;
        int sbk = b[0][0].length;

        int si = sai + sbi - 1;
        int sj = saj + sbj - 1;
        int sk = sak + sbk - 1;

        int i;
        int j;
        int k;
        int m, n, o;
        double val;

        NslDouble3 d = new NslDouble3(si, sj, sk);

        double[][][] dv = d.getdouble3();

        d.setSector(b, sai >> 1, saj >> 1, sak >> 1);
        if (dest.length != sbi || dest[0].length != sbj || dest[0][0].length != sbk)
        {
            dest = new double[sbi][sbj][sbk];
        }

        for (i = 0; i < sbi; i++)
        {
            for (j = 0; j < sbj; j++)
            {
                for(k = 0; k < sbk; k++)
                {
                    val = 0.0;
                    for (m = 0; m < sai; m++)
                    {
                        for (n = 0; n < saj; n++)
                        {
                            for (o = 0; o < sak; o++)
                                val += (a[m][n][o] * dv[i + m][j + n][k + o]);
                        }
                    }
                    dest[i][j][k] = val; // c[i][j]=val;
                }
            }
        }
        return dest; // return c;
    }

    /**
     * ******************************
     */

// added for floats.
    public static float[][] eval(float[][] a, float[][] b)
    {
        float[][] dest;
        dest = new float[b.length][b[0].length];
        return eval(dest, a, b);
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    public static float[][] eval(float[][] dest, float[][] a, float[][] b)
    {
        int sai = a.length; // size of a
        int saj = a[0].length;
        int sbi = b.length;
        int sbj = b[0].length;

        int si = sai + sbi - 1;
        int sj = saj + sbj - 1;

        int i;
        int j;
        int m, n;
        float val;

        NslFloat2 d = new NslFloat2(si, sj);

        float[][] dv = d.getfloat2();

        d.setSector(b, sai >> 1, saj >> 1);
        if (dest.length != sbi || dest[0].length != sbj)
        {
            dest = new float[sbi][sbj];
        }

        for (i = 0; i < sbi; i++)
        {
            for (j = 0; j < sbj; j++)
            {
                val = 0;
                for (m = 0; m < sai; m++)
                {
                    for (n = 0; n < saj; n++)
                    {
                        val += (a[m][n] * dv[i + m][j + n]);
                    }
                }
                dest[i][j] = val; // c[i][j]=val;
            }
        }
        return dest; // return c;
    }

    /**
     * *******************************
     */

    public static float[][][] eval(float[][][] a, float[][][] b)
    {
        float[][][] dest;
        dest = new float[b.length][b[0].length][b[0][0].length];
        return eval(dest, a, b);
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    public static float[][][] eval(float[][][] dest, float[][][] a, float[][][] b)
    {
        int sai = a.length; // size of a
        int saj = a[0].length;
        int sak = a[0][0].length;
        int sbi = b.length;
        int sbj = b[0].length;
        int sbk = b[0][0].length;

        int si = sai + sbi - 1;
        int sj = saj + sbj - 1;
        int sk = sak + sbk - 1;

        int i;
        int j;
        int k;
        int m, n, o;
        float val;

        NslFloat3 d = new NslFloat3(si, sj, sk);

        float[][][] dv = d.getfloat3();

        d.setSector(b, sai >> 1, saj >> 1, sak >> 1);
        if (dest.length != sbi || dest[0].length != sbj || dest[0][0].length != sbk)
        {
            dest = new float[sbi][sbj][sbk];
        }

        for (i = 0; i < sbi; i++)
        {
            for (j = 0; j < sbj; j++)
            {
                for(k = 0; k < sbk; k++)
                {
                    val = 0.0f;
                    for (m = 0; m < sai; m++)
                    {
                        for (n = 0; n < saj; n++)
                        {
                            for (o = 0; o < sak; o++)
                                val += (a[m][n][o] * dv[i + m][j + n][k + o]);
                        }
                    }
                    dest[i][j][k] = val; // c[i][j]=val;
                }
            }
        }
        return dest; // return c;
    }
    /**
     * ******************************
     */

//added for integers.
    public static int[][] eval(int[][] a, int[][] b)
    {
        int[][] dest;
        dest = new int[b.length][b[0].length];
        return eval(dest, a, b);
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    public static int[][] eval(int[][] dest, int[][] a, int[][] b)
    {
        int sai = a.length; // size of a
        int saj = a[0].length;
        int sbi = b.length;
        int sbj = b[0].length;

        int si = sai + sbi - 1;
        int sj = saj + sbj - 1;

        int i;
        int j;
        int m, n;
        int val;

        NslInt2 d = new NslInt2(si, sj);

        int[][] dv = d.getint2();

        d.setSector(b, sai >> 1, saj >> 1);
        if (dest.length != sbi || dest[0].length != sbj)
        {
            dest = new int[sbi][sbj];
        }

        for (i = 0; i < sbi; i++)
        {
            for (j = 0; j < sbj; j++)
            {
                val = 0;
                for (m = 0; m < sai; m++)
                {
                    for (n = 0; n < saj; n++)
                    {
                        val += (a[m][n] * dv[i + m][j + n]);
                    }
                }
                dest[i][j] = val; // c[i][j]=val;
            }
        }
        return dest; // return c;
    }

    /**
     * *******************************
     */

    public static int[][][] eval(int[][][] a, int[][][] b)
    {
        int[][][] dest;
        dest = new int[b.length][b[0].length][b[0][0].length];
        return eval(dest, a, b);
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    public static int[][][] eval(int[][][] dest, int[][][] a, int[][][] b)
    {
        int sai = a.length; // size of a
        int saj = a[0].length;
        int sak = a[0][0].length;
        int sbi = b.length;
        int sbj = b[0].length;
        int sbk = b[0][0].length;

        int si = sai + sbi - 1;
        int sj = saj + sbj - 1;
        int sk = sak + sbk - 1;

        int i;
        int j;
        int k;
        int m, n, o;
        int val;

        NslInt3 d = new NslInt3(si, sj, sk);

        int[][][] dv = d.getint3();

        d.setSector(b, sai >> 1, saj >> 1, sak >> 1);
        if (dest.length != sbi || dest[0].length != sbj || dest[0][0].length != sbk)
        {
            dest = new int[sbi][sbj][sbk];
        }

        for (i = 0; i < sbi; i++)
        {
            for (j = 0; j < sbj; j++)
            {
                for(k = 0; k < sbk; k++)
                {
                    val = 0;
                    for (m = 0; m < sai; m++)
                    {
                        for (n = 0; n < saj; n++)
                        {
                            for (o = 0; o < sak; o++)
                                val += (a[m][n][o] * dv[i + m][j + n][k + o]);
                        }
                    }
                    dest[i][j][k] = val; // c[i][j]=val;
                }
            }
        }
        return dest; // return c;
    }

    /**
     * ******************************
     */

// 1D @ 2D
    public static double[][] eval(double[] a, double[][] b)
    {
        double[][] dest;
        dest = new double[b.length][b[0].length];
        return eval(dest, a, b);
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    public static double[][] eval(double[][] dest, double[] a, double[][] b)
    {
        int sai = a.length; // size of a
        int sbi = b.length;
        int sbj = b[0].length;

        int si = sai + sbi - 1;
        int sj = sbj - 1;

        int i;
        int j;
        int m, n;
        double val;

        NslDouble2 d = new NslDouble2(si, sj);

        double[][] dv = d.getdouble2();

        d.setSector(b, sai >> 1, 0);
        if (dest.length != sbi || dest[0].length != sbj)
        {
            dest = new double[sbi][sbj];
        }

        for (i = 0; i < sbi; i++)
        {
            for (j = 0; j < sbj; j++)
            {
                val = 0.0;
                for (m = 0; m < sbi; m++)
                {
                    for (n = 0; n < sbj; n++)
                    {
                        val += (a[m] * dv[i + m][j]);
                    }
                }
                dest[i][j] = val; // c[i][j]=val;
            }
        }
        return dest; // return c;
    }

    /**
     * ***************************
     */

//added for floats

// 1D @ 2D
    public static float[][] eval(float[] a, float[][] b)
    {
        float[][] dest;
        dest = new float[b.length][b[0].length];
        return eval(dest, a, b);
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    public static float[][] eval(float[][] dest, float[] a, float[][] b)
    {
        int sai = a.length; // size of a
        int sbi = b.length;
        int sbj = b[0].length;

        int si = sai + sbi - 1;
        int sj = sbj - 1;

        int i;
        int j;
        int m, n;
        float val;

        NslFloat2 d = new NslFloat2(si, sj);

        float[][] dv = d.getfloat2();

        d.setSector(b, sai >> 1, 0);
        if (dest.length != sbi || dest[0].length != sbj)
        {
            dest = new float[sbi][sbj];
        }

        for (i = 0; i < sbi; i++)
        {
            for (j = 0; j < sbj; j++)
            {
                val = 0;
                for (m = 0; m < sbi; m++)
                {
                    for (n = 0; n < sbj; n++)
                    {
                        val += (a[m] * dv[i + m][j]);
                    }
                }
                dest[i][j] = val; // c[i][j]=val;
            }
        }
        return dest; // return c;
    }

    /**
     * *************************
     */

//added for ints

// 1D @ 2D
    public static int[][] eval(int[] a, int[][] b)
    {
        int[][] dest;
        dest = new int[b.length][b[0].length];
        return eval(dest, a, b);
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    public static int[][] eval(int[][] dest, int[] a, int[][] b)
    {
        int sai = a.length; // size of a
        int sbi = b.length;
        int sbj = b[0].length;

        int si = sai + sbi - 1;
        int sj = sbj - 1;

        int i;
        int j;
        int m, n;
        int val;

        NslInt2 d = new NslInt2(si, sj);

        int[][] dv = d.getint2();

        d.setSector(b, sai >> 1, 0);
        if (dest.length != sbi || dest[0].length != sbj)
        {
            dest = new int[sbi][sbj];
        }

        for (i = 0; i < sbi; i++)
        {
            for (j = 0; j < sbj; j++)
            {
                val = 0;
                for (m = 0; m < sbi; m++)
                {
                    for (n = 0; n < sbj; n++)
                    {
                        val += (a[m] * dv[i + m][j]);
                    }
                }
                dest[i][j] = val; // c[i][j]=val;
            }
        }
        return dest; // return c;
    }

    /**
     * *************************
     */

// 2D @ 1D
    public static double[][] eval(double[][] b, double[] a)
    {
        double[][] dest;
        dest = new double[b.length][b[0].length];
        return eval(dest, a, b);
    }

    public static double[][] eval(double[][] dest, double[][] b, double[] a)
    {
        return eval(dest, a, b); // return c;
    }

// 2D @ 0D

    public static double[][] eval(double[][] a, double b)
    {
        double[][] dest;
        dest = new double[a.length][a[0].length];
        return eval(dest, a, b);
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    public static double[][] eval(double[][] dest, double[][] a, double b)
    {
        int sai = a.length;
        int saj = a[0].length;

        int i;
        int j;
        if (dest.length != sai || dest[0].length != saj)
        {
            dest = new double[sai][saj];
        }

        for (i = 0; i < sai; i++)
        {
            for (j = 0; j < saj; j++)
            {
                dest[i][j] = a[i][j] * b;
            }
        }

        return dest; // return c;
    }

// 0D @ 2D

    public static double[][] eval(double b, double[][] a)
    {
        return eval(a, b);
    }

    public static double[][] eval(double[][] dest, double b, double[][] a)
    {
        return eval(dest, a, b);
    }

    /**
     * ************************
     */

//added for floats.

// 2D @ 1D
    public static float[][] eval(float[][] b, float[] a)
    {
        float[][] dest;
        dest = new float[b.length][b[0].length];
        return eval(dest, a, b);
    }

    public static float[][] eval(float[][] dest, float[][] b, float[] a)
    {
        return eval(dest, a, b); // return c;
    }

// 2D @ 0D

    public static float[][] eval(float[][] a, float b)
    {
        float[][] dest;
        dest = new float[a.length][a[0].length];
        return eval(dest, a, b);
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    public static float[][] eval(float[][] dest, float[][] a, float b)
    {
        int sai = a.length;
        int saj = a[0].length;

        int i;
        int j;
        if (dest.length != sai || dest[0].length != saj)
        {
            dest = new float[sai][saj];
        }

        for (i = 0; i < sai; i++)
        {
            for (j = 0; j < saj; j++)
            {
                dest[i][j] = a[i][j] * b;
            }
        }

        return dest; // return c;
    }

// 0D @ 2D

    public static float[][] eval(float b, float[][] a)
    {
        return eval(a, b);
    }

    public static float[][] eval(float[][] dest, float b, float[][] a)
    {
        return eval(dest, a, b);
    }


    /**
     * ************************
     */

//added for ints.

// 2D @ 1D
    public static int[][] eval(int[][] b, int[] a)
    {
        int[][] dest;
        dest = new int[b.length][b[0].length];
        return eval(dest, a, b);
    }

    public static int[][] eval(int[][] dest, int[][] b, int[] a)
    {
        return eval(dest, a, b); // return c;
    }

// 2D @ 0D

    public static int[][] eval(int[][] a, int b)
    {
        int[][] dest;
        dest = new int[a.length][a[0].length];
        return eval(dest, a, b);
    }

    // see nsl_mastrix Nslconv_zero(nsl_matrix& a, nsl_matrix& b)
    public static int[][] eval(int[][] dest, int[][] a, int b)
    {
        int sai = a.length;
        int saj = a[0].length;

        int i;
        int j;
        if (dest.length != sai || dest[0].length != saj)
        {
            dest = new int[sai][saj];
        }

        for (i = 0; i < sai; i++)
        {
            for (j = 0; j < saj; j++)
            {
                dest[i][j] = a[i][j] * b;
            }
        }

        return dest; // return c;
    }

// 0D @ 2D

    public static int[][] eval(int b, int[][] a)
    {
        return eval(a, b);
    }

    public static int[][] eval(int[][] dest, int b, int[][] a)
    {
        return eval(dest, a, b);
    }


    /**
     * ************************
     */

/* Added by Weifang */
    public static int eval(NslInt0 a, int b)
    {
        return eval(a.getint(), b);
    }

    public static int eval(int a, NslInt0 b)
    {
        return eval(a, b.getint());
    }

    public static int eval(NslInt0 a, NslInt0 b)
    {
        return eval(a.getint(), b.getint());
    }

    public static double eval(NslDouble0 a, double b)
    {
        return eval(a.getdouble(), b);
    }

    public static double eval(double a, NslDouble0 b)
    {
        return eval(a, b.getdouble());
    }

    public static double eval(NslDouble0 a, NslDouble0 b)
    {
        return eval(a.getdouble(), b.getdouble());
    }

// for floats

    public static float eval(NslFloat0 a, float b)
    {
        return eval(a.getfloat(), b);
    }

    public static float eval(float a, NslFloat0 b)
    {
        return eval(a, b.getfloat());
    }

    public static float eval(NslFloat0 a, NslFloat0 b)
    {
        return eval(a.getfloat(), b.getfloat());
    }


    /**
     * ***********************
     */

    public static int[] eval(int a, NslInt1 b)
    {
        return eval(a, b.getint1());
    }

    public static int[] eval(NslInt0 a, NslInt1 b)
    {
        return eval(a.getint(), b.getint1());
    }

    public static double[] eval(double a, NslDouble1 b)
    {
        return eval(a, b.getdouble1());
    }

    public static double[] eval(NslDouble0 a, NslDouble1 b)
    {
        return eval(a.getdouble(), b.getdouble1());
    }

    public static float[] eval(float a, NslFloat1 b)
    {
        return eval(a, b.getfloat1());
    }

    public static float[] eval(NslFloat0 a, NslFloat1 b)
    {
        return eval(a.getfloat(), b.getfloat1());
    }

    public static int[] eval(NslInt1 a, int b)
    {
        return eval(a.getint1(), b);
    }

    public static int[] eval(NslInt1 a, NslInt0 b)
    {
        return eval(a.getint1(), b.getint());
    }

    public static double[] eval(NslDouble1 a, double b)
    {
        return eval(a.getdouble1(), b);
    }

    public static double[] eval(NslDouble1 a, NslDouble0 b)
    {
        return eval(a.getdouble1(), b.getdouble());
    }

    public static float[] eval(NslFloat1 a, float b)
    {
        return eval(a.getfloat1(), b);
    }

    public static float[] eval(NslFloat1 a, NslFloat0 b)
    {
        return eval(a.getfloat1(), b.getfloat());
    }

    /**
     * *****************************
     */

    public static int[] eval(NslInt1 a, int[] b)
    {
        return eval(a.getint1(), b);
    }

    public static int[] eval(int[] dest, NslInt1 a, int[] b)
    {
        return eval(dest, a.getint1(), b);
    }

    public static int[] eval(int[] a, NslInt1 b)
    {
        return eval(a, b.getint1());
    }

    public static int[] eval(int[] dest, int[] a, NslInt1 b)
    {
        return eval(dest, a, b.getint1());
    }

    public static int[] eval(NslInt1 a, NslInt1 b)
    {
        return eval(a.getint1(), b.getint1());
    }

    public static int[] eval(int[] dest, NslInt1 a, NslInt1 b)
    {
        return eval(dest, a.getint1(), b.getint1());
    }

    public static double[] eval(NslDouble1 a, double[] b)
    {
        return eval(a.getdouble1(), b);
    }

    public static double[] eval(double[] dest, NslDouble1 a, double[] b)
    {
        return eval(dest, a.getdouble1(), b);
    }

    public static double[] eval(double[] a, NslDouble1 b)
    {
        return eval(a, b.getdouble1());
    }

    public static double[] eval(double[] dest, double[] a, NslDouble1 b)
    {
        return eval(dest, a, b.getdouble1());
    }

    public static double[] eval(NslDouble1 a, NslDouble1 b)
    {
        return eval(a.getdouble1(), b.getdouble1());
    }

    public static double[] eval(NslDouble1 a, NslDoutDouble1 b)
    {
        return eval(a.getdouble1(), b.getdouble1());
    }

    public static double[] eval(double[] dest, NslDouble1 a, NslDouble1 b)
    {
        return eval(dest, a.getdouble1(), b.getdouble1());
    }

    public static float[] eval(NslFloat1 a, float[] b)
    {
        return eval(a.getfloat1(), b);
    }

    public static float[] eval(float[] dest, NslFloat1 a, float[] b)
    {
        return eval(dest, a.getfloat1(), b);
    }

    public static float[] eval(float[] a, NslFloat1 b)
    {
        return eval(a, b.getfloat1());
    }

    public static float[] eval(float[] dest, float[] a, NslFloat1 b)
    {
        return eval(dest, a, b.getfloat1());
    }

    public static float[] eval(NslFloat1 a, NslFloat1 b)
    {
        return eval(a.getfloat1(), b.getfloat1());
    }

    public static float[] eval(float[] dest, NslFloat1 a, NslFloat1 b)
    {
        return eval(dest, a.getfloat1(), b.getfloat1());
    }

    /**
     * ********************************
     */

    public static int[][] eval(NslInt2 a, int[][] b)
    {
        return eval(a.getint2(), b);
    }

    public static int[][] eval(int[][] dest, NslInt2 a, int[][] b)
    {
        return eval(dest, a.getint2(), b);
    }

    public static int[][] eval(int[][] a, NslInt2 b)
    {
        return eval(a, b.getint2());
    }

    public static int[][] eval(int[][] dest, int[][] a, NslInt2 b)
    {
        return eval(dest, a, b.getint2());
    }

    public static int[][] eval(NslInt2 a, NslInt2 b)
    {
        return eval(a.getint2(), b.getint2());
    }

    public static int[][] eval(int[][] dest, NslInt2 a, NslInt2 b)
    {
        return eval(dest, a.getint2(), b.getint2());
    }

    /**
     * ********************************
     */

    public static int[][][] eval(NslInt3 a, int[][][] b)
    {
        return eval(a.getint3(), b);
    }

    public static int[][][] eval(int[][][] dest, NslInt3 a, int[][][] b)
    {
        return eval(dest, a.getint3(), b);
    }

    public static int[][][] eval(int[][][] a, NslInt3 b)
    {
        return eval(a, b.getint3());
    }

    public static int[][][] eval(int[][][] dest, int[][][] a, NslInt3 b)
    {
        return eval(dest, a, b.getint3());
    }

    public static int[][][] eval(NslInt3 a, NslInt3 b)
    {
        return eval(a.getint3(), b.getint3());
    }

    public static int[][][] eval(int[][][] dest, NslInt3 a, NslInt3 b)
    {
        return eval(dest, a.getint3(), b.getint3());
    }

/*&&*/

    public static double[][] eval(NslDouble2 a, double[][] b)
    {
        return eval(a.getdouble2(), b);
    }

    public static double[][] eval(double[][] dest, NslDouble2 a, double[][] b)
    {
        return eval(dest, a.getdouble2(), b);
    }

    public static double[][] eval(double[][] a, NslDouble2 b)
    {
        return eval(a, b.getdouble2());
    }

    public static double[][] eval(double[][] dest, double[][] a, NslDouble2 b)
    {
        return eval(dest, a, b.getdouble2());
    }

    public static double[][] eval(NslDouble2 a, NslDouble2 b)
    {
        return eval(a.getdouble2(), b.getdouble2());
    }

    public static double[][] eval(double[][] dest, NslDouble2 a, NslDouble2 b)
    {
        return eval(dest, a.getdouble2(), b.getdouble2());
    }

    /*&&*/

    public static double[][][] eval(NslDouble3 a, double[][][] b)
    {
        return eval(a.getdouble3(), b);
    }

    public static double[][][] eval(double[][][] dest, NslDouble3 a, double[][][] b)
    {
        return eval(dest, a.getdouble3(), b);
    }

    public static double[][][] eval(double[][][] a, NslDouble3 b)
    {
        return eval(a, b.getdouble3());
    }

    public static double[][][] eval(double[][][] dest, double[][][] a, NslDouble3 b)
    {
        return eval(dest, a, b.getdouble3());
    }

    public static double[][][] eval(NslDouble3 a, NslDouble3 b)
    {
        return eval(a.getdouble3(), b.getdouble3());
    }

    public static double[][][] eval(double[][][] dest, NslDouble3 a, NslDouble3 b)
    {
        return eval(dest, a.getdouble3(), b.getdouble3());
    }

/*&*/

    public static float[][] eval(NslFloat2 a, float[][] b)
    {
        return eval(a.getfloat2(), b);
    }

    public static float[][] eval(float[][] dest, NslFloat2 a, float[][] b)
    {
        return eval(dest, a.getfloat2(), b);
    }

    public static float[][] eval(float[][] a, NslFloat2 b)
    {
        return eval(a, b.getfloat2());
    }

    public static float[][] eval(float[][] dest, float[][] a, NslFloat2 b)
    {
        return eval(dest, a, b.getfloat2());
    }

    public static float[][] eval(NslFloat2 a, NslFloat2 b)
    {
        return eval(a.getfloat2(), b.getfloat2());
    }

    public static float[][] eval(float[][] dest, NslFloat2 a, NslFloat2 b)
    {
        return eval(dest, a.getfloat2(), b.getfloat2());
    }

    /*&*/

    public static float[][][] eval(NslFloat3 a, float[][][] b)
    {
        return eval(a.getfloat3(), b);
    }

    public static float[][][] eval(float[][][] dest, NslFloat3 a, float[][][] b)
    {
        return eval(dest, a.getfloat3(), b);
    }

    public static float[][][] eval(float[][][] a, NslFloat3 b)
    {
        return eval(a, b.getfloat3());
    }

    public static float[][][] eval(float[][][] dest, float[][][] a, NslFloat3 b)
    {
        return eval(dest, a, b.getfloat3());
    }

    public static float[][][] eval(NslFloat3 a, NslFloat3 b)
    {
        return eval(a.getfloat3(), b.getfloat3());
    }

    public static float[][][] eval(float[][][] dest, NslFloat3 a, NslFloat3 b)
    {
        return eval(dest, a.getfloat3(), b.getfloat3());
    }

    /**
     * ***********************************
     */

// 1D @ 2D  
    public static int[][] eval(NslInt1 a, int[][] b)
    {
        return eval(a.getint1(), b);
    }

    public static int[][] eval(int[][] dest, NslInt1 a, int[][] b)
    {
        return eval(dest, a.getint1(), b);
    }

    public static int[][] eval(int[] a, NslInt2 b)
    {
        return eval(a, b.getint2());
    }

    public static int[][] eval(int[][] dest, int[] a, NslInt2 b)
    {
        return eval(dest, a, b.getint2());
    }

    public static int[][] eval(NslInt1 a, NslInt2 b)
    {
        return eval(a.getint1(), b.getint2());
    }

    public static int[][] eval(int[][] dest, NslInt1 a, NslInt2 b)
    {
        return eval(dest, a.getint1(), b.getint2());
    }

/*&&*/

    public static double[][] eval(NslDouble1 a, double[][] b)
    {
        return eval(a.getdouble1(), b);
    }

    public static double[][] eval(double[][] dest, NslDouble1 a, double[][] b)
    {
        return eval(dest, a.getdouble1(), b);
    }

    public static double[][] eval(double[] a, NslDouble2 b)
    {
        return eval(a, b.getdouble2());
    }

    public static double[][] eval(double[][] dest, double[] a, NslDouble2 b)
    {
        return eval(dest, a, b.getdouble2());
    }

    public static double[][] eval(NslDouble1 a, NslDouble2 b)
    {
        return eval(a.getdouble1(), b.getdouble2());
    }

    public static double[][] eval(double[][] dest, NslDouble1 a, NslDouble2 b)
    {
        return eval(dest, a.getdouble1(), b.getdouble2());
    }

/*&&*/

    public static float[][] eval(NslFloat1 a, float[][] b)
    {
        return eval(a.getfloat1(), b);
    }

    public static float[][] eval(float[][] dest, NslFloat1 a, float[][] b)
    {
        return eval(dest, a.getfloat1(), b);
    }

    public static float[][] eval(float[] a, NslFloat2 b)
    {
        return eval(a, b.getfloat2());
    }

    public static float[][] eval(float[][] dest, float[] a, NslFloat2 b)
    {
        return eval(dest, a, b.getfloat2());
    }

    public static float[][] eval(NslFloat1 a, NslFloat2 b)
    {
        return eval(a.getfloat1(), b.getfloat2());
    }

    public static float[][] eval(float[][] dest, NslFloat1 a, NslFloat2 b)
    {
        return eval(dest, a.getfloat1(), b.getfloat2());
    }

    /**
     * **************************************
     */

// 2D @ 1D
    public static int[][] eval(NslInt2 a, int[] b)
    {
        return eval(a.getint2(), b);
    }

    public static int[][] eval(int[][] dest, NslInt2 a, int[] b)
    {
        return eval(dest, a.getint2(), b);
    }

    public static int[][] eval(int[][] a, NslInt1 b)
    {
        return eval(a, b.getint1());
    }

    public static int[][] eval(int[][] dest, int[][] a, NslInt1 b)
    {
        return eval(dest, a, b.getint1());
    }

    public static int[][] eval(NslInt2 a, NslInt1 b)
    {
        return eval(a.getint2(), b.getint1());
    }

    public static int[][] eval(int[][] dest, NslInt2 a, NslInt1 b)
    {
        return eval(dest, a.getint2(), b.getint1());
    }

/*&&*/

    public static double[][] eval(NslDouble2 a, double[] b)
    {
        return eval(a.getdouble2(), b);
    }

    public static double[][] eval(double[][] dest, NslDouble2 a, double[] b)
    {
        return eval(dest, a.getdouble2(), b);
    }

    public static double[][] eval(double[][] a, NslDouble1 b)
    {
        return eval(a, b.getdouble1());
    }

    public static double[][] eval(double[][] dest, double[][] a, NslDouble1 b)
    {
        return eval(dest, a, b.getdouble1());
    }

    public static double[][] eval(NslDouble2 a, NslDouble1 b)
    {
        return eval(a.getdouble2(), b.getdouble1());
    }

    public static double[][] eval(double[][] dest, NslDouble2 a, NslDouble1 b)
    {
        return eval(dest, a.getdouble2(), b.getdouble1());
    }

/*&&*/

    public static float[][] eval(NslFloat2 a, float[] b)
    {
        return eval(a.getfloat2(), b);
    }

    public static float[][] eval(float[][] dest, NslFloat2 a, float[] b)
    {
        return eval(dest, a.getfloat2(), b);
    }

    public static float[][] eval(float[][] a, NslFloat1 b)
    {
        return eval(a, b.getfloat1());
    }

    public static float[][] eval(float[][] dest, float[][] a, NslFloat1 b)
    {
        return eval(dest, a, b.getfloat1());
    }

    public static float[][] eval(NslFloat2 a, NslFloat1 b)
    {
        return eval(a.getfloat2(), b.getfloat1());
    }

    public static float[][] eval(float[][] dest, NslFloat2 a, NslFloat1 b)
    {
        return eval(dest, a.getfloat2(), b.getfloat1());
    }

    /**
     * *******************************
     */

// 2D @ 0D
    public static int[][] eval(NslInt2 a, int b)
    {
        return eval(a.getint2(), b);
    }

    public static int[][] eval(int[][] dest, NslInt2 a, int b)
    {
        return eval(dest, a.getint2(), b);
    }

    public static int[][] eval(int[][] a, NslInt0 b)
    {
        return eval(a, b.getint());
    }

    public static int[][] eval(int[][] dest, int[][] a, NslInt0 b)
    {
        return eval(dest, a, b.getint());
    }

    public static int[][] eval(NslInt2 a, NslInt0 b)
    {
        return eval(a.getint2(), b.getint());
    }

    public static int[][] eval(int[][] dest, NslInt2 a, NslInt0 b)
    {
        return eval(dest, a.getint2(), b.getint());
    }

/*&&*/

    public static double[][] eval(NslDouble2 a, double b)
    {
        return eval(a.getdouble2(), b);
    }

    public static double[][] eval(double[][] dest, NslDouble2 a, double b)
    {
        return eval(dest, a.getdouble2(), b);
    }

    public static double[][] eval(double[][] a, NslDouble0 b)
    {
        return eval(a, b.getdouble());
    }

    public static double[][] eval(double[][] dest, double[][] a, NslDouble0 b)
    {
        return eval(dest, a, b.getdouble());
    }

    public static double[][] eval(NslDouble2 a, NslDouble0 b)
    {
        return eval(a.getdouble2(), b.getdouble());
    }

    public static double[][] eval(double[][] dest, NslDouble2 a, NslDouble0 b)
    {
        return eval(dest, a.getdouble2(), b.getdouble());
    }

/*&&*/

    public static float[][] eval(NslFloat2 a, float b)
    {
        return eval(a.getfloat2(), b);
    }

    public static float[][] eval(float[][] dest, NslFloat2 a, float b)
    {
        return eval(dest, a.getfloat2(), b);
    }

    public static float[][] eval(float[][] a, NslFloat0 b)
    {
        return eval(a, b.getfloat());
    }

    public static float[][] eval(float[][] dest, float[][] a, NslFloat0 b)
    {
        return eval(dest, a, b.getfloat());
    }

    public static float[][] eval(NslFloat2 a, NslFloat0 b)
    {
        return eval(a.getfloat2(), b.getfloat());
    }

    public static float[][] eval(float[][] dest, NslFloat2 a, NslFloat0 b)
    {
        return eval(dest, a.getfloat2(), b.getfloat());
    }

    /**
     * ************************************
     */

// 0D @ 2D
    public static int[][] eval(NslInt0 a, int[][] b)
    {
        return eval(a.getint(), b);
    }

    public static int[][] eval(int[][] dest, NslInt0 a, int[][] b)
    {
        return eval(dest, a.getint(), b);
    }

    public static int[][] eval(int a, NslInt2 b)
    {
        return eval(a, b.getint2());
    }

    public static int[][] eval(int[][] dest, int a, NslInt2 b)
    {
        return eval(dest, a, b.getint2());
    }

    public static int[][] eval(NslInt0 a, NslInt2 b)
    {
        return eval(a.getint(), b.getint2());
    }

    public static int[][] eval(int[][] dest, NslInt0 a, NslInt2 b)
    {
        return eval(dest, a.getint(), b.getint2());
    }

/*&&*/

    public static double[][] eval(NslDouble0 a, double[][] b)
    {
        return eval(a.getdouble(), b);
    }

    public static double[][] eval(double[][] dest, NslDouble0 a, double[][] b)
    {
        return eval(dest, a.getdouble(), b);
    }

    public static double[][] eval(double a, NslDouble2 b)
    {
        return eval(a, b.getdouble2());
    }

    public static double[][] eval(double[][] dest, double a, NslDouble2 b)
    {
        return eval(dest, a, b.getdouble2());
    }

    public static double[][] eval(NslDouble0 a, NslDouble2 b)
    {
        return eval(a.getdouble(), b.getdouble2());
    }

    public static double[][] eval(double[][] dest, NslDouble0 a, NslDouble2 b)
    {
        return eval(dest, a.getdouble(), b.getdouble2());
    }

/*&&*/

    public static float[][] eval(NslFloat0 a, float[][] b)
    {
        return eval(a.getfloat(), b);
    }

    public static float[][] eval(float[][] dest, NslFloat0 a, float[][] b)
    {
        return eval(dest, a.getfloat(), b);
    }

    public static float[][] eval(float a, NslFloat2 b)
    {
        return eval(a, b.getfloat2());
    }

    public static float[][] eval(float[][] dest, float a, NslFloat2 b)
    {
        return eval(dest, a, b.getfloat2());
    }

    public static float[][] eval(NslFloat0 a, NslFloat2 b)
    {
        return eval(a.getfloat(), b.getfloat2());
    }

    public static float[][] eval(float[][] dest, NslFloat0 a, NslFloat2 b)
    {
        return eval(dest, a.getfloat(), b.getfloat2());
  }


/**********************************/

}




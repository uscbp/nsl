package nslj.src.system;

import nslj.src.lang.NslString0;

/**
 * Nsl-Java - Matlab interface for evaluating String expressions in Matlab and returning the
 * results
 */
public class NslMatlab
{
    /**
     * Evaluations the given expression in matlab
     * Prepends "nslReserved=" to the expression in order to ensure that the result can be returned. Do not
     * use nslReserved in the expression. If the expression is an assigment, it is broken into two expressions so that
     * the assigned variable=nslReserved
     * @param expression - The expression to evaluate
     */
    private static void evalExpression(String expression)
    {
        StringBuilder str=new StringBuilder("nslReserved=");
        String varName=null;
        if(expression.indexOf('=')>0 && expression.charAt(expression.indexOf('=')-1)!='~' &&
                expression.charAt(expression.indexOf('=')+1)!='=')
        {
            varName=expression.substring(0,expression.indexOf('='));
            expression=expression.substring(expression.indexOf('=')+1);
        }
        str.append(expression);
        str.append(';');
        if(varName !=null)
        {
            str.append(varName);
            str.append("=nslReserved;");
        }
        NslSystem.getMatlabEngine().engEvalString(str.toString());
    }

    /**
     * Evaluates the given expression in Matlab and returns the result as a scalar
     * @param expression - The expression to evaluate
     * @return - The result as a double
     */
    public static double evalScalar(String expression)
    {
        evalExpression(expression);
        return NslSystem.getMatlabEngine().engGetScalar("nslReserved");
    }

    /**
     * Evaluates the given expression in Matlab and returns the result as a vector
     * @param expression - The expression to evaluate
     * @return - The result as a vector of doubles
     */
    public static double[] evalVector(String expression)
    {
        evalExpression(expression);
        double[] ans=new double[0];
        double[][] tmp=NslSystem.getMatlabEngine().engGetArray("nslReserved");
        if(tmp.length>1)
        {
            ans=new double[tmp.length];
            for(int i=0; i<tmp.length; i++)
            {
                if(tmp[i].length>0)
                    ans[i]=tmp[i][0];
            }
        }
        else if(tmp.length==1)
        {
            ans=new double[tmp[0].length];
            System.arraycopy(tmp[0],0,ans,0,tmp[0].length);
        }
        return ans;
    }

    /**
     * Evaluates the given expression in Matlab and returns the result as a matrix
     * @param expression - The expression to evaluate
     * @return - The result as a matrix of doubles
     */
    public static double[][] evalMatrix(String expression)
    {
        evalExpression(expression);
        return NslSystem.getMatlabEngine().engGetArray("nslReserved");
    }

    /**
     * Evaluates the given expression in Matlab and returns the result as a scalar
     * @param expression - The expression to evaluate
     * @return - The result as a double
     */
    public static double evalScalar(NslString0 expression)
    {
        return evalScalar(expression.getstring());
    }

    /**
     * Evaluates the given expression in Matlab and returns the result as a vector
     * @param expression - The expression to evaluate
     * @return - The result as a vector of doubles
     */
    public static double[] evalVector(NslString0 expression)
    {
        return evalVector(expression.getstring());
    }

    /**
     * Evaluates the given expression in Matlab and returns the result as a matrix
     * @param expression - The expression to evaluate
     * @return - The result as a matrix of doubles
     */
    public static double[][] evalMatrix(NslString0 expression)
    {
        return evalMatrix(expression.getstring());
    }
}

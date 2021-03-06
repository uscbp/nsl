package nslc.src;/* Generated By:JJTree: Do not edit this line. nslc.src.ASTMultiplicativeExpression.java */

/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

import java.io.PrintStream;

public class ASTMultiplicativeExpression extends ASTExpression
{
    public ASTMultiplicativeExpression(int id)
    {
        super(id);
    }

    public ASTMultiplicativeExpression(NslParser p, int id)
    {
        super(p, id);
    }

    /**
     * Accept the visitor. *
     */
    public Object jjtAccept(NslParserVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }

    public String getOperator(int child)
    {
        int childNum = jjtGetNumChildren();

        if (childNum < 2)
        {
            return "";
        }

        ASTExpression oper = (ASTExpression) jjtGetChild(child - 1);
        return oper.getLastToken().next.image;
    }

    public static String getNslProductHeader(boolean isNsl, String temp)
    {
        String assign = (isNsl ? ".setReference(" : " = (");
        String get = (isNsl ? ".get(), " : ", ");
        return temp + assign + "NslProduct.eval(" + temp + get;
    }

    public static String getNslElemMultHeader(boolean isNsl, String temp)
    {
        String assign = (isNsl ? ".setReference(" : " = (");
        String get = (isNsl ? ".get(), " : ", ");
        return temp + assign + "NslElemMult.eval(" + temp + get;
    }

    public static String getNslElemDivHeader(boolean isNsl, String temp)
    {
        String assign = (isNsl ? ".setReference(" : " = (");
        String get = (isNsl ? ".get(), " : ", ");
        return temp + assign + "NslElemDiv.eval(" + temp + get;
    }

    public static String getNslConvHeader(boolean isNsl, String temp)
    {
        String assign = (isNsl ? ".setReference(" : " = (");
        String get = (isNsl ? ".get(), " : ", ");
        return temp + assign + "NslConv.eval(" + temp + get;
    }

    public static boolean canApplyProduct(int dim1, int dim2)
    {
        return dim1 >= 1 && dim1 <= 2 && dim2 >= 1 && dim2 <= 2 && !(dim1 == dim2 && dim1 == 1);
    }

    public static boolean canApplyElemMult(String type1, String type2)
    {
        return ((type1.toLowerCase().indexOf("int") >= 0) && (type2.toLowerCase().indexOf("int") >= 0)) ||
                ((type1.toLowerCase().indexOf("float") >= 0) && (type2.toLowerCase().indexOf("float") >= 0)) ||
                ((type1.toLowerCase().indexOf("double") >= 0) && (type2.toLowerCase().indexOf("double") >= 0)) ||
                ((type1.toLowerCase().indexOf("boolean") >= 0) && (type2.toLowerCase().indexOf("boolean") >= 0));
    }

    public static boolean canApplyElemProduct(String type1, String type2)
    {
        return canApplyElemMult(type1, type2);
    }

    public static boolean canApplyElemDiv(String type1, String type2)
    {
        return canApplyElemMult(type1, type2);
    }

    public static boolean canApplyConv(String type1, String type2)
    {
        return canApplyElemMult(type1, type2);
    }

    public static boolean canApplyElemMult(int dim1, int dim2)
    {
        return dim1 == dim2 || dim1 == 0 || dim2 == 0;
    }

    public static boolean canApplyElemDiv(int dim1, int dim2)
    {
        return canApplyElemMult(dim1, dim2);
    }

    public static boolean canApplyConv(int dim1, int dim2)
    {
        return dim1 >= 0 && dim1 <= 2 && dim2 >= 0 && dim2 <= 2 && !(dim1 == dim2 && dim1 == 0);
    }

    public static String getMultType(String type1, String type2)
    {
        if (type1.equals("double") || type2.equals("double"))
        {
            return "double";
        }
        else if (type1.equals("float") || type2.equals("float"))
        {
            return "float";
        }
        else
        {
            return type1;
        }
    }

    public static String getDivType(String type1, String type2)
    {
        return getMultType(type1, type2);
    }

    public static String getProductType(NslScope scope, String type1, String type2)
    {

        if (scope.isNslType(type1))
        {
            return type1;
        }
        else if (scope.isNslType(type2))
        {
            return type2.substring(0, type2.length() - 1) + scope.getArrayDim(type1);
        }
        else
        {
            return type1;
        }
    }

    public static String getConvType(NslScope scope, String type1, String type2)
    {
        int dim1, dim2;
        dim1 = scope.getDim(type1);
        dim2 = scope.getDim(type2);

        if (scope.isNslType(type1))
        {
            if (dim1 >= dim2)
            {
                return type1;
            }
            else
            {
                return type2.substring(0, type2.length() - 1) + scope.getArrayDim(type1);
            }
        }
        else if (scope.isNslType(type2))
        {
            if (dim2 >= dim1)
            {
                return type2;
            }
            else
            {
                return type1.substring(0, type1.length() - 1) + scope.getArrayDim(type2);
            }
        }
        else if (dim1 >= dim2)
        {
            return type1;
        }
        else
        {
            return type2;
        }
    }

    public static String getElemMultType(NslScope scope, String type1, String type2)
    {
        if (scope.getDim(type1) == 0 && scope.getDim(type2) != 0)
        {
            if (scope.isNslType(type1))
            {
                if (scope.isNslType(type2))
                {
                    return type2;
                }
                else
                {
                    return "Nsl" + type2.substring(0, 1).toUpperCase() + type2.substring(1, type2.indexOf("[")) + scope.getArrayDim(type2);
                }
            }
            else
            {
                return type2;
            }
        }
        else if (scope.getDim(type2) == 0 && scope.getDim(type1) != 0)
        {
            if (scope.isNslType(type2))
            {
                if (scope.isNslType(type1))
                {
                    return type1;
                }
                else
                {
                    return "Nsl" + type1.substring(0, 1).toUpperCase() + type1.substring(1, type1.indexOf("[")) + scope.getArrayDim(type1);
                }
            }
            else
            {
                return type1;
            }
        }
        else
        {
            if (scope.isNslType(type1))
            {
                return type1;
            }
            else if (scope.isNslType(type2))
            {
                return type2;
            }
            else
            {
                return type1;
            }
        }
    }

    public static String getElemDivType(NslScope scope, String type1, String type2)
    {
        return getElemMultType(scope, type1, type2);
    }

    public String getExpressionType()
    {
        int childNum = jjtGetNumChildren();

        if (childNum < 2)
        {
            return super.getExpressionType();
        }
        return expressionType;
    }

    public String toJava(NslScope scope)
    {

        int childNum = jjtGetNumChildren();

        if (childNum < 2)
        {
            return "";
        }

        String type1, type2;
        StringBuilder mults;
        String operator;
        ASTExpression oper;
        int dim1, dim2;
        boolean isArray2, isArray1;

        oper = (ASTExpression) jjtGetChild(0);
        type1 = oper.getExpressionType();
        dim1 = scope.getDim(type1);
        isArray1 = dim1 > 0;
        mults = new StringBuilder(oper.getExpression());

        for (int i = 1; i < childNum; i++)
        {
            oper = (ASTExpression) jjtGetChild(i);
            type2 = oper.getExpressionType();
            dim2 = scope.getDim(type2);
            isArray2 = dim2 > 0;

            operator = getOperator(i);

            if (operator.equals("*"))
            {

                if (((isArray1 || isArray2) && !canApplyElemProduct(type1, type2)))
                {
                    NslCompiler.printError("nslc.src.ASTMultiplicativeExpression", "Trying to multiply non compatible types: " + type1 + " and " + type2, oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else if ((isArray1 || isArray2) && canApplyProduct(dim1, dim2))
                {
                    type1 = getProductType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                    String t=mults.toString();
                    mults = new StringBuilder(getNslProductHeader(scope.isNslType(type1), scope.getTempName(type1)));
                    mults.append(t);
                    mults.append(", ");
                    mults.append(oper.getExpression());
                    mults.append("))");
                }
                else if (((isArray1 || isArray2) && canApplyElemMult(dim1, dim2)))
                {
                    type1 = getElemMultType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                    String t=mults.toString();
                    mults = new StringBuilder(getNslElemMultHeader(scope.isNslType(type1), scope.getTempName(type1)));
                    mults.append(t);
                    mults.append(", ");
                    mults.append(oper.getExpression());
                    mults.append("))");
                }
                else if (isArray1 || isArray2)
                {
                    NslCompiler.printError("nslc.src.ASTMultiplicativeExpression", "Trying to multiply wrong dimensions", oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else
                {
                    // Scalar multiplication
                    String ext1 = "", ext2 = "", wrapStart = "", wrapEnd = "", temp;

                    if (scope.isNslType(type1) || scope.isNslType(type2))
                    {
                        if (scope.isNslType(type1))
                        {
                            ext1 = ".get()";
                        }
                        if (scope.isNslType(type2))
                        {
                            type1 = type2;
                            ext2 = ".get()";
                        }

                        temp = scope.getTempName(type1);
                        wrapStart = temp + ".setReference(";
                        wrapEnd = ")";

                    }
                    else
                    {
                        // Simple multiplication
                        type1 = getMultType(type1, type2);
                    }
                    dim1 = 0;
                    isArray1 = false;
                    String t=mults.toString();
                    mults = new StringBuilder(wrapStart);
                    mults.append(t);
                    mults.append(ext1);
                    mults.append(operator);
                    mults.append(oper.getExpression());
                    mults.append(ext2);
                    mults.append(wrapEnd);
                }

            }
            else if (operator.equals("/"))
            {

                if (((isArray1 || isArray2) && !canApplyElemDiv(type1, type2)))
                {
                    NslCompiler.printError("nslc.src.ASTMultiplicativeExpression", "Trying to divide non compatible types: " + type1 + " and " + type2, oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else if (((isArray1 || isArray2) && canApplyElemDiv(dim1, dim2)))
                {
                    type1 = getElemDivType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                    String t=mults.toString();
                    mults = new StringBuilder(getNslElemDivHeader(scope.isNslType(type1), scope.getTempName(type1)));
                    mults.append(t);
                    mults.append(", ");
                    mults.append(oper.getExpression());
                    mults.append("))");
                }
                else if (isArray1 || isArray2)
                {
                    NslCompiler.printError("nslc.src.ASTMultiplicativeExpression", "Trying to divide wrong dimensions", oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else
                {
                    // Scalar division
                    String ext1 = "", ext2 = "", wrapStart = "", wrapEnd = "", temp;

                    if (scope.isNslType(type1) || scope.isNslType(type2))
                    {

                        if (scope.isNslType(type1))
                        {
                            ext1 = ".get()";
                        }
                        if (scope.isNslType(type2))
                        {
                            type1 = type2;
                            ext2 = ".get()";
                        }

                        temp = scope.getTempName(type1);
                        wrapStart = temp + ".setReference(";
                        wrapEnd = ")";

                    }
                    else
                    {
                        // Simple division
                        type1 = getDivType(type1, type2);
                    }
                    dim1 = 0;
                    isArray1 = false;
                    String t=mults.toString();
                    mults = new StringBuilder(wrapStart);
                    mults.append(t);
                    mults.append(ext1);
                    mults.append(operator);
                    mults.append(oper.getExpression());
                    mults.append(ext2);
                    mults.append(wrapEnd);
                }
            }
            else if (operator.equals("@"))
            {

                if (((isArray1 || isArray2) && !canApplyConv(type1, type2)))
                {
                    NslCompiler.printError("nslc.src.ASTMultiplicativeExpression", "Trying to convolve non compatible types: " + type1 + " and " + type2, oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                if (canApplyConv(dim1, dim2))
                {
                    type1 = getConvType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                    String t=mults.toString();
                    mults = new StringBuilder(getNslConvHeader(scope.isNslType(type1), scope.getTempName(type1)));
                    mults.append(t);
                    mults.append(", ");
                    mults.append(oper.getExpression());
                    mults.append("))");
                }
                else if (isArray1 || isArray2)
                {
                    NslCompiler.printError("nslc.src.ASTMultiplicativeExpression", "Trying to convolve wrong dimensions", oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else
                {
                    // Scalar multiplication
                    String ext1 = "", ext2 = "", wrapStart = "", wrapEnd = "", temp;

                    if (scope.isNslType(type1) || scope.isNslType(type2))
                    {

                        if (scope.isNslType(type1))
                        {
                            ext1 = ".get()";
                        }
                        if (scope.isNslType(type2))
                        {
                            type1 = type2;
                            ext2 = ".get()";
                        }

                        temp = scope.getTempName(type1);
                        wrapStart = temp + ".setReference(";
                        wrapEnd = ")";

                    }
                    else
                    {
                        // Simple multiplication
                        type1 = getMultType(type1, type2);
                    }
                    dim1 = 0;
                    isArray1 = false;
                    String t=mults.toString();
                    mults = new StringBuilder(wrapStart);
                    mults.append(t);
                    mults.append(ext1);
                    mults.append('*');
                    mults.append(oper.getExpression());
                    mults.append(ext2);
                    mults.append(wrapEnd);
                }
            }
            else
            {
                // Scalar multiplication
                String ext1 = "", ext2 = "", wrapStart = "", wrapEnd = "", temp;

                if (scope.isNslType(type1) || scope.isNslType(type2))
                {

                    if (scope.isNslType(type1))
                    {
                        ext1 = ".get()";
                    }
                    if (scope.isNslType(type2))
                    {
                        type1 = type2;
                        ext2 = ".get()";
                    }

                    temp = scope.getTempName(type1);
                    wrapStart = temp + ".setReference(";
                    wrapEnd = ")";

                }
                else
                {
                    // Simple multiplication
                    type1 = getMultType(type1, type2);
                }
                dim1 = 0;
                isArray1 = false;
                String t=mults.toString();
                mults = new StringBuilder(wrapStart);
                mults.append(t);
                mults.append(ext1);
                mults.append(operator);
                mults.append(oper.getExpression());
                mults.append(ext2);
                mults.append(wrapEnd);
            }
        }

        expressionType = type1;

        first.image = mults.toString();
        last.image = "";
        first.next = last;

        return "";
    }

    public String computeType(NslScope scope)
    {

        int childNum = jjtGetNumChildren();

        if (childNum < 2)
        {
            return "";
        }

        String type1, type2;
        String operator;
        ASTExpression oper;
        int dim1, dim2;
        boolean isArray2, isArray1;

        oper = (ASTExpression) jjtGetChild(0);
        type1 = oper.getExpressionType();
        dim1 = scope.getDim(type1);
        isArray1 = dim1 > 0;

        for (int i = 1; i < childNum; i++)
        {
            oper = (ASTExpression) jjtGetChild(i);
            type2 = oper.getExpressionType();
            dim2 = scope.getDim(type2);
            isArray2 = dim2 > 0;

            operator = getOperator(i);

            if (operator.equals("*"))
            {

                if (((isArray1 || isArray2) && !canApplyElemProduct(type1, type2)))
                {
                    NslCompiler.printError("nslc.src.ASTMultiplicativeExpression", "Trying to multiply non compatible types: " + type1 + " and " + type2, oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else if ((isArray1 || isArray2) && canApplyProduct(dim1, dim2))
                {
                    type1 = getProductType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                }
                else if (((isArray1 || isArray2) && canApplyElemMult(dim1, dim2)))
                {
                    type1 = getElemMultType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                }
                else if (isArray1 || isArray2)
                {
                    NslCompiler.printError("nslc.src.ASTMultiplicativeExpression", "Trying to multoply wrong dimensions", oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else
                {
                    // Scalar multiplication

                    if (scope.isNslType(type1) || scope.isNslType(type2))
                    {

                        if (scope.isNslType(type2))
                        {
                            type1 = type2;
                        }

                    }
                    else
                    {
                        // Simple multiplication
                        type1 = getMultType(type1, type2);
                    }
                    dim1 = 0;
                    isArray1 = false;
                }

            }
            else if (operator.equals("/"))
            {

                if (((isArray1 || isArray2) && !canApplyElemDiv(type1, type2)))
                {
                    NslCompiler.printError("nslc.src.ASTMultiplicativeExpression", "Trying to divide non compatible types: " + type1 + " and " + type2, oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else if (((isArray1 || isArray2) && canApplyElemDiv(dim1, dim2)))
                {
                    type1 = getElemDivType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                }
                else if (isArray1 || isArray2)
                {
                    NslCompiler.printError("nslc.src.ASTMultiplicativeExpression", "Trying to divide wrong dimensions", oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else
                {
                    // Scalar division

                    if (scope.isNslType(type1) || scope.isNslType(type2))
                    {

                        if (scope.isNslType(type2))
                        {
                            type1 = type2;
                        }

                    }
                    else
                    {
                        // Simple division
                        type1 = getDivType(type1, type2);
                    }
                    dim1 = 0;
                    isArray1 = false;
                }

            }
            else if (operator.equals("@"))
            {

                if (((isArray1 || isArray2) && !canApplyConv(type1, type2)))
                {
                    NslCompiler.printError("nslc.src.ASTMultiplicativeExpression", "Trying to convolve non compatible types: " + type1 + " and " + type2, oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else if (canApplyConv(dim1, dim2))
                {
                    type1 = getConvType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                }
                else if (isArray1 || isArray2)
                {
                    NslCompiler.printError("nslc.src.ASTMultiplicativeExpression", "Trying to convolve wrong dimensions", oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else
                {
                    // Scalar multiplication

                    if (scope.isNslType(type1) || scope.isNslType(type2))
                    {

                        if (scope.isNslType(type2))
                        {
                            type1 = type2;
                        }

                    }
                    else
                    {
                        // Simple multiplication
                        type1 = getMultType(type1, type2);
                    }
                    dim1 = 0;
                    isArray1 = false;
                }
            }
            else
            {
                // Scalar multiplication

                if (scope.isNslType(type1) || scope.isNslType(type2))
                {
                    if (scope.isNslType(type2))
                    {
                        type1 = type2;
                    }
                }
                else
                {
                    // Simple multiplication
                    type1 = getMultType(type1, type2);
                }
                dim1 = 0;
                isArray1 = false;
            }
        }

        expressionType = type1;

        return "";
    }

    public void dumpXML(String prefix, PrintStream out)
    {
        String xml = toXMLOpen(prefix);
        if (xml != null)
        {
            out.println(xml);
        }
        if (children != null)
        {
            SimpleNode n = (SimpleNode) children[0];
            if (n != null)
            {
                n.dumpXML(prefix + ' ', out);
            }
            for (int i = 1; i < children.length; ++i)
            {
                n = (SimpleNode) children[i];
                if (n != null)
                {
                    xml = "<Operator>" + getOperator(i);
                    out.println(prefix + ' ' + xml);
                    xml = "</Operator>";
                    out.println(prefix + ' ' + xml);
                    n.dumpXML(prefix + ' ', out);
                }
            }
        }
        xml = toXMLClose(prefix);
        if (xml != null)
        {
            out.println(xml);
        }
    }

}

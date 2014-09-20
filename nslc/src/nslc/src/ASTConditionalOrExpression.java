package nslc.src;/* Generated By:JJTree: Do not edit this line. nslc.src.ASTConditionalOrExpression.java */

/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

public class ASTConditionalOrExpression extends ASTExpression
{
    public ASTConditionalOrExpression(int id)
    {
        super(id);
    }

    public ASTConditionalOrExpression(NslParser p, int id)
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

    public static boolean canApplyElemOr(int dim1, int dim2)
    {
        return dim1 == dim2 || dim1 == 0 || dim2 == 0;
    }

    public static boolean canApplyElemOr(String type1, String type2)
    {
        return ((type1.toLowerCase().indexOf("boolean") >= 0) && (type2.toLowerCase().indexOf("boolean") >= 0));
    }

    public static String getNslElemOrHeader(boolean isNsl, String temp)
    {
        String assign = (isNsl ? ".setReference(" : " = (");
        String get = (isNsl ? ".get(), " : ", ");
        return temp + assign + "NslOr.eval(" + temp + get;
    }

    public static String getOrType()
    {
        return "boolean";
    }

    public static String getElemOrType(NslScope scope, String type1, String type2)
    {

        if (scope.getDim(type1) == 0 && scope.getDim(type2) != 0)
        {
            if (scope.isNslType(type2))
            {
                return "NslBoolean" + scope.getDim(type2);
            }
            else
            {
                return "boolean" + type2.substring(type2.indexOf("["));
            }
        }
        else if (scope.getDim(type2) == 0 && scope.getDim(type1) != 0)
        {
            if (scope.isNslType(type1))
            {
                return "NslBoolean" + scope.getDim(type1);
            }
            else
            {
                return "boolean" + type1.substring(type1.indexOf("["));
            }
        }
        else
        {
            if (scope.isNslType(type1) || scope.isNslType(type2))
            {
                return "NslBoolean" + scope.getDim(type1);
            }
            else
            {
                return "boolean" + type1.substring(type1.indexOf("["));
            }
        }
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

            if (operator.equals("||"))
            {
                if (((isArray1 || isArray2) && !canApplyElemOr(type1, type2)))
                {
                    NslCompiler.printError("nslc.src.ASTConditionalOrExpression", "Trying to use the \"or\" operator with non boolean types: " + type1 + " and " + type2, oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else if (((isArray1 || isArray2) && canApplyElemOr(dim1, dim2)))
                {
                    type1 = getElemOrType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                    String t=mults.toString();
                    mults = new StringBuilder(getNslElemOrHeader(scope.isNslType(type1), scope.getTempName(type1)));
                    mults.append(t);
                    mults.append(", ");
                    mults.append(oper.getExpression());
                    mults.append("))");
                }
                else if (isArray1 || isArray2)
                {
                    NslCompiler.printError("nslc.src.ASTConditionalOrExpression", "Trying to use the \"or\" operator with wrong dimensions", oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else
                {
                    // Scalar conditional or
                    String ext1 = "", ext2 = "", wrapStart = "", wrapEnd = "";

                    if (scope.isNslType(type1) || scope.isNslType(type2))
                    {

                        if (scope.isNslType(type1))
                        {
                            ext1 = ".get()";
                        }
                        if (scope.isNslType(type2))
                        {
                            ext2 = ".get()";
                        }

                        //temp = scope.getTempName(type1);
                        //wrapStart = temp + ".setReference(";
                        //wrapEnd   = ")";

                    } //else {
                    // Simple addition
                    type1 = getOrType();
                    //}
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

            if (operator.equals("||"))
            {
                if (((isArray1 || isArray2) && !canApplyElemOr(type1, type2)))
                {
                    NslCompiler.printError("nslc.src.ASTConditionalOrExpression", "Trying to use the \"or\" operator with non boolean types: " + type1 + " and " + type2, oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else if (((isArray1 || isArray2) && canApplyElemOr(dim1, dim2)))
                {
                    type1 = getElemOrType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                }
                else if (isArray1 || isArray2)
                {
                    NslCompiler.printError("nslc.src.ASTConditionalOrExpression", "Trying to use the \"or\" operator with wrong dimensions", oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else
                {
                    // Scalar conditional or
                    type1 = getOrType();
                    dim1 = 0;
                    isArray1 = false;
                }
            }
        }

        expressionType = type1;

        return "";
    }
}
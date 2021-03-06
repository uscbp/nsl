package nslc.src;/* Generated By:JJTree: Do not edit this line. nslc.src.ASTEqualityExpression.java */

/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

import java.io.PrintStream;

public class ASTEqualityExpression extends ASTExpression
{
    public ASTEqualityExpression(int id)
    {
        super(id);
    }

    public ASTEqualityExpression(NslParser p, int id)
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

    public static String getNslElemEquHeader(boolean isNsl, String temp)
    {
        String assign = (isNsl ? ".setReference(" : " = (");
        String get = (isNsl ? ".get(), " : ", ");
        return temp + assign + "NslEqu.eval(" + temp + get;
    }

    public static String getNslElemNeqHeader(boolean isNsl, String temp)
    {
        String assign = (isNsl ? ".setReference(" : " = (");
        String get = (isNsl ? ".get(), " : ", ");
        return temp + assign + "NslNeq.eval(" + temp + get;
    }

    public static String getEquType()
    {
        return "boolean";
    }

    public static String getNeqType()
    {
        return getEquType();
    }

    public static boolean canApplyElemEqu(int dim1, int dim2)
    {
        return dim1 == dim2 || dim1 == 0 || dim2 == 0;
    }

    public static boolean canApplyElemNeq(int dim1, int dim2)
    {
        return canApplyElemEqu(dim1, dim2);
    }

    public static boolean canApplyElemEqu(String type1, String type2)
    {
        return type1.equals("null") || type2.equals("null") || 
                ((type1.toLowerCase().indexOf("int") >= 0) && (type2.toLowerCase().indexOf("int") >= 0)) ||
                ((type1.toLowerCase().indexOf("float") >= 0) && (type2.toLowerCase().indexOf("float") >= 0)) ||
                ((type1.toLowerCase().indexOf("double") >= 0) && (type2.toLowerCase().indexOf("double") >= 0)) ||
                ((type1.toLowerCase().indexOf("boolean") >= 0) && (type2.toLowerCase().indexOf("boolean") >= 0)) ||
                ((type1.toLowerCase().indexOf("string") >= 0) && (type2.toLowerCase().indexOf("string") >= 0));
    }

    public static boolean canApplyElemNeq(String type1, String type2)
    {
        return canApplyElemEqu(type1, type2);
    }

    public static String getElemEquType(NslScope scope, String type1, String type2)
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

    public static String getElemNeqType(NslScope scope, String type1, String type2)
    {
        return getElemEquType(scope, type1, type2);
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

            if (operator.equals("=="))
            {

                if (((isArray1 || isArray2) && !canApplyElemEqu(type1, type2)))
                {
                    NslCompiler.printError("nslc.src.ASTEqualityExpression", "Trying to use the \"==\" operator with non boolean types: " + type1 + " and " + type2, oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else if (((isArray1 || isArray2) && canApplyElemEqu(dim1, dim2)))
                {
                    type1 = getElemEquType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                    String t=mults.toString();
                    mults = new StringBuilder(getNslElemEquHeader(scope.isNslType(type1), scope.getTempName(type1)));
                    mults.append(t);
                    mults.append(", ");
                    mults.append(oper.getExpression());
                    mults.append("))");
                }
                else if (isArray1 || isArray2)
                {
                    NslCompiler.printError("nslc.src.ASTEqualityExpression", "Trying to compare wrong dimensions", oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else
                {
                    // Scalar equality
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
                    }
                    // Simple equality
                    type1 = getEquType();
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
            else if (operator.equals("!="))
            {

                if (((isArray1 || isArray2) && !canApplyElemNeq(type1, type2)))
                {
                    NslCompiler.printError("nslc.src.ASTEqualityExpression", "Trying to use the \"!=\" operator with non boolean types: " + type1 + " and " + type2, oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else if (((isArray1 || isArray2) && canApplyElemNeq(dim1, dim2)))
                {
                    type1 = getElemNeqType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                    String t=mults.toString();
                    mults = new StringBuilder(getNslElemNeqHeader(scope.isNslType(type1), scope.getTempName(type1)));
                    mults.append(t);
                    mults.append(", ");
                    mults.append(oper.getExpression());
                    mults.append("))");
                }
                else if (isArray1 || isArray2)
                {
                    NslCompiler.printError("nslc.src.ASTEqualityExpression", "Trying to compare wrong dimensions", oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else
                {
                    // Scalar inequality
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
                    }
                    // Simple inequality
                    type1 = getNeqType();
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

            if (operator.equals("=="))
            {

                if (((isArray1 || isArray2) && !canApplyElemEqu(type1, type2)))
                {
                    NslCompiler.printError("nslc.src.ASTEqualityExpression", "Trying to use the \"==\" operator with non boolean types: " + type1 + " and " + type2, oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else if (((isArray1 || isArray2) && canApplyElemEqu(dim1, dim2)))
                {
                    type1 = getElemEquType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                }
                else if (isArray1 || isArray2)
                {
                    NslCompiler.printError("nslc.src.ASTEqualityExpression", "Trying to compare wrong dimensions", oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else
                {
                    // Scalar equality
                    type1 = getEquType();
                    dim1 = 0;
                    isArray1 = false;
                }
            }
            else if (operator.equals("!="))
            {

                if (((isArray1 || isArray2) && !canApplyElemEqu(type1, type2)))
                {
                    NslCompiler.printError("nslc.src.ASTEqualityExpression", "Trying to use the \"!=\" operator with non boolean types: " + type1 + " and " + type2, oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else if (((isArray1 || isArray2) && canApplyElemNeq(dim1, dim2)))
                {
                    type1 = getElemNeqType(scope, type1, type2);
                    dim1 = scope.getDim(type1);
                    isArray1 = dim1 > 0;
                }
                else if (isArray1 || isArray2)
                {
                    NslCompiler.printError("nslc.src.ASTEqualityExpression", "Trying to compare wrong dimensions", oper.getFirstToken().beginLine, oper.getFirstToken().beginColumn);
                    return "";
                }
                else
                {
                    // Scalar inequality
                    type1 = getNeqType();
                    dim1 = 0;
                    isArray1 = false;
                }
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

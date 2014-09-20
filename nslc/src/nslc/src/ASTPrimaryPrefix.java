package nslc.src;/* Generated By:JJTree: Do not edit this line. nslc.src.ASTPrimaryPrefix.java */

/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

public class ASTPrimaryPrefix extends SimpleNode
{

    String expressionType = "";

    public ASTPrimaryPrefix(int id)
    {
        super(id);
    }

    public ASTPrimaryPrefix(NslParser p, int id)
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

    public String getName()
    {
        if (isNodeOfType("name"))
        {
            ASTName name = (ASTName) jjtGetChild(0);
            return name.getName();
        }
        return "";
    }

    public void translateNslName()
    {
        ASTName name = (ASTName) jjtGetChild(0);
        name.translateNslName();
    }

    public String getType()
    {
        if (isNodeOfType("name"))
        {
            ASTName name = (ASTName) jjtGetChild(0);
            return name.getVariableType();
        }
        else if (isNodeOfType("expression"))
        {
            ASTExpression expr = (ASTExpression) jjtGetChild(0);
            return expr.getExpressionType();
        }
        else if (isNodeOfType("literal"))
        {
            ASTLiteral expr = (ASTLiteral) jjtGetChild(0);
            return expr.getType();
        }
        else if (isNodeOfType("allocation"))
        {
            ASTAllocationExpression expr = (ASTAllocationExpression) jjtGetChild(0);
            return expr.getType();
        }

        return "";
    }

    public void setType(String type)
    {
        if (isNodeOfType("name"))
        {
            ASTName name = (ASTName) jjtGetChild(0);
            name.setVariableType(type);
        }
        else
        {
            expressionType = type;
        }
    }

    public boolean isMethodName()
    {
        if (isNodeOfType("name"))
        {
            ASTName name = (ASTName) jjtGetChild(0);
            return name.isMethodName();
        }
        else
        {
            return false;
        }
    }

    public NslScope getMethodScope()
    {
        if (isNodeOfType("name"))
        {
            ASTName name = (ASTName) jjtGetChild(0);
            return name.getMethodScope();
        }
        else
        {
            return null;
        }
    }

    public String getMethodName()
    {
        if (isNodeOfType("name"))
        {
            ASTName name = (ASTName) jjtGetChild(0);
            return name.getMethodName();
        }
        else
        {
            return null;
        }
    }

    public boolean isNslExpression()
    {
        if (isNodeOfType("name"))
        {
            ASTName name = (ASTName) jjtGetChild(0);
            return name.isNslVariable();
        }
        else if (isNodeOfType("expression"))
        {
            ASTExpression expr = (ASTExpression) jjtGetChild(0);
            return expr.isNslExpression();
        }
        else
        {
            return false;
        }
    }

    public String toXMLOpen(String prefix)
    {
        String nodeType = getNodeType();
        String value = "";
        if (nodeType.equals("this"))
        {
            value = "<Name type=\"" + expressionType + "\">this</Name>";
        }
        else if (nodeType.equals("super."))
        {
            value = "<Name type=\"" + expressionType + "\">super." + first.next.next.image + "</Name>";
        }
        return prefix + '<' + toString() + '>' + value;
    }
}
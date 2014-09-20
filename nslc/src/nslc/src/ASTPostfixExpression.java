package nslc.src;/* Generated By:JJTree: Do not edit this line. nslc.src.ASTPostfixExpression.java */

/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

public class ASTPostfixExpression extends ASTExpression
{

    String operator = null;

    public ASTPostfixExpression(int id)
    {
        super(id);
    }

    public ASTPostfixExpression(NslParser p, int id)
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

    public void setOperator(String oper)
    {
        operator = oper;
    }

    public String getOperator()
    {
        return operator;
    }

    public String toXMLOpen(String prefix)
    {

        operator = ((operator == null) ? "" : " operator=\"" + operator + '\"');

        return prefix + '<' + toString() + operator + '>';
    }
}

package nslc.src;/* Generated By:JJTree: Do not edit this line. nslc.src.ASTFormalParameter.java */

/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

public class ASTFormalParameter extends SimpleNode
{
    private boolean isFinal = false;

    public ASTFormalParameter(int id)
    {
        super(id);
    }

    public ASTFormalParameter(NslParser p, int id)
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

    public void setFinal(boolean value)
    {
        isFinal = value;
    }

    public boolean getFinal()
    {
        return isFinal;
    }

    public String getType()
    {
        ASTType typeNode = (ASTType) jjtGetChild(0);
        return (isFinal ? "final " : "") + typeNode.getType();
    }

    public String getName()
    {
        ASTVariableDeclaratorId nameNode = (ASTVariableDeclaratorId) jjtGetChild(1);
        return nameNode.getName();
    }

}

package nslc.src;/* Generated By:JJTree: Do not edit this line. nslc.src.ASTUnmodifiedClassDeclaration.java */

/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

public class ASTUnmodifiedClassDeclaration extends SimpleNode
{

    private boolean inherits = false;
    private boolean implement = false;
    private String className;

    public ASTUnmodifiedClassDeclaration(int id)
    {
        super(id);
    }

    public ASTUnmodifiedClassDeclaration(NslParser p, int id)
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

    public void setExtends(boolean value)
    {
        inherits = value;
    }

    public boolean getExtends()
    {
        return inherits;
    }

    public void setImplements(boolean value)
    {
        implement = value;
    }

    public boolean getImplements()
    {
        return implement;
    }

    public void setClassName(String name)
    {
        className = name;
    }

    public String getClassName()
    {
        return className;
    }

    public String getSuperClass()
    {
        if (inherits)
        {
            ASTName node = (ASTName) jjtGetChild(0);
            return node.getName();
        }
        else
        {
            return "Object";
        }
    }

    public String[] getInterfaceNames()
    {
        if (implement)
        {
            int index = (inherits ? 1 : 0);
            ASTNameList node = (ASTNameList) jjtGetChild(index);
            return node.getNames();
        }
        return null;
    }
}
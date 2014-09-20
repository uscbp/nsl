package nslc.src;/* Generated By:JJTree: Do not edit this line. nslc.src.ASTNslVariableDeclarator.java */

/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

public class ASTNslVariableDeclarator extends SimpleNode
{

    public ASTNslVariableDeclarator(int id)
    {
        super(id);
    }

    public ASTNslVariableDeclarator(NslParser p, int id)
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


    public boolean hasVariableInitializer()
    {
        return jjtGetNumChildren() > 1;
    }

    public String getName()
    {

        ASTArguments arg = (ASTArguments) jjtGetChild(0);

        Token t = first;
        Token end = arg.getFirstToken();

        StringBuilder name = new StringBuilder("");
        do
        {
            name.append(t.image);
            t = t.next;
        }
        while (t != end);

        return name.toString();
    }

    public String getArguments()
    {
        ASTArguments arg = (ASTArguments) jjtGetChild(0);
        String args[] = arg.getArguments();

        StringBuilder arguments = new StringBuilder("");

        if (args.length > 0)
        {
            arguments.append(args[0]);
            for (int i = 1; i < args.length; i++)
            {
                arguments.append(", ");
                arguments.append(args[i]);
            }
        }
        return arguments.toString();
    }

    public String getVariableInitializer()
    {
        if (jjtGetNumChildren() > 1)
        {
            return ((ASTVariableInitializer) jjtGetChild(1)).getVariableInitializer();
        }
        else
        {
            return null;
        }
    }

    public String toXMLOpen(String prefix)
    {
        String name = getName();
        return prefix + '<' + toString() + " name=\"" + name + "\">";
    }
}
package nslc.src;/* Generated By:JJTree: Do not edit this line. nslc.src.ASTLocalVariableDeclaration.java */

/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

public class ASTLocalVariableDeclaration extends ASTFieldDeclaration
{
    public ASTLocalVariableDeclaration(int id)
    {
        super(id);
    }

    public ASTLocalVariableDeclaration(NslParser p, int id)
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

    public String getQualifiers()
    {
        StringBuilder qual = new StringBuilder("");
        SimpleNode node = (SimpleNode) jjtGetChild(0);

        Token t = first;
        Token end = node.getFirstToken();

        while (t != end)
        {
            qual.append(t.image);
            qual.append(' ');
            t = t.next;
        }
        return qual.toString();
    }

    public String toXMLOpen(String prefix)
    {
        String modifiers = getQualifiers();
        return prefix + '<' + toString() + " modifiers=\"" + modifiers + "\">";
    }
}

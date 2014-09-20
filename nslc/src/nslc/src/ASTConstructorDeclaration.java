package nslc.src;/* Generated By:JJTree: Do not edit this line. nslc.src.ASTConstructorDeclaration.java */

/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

public class ASTConstructorDeclaration extends SimpleNode
{
    public ASTConstructorDeclaration(int id)
    {
        super(id);
    }

    public ASTConstructorDeclaration(NslParser p, int id)
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

    String name;

    public void setName(String name)
    {

        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String[] getFormalTypes()
    {
        ASTFormalParameters formals = (ASTFormalParameters) jjtGetChild(0);
        return formals.getTypes();
    }

    public String[] getFormalNames()
    {
        ASTFormalParameters formals = (ASTFormalParameters) jjtGetChild(0);
        return formals.getNames();
    }

    public void updateScope(NslScope scope)
    {
        if (scope.getClassName() != null && scope.getClassName().equals(name))
        {
            String types[] = getFormalTypes();
            String names[] = getFormalNames();
            NslVariable temp;
            for (int i = 0; i < names.length; i++)
            {
                temp = new NslVariable(types[i], names[i]);
                if (!scope.addLocalVar(temp))
                {
                    ASTFormalParameters variable = (ASTFormalParameters) jjtGetChild(i + 1);
                    NslCompiler.printError("nslc.src.ASTConstructorDeclaration", "Variable " + names[i] + " was already defined in this scope", variable.getFirstToken().beginLine, variable.getFirstToken().beginColumn);
                }
            }
        }
        else
        {
            NslCompiler.printError("nslc.src.ASTConstructorDeclaration", "Constructor " + name + " has to be named as its class " + scope.getClassName(), getFirstToken().beginLine, getFirstToken().beginColumn);
        }
    }

    public String toXMLOpen(String prefix)
    {
        String name = this.name;

        return prefix + '<' + toString() + " name=\"" + name + "\">";
    }
}

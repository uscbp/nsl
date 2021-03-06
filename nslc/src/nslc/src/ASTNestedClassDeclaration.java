package nslc.src;/* Generated By:JJTree: Do not edit this line. nslc.src.ASTNestedClassDeclaration.java */

/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

public class ASTNestedClassDeclaration extends SimpleNode
{
    public ASTNestedClassDeclaration(int id)
    {
        super(id);
    }

    public ASTNestedClassDeclaration(NslParser p, int id)
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
        SimpleNode arg = (SimpleNode) jjtGetChild(0);

        Token t = first;
        Token end = arg.getFirstToken();

        while (t != end)
        {
            qual.append(t.image);
            qual.append(' ');
            t = t.next;
        }
        return qual.toString();
    }

    public String getClassName()
    {
        ASTUnmodifiedClassDeclaration node = (ASTUnmodifiedClassDeclaration) jjtGetChild(0);
        return node.getClassName();
    }

    public String getSuperClass()
    {
        ASTUnmodifiedClassDeclaration node = (ASTUnmodifiedClassDeclaration) jjtGetChild(0);
        return node.getSuperClass();
    }

    public void updateScope(NslScope scope)
    {

        NslScope subClassScope = new NslScope();

        subClassScope.setClassKind("class");
        subClassScope.setClassName(getClassName());

        String superClass = getSuperClass();
        subClassScope.setSuperClassName(superClass);

        NslScope superClassScope = NslCompiler.parseType(superClass, NslCompiler.libraryDir, scope.getImportList());
        if (superClassScope != null)
        {
            subClassScope.setSuperClassScope(superClassScope);
        }
        subClassScope.setContainerScope(scope);

        NslParserVisitor visitor = new SymbolTableClassVisitor(subClassScope);
        ASTUnmodifiedClassDeclaration node = (ASTUnmodifiedClassDeclaration) jjtGetChild(0);
        node.jjtAccept(visitor, null);

        visitor = new SymbolTableVisitor(subClassScope);
        node.jjtAccept(visitor, null);

        scope.addSubClass(subClassScope);
    }
}

/*  SCCS - @(#)TreeNode.java	1.5 - 09/01/99 - 00:15:52 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: TreeNode.java,v $
// Revision 1.2  1997/05/09 22:30:27  danjie
// add some comments and Log
//
//--------------------------------------


package nslj.src.display;

import nslj.src.lang.NslModule;

import java.awt.*;
//import Tree;

/**
 * A TreeNode class which can be used as a base clas to implemnt a node
 * Tree datastructres like heirarchical file systems.
 * <p/>
 * Derive a class from TreeNode to override the methods
 * like added, deleted, select and expandCollapse
 *
 * @author Sandip Chitale
 */
public class TreeNode
{
    Object mObject;
    int mLevel;
    boolean mIsExpandable;
    boolean mIsExpanded;
    boolean loaded=false;
    NslModule module;

// changed by danjie

    public TreeNode(Object pObject)
    {
        mObject = pObject;
        mLevel = 10000;
        mIsExpandable=false;
        mIsExpanded = false;
    }

    // derived class should override this
    public void
            added(Tree pFromTree)
    {
    }

    // derived class should override this
    public void
            deleted(Tree pFromTree)
    {
    }

    // derived class should override this
    public void
            select(Tree pFromTree, int pModifiers)
    {
    }

    // derived class should override this
    public void
            expandCollapse(Tree pFromTree, int pModifiers)
    {
        if (isExpandable())
        {
            toggleExpanded();
        }
    }

    // various accesor fuctions
    public boolean
            isExpandable()
    {
        return mIsExpandable;
    }

    public void
            setExpandable(boolean e)
    {
        mIsExpandable = e;
    }

    public boolean
            isExpanded()
    {
        return (mIsExpanded);
    }

    public void
            setExpanded()
    {
        if (isExpandable())
        {
            mIsExpanded = true;
        }
    }

    public void
            unsetExpanded()
    {
        if (isExpandable())
        {
            mIsExpanded = false;
        }
    }

    public void
            toggleExpanded()
    {
        if (isExpanded())
        {
            unsetExpanded();
        }
        else
        {
            setExpanded();
        }
    }

    public Object
            getObject()
    {
        return (mObject);
    }

    public int
            getLevel()
    {
        return (mLevel);
    }

    public void
            setLevel(int pLevel)
    {
        mLevel = pLevel;
    }

    public String
            toString()
    {
        return (mObject.toString());
    }

    public boolean
            equals(TreeNode pOther)
    {
        return (mObject.equals(pOther.getObject()));
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public void setLoaded(boolean loaded)
    {
        this.loaded = loaded;
    }

    public NslModule getModule()
    {
        return module;
    }

    public void setModule(NslModule module)
    {
        this.module = module;
    }
}

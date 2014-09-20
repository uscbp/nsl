/*  SCCS - @(#)Tree.java	1.6 - 09/01/99 - 00:15:50 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: Tree.java,v $
//
// Revision 1.2  1997/05/09 22:30:27  danjie
// add some comments and Log
//
//--------------------------------------


package nslj.src.display;

import nslj.src.lang.NslModule;
import nslj.src.lang.NslData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Collections;
import java.util.Iterator;

/**
 * A Tree class which can display Tree datastructres like heirarchical
 * file systems.
 *
 * @author Sandip Chitale
 */
public
class Tree extends JPanel
{
    public static int TREE_HASLINES = 1;
    int mStyle;
    Vector<TreeNode> mVector;
    Vector<TreeNode> mDisplayVector;
    TreeNode mSelectedNode;
    public String variableSelect;
    NslInput mNslVariableInput;

    int mMaxWidth = -1;
    int mMaxHeight = -1;

    // constants
    public static int CELLSIZE = 20;
    public static int CELLOFFSET = CELLSIZE >> 1;
    public static int IMAGEMARGIN = 1;
    public static int IMAGESIZE = CELLSIZE - (2 * IMAGEMARGIN);
    public static int TRIGGERMARGIN = 6;
    public static int TRIGGERSIZE = CELLSIZE - (2 * TRIGGERMARGIN);

    // constructors
    public Tree(NslInput mNslVariableInput)
    {
        initialize();
        this.mNslVariableInput=mNslVariableInput;
    }

    public Tree(int pStyle, NslInput mNslVariableInput)
    {
        initialize();
        mStyle = pStyle;
        this.mNslVariableInput=mNslVariableInput;
    }

    public void initialize()
    {
        mStyle = 0;
        mVector = new Vector<TreeNode>(0);
        mDisplayVector = new Vector<TreeNode>(0);
        mSelectedNode = null;
        createGui();
    }

    public TreeNode getSelectedNode()
    {
        return mSelectedNode;
    }

    public void createGui()
    {
        setLayout(new GridLayout());
        setPreferredSize(getSize());
        setMinimumSize(getSize());
        addMouseListener(new TreeMouseAdapter());
    }

    // methods to add a root TreeNodes
    public void addTreeNode(TreeNode pNode)
    {
        pNode.setLevel(0);
        mVector.addElement(pNode);
        pNode.added(this);
    }

    // methods to add a TreeNodes under another
    public void addTreeNode(TreeNode pSuper, TreeNode pSub)
    {
        int foundAt = mVector.indexOf(pSuper);

        if (foundAt == -1) return;
        if (mVector.indexOf(pSub) != -1) return;

        int lLevel = pSuper.getLevel();
        pSub.setLevel(lLevel + 1);

        TreeNode lNode;
        int lCount = mVector.size();
        int i;
        for (i = foundAt + 1; i < lCount; i++)
        {
            lNode = mVector.elementAt(i);
            if (lLevel >= lNode.getLevel())
            {
                break;
            }
        }
        if (i == lCount)
        {
            mVector.addElement(pSub);
        }
        else
        {
            mVector.insertElementAt(pSub, i);
        }
        pSub.added(this);
    }

    // remove a TreeNode
    public void removeTreeNode(TreeNode pNode)
    {
        int foundAt = mVector.indexOf(pNode);

        if (foundAt == -1)
        {
            return;
        }
        removeSubTreeNodes(pNode);
        mVector.removeElementAt(foundAt);
        pNode.deleted(this);
        if (mSelectedNode == pNode)
        {
            mSelectedNode = null;
        }
    }

    // remove the subtree of a TreeNode
    public void removeSubTreeNodes(TreeNode pNode)
    {
        int foundAt = mVector.indexOf(pNode);

        if (foundAt == -1)
        {
            return;
        }

        int lLevel = pNode.getLevel();

        TreeNode lNode;
        int lNodeLevel;
        int i;
        for (i = foundAt + 1; i < mVector.size();)
        {
            lNode = mVector.elementAt(i);
            lNodeLevel = lNode.getLevel();
            if (lLevel == lNodeLevel)
            {
                break;
            }
            if (lLevel < lNodeLevel)
            {
                mVector.removeElementAt(i);
                lNode.deleted(this);
                if (mSelectedNode == lNode)
                {
                    mSelectedNode = null;
                }
            }
        }
    }

    // adjust the Scrollbar
    public void refresh()
    {
        computeDisplayTree();
        repaint();
    }

    // compute the display Tree
    protected int computeDisplayTree()
    {
        int lCount;
        TreeNode lNode;
        int lLevel;
        int lDepth = 0;

        mDisplayVector.removeAllElements();

        lCount = mVector.size();
        for (int i = 0; i < lCount; i++)
        {
            lNode = mVector.elementAt(i);
            lLevel = lNode.getLevel();
            if (lLevel <= lDepth)
            {
                mDisplayVector.addElement(lNode);

                lDepth = lLevel;
                if (lNode.isExpandable())
                {
                    if (lNode.isExpanded())
                    {
                        lDepth++;
                    }
                }
            }
        }
        return lDepth;
    }

    // detect selection of a TreeNode
    private class TreeMouseAdapter extends MouseAdapter
    {
        public void mousePressed(MouseEvent pe)
        {
            int px = pe.getX();
            int py = pe.getY();

            int lIndex = (py / CELLSIZE);// + mVOffset;
            if (lIndex < mDisplayVector.size())
            {
                TreeNode lNode = mDisplayVector.elementAt(lIndex);
                mSelectedNode = lNode;
                int lLevel = lNode.getLevel();
                Rectangle lRect = new Rectangle((lLevel * CELLSIZE) + TRIGGERMARGIN
                        , ((py / CELLSIZE) * CELLSIZE) + TRIGGERMARGIN
                        , TRIGGERSIZE, TRIGGERSIZE);

                // modification by Danjie
                StringBuilder variableName = new StringBuilder('.');
                variableName.append(lNode.toString());
                int ll = lLevel - 1;
                TreeNode llNode;

                if (!lNode.isExpandable())
                {
                    for (int i = lIndex - 1; i >= 0; i--)
                    {
                        llNode = mDisplayVector.elementAt(i);
                        if (llNode.getLevel() == ll)
                        {
                            variableName.insert(0,'.');
                            variableName.insert(0,llNode.toString());
                            ll--;
                        }
                    }
                    variableSelect = variableName.toString();
                }

                if (lRect.contains(px, py))
                {
                    if (lNode.isExpandable())
                    {
                        if(!lNode.isLoaded())
                        {
                            treeBuilder(lNode.getModule(), lNode);
                        }
                        lNode.expandCollapse((Tree) pe.getSource(), pe.getModifiers());
                        refresh();
                    }
                }
                else
                {
                    lNode.select((Tree) pe.getSource(), pe.getModifiers());
                    repaint();
                }
            }
        }
    }

    public void treeBuilder(NslModule parentModule, TreeNode lSuper)
    {
        TreeNode lSub;
        String varName;

        NslModule nm = parentModule;
        Vector variableList = nm.nslGetDataVarsVector();
        int varCount = variableList.size();

        Vector childrenModuleVector = nm.nslGetModuleChildrenVector();
        int childCount = childrenModuleVector.size();

        Hashtable<String,TreeNode> children=new Hashtable<String,TreeNode>();
        // do  NSL variables
        for (int j = 0; j < varCount; j++)
        {
            varName = ((NslData) variableList.elementAt(j)).nslGetName();
            lSub = new NslTreeNode(varName, mNslVariableInput);
            //addTreeNode(lSuper, lSub);
            children.put(varName,lSub);
        }
        // 99/5/13 aa: I guess modules are not variables
        // thus we must now list the modules
        for (int i = 0; i < childCount; i++)
        {
            nm = (NslModule) childrenModuleVector.elementAt(i);
            lSub = new NslTreeNode(nm.nslGetName(), mNslVariableInput);
            lSub.setModule(nm);
            //addTreeNode(lSuper, lSub);
            if(nm.nslGetDataVarsVector().size()>0 || nm.nslGetModuleChildrenVector().size()>0)
                lSub.setExpandable(true);
            children.put(nm.nslGetName(),lSub);
        }

        // Sort variables and submodules by name
        Vector v=new Vector(children.keySet());
        Collections.sort(v);
        Iterator it=v.iterator();
        while(it.hasNext())
            addTreeNode(lSuper,children.get(it.next()));
        
        lSuper.setLoaded(true);
        if(childCount>0 || varCount>0)
            lSuper.setExpandable(true);
    }

    // paint at correct offset
    public void paintComponent(Graphics pg)
    {
        super.paintComponent(pg);
        int lCount;
        TreeNode lNode;
        int lLevel;
        int lWidth;
        FontMetrics lFM = pg.getFontMetrics();

        mMaxWidth = -1;
        mMaxHeight = -1;
        pg.setColor(Color.white);
        pg.fillRect(0, 0, getWidth(), getHeight());
        pg.setColor(Color.gray);
        if ((mStyle & TREE_HASLINES) != 0)
        {
            int j = (getSize().height / CELLSIZE) + 1;
            for (int ii = 0; ii < j; ii++)
            {
                int i = ii;// - mVOffset;
                pg.drawRect(0, (i * CELLSIZE), getSize().width, CELLSIZE);
            }
        }
        lCount = mDisplayVector.size();
        for (int ii = 0; ii < lCount; ii++)
        {
            int i = ii;// - mVOffset;
            lNode = mDisplayVector.elementAt(ii);
            lLevel = lNode.getLevel();
            if (lNode == mSelectedNode)
            {
                pg.setColor(Color.black);
                pg.fillRect(((lLevel + 2) * CELLSIZE), (i * CELLSIZE)
                        , lFM.stringWidth(lNode.toString()) + (2 * TRIGGERMARGIN)
                        , CELLSIZE);
            }

            pg.setColor(Color.gray);
            pg.drawLine((lLevel * CELLSIZE) + (CELLSIZE >> 1)
                    , (i * CELLSIZE) + (CELLSIZE >> 1)
                    , ((lLevel + 1) * CELLSIZE) + (CELLSIZE >> 1)
                    , (i * CELLSIZE) + (CELLSIZE >> 1));
            if (ii + 1 < lCount)
            {
                if (mDisplayVector.elementAt(ii + 1).getLevel() >= lLevel)
                {
                    if (mDisplayVector.elementAt(ii + 1).getLevel() > lLevel)
                    {
                        pg.drawLine(((lLevel + 1) * CELLSIZE) + (CELLSIZE >> 1)
                                , (i * CELLSIZE) + (CELLSIZE >> 1)
                                , ((lLevel + 1) * CELLSIZE) + (CELLSIZE >> 1)
                                , ((i + 1) * CELLSIZE) + (CELLSIZE >> 1));
                    }
                    int j, k;
                    for (j = ii + 1, k = -1; j < lCount; j++)
                    {
                        if (mDisplayVector.elementAt(j).getLevel() == lLevel)
                        {
                            k = j;
                            break;
                        }
                        if (mDisplayVector.elementAt(j).getLevel() < lLevel)
                        {
                            break;
                        }
                    }
                    if (j < lCount)
                    {
                        if (k != -1)
                        {
                            pg.drawLine((lLevel * CELLSIZE) + (CELLSIZE >> 1)
                                    , (i * CELLSIZE) + (CELLSIZE >> 1)
                                    , ((lLevel) * CELLSIZE) + (CELLSIZE >> 1)
                                    , ((k/* - mVOffset*/) * CELLSIZE) + (CELLSIZE >> 1));
                        }
                    }
                }
            }

            if (lNode.isExpandable())
            {
                pg.setColor(Color.white);
                pg.fillRect((lLevel * CELLSIZE) + TRIGGERMARGIN
                        , (i * CELLSIZE) + TRIGGERMARGIN
                        , TRIGGERSIZE, TRIGGERSIZE);
                pg.setColor(Color.black);
                pg.drawRect((lLevel * CELLSIZE) + TRIGGERMARGIN
                        , (i * CELLSIZE) + TRIGGERMARGIN
                        , TRIGGERSIZE, TRIGGERSIZE);
                pg.setColor(Color.black);
                pg.drawLine((lLevel * CELLSIZE) + TRIGGERMARGIN
                        , (i * CELLSIZE) + TRIGGERMARGIN + (TRIGGERSIZE >> 1)
                        , (lLevel * CELLSIZE) + TRIGGERMARGIN + TRIGGERSIZE
                        , (i * CELLSIZE) + TRIGGERMARGIN + (TRIGGERSIZE >> 1));
            }
            pg.setColor(Color.black);
            if (!lNode.isExpanded() && lNode.isExpandable())
            {
                pg.drawLine((lLevel * CELLSIZE) + TRIGGERMARGIN + (TRIGGERSIZE >> 1)
                        , (i * CELLSIZE) + TRIGGERMARGIN
                        , (lLevel * CELLSIZE) + TRIGGERMARGIN + (TRIGGERSIZE >> 1)
                        , (i * CELLSIZE) + TRIGGERMARGIN + TRIGGERSIZE);
            }
            if (lNode == mSelectedNode)
            {
                pg.setColor(Color.white);
            }
            else
            {
                pg.setColor(Color.black);
            }
            pg.drawString(lNode.toString()
                    , ((lLevel + 2) * CELLSIZE) + TRIGGERMARGIN
                    , ((i + 1) * CELLSIZE) - TRIGGERMARGIN);
            lWidth = ((lLevel + 2) * CELLSIZE) + lFM.stringWidth(lNode.toString()) + (2 * TRIGGERMARGIN);
            if (mMaxWidth < lWidth)
            {
                mMaxWidth = lWidth;
            }
            int lHeight = (i * CELLSIZE) + TRIGGERMARGIN + TRIGGERSIZE+IMAGEMARGIN+IMAGESIZE;
            if (mMaxHeight < lHeight)
            {
                mMaxHeight = lHeight;
            }
        }
        setPreferredSize(new Dimension(getWidth(),mMaxHeight));
        revalidate();
    }
}

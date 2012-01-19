/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.model;

import java.util.List;

/**
 * The INode interface represents nodes in a tree. Each node has zero or more children, which are also
 * <code>INode</code>s, connected by weighted edges.
 */
public interface INode
{
	public abstract int getId();

	public abstract String getLabel();

	public abstract void setLabel(String label);

	public abstract int getNumberOfChildren();

	public abstract List<? extends INode> getChildren();

	public abstract INode getChild(int index);
	
	public abstract INode getParent();

	/**
	 * @return true if this node has no children
	 */
	public abstract Boolean isLeaf();

	/**
	 * @return the number of childless ("leaf" or "tip") nodes in the subtree rooted at this node.
	 */
	public abstract int getNumberOfLeafNodes();

	/**
	 * @return the total number of nodes in the subtree rooted at this node, including this node.
	 */
	public abstract int getNumberOfNodes();

	/**
	 * @return the greatest number of parent-to-child steps from this node to a leaf, i.e. the height of
	 *         this node in the tree
	 */
	public abstract int findMaximumDepthToLeaf();

	/**
	 * @return the longest parent-to-child path from this node to a leaf
	 */
	public abstract double findMaximumDistanceToLeaf();

	/**
	 * This method can be used to label internal nodes based on an arbitrarily chosen leaf in its subtree.
	 * @return the label of the first labeled leaf in a preorder traversal of the subtree
	 */
	public abstract String findLabelOfFirstLeafNode();

	/**
	 * @return the branch length, the weight of the edge between this node and its parent. A non-negative
	 *         real number.
	 */
	public abstract Double getBranchLength();

	public abstract void setBranchLength(Double branchLength);
	
	@Deprecated
	public String getMetaDataString();
}
package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * A structure for tree topology stats for a node. Allows the data to be calculated once when the tree is
 * build, then persisted and serialized to the client.
 */
@Embeddable
public class NodeTopology implements Serializable
{
	private static final long serialVersionUID = 552531523709182005L;
	private int numChildren = 0;
	private int numNodes = 1;
	private int numLeaves = 1;
	private int height = 0;
	private double branchLengthHeight = 0.0; //this node's height, accounting for branch lengths
	private int depth = 0;
	/** any node (in the same tree) with a leftIndex >= this.leftIndex and rightIndex <= this.rightIndex is in this node's subtree */
	private int leftIndex = 1;
	private int rightIndex = 2;
	private String altLabel = null;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private RemoteNode rootNode;

	public NodeTopology()
	{
	}

	/**
	 * Creates a shallow copy of the given NodeTopology
	 */
	public NodeTopology(NodeTopology topology)
	{
		this.setAltLabel(topology.getAltLabel());
		this.setBranchLengthHeight(topology.getBranchLengthHeight());
		this.setDepth(topology.getDepth());
		this.setHeight(topology.getHeight());
		this.setLeftIndex(topology.getLeftIndex());
		this.setNumChildren(topology.getNumChildren());
		this.setNumLeaves(topology.getNumLeaves());
		this.setNumNodes(topology.getNumNodes());
		this.setRightIndex(topology.getRightIndex());
		this.setRootNode(topology.getRootNode());
	}
	
	/**
	 * @return the number of children this node has
	 */
	public int getNumChildren()
	{
		return numChildren;
	}

	public void setNumChildren(int numChildren)
	{
		this.numChildren = numChildren;
	}

	/**
	 * @return the number of nodes in this node's subtree
	 */
	public int getNumNodes()
	{
		return numNodes;
	}

	public void setNumNodes(int numNodes)
	{
		this.numNodes = numNodes;
	}

	/**
	 * @return the number of leaf nodes in this node's subtree
	 */
	public int getNumLeaves()
	{
		return numLeaves;
	}

	public void setNumLeaves(int numLeaves)
	{
		this.numLeaves = numLeaves;
	}

	/**
	 * @return the height of the node in the tree (i.e. the length of the longest path from this node to
	 *         a leaf)
	 */
	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	/**
	 * @return the depth of this node in the tree (i.e. the length of the path from the root to this
	 *         node).
	 */
	public int getDepth()
	{
		return depth;
	}

	public void setDepth(int depth)
	{
		this.depth = depth;
	}

	/**
	 * The left index is a number that is smaller than the left index of all nodes in this node's subtree. Used, with getRightIndex(), for fast subtree checks.
	 * @return the left index
	 */
	public int getLeftIndex()
	{
		return leftIndex;
	}

	public void setLeftIndex(int leftIndex)
	{
		this.leftIndex = leftIndex;
	}

	/**
	 * The right index is a number that is larger than the right index of all nodes in this node's subtree. Used, with getLeftIndex(), for fast subtree checks.
	 * @return the right index
	 */
	public int getRightIndex()
	{
		return rightIndex;
	}

	public void setRightIndex(int rightIndex)
	{
		this.rightIndex = rightIndex;
	}
	
	/**
	 * @return true if this subtree contains the node described by the given NodeTopology
	 */
	public boolean subtreeContains(NodeTopology topology)
	{
		return subtreeContains(topology.getLeftIndex());
	}
	
	/**
	 * @return true if this subtree contains the node described by the given traversalIndex (either a getRightIndex() or a getLeftIndex()))
	 */
	public boolean subtreeContains(int traversalIndex)
	{
		return traversalIndex >= this.getLeftIndex() && traversalIndex <= this.getRightIndex();
	}

	/**
	 * @return the weighted height of this node (i.e. the length of the longest weighted path from this
	 *         node to a leaf.)
	 */
	public double getBranchLengthHeight()
	{
		return branchLengthHeight;
	}

	public void setBranchLengthHeight(double branchLengthHeight)
	{
		this.branchLengthHeight = branchLengthHeight;
	}

	/**
	 * @return an alternative label for an unlabeled internal node, which one may assume to be
	 *         arbitrarily chosen from a descendant node
	 */
	public String getAltLabel()
	{
		return altLabel;
	}

	public void setAltLabel(String altLabel)
	{
		this.altLabel = altLabel;
	}

	/**
	 * @return the root node of this node's tree
	 */
	public RemoteNode getRootNode()
	{
		return rootNode;
	}

	public void setRootNode(RemoteNode rootNode)
	{
		this.rootNode = rootNode;
	}
}
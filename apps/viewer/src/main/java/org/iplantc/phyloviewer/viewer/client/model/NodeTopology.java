package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

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

	public int getNumChildren()
	{
		return numChildren;
	}

	public void setNumChildren(int numChildren)
	{
		this.numChildren = numChildren;
	}

	public int getNumNodes()
	{
		return numNodes;
	}

	public void setNumNodes(int numNodes)
	{
		this.numNodes = numNodes;
	}

	public int getNumLeaves()
	{
		return numLeaves;
	}

	public void setNumLeaves(int numLeaves)
	{
		this.numLeaves = numLeaves;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getDepth()
	{
		return depth;
	}

	public void setDepth(int depth)
	{
		this.depth = depth;
	}

	public int getLeftIndex()
	{
		return leftIndex;
	}

	public void setLeftIndex(int leftIndex)
	{
		this.leftIndex = leftIndex;
	}

	public int getRightIndex()
	{
		return rightIndex;
	}

	public void setRightIndex(int rightIndex)
	{
		this.rightIndex = rightIndex;
	}
	
	public boolean subtreeContains(NodeTopology topology)
	{
		return subtreeContains(topology.getLeftIndex());
	}
	
	public boolean subtreeContains(int traversalIndex)
	{
		return traversalIndex >= this.getLeftIndex() && traversalIndex <= this.getRightIndex();
	}

	public double getBranchLengthHeight()
	{
		return branchLengthHeight;
	}

	public void setBranchLengthHeight(double branchLengthHeight)
	{
		this.branchLengthHeight = branchLengthHeight;
	}

	public String getAltLabel()
	{
		return altLabel;
	}

	public void setAltLabel(String altLabel)
	{
		this.altLabel = altLabel;
	}

	public RemoteNode getRootNode()
	{
		return rootNode;
	}

	public void setRootNode(RemoteNode rootNode)
	{
		this.rootNode = rootNode;
	}
}
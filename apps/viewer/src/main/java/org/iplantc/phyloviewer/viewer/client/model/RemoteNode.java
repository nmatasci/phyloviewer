package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class RemoteNode extends PersistentNode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Embedded
	private NodeTopology topology = new NodeTopology();

	public RemoteNode(String label, NodeTopology topology) {
		this(label);
		this.topology = topology;
	}
	
	public RemoteNode(String label)
	{
		super(label);
	}

	public RemoteNode() 
	{ 
	}

	@Override @Transient
	public int getNumberOfLeafNodes() {
		return topology.getNumLeaves();
	}
	

	@Override
	public int findMaximumDepthToLeaf() {
		return topology.getHeight();
	}

	/**
	 * @return the number of children this node has. These children may not have been fetched yet.
	 */
	@Override @Transient
	public int getNumberOfChildren() 
	{
		return topology.getNumChildren();
	}
		
	@Override @Transient
	public int getNumberOfNodes()
	{
		return topology.getNumNodes();
	}
	
	@Override
	public double findMaximumDistanceToLeaf()
	{
		return topology.getBranchLengthHeight();
	}

	public NodeTopology getTopology()
	{
		return topology;
	}

	protected void setTopology(NodeTopology topology)
	{
		this.topology = topology;
	}

	@Override
	public void setChildren(List<? extends PersistentNode> children)
	{
		super.setChildren(children);
		
		if (children != null)
		{
			this.topology.setNumChildren(children.size());
		}
		else
		{
			this.topology.setNumChildren(0);
		}
	}

	/**
	 * @param depth this nodes depth
	 * @param leftIndex this node's leftIndex
	 * @return
	 */
	public NodeTopology reindex(int depth, int leftIndex)
	{
		int numNodes = 1;
		int maxChildHeight = -1;
		double maxBranchLengthHeight = 0.0;
		int numLeaves = this.isLeaf() ? 1 : 0;
		int nextTraversalIndex = leftIndex + 1;
		
		if (this.getChildren() != null)
		{
			for (PersistentNode child : this.getChildren())
			{
				NodeTopology childTopology = ((RemoteNode)child).reindex(depth + 1, nextTraversalIndex);
				
				maxChildHeight = Math.max(maxChildHeight, childTopology.getHeight());
				maxBranchLengthHeight = Math.max(maxBranchLengthHeight, childTopology.getBranchLengthHeight() + child.getBranchLength());
				numLeaves += childTopology.getNumLeaves();
				numNodes += childTopology.getNumNodes();
				nextTraversalIndex = childTopology.getRightIndex() + 1;
			}
		}
		
		if (this.topology == null)
		{
			this.topology = new NodeTopology();
		}
		this.topology.setDepth(depth);
		this.topology.setLeftIndex(leftIndex);
		this.topology.setBranchLengthHeight(maxBranchLengthHeight);
		this.topology.setHeight(maxChildHeight + 1);
		this.topology.setNumChildren(super.getNumberOfChildren());
		this.topology.setNumLeaves(numLeaves);
		this.topology.setNumNodes(numNodes);
		this.topology.setRightIndex(nextTraversalIndex);
		
		return this.topology;
	}
}

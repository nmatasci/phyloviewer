package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.iplantc.phyloviewer.shared.model.AbstractNode;
import org.iplantc.phyloviewer.shared.model.INode;

@Entity
@Table(name="node")
public class RemoteNode extends AbstractNode implements INode, Serializable {
	private static final long serialVersionUID = 3L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name="node_id")
	private int id;

	private String label;

	private Double branchLength = 1.0;

	@ManyToOne(fetch = FetchType.LAZY)
	private RemoteNode parent;

	/*
	 * need to avoid trying to serialize lazy-loading children on detached nodes. The node will have to
	 * be cloned to a DTO, or use Gilead, or use custom serialization (needs GWT 2.4).
	 */
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	protected List<RemoteNode> children;
	
	@Embedded
	private NodeTopology topology = new NodeTopology();

	public RemoteNode(String label, NodeTopology topology) {
		this(label);
		this.topology = topology;
	}
	
	public RemoteNode(String label)
	{
		this.label = label;
	}

	protected RemoteNode() 
	{ 
	}

	@Override
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
	@Override
	public int getNumberOfChildren() 
	{
		return topology.getNumChildren();
	}
		
	@Override
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

	public void setTopology(NodeTopology topology)
	{
		this.topology = topology;
	}
	
	public void addChild(RemoteNode child)
	{
		if (this.children == null) 
		{
			this.children = new ArrayList<RemoteNode>();
		}
		
		this.children.add(child);
		child.setParent(this);
		
		this.topology.setNumChildren(this.children.size());
	}
	
	@Override
	public List<RemoteNode> getChildren()
	{
		return children;
	}
	
	public void setChildren(List<RemoteNode> children)
	{
		this.children = children;
		
		for (RemoteNode child : children)
		{
			child.setParent(this);
		}
		
		this.topology.setNumChildren(children.size());
	}
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public Double getBranchLength()
	{
		return branchLength;
	}

	public void setBranchLength(Double branchLength)
	{
		this.branchLength = branchLength;
	}

	public RemoteNode getParent()
	{
		return parent;
	}

	public void setParent(RemoteNode parent)
	{
		this.parent = parent;
	}
	
	public String getMetaDataString()
	{
		return null;
	}

	/**
	 * Recalculates the NodeTopology for this node's subtree.
	 * @param depth this nodes depth
	 * @param leftIndex this node's leftIndex
	 */
	public NodeTopology reindex(int depth, int leftIndex, RemoteNode rootNode)
	{
		int numNodes = 1;
		int maxChildHeight = -1;
		double maxBranchLengthHeight = 0.0;
		int numLeaves = this.isLeaf() ? 1 : 0;
		int nextTraversalIndex = leftIndex + 1;
		String altLabel = null;
		
		if (this.getChildren() != null)
		{
			for (RemoteNode child : this.getChildren())
			{
				NodeTopology childTopology = ((RemoteNode)child).reindex(depth + 1, nextTraversalIndex, rootNode);
				
				maxChildHeight = Math.max(maxChildHeight, childTopology.getHeight());
				maxBranchLengthHeight = Math.max(maxBranchLengthHeight, childTopology.getBranchLengthHeight() + child.getBranchLength());
				numLeaves += childTopology.getNumLeaves();
				numNodes += childTopology.getNumNodes();
				nextTraversalIndex = childTopology.getRightIndex() + 1;
				
				if (altLabel == null) {
					altLabel = childTopology.getAltLabel();
				}
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
		this.topology.setAltLabel(altLabel);
		this.topology.setRootNode(rootNode);
		
		return this.topology;
	}
	
	/**
	 * Recalculates the NodeTopology for this tree.  (Assumes this node is the root of the entire tree.)
	 */
	public NodeTopology reindex() {
		return reindex(0, 1, this);
	}
}

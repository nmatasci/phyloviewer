package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * A serializable, persistent INode implementation that stores it's topology information in a
 * NodeTopology so methods like getNumberOfLeafNodes() can be calculated on the client.
 */
@Entity
@Table(name="node")
public class RemoteNode extends AbstractNode implements INode, Serializable {
	private static final long serialVersionUID = 4L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name="node_id")
	private int id;

	private String label;

	private Double branchLength = 1.0;

	@ManyToOne(fetch = FetchType.LAZY)
	private RemoteNode parent;

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	protected List<RemoteNode> children;
	
	@Embedded
	private NodeTopology topology = new NodeTopology();
	
	public RemoteNode() 
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

	/**
	 * Get the NodeTopology. By default, it's an empty new NodeTopology(). When the full tree is local
	 * (e.g. on the server), it is updated by calling reindex()
	 * 
	 * @return this node's NodeTopology.
	 * @see RemoteNode#reindex()
	 */
	public NodeTopology getTopology()
	{
		return topology;
	}

	/**
	 * Set this node's NodeTopology
	 */
	public void setTopology(NodeTopology topology)
	{
		this.topology = topology;
	}
	
	/**
	 * Add a child node
	 */
	public void addChild(RemoteNode child)
	{
		if (this.children == null) 
		{
			this.children = new ArrayList<RemoteNode>();
		}
		
		this.children.add(child);
		child.setParent(this);
		
		this.topology.setNumChildren(this.children.size());
		//TODO: update the rest of the NodeTopology based on child NodeTopology?
	}
	
	@Override
	public List<RemoteNode> getChildren()
	{
		return children;
	}
	
	/**
	 * Set the child list for this node to the given list
	 */
	public void setChildren(List<RemoteNode> children)
	{
		this.children = children;
		
		if (children != null) {
			for (RemoteNode child : children)
			{
				child.setParent(this);
			}
			
			this.topology.setNumChildren(children.size());
			//TODO: update the rest of the NodeTopology based on child NodeTopology?
		}
	}
	
	@Override
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	@Override
	public String getLabel()
	{
		return label;
	}

	@Override
	public void setLabel(String label)
	{
		this.label = label;
	}

	@Override
	public Double getBranchLength()
	{
		return branchLength;
	}

	@Override
	public void setBranchLength(Double branchLength)
	{
		this.branchLength = branchLength;
	}

	@Override
	public RemoteNode getParent()
	{
		return parent;
	}

	public void setParent(RemoteNode parent)
	{
		this.parent = parent;
	}

	@Override
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
		String altLabel = this.isLeaf() ? this.label : null;
		
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

	@Override
	public String findLabelOfFirstLeafNode()
	{
		return topology.getAltLabel();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof RemoteNode && super.equals(obj))
		{
			RemoteNode other = (RemoteNode) obj;
			return this.label == null && other.label == null || this.label.equals(other.label) 
					&& this.branchLength == null && other.branchLength == null || this.branchLength.equals(other.branchLength);
		}
		else 
		{
			return false;
		}
	}
	
	/**
	 * Convenience method for building trees in code.  Mostly for testing.
	 * <br><br>
	 * Example:
	 *<pre>
	 *RemoteNode root = rn(null, 0.0,
	 *    rn("Protomyces_inouyei", 1.0),
	 *    rn(null, 2.0,
	 *        rn("Taphrina_wiesneri", 3.0),
	 *        rn("Taphrina_deformans", 4.0)
	 *    )
	 *);
	 *</pre>
	 */
	public static RemoteNode rn(String label, Double branchLength, RemoteNode... children) 
	{
		RemoteNode node = new RemoteNode();

		if (label != null)
		{
			node.setLabel(label);
		}
		
		if(branchLength != null)
		{
			node.setBranchLength(branchLength);
		}
		
		if (children != null)
		{
			node.setChildren(Arrays.asList(children));
		}
		
		return node;
	}
}

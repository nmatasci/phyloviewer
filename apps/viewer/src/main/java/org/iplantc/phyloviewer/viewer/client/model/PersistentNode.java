package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.iplantc.phyloviewer.shared.model.INode;

@Entity
public class PersistentNode implements INode, Serializable
{
	private static final long serialVersionUID = 3329649649400777449L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	
	private String label;
	private Double branchLength = 1.0;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private PersistentNode parent;
	
	@OneToMany(mappedBy="parent", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<? extends PersistentNode> children;

	public PersistentNode(String label)
	{
		this.label = label;
	}

	/**
	 * No-arg constructor, required by Serializable and @Entity
	 */
	public PersistentNode()
	{
	}

	public String findLabelOfFirstLeafNode()
	{
		if(this.isLeaf())
		{
			return this.getLabel();
		}

		return this.getChild(0).findLabelOfFirstLeafNode();
	}

	public int findMaximumDepthToLeaf()
	{
		int maxChildHeight = -1; // -1 so leaf will return 0

		for(int index = 0;index < getNumberOfChildren();index++)
		{
			INode child = getChild(index);
			maxChildHeight = Math.max(maxChildHeight, child.findMaximumDepthToLeaf());
		}

		return maxChildHeight + 1;
	}

	public INode getChild(int index)
	{
		if(children == null)
		{
			return null;
		}

		return children.get(index);
	}
	
	public List<? extends PersistentNode> getChildren()
	{
		return children;
	}

	public int getId()
	{
		return id;
	}

	public String getLabel()
	{
		return label;
	}

	public int getNumberOfChildren()
	{
		if(getChildren() == null)
		{
			return 0;
		}
		else
		{
			return getChildren().size();
		}
	}

	public int getNumberOfLeafNodes()
	{
		int count = 0;
		if(this.isLeaf())
		{
			count = 1;
		}
		else
		{
			for(int i = 0;i < this.getNumberOfChildren();++i)
			{
				count += this.getChild(i).getNumberOfLeafNodes();
			}
		}

		return count;
	}

	public int getNumberOfNodes()
	{
		int count = 1;

		if(getChildren() != null)
		{
			for(INode child : getChildren())
			{
				count += child.getNumberOfNodes();
			}
		}

		return count;
	}

	public Boolean isLeaf()
	{
		return getNumberOfChildren() == 0;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		
		if(obj == null || !(obj instanceof PersistentNode))
		{
			return false;
		}

		PersistentNode that = (PersistentNode)obj;

		return this.id == that.getId();
	}
	
	@Override
	public int hashCode()
	{
		return this.id;
	}

	public void setChildren(List<? extends PersistentNode> children)
	{
		this.children = children;
		for (PersistentNode child : children) {
			child.setParent(this);
		}
	}

	public String toString()
	{
		return label;
	}

	public Double getBranchLength()
	{
		return branchLength;
	}

	public void setBranchLength(Double branchLength)
	{
		this.branchLength = branchLength;
	}

	public double findMaximumDistanceToLeaf()
	{
		return this.findMaximumDistanceToLeaf(0.0);
	}

	private double findMaximumDistanceToLeaf(double currentDistance)
	{
		double localMaximum = currentDistance;

		int numChildren = this.getNumberOfChildren();
		if(0 < numChildren)
		{
			for(int i = 0;i < numChildren;++i)
			{
				PersistentNode child = (PersistentNode) this.getChild(i);
				double distance = child.findMaximumDistanceToLeaf(currentDistance);

				if(distance > localMaximum)
				{
					localMaximum = distance;
				}
			}
		}

		double branchLength = this.getBranchLength() != null ? this.getBranchLength() : 0.0;
		return localMaximum + branchLength;
	}

	public String getMetaDataString()
	{
		return null;
	}

	public PersistentNode getParent()
	{
		return this.parent;
	}
	
	protected void setParent(PersistentNode node)
	{
		this.parent = node;
	}
}

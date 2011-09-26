package org.iplantc.phyloviewer.shared.model;

import java.io.Serializable;
import java.util.List;

public class Node implements INode, Serializable
{
	private static final long serialVersionUID = 3329649649400777449L;
	
	private int id;
	private String label;
	private List<? extends Node> children;
	private Double branchLength;
	private INode parent;

	public Node(int id, String label)
	{
		this.id = id;
		this.label = label;
	}

	public Node()
	{
	}

	@Override
	public String findLabelOfFirstLeafNode()
	{
		if(this.isLeaf())
		{
			return this.getLabel();
		}

		return this.getChild(0).findLabelOfFirstLeafNode();
	}

	@Override
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

	@Override
	public Node getChild(int index)
	{
		if(children == null)
		{
			return null;
		}

		return children.get(index);
	}

	@Override
	public List<? extends Node> getChildren()
	{
		return children;
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public String getLabel()
	{
		return label;
	}

	@Override
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

	@Override
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

	@Override
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

	@Override
	public Boolean isLeaf()
	{
		return getNumberOfChildren() == 0;
	}

	@Override
	public void setId(int id)
	{
		this.id = id;
	}

	@Override
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
		
		if(obj == null || !(obj instanceof Node))
		{
			return false;
		}

		Node that = (Node)obj;

		return this.getId() == that.getId();
	}

	public void setChildren(List<? extends Node> children)
	{
		this.children = children;
	}

	@Override
	public String toString()
	{
		return label;
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
				Node child = this.getChild(i);
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

	@Override
	public String getMetaDataString()
	{
		return null;
	}

	@Override
	public INode getParent()
	{
		return this.parent;
	}
	
	void setParent(INode node)
	{
		this.parent = node;
	}
}
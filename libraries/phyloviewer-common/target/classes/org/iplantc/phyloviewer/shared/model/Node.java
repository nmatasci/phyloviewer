package org.iplantc.phyloviewer.shared.model;

import java.io.Serializable;
import java.util.List;

/**
 * A basic implementation of INode.
 */
public class Node extends AbstractNode implements Serializable
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

	public void setId(int id)
	{
		this.id = id;
	}

	@Override
	public void setLabel(String label)
	{
		this.label = label;
	}

	public void setChildren(List<? extends Node> children)
	{
		this.children = children;
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
				Node child = (Node) this.getChild(i);
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
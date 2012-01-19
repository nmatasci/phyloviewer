package org.iplantc.phyloviewer.shared.model;

import java.util.List;

/**
 * Some default implementations of INode methods that don't directly access state. Expect subtree
 * aggregate methods (getNumberOfNodes(), getNumberOfLeafNodes(), findMaximumDepthToLeaf(),
 * findMaximumDistanceToLeaf()), to traverse the entire subtree.
 */
public abstract class AbstractNode implements INode
{
	@Override
	public int getNumberOfChildren()
	{
		if (this.getChildren() == null)
		{
			return 0;
		}
		
		return this.getChildren().size();
	}

	@Override
	public INode getChild(int index)
	{
		if(getChildren() == null)
		{
			return null;
		}

		return getChildren().get(index);
	}

	@Override
	public Boolean isLeaf()
	{
		return getNumberOfChildren() == 0;
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
			for(INode child : getChildren())
			{
				count += child.getNumberOfLeafNodes();
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
	public int findMaximumDepthToLeaf()
	{
		int maxChildHeight = -1; // -1 so leaf will return 0

		List<? extends INode> children = getChildren();
		if (children != null) 
		{
			for(INode child : getChildren())
			{
				maxChildHeight = Math.max(maxChildHeight, child.findMaximumDepthToLeaf());
			}
		}

		return maxChildHeight + 1;
	}

	@Override
	public double findMaximumDistanceToLeaf()
	{
		double maxHeight = 0.0;
		
		List<? extends INode> children = getChildren();
		if (children != null) 
		{
			for(INode child : getChildren())
			{
				maxHeight = Math.max(maxHeight, child.findMaximumDepthToLeaf() + child.getBranchLength());
			}
		}

		return maxHeight;
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
	public boolean equals(Object obj)
	{
		if (this == obj) 
		{
			return true;
		}
		
		if(obj == null || !(obj instanceof INode))
		{
			return false;
		}

		INode that = (INode)obj;

		return this.getId() == that.getId();
	}
	
	@Override
	public String toString()
	{
		String s = getLabel();
		
		if (s == null)
		{
			s = "";
		}
		
		return s;
		
	}
}

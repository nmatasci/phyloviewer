package org.iplantc.phyloviewer.viewer.server;

import java.util.Comparator;
import java.util.Set;

import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloviewer.shared.model.INode;

public class PhyloparserNodeAdapter extends org.iplantc.phyloviewer.shared.model.Node
{
	Node phyloparserNode;
	PhyloparserNodeAdapter[] children; //children will get adapted lazily
	int numNodes;
	int numLeaves;
	
	public PhyloparserNodeAdapter(Node node)
	{
		this.phyloparserNode = node;
		int numChildren = node.getChildren().size();
		children = new PhyloparserNodeAdapter[numChildren];
	}
	
	@Override
	public String findLabelOfFirstLeafNode()
	{
		throw new UnsupportedOperationException("PhyloparserNodeAdapter does not support findLabelOfFirstLeafNode()");
	}

	@Override
	public Double getBranchLength()
	{
		return phyloparserNode.getBranchLength();
	}

	@Override
	public PhyloparserNodeAdapter getChild(int index)
	{
		if (children[index] == null) {
			children[index] = new PhyloparserNodeAdapter(phyloparserNode.getChildren().get(index));
		}
		
		return children[index];
	}

	@Override
	public PhyloparserNodeAdapter[] getChildren()
	{
		//make sure the children have all been adapted
		for (int index = 0; index < children.length; index++)
		{
			getChild(index);
		}
		
		return children;
	}

	@Override
	public String getLabel()
	{
		return phyloparserNode.getName();
	}

	@Override
	public String getMetaDataString()
	{
		throw new UnsupportedOperationException("PhyloparserNodeAdapter does not support getMetaDataString()");
	}

	@Override
	public int getNumberOfChildren()
	{
		return children.length;
	}

	@Override
	public int getNumberOfLeafNodes()
	{
		if (numLeaves == 0)
		{
			numLeaves = super.getNumberOfLeafNodes();
		}
		
		return numLeaves;
	}

	@Override
	public int getNumberOfNodes()
	{
		if (numNodes == 0)
		{
			numNodes = super.getNumberOfLeafNodes();
		}
		
		return numNodes;
	}

	@Override
	public Boolean isLeaf()
	{
		return children.length == 0;
	}

	@Override
	public INode mrca(Set<INode> nodes)
	{
		throw new UnsupportedOperationException("PhyloparserNodeAdapter does not support mrca()");
	}

	@Override
	public void setBranchLength(Double branchLength)
	{
		phyloparserNode.setBranchLength(branchLength);
	}

	@Override
	public void setLabel(String label)
	{
		phyloparserNode.setName(label);
	}

	@Override
	public void sortChildrenBy(Comparator<INode> comparator)
	{
		throw new UnsupportedOperationException("PhyloparserNodeAdapter does not support sortChildrenBy()");
	}

}

package org.iplantc.phyloviewer.viewer.server;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloparser.model.Node;

public class PhyloparserNodeAdapter extends org.iplantc.phyloviewer.shared.model.Node
{
	private static final long serialVersionUID = 1L;
	Node phyloparserNode;
	List<PhyloparserNodeAdapter> children; //children will get adapted lazily
	int numNodes;
	int numLeaves;
	
	public PhyloparserNodeAdapter(Node node)
	{
		this.phyloparserNode = node;
		int numChildren = node.getChildren().size();
		children = new ArrayList<PhyloparserNodeAdapter>(numChildren);
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
		if (children.get(index) == null) {
			children.set(index, new PhyloparserNodeAdapter(phyloparserNode.getChildren().get(index)));
		}
		
		return children.get(index);
	}

	@Override
	public List<PhyloparserNodeAdapter> getChildren()
	{
		//make sure the children have all been adapted
		for (int index = 0; index < children.size(); index++)
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
		return children.size();
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
		return children.size() == 0;
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
}

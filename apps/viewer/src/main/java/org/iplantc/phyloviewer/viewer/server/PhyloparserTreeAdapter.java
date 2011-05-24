package org.iplantc.phyloviewer.viewer.server;

import org.iplantc.phyloparser.model.Tree;
import org.iplantc.phyloviewer.shared.model.INode;

/**
 * Adapts a phyloparser Tree to our tree interface
 */
public class PhyloparserTreeAdapter extends org.iplantc.phyloviewer.shared.model.Tree
{
	Tree phyloparserTree;
	int size;
	
	public PhyloparserTreeAdapter(Tree tree)
	{
		this.phyloparserTree = tree;
		super.setRootNode(new PhyloparserNodeAdapter(phyloparserTree.getRoot()));
	}

	@Override
	public void setRootNode(INode node)
	{
		throw new UnsupportedOperationException("PhyloparserTreeAdapter does not support setRootNode()");
	}
}

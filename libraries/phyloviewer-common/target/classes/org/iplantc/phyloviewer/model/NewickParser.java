package org.iplantc.phyloviewer.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.block.Block;
import org.iplantc.phyloparser.model.block.TreesBlock;

public class NewickParser
{
	public NewickParser()
	{
	}

	public org.iplantc.phyloviewer.shared.model.Tree parse(File file)
	{
		org.iplantc.phyloparser.parser.NewickParser parser = new org.iplantc.phyloparser.parser.NewickParser();
		try
		{
			FileData data = parser.parse(file);
			return getTree(data);
		}
		catch(IOException e)
		{
		}
		catch(ParserException e)
		{
		}

		return null;
	}

	public org.iplantc.phyloviewer.shared.model.Tree parse(Reader reader)
	{
		org.iplantc.phyloparser.parser.NewickParser parser = new org.iplantc.phyloparser.parser.NewickParser();
		try
		{
			FileData data = parser.parse(reader, false);
			return getTree(data);
		}
		catch(IOException e)
		{
		}
		catch(ParserException e)
		{
		}

		return null;
	}

	private org.iplantc.phyloviewer.shared.model.Tree getTree(FileData data)
	{
		org.iplantc.phyloparser.model.Tree tree = null;

		List<Block> blocks = data.getBlocks();
		for(Block block : blocks)
		{
			if(block instanceof TreesBlock)
			{
				TreesBlock trees = (TreesBlock)block;
				tree = trees.getTrees().get(0);
			}
		}

		ConvertDataModels converter = new ConvertDataModels();
		org.iplantc.phyloviewer.shared.model.Node node = converter.convertDataModels(tree.getRoot());
		tree = null;

		org.iplantc.phyloviewer.shared.model.Tree myTree = new org.iplantc.phyloviewer.shared.model.Tree();
		myTree.setRootNode(node);

		return myTree;
	}

	private class ConvertDataModels
	{
		int nextId = 0;

		public org.iplantc.phyloviewer.shared.model.Node convertDataModels(
				org.iplantc.phyloparser.model.Node node)
		{
			ArrayList<org.iplantc.phyloviewer.shared.model.Node> children = new ArrayList<org.iplantc.phyloviewer.shared.model.Node>();

			int id = nextId++;

			for(Node myChild : node.getChildren())
			{
				children.add(convertDataModels(myChild));
			}

			String label = node.getName();

			// create a Node for the current node
			org.iplantc.phyloviewer.shared.model.Node rNode = new org.iplantc.phyloviewer.shared.model.Node(
					id, label);
			rNode.setChildren(children);
			rNode.setBranchLength(node.getBranchLength());

			return rNode;
		}
	}
}

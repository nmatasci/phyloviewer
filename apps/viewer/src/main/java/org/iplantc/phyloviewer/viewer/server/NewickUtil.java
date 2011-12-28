package org.iplantc.phyloviewer.viewer.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.block.Block;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloviewer.viewer.client.model.AnnotatedNode;
import org.iplantc.phyloviewer.viewer.client.model.LiteralMetaAnnotation;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;

public class NewickUtil
{

	public static RemoteNode rootNodeFromNewick(String newick) throws ParserException {
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "parsing newick");
		org.iplantc.phyloparser.model.Tree tree = NewickUtil.phyloparserTreeFromNewick(newick);
		if (tree == null)
		{
			return null;
		}
		
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "converting phyloparser.model.Tree to RemoteTree");
		RemoteNode root = NewickUtil.convertDataModels(tree.getRoot());
		
		root.reindex();
		
		return root;
	}

	public static RemoteTree treeFromNewick(String newick, String name) throws ParserException
	{
		RemoteTree tree = new RemoteTree(name);
		tree.setName(name);
		
		RemoteNode root = rootNodeFromNewick(newick);
		tree.setRootNode(root);
		
		return tree;
	}

	public static org.iplantc.phyloparser.model.Tree phyloparserTreeFromNewick(String newick) throws ParserException
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Parsing newick string");
		org.iplantc.phyloparser.parser.NewickParser parser = new org.iplantc.phyloparser.parser.NewickParser();
		FileData data = null;
		try {
			data = parser.parse(newick);
		} catch (IOException e) {
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "IOException parsing tree string: " + newick);
		} catch (ParserException e) {
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "ParserException parsing tree string: " + newick);
			throw e;
		}
		
		org.iplantc.phyloparser.model.Tree tree = null;
		
		List<Block> blocks = data.getBlocks();
		for ( Block block : blocks ) {
			if ( block instanceof TreesBlock ) {
				TreesBlock trees = (TreesBlock) block;
				tree = trees.getTrees().get( 0 );
			}
		}
		
		return tree;
	}

	public static AnnotatedNode convertDataModels(org.iplantc.phyloparser.model.Node parserNode) 
	{
		ArrayList<RemoteNode> children = new ArrayList<RemoteNode>(parserNode.getChildren().size());
	
		AnnotatedNode rNode = new AnnotatedNode();
		rNode.setLabel(parserNode.getName());
		
		//convert NHX annotations
		for (org.iplantc.phyloparser.model.Annotation annotation : parserNode.getAnnotations())
		{
			LiteralMetaAnnotation a = new LiteralMetaAnnotation();
	    	a.setProperty("NHX");
	    	a.setValue(annotation.getContent());
	    	rNode.addAnnotation(a);
		}
		
		for (Node parserChild : parserNode.getChildren()) {			
			children.add(convertDataModels(parserChild));
		}
		
		rNode.setChildren(children);
		
		Double branchLength = parserNode.getBranchLength();
		if (branchLength != null && branchLength > 0) {
			rNode.setBranchLength(branchLength);
		}
		
		return rNode;
	}

}

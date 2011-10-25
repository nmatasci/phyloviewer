package org.iplantc.phyloviewer.viewer.server;

import java.awt.image.BufferedImage;
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
import org.iplantc.phyloviewer.server.render.ImageGraphics;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.model.Document;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.iplantc.phyloviewer.shared.render.RenderTreeCladogram;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.nexml.model.Edge;

/**
 * Static methods removed from org.iplantc.phyloviewer.viewer.server.db.ImportTreeData
 */
public class ImportTreeUtil
{
	public static RemoteNode rootNodeFromNewick(String newick, String name) throws ParserException {
		org.iplantc.phyloparser.model.Tree tree = treeFromNewick(newick, name);
		if (tree == null)
		{
			return null;
		}
		
		RemoteNode root = convertDataModels(tree.getRoot());
		
		root.reindex();
		
		return root;
	}
	
	public static org.iplantc.phyloparser.model.Tree treeFromNewick(String newick, String name) throws ParserException
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
	
	public static BufferedImage renderTreeImage(Tree tree, ILayoutData layout,
			int width, int height) {

		ImageGraphics graphics = new ImageGraphics(width, height);

		RenderTreeCladogram renderer = new RenderTreeCladogram();
		renderer.getRenderPreferences().setCollapseOverlaps(false);
		renderer.getRenderPreferences().setDrawLabels(false);
		renderer.getRenderPreferences().setDrawPoints(false);

		Document document = new Document();
		document.setTree(tree);
		document.setLayout(layout);
		
		renderer.setDocument(document);
		renderer.renderTree(graphics, Matrix33.makeScale(width, height));

		return graphics.getImage();
	}
	
	public static RemoteNode convertDataModels(org.iplantc.phyloparser.model.Node parserNode) 
	{
		ArrayList<RemoteNode> children = new ArrayList<RemoteNode>(parserNode.getChildren().size());

		RemoteNode rNode = new RemoteNode(parserNode.getName());
		
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
	
	public static RemoteTree convertDataModels(org.nexml.model.Tree<Edge> in) {
		RemoteTree out = new RemoteTree();
		out.setName(in.getId());
		
		org.nexml.model.Node inRoot = findRoot(in);
		RemoteNode outRoot = convertDataModels(inRoot, in, null);
		out.setRootNode(outRoot);
		
		out.getRootNode().reindex();
		
		return out;
	}

	private static org.nexml.model.Node findRoot(org.nexml.model.Tree<Edge> tree)
	{
		org.nexml.model.Node root = tree.getRoot();
		
		if(root == null) {
			//no node has the actual attribute root="true", so check if there's a node without a parent.  Just return the first one found.
			for (org.nexml.model.Node node : tree.getNodes()) {
				if (tree.getInNodes(node).size() == 0) {
					return node;
				}
			}
		}
		
		return root;
	}

	private static RemoteNode convertDataModels(org.nexml.model.Node in, org.nexml.model.Tree<Edge> tree, Edge parentEdge)
	{
		String label = in.getLabel();
		RemoteNode out = new RemoteNode(label);
		
		ArrayList<RemoteNode> children = new ArrayList<RemoteNode>();
		for(org.nexml.model.Node child : tree.getOutNodes(in)) {
			Edge e = tree.getEdge(in, child);
			children.add(convertDataModels(child, tree, e));
		}
		
		out.setChildren(children);
		
		if (parentEdge != null && parentEdge.getLength() != null && parentEdge.getLength().doubleValue() > 0) {
			out.setBranchLength(parentEdge.getLength().doubleValue());
		}
		
		return out;
	}
}

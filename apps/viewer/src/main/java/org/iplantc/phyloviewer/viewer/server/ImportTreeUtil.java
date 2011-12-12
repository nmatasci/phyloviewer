package org.iplantc.phyloviewer.viewer.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

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
import org.iplantc.phyloviewer.viewer.client.model.AnnotatedNode;
import org.iplantc.phyloviewer.viewer.client.model.Annotation;
import org.iplantc.phyloviewer.viewer.client.model.LiteralMetaAnnotation;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.client.model.ResourceMetaAnnotation;
import org.nexml.model.DocumentFactory;
import org.nexml.model.Edge;
import org.nexml.model.Network;
import org.nexml.model.TreeBlock;
import org.xml.sax.SAXException;

/**
 * Static methods removed from org.iplantc.phyloviewer.viewer.server.db.ImportTreeData
 */
public class ImportTreeUtil
{
	public static RemoteNode rootNodeFromNewick(String newick, String name) throws ParserException {
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "parsing newick");
		org.iplantc.phyloparser.model.Tree tree = treeFromNewick(newick, name);
		if (tree == null)
		{
			return null;
		}
		
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "converting phyloparser.model.Tree to RemoteTree");
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
	
	public static RemoteTree convertDataModels(org.nexml.model.Tree<Edge> in) {
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "converting nexml to RemoteTree");
		RemoteTree out = new RemoteTree();
		out.setName(in.getId());
		
		org.nexml.model.Node inRoot = findRoot(in);
		RemoteNode outRoot = convertDataModels(inRoot, in, null);
		out.setRootNode(outRoot);
		
		((RemoteNode)out.getRootNode()).reindex();
		
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

	private static AnnotatedNode convertDataModels(org.nexml.model.Node in, org.nexml.model.Tree<Edge> tree, Edge parentEdge)
	{
		AnnotatedNode out = new AnnotatedNode();
		out.setLabel(in.getLabel());
		
		//convert annotations
		for (org.nexml.model.Annotation nexmlAnnotation : in.getAllAnnotations()) 
		{
			Annotation annotation = convertDataModels(nexmlAnnotation);
			out.addAnnotation(annotation);
		}
		
		//convert children
		ArrayList<RemoteNode> children = new ArrayList<RemoteNode>();
		for(org.nexml.model.Node child : tree.getOutNodes(in)) {
			Edge e = tree.getEdge(in, child);
			children.add(convertDataModels(child, tree, e));
		}
		
		out.setChildren(children);
		
		//copy branch length if it exists (else note: default branch length = 1.0)
		if (parentEdge != null && parentEdge.getLength() != null && parentEdge.getLength().doubleValue() > 0) {
			out.setBranchLength(parentEdge.getLength().doubleValue());
		}
		
		return out;
	}
	
	private static Annotation convertDataModels(org.nexml.model.Annotation nexmlAnnotation) 
	{
		Object value = nexmlAnnotation.getValue();
		Annotation a = null;
		if (value instanceof org.nexml.model.Annotation
				|| value instanceof Set
				|| value instanceof URI)
		{
			a = createResourceAnnotation(nexmlAnnotation);
		}
		else
		{
			a = createLiteralAnnotation(nexmlAnnotation);
		}
		
		URI predicateNamespaceURI = nexmlAnnotation.getPredicateNamespace();
		if (predicateNamespaceURI != null) 
		{
			a.setPredicateNamespace(predicateNamespaceURI.toString());
		}
		
		return a;
	}
	
	private static LiteralMetaAnnotation createLiteralAnnotation(org.nexml.model.Annotation nexmlAnnotation)
	{
		LiteralMetaAnnotation a = new LiteralMetaAnnotation();
		
		a.setProperty(nexmlAnnotation.getProperty());
		a.setDatatype(nexmlAnnotation.getXsdType().toString());
		a.setValue(nexmlAnnotation.getValue().toString());
		
		return a;
	}
	
	@SuppressWarnings("unchecked")
	private static ResourceMetaAnnotation createResourceAnnotation(org.nexml.model.Annotation nexmlAnnotation)
	{
		ResourceMetaAnnotation a = new ResourceMetaAnnotation();
		a.setRel(nexmlAnnotation.getRel());
		
		Object value = nexmlAnnotation.getValue();
		Set<org.nexml.model.Annotation> nestedAnnotations;
		if(value instanceof org.nexml.model.Annotation)
		{
			a.addAnnotation(convertDataModels((org.nexml.model.Annotation) value));
		}
		else if (value instanceof Set)
		{
			//note: in this case both nexmlAnnotation.getValue() and nexmlAnnotation.getAllAnnotations() will return different Set objects, but they both seem to have the same contents
			nestedAnnotations = (Set<org.nexml.model.Annotation>) value;
			for (org.nexml.model.Annotation nestedAnnotation : nestedAnnotations)
			{
				a.addAnnotation(convertDataModels(nestedAnnotation));
			}
		}
		else if (value instanceof URI)
		{
			a.setHref(value.toString());
			
			nestedAnnotations = nexmlAnnotation.getAllAnnotations();
			for (org.nexml.model.Annotation nestedAnnotation : nestedAnnotations)
			{
				a.addAnnotation(convertDataModels(nestedAnnotation));
			}
		}
		
		return a;
	}

	public static org.nexml.model.Document parse(String nexml) throws UnsupportedEncodingException,
			ParserConfigurationException, SAXException, IOException
	{
		InputStream stream = new ByteArrayInputStream(nexml.getBytes("UTF-8"));
		org.nexml.model.Document document = DocumentFactory.parse(stream);
		stream.close();
		return document;
	}
	
	/**
	 * @return a flattened list of all of the trees in all of the tree blocks in the document
	 */
	public static List<org.nexml.model.Tree<Edge>> getAllTrees(org.nexml.model.Document document) 
	{
		List<org.nexml.model.Tree<Edge>> trees = new ArrayList<org.nexml.model.Tree<Edge>>();
		
		for(TreeBlock block : document.getTreeBlockList())
		{
			for (Network<?> network : block) 
			{
				if (network instanceof org.nexml.model.Tree) {
					@SuppressWarnings("unchecked")
					org.nexml.model.Tree<Edge> tree = (org.nexml.model.Tree<Edge>) network;
					trees.add(tree);
				}
			}
		}
		
		return trees;
	}
}

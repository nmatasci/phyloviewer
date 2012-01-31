package org.iplantc.phyloviewer.viewer.server;

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

import org.iplantc.phyloviewer.shared.model.metadata.Annotation;
import org.iplantc.phyloviewer.viewer.client.model.AnnotatedNode;
import org.iplantc.phyloviewer.viewer.client.model.AnnotatedTree;
import org.iplantc.phyloviewer.viewer.client.model.AnnotationEntity;
import org.iplantc.phyloviewer.viewer.client.model.LiteralMetaAnnotation;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.ResourceMetaAnnotation;
import org.nexml.model.DocumentFactory;
import org.nexml.model.Edge;
import org.nexml.model.Network;
import org.nexml.model.TreeBlock;
import org.xml.sax.SAXException;

/**
 * Static utility methods for parsing and converting nexml strings and documents into ITrees
 */
public class NexmlUtil
{

	/**
	 * Parses the given string into a nexml model Document.
	 * @see DocumentFactory#parse(String)
	 */
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

	/**
	 * @return the first Tree in the document
	 */
	public static org.nexml.model.Tree<Edge> getFirstTree(org.nexml.model.Document document)
	{
		for(TreeBlock block : document.getTreeBlockList())
		{
			for (Network<?> network : block) 
			{
				if (network instanceof org.nexml.model.Tree) {
					@SuppressWarnings("unchecked")
					org.nexml.model.Tree<Edge> tree = (org.nexml.model.Tree<Edge>) network;
					return tree;
				}
			}
		}
		
		return null;
	}

	/**
	 * Create a new AnnotatedTree based on the given nexml model Tree.
	 */
	public static AnnotatedTree convertDataModels(org.nexml.model.Tree<Edge> in) {
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "converting nexml to RemoteTree");
		AnnotatedTree out = new AnnotatedTree();
		out.setName(in.getId());
		
		//convert annotations
		for (org.nexml.model.Annotation nexmlAnnotation : in.getAllAnnotations()) 
		{
			Annotation annotation = convertDataModels(nexmlAnnotation);
			out.addAnnotation(annotation);
		}
		
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
		AnnotationEntity a = null;
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
}

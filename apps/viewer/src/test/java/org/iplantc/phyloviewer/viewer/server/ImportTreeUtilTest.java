package org.iplantc.phyloviewer.viewer.server;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloviewer.viewer.client.model.AnnotatedNode;
import org.iplantc.phyloviewer.viewer.client.model.Annotation;
import org.iplantc.phyloviewer.viewer.client.model.LiteralMetaAnnotation;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.client.model.ResourceMetaAnnotation;
import org.junit.Test;
import org.nexml.model.Document;
import org.nexml.model.DocumentFactory;
import org.nexml.model.Edge;
import org.nexml.model.Node;
import org.nexml.model.Tree;
import org.xml.sax.SAXException;

public class ImportTreeUtilTest
{
	String newick = "(a:1,(ba:1[&&NHX:someAnnotation],bb:2)b:2)r;";
	Tree<Edge> nexmlTree;

	@Test
	public void testConvertDataModelsNewick() throws ParserException
	{
		String name = "node";
		double branchLength = 42.0;
		org.iplantc.phyloparser.model.Node parserNode = new org.iplantc.phyloparser.model.Node(name, branchLength);
		
		AnnotatedNode node = ImportTreeUtil.convertDataModels(parserNode);
		assertEquals(branchLength, node.getBranchLength().doubleValue(), 0.0);
		assertEquals(name, node.getLabel());
		
		org.iplantc.phyloparser.model.Tree phyloparserTree = ImportTreeUtil.treeFromNewick(newick, null);
		
		node = ImportTreeUtil.convertDataModels(phyloparserTree.getRoot());
		assertEquals(1.0, node.getBranchLength().doubleValue(), 0.0);
		assertEquals("r", node.getLabel());
		
		node = (AnnotatedNode) node.getChild(1);
		assertEquals(2.0, node.getBranchLength().doubleValue(), 0.0);
		assertEquals("b", node.getLabel());
		
		node = (AnnotatedNode) node.getChild(0);
		assertEquals(1.0, node.getBranchLength().doubleValue(), 0.0);
		assertEquals("ba", node.getLabel());
		LiteralMetaAnnotation annotation = (LiteralMetaAnnotation) node.getAnnotations("NHX").iterator().next();
		assertEquals("&&NHX:someAnnotation", annotation.getValue());
	}

	@Test
	public void testConvertDataModelsNexml() throws SAXException, IOException, ParserConfigurationException
	{
		URL url = this.getClass().getResource("/trees.xml");
		nexmlTree = getFirstTree(url);
		
		RemoteTree out = ImportTreeUtil.convertDataModels(nexmlTree);
		assertEquals(nexmlTree.getId(), out.getName());
		
		Node rootIn = nexmlTree.getRoot();
		AnnotatedNode rootOut = (AnnotatedNode) out.getRootNode();
		assertEquals(rootIn.getLabel(), rootOut.getLabel());
		
		Node childIn = nexmlTree.getOutNodes(rootIn).iterator().next();
		AnnotatedNode childOut = (AnnotatedNode) rootOut.getChild(0);
		assertEquals(childIn.getLabel(), childOut.getLabel());
		assertEquals(rootIn.getAllAnnotations().size(), rootOut.getAnnotations().size());
//		Edge edge = in.getEdge(rootIn, childIn);
//		assertEquals(edge.getLength().doubleValue(), nodeOut.getBranchLength().doubleValue(), 0.0); //FIXME get rid of RemoteNode default branch length = 1.0?

		Set<Annotation> annotations = rootOut.getAnnotations("intmeta");
		assertEquals(1, annotations.size());
		LiteralMetaAnnotation annotation = (LiteralMetaAnnotation) annotations.iterator().next();
		assertEquals("1", annotation.getValue());
		
		annotations = rootOut.getAnnotations("stringmeta");
		assertEquals(1, annotations.size());
		annotation = (LiteralMetaAnnotation) annotations.iterator().next();
		assertEquals("some string", annotation.getValue());
		
		annotations = rootOut.getAnnotations("booleanmeta");
		assertEquals(1, annotations.size());
		annotation = (LiteralMetaAnnotation) annotations.iterator().next();
		assertEquals(Boolean.TRUE.toString(), annotation.getValue());
		
		annotations = rootOut.getAnnotations("urimeta");
		assertEquals(1, annotations.size());
		ResourceMetaAnnotation resourceMetaAnnotation = (ResourceMetaAnnotation) annotations.iterator().next();
		assertEquals("http://example.org/path/to/resource", resourceMetaAnnotation.getHref());
		
		//now some nested meta elements
		annotations = resourceMetaAnnotation.getAnnotations("nestedresourcemeta");
		assertEquals(1, annotations.size());
		resourceMetaAnnotation = (ResourceMetaAnnotation) annotations.iterator().next();
		annotations = resourceMetaAnnotation.getAnnotations("nestedliteralmeta");
		assertEquals(1, annotations.size());
		annotation = (LiteralMetaAnnotation) annotations.iterator().next();
		assertEquals("some other string", annotation.getValue());
		
	}

	private Tree<Edge> getFirstTree(URL url) throws SAXException, IOException, ParserConfigurationException
	{
		File file = new File(url.getFile());
		Document document = DocumentFactory.parse(file);
		return ImportTreeUtil.getAllTrees(document).get(0);
	}
}

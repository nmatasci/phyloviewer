package org.iplantc.phyloviewer.viewer.server;

import static org.junit.Assert.assertEquals;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloviewer.viewer.client.model.AnnotatedNode;
import org.iplantc.phyloviewer.viewer.client.model.LiteralMetaAnnotation;
import org.junit.Test;

public class NewickUtilTest
{
	String newick = "(a:1,(ba:1[&&NHX:someAnnotation],bb:2)b:2)r;";
	
	@Test
	public void testConvertDataModels() throws ParserException
	{
		String name = "node";
		double branchLength = 42.0;
		org.iplantc.phyloparser.model.Node parserNode = new org.iplantc.phyloparser.model.Node(name, branchLength);
		
		AnnotatedNode node = NewickUtil.convertDataModels(parserNode);
		assertEquals(branchLength, node.getBranchLength().doubleValue(), 0.0);
		assertEquals(name, node.getLabel());
		
		org.iplantc.phyloparser.model.Tree phyloparserTree = NewickUtil.phyloparserTreeFromNewick(newick);
		
		node = NewickUtil.convertDataModels(phyloparserTree.getRoot());
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
	
	
}

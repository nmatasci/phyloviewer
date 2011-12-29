package org.iplantc.phyloviewer.viewer.client.model;

import org.iplantc.phyloviewer.viewer.server.persistence.PersistenceTest;
import org.junit.Test;

public class AnnotatedNodeTest extends PersistenceTest
{

	@Test
	public void testPersistAnnotatedNode() throws SecurityException, NoSuchMethodException
	{
		LiteralMetaAnnotation literal = new LiteralMetaAnnotation();
		literal.setPredicateNamespace("predicateNamespace");
		literal.setDatatype("datatype");
		literal.setProperty("property");
		literal.setValue("value");
		
		AnnotatedNode node = new AnnotatedNode();
		node.addAnnotation(literal);
		
		testPersist(node, AnnotatedNode.class.getMethod("getId"));
	}

}

package org.iplantc.phyloviewer.viewer.client.model;

import org.iplantc.phyloviewer.viewer.server.persistence.PersistenceTest;
import org.junit.Test;

public class AnnotatedTreeTest extends PersistenceTest
{
	@Test
	public void testPersistAnnotatedNode() throws SecurityException, NoSuchMethodException
	{
		LiteralMetaAnnotation literal = new LiteralMetaAnnotation();
		literal.setPredicateNamespace("predicateNamespace");
		literal.setDatatype("datatype");
		literal.setProperty("property");
		literal.setValue("value");
		
		AnnotatedTree tree = new AnnotatedTree();
		tree.addAnnotation(literal);
		
		testPersist(tree, AnnotatedTree.class.getMethod("getId"));
	}

}

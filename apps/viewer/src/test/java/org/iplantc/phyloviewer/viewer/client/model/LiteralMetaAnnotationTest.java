package org.iplantc.phyloviewer.viewer.client.model;

import org.iplantc.phyloviewer.viewer.server.persistence.PersistenceTest;
import org.junit.Test;

public class LiteralMetaAnnotationTest extends PersistenceTest
{

	@Test
	public void testPersistLiteralMetaAnnotation() throws SecurityException, NoSuchMethodException
	{
		LiteralMetaAnnotation anno = new LiteralMetaAnnotation();
		anno.setDatatype("datatype");
		anno.setPredicateNamespace("predicateNamespace");
		anno.setProperty("propertyValue");
		anno.setValue("value");
		
		testPersist(anno, LiteralMetaAnnotation.class.getMethod("getId"));
	}
}

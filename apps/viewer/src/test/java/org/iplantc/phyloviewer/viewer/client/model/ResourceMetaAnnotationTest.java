package org.iplantc.phyloviewer.viewer.client.model;

import org.iplantc.phyloviewer.viewer.server.persistence.PersistenceTest;
import org.junit.Test;

public class ResourceMetaAnnotationTest extends PersistenceTest
{

	
	@Test
	public void testPersistResourceMetaAnnotation() throws SecurityException, NoSuchMethodException
	{
		ResourceMetaAnnotation resource = new ResourceMetaAnnotation();
		resource.setRel("rel");
		
		ResourceMetaAnnotation nestedResource = new ResourceMetaAnnotation();
		nestedResource.setHref("href");
		nestedResource.setRel("rel");
		resource.addAnnotation(nestedResource);
		
		testPersist(resource, ResourceMetaAnnotation.class.getMethod("getId"));
	}
	

}

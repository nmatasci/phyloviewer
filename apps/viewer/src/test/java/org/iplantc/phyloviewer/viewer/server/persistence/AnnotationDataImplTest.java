package org.iplantc.phyloviewer.viewer.server.persistence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.iplantc.phyloviewer.shared.model.metadata.AnnotationMetadata;
import org.iplantc.phyloviewer.viewer.client.model.AnnotatedNode;
import org.iplantc.phyloviewer.viewer.client.model.LiteralMetaAnnotation;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.junit.Before;
import org.junit.Test;

public class AnnotationDataImplTest extends PersistenceTest
{
	private RemoteTree tree;
	
	@Before
	public void before() {
		//persist an annotated tree.
		
		tree = new RemoteTree();
		
		AnnotatedNode root = new AnnotatedNode();
		AnnotatedNode child = new AnnotatedNode();
		root.addChild(child);
		tree.setRootNode(root);
		
		LiteralMetaAnnotation annotation1 = new LiteralMetaAnnotation();
		annotation1.setDatatype("xsd:double");
		annotation1.setProperty("someDoubleProperty");
		annotation1.setValue("42.0");
		
		LiteralMetaAnnotation annotation2 = new LiteralMetaAnnotation();
		annotation2.setDatatype("xsd:string");
		annotation2.setProperty("someStringProperty");
		annotation2.setValue("value");
		
		LiteralMetaAnnotation annotation3 = new LiteralMetaAnnotation();
		annotation3.setDatatype("xsd:string");
		annotation3.setProperty("someStringProperty");
		annotation3.setValue("another value");
		
		root.addAnnotation(annotation1);
		root.addAnnotation(annotation2);
		child.addAnnotation(annotation3);
		
		tree.getRootNode().reindex();
		
		EntityManager em = PersistenceTest.entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(tree);
		em.getTransaction().commit();
		em.close();
	}
	
	@Test
	public void testGetAnnotationMetadataRemoteTree()
	{
		AnnotationDataImpl data = new AnnotationDataImpl(PersistenceTest.entityManagerFactory);
		List<AnnotationMetadata> metadata = data.getAnnotationMetadata(tree);
		assertTrue(metadata.size() == 2);
		//TODO implement AnnotationMetadataImpl.equals() so I can test whether the list contains the expected elements
	}

	@Test
	public void testGetAnnotationMetadataRemoteTreeString()
	{
		AnnotationDataImpl data = new AnnotationDataImpl(PersistenceTest.entityManagerFactory);
		
		List<AnnotationMetadata> metadata = data.getAnnotationMetadata(tree, "someStringProperty");
		assertNotNull(metadata);
		assertEquals(1, metadata.size());
		AnnotationMetadata metadatum = metadata.get(0);
		assertEquals("someStringProperty", metadatum.getName());
		assertEquals(String.class, metadatum.getDatatype());
		
		metadata = data.getAnnotationMetadata(tree, "someDoubleProperty");
		assertNotNull(metadata);
		assertEquals(1, metadata.size());
		metadatum = metadata.get(0);
		assertEquals("someDoubleProperty", metadatum.getName());
		assertEquals(Double.class, metadatum.getDatatype());
	}

}

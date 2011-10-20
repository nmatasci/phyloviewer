package org.iplantc.phyloviewer.viewer.server.persistence;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.server.persistence.PersistTreeData;
import org.iplantc.phyloviewer.viewer.server.persistence.UnpersistTreeData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPersistTreeData
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testImportFromNewick() throws Exception
	{
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.iplantc.phyloviewer");
		PersistTreeData out = new PersistTreeData(emf);
		int treeID = out.importFromNewick("(Protomyces_inouyei,(Taphrina_wiesneri,Taphrina_deformans));", "test");
		
		UnpersistTreeData in = new UnpersistTreeData(emf);
		EntityManager em = emf.createEntityManager();
		RemoteNode root = in.getRootNode(treeID, em);
		
		RemoteNode node = (RemoteNode) root.getChild(0);
		assertEquals("Protomyces_inouyei", node.getLabel());
		
		node = (RemoteNode) root.getChild(1).getChild(0);
		assertEquals("Taphrina_wiesneri", node.getLabel());
		
		node = (RemoteNode) root.getChild(1).getChild(1);
		assertEquals("Taphrina_deformans", node.getLabel());
	}
	
	@Test
	public void testDuplicateTree() throws Exception
	{
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.iplantc.phyloviewer");
		PersistTreeData out = new PersistTreeData(emf);
		int treeID = out.importFromNewick("(Protomyces_inouyei,(Taphrina_wiesneri,Taphrina_deformans));", "test");
		int treeID2 = out.importFromNewick("(Protomyces_inouyei,(Taphrina_wiesneri,Taphrina_deformans));", "duplicate");
		
		UnpersistTreeData in = new UnpersistTreeData(emf);
		RemoteNode root = in.getRootNode(treeID);
		RemoteNode root2 = in.getRootNode(treeID2);
		assertEquals("not the same root node", root, root2);
	}

}

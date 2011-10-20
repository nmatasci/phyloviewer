package org.iplantc.phyloviewer.viewer.server.persistence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UnpersistTreeDataTest
{
	static EntityManagerFactory emf;
	static int treeID;
	static int nodeID;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		emf = Persistence.createEntityManagerFactory("org.iplantc.phyloviewer");
		PersistTreeData out = new PersistTreeData(emf);
		treeID = out.importFromNewick("(A,(B,C)D)root;", "test");
		
		out.importFromNewick("(A,(B,C)D)root;", "test2"); //a duplicate tree will create a tree record, but no node records
		
		//save another tree that I have a nodeID for
		RemoteNode node = new RemoteNode("root");
		node.addChild(new RemoteNode("child1"));
		node.addChild(new RemoteNode("child2"));
		RemoteTree tree = new RemoteTree("test3");
		tree.setRootNode(node);
		
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(tree);
		em.getTransaction().commit();
		nodeID = node.getId();
		
		em.close();
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
	public void testGetRootNode() throws TreeDataException
	{
		UnpersistTreeData in = new UnpersistTreeData(emf);
		RemoteNode root = in.getRootNode(treeID);
		assertEquals("root", root.getLabel());
	}

	@Test
	public void testGetTrees() throws TreeDataException
	{
		UnpersistTreeData in = new UnpersistTreeData(emf);
		List<ITree> trees = in.getTrees();
		assertEquals(3, trees.size());
	}

	@Test
	public void testGetChildren() throws TreeDataException
	{
		UnpersistTreeData in = new UnpersistTreeData(emf);
		List<RemoteNode> children = in.getChildren(nodeID);
		assertEquals(2, children.size());
		assertEquals("child1", children.get(0).getLabel());
		assertEquals("child2", children.get(1).getLabel());
	}

}

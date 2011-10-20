package org.iplantc.phyloviewer.viewer.server.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.junit.Test;

public class RemoteNodePersistTest extends PersistenceTest
{
	private int nodeCount = 0;

	@Test
	public void test()
	{
		//make a little tree
		RemoteNode cavemanLawyer = createTree(1, 2);
		
		persist(cavemanLawyer);
		
		//pull it back out
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		RemoteNode unfrozenCavemanLawyer = get(cavemanLawyer.getId(), entityManager);
		
		//check that tree is same
		assertEquals(cavemanLawyer, unfrozenCavemanLawyer);
		assertEquals(cavemanLawyer.getBranchLength(), unfrozenCavemanLawyer.getBranchLength());
		List<RemoteNode> unfrozenChildren = unfrozenCavemanLawyer.getChildren();
		assertNotNull(unfrozenChildren);
		assertEquals(2, unfrozenChildren.size());
		assertEquals(cavemanLawyer.getChild(0), unfrozenChildren.get(0));
		assertEquals(cavemanLawyer.getChild(1), unfrozenChildren.get(1));
		
		entityManager.close();
	}

	@Test(expected=org.hibernate.LazyInitializationException.class)
	public void testLazy()
	{
		RemoteNode tree = createTree(2, 2);
		persist(tree);
		
		//pull it back out
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		RemoteNode newTree = get(tree.getId(), entityManager);
		
		//but close the entitymanager before trying to get children.  should throw exception.
		entityManager.detach(newTree);
		entityManager.close();
		
		newTree.getChildren().get(0);
	}
	
	//@Test
	public void testBigTree()
	{
		int depth = 10;
		int numChildren = 2;
		long startTime;
		this.nodeCount = 0;
		
		System.out.print("building tree...");
		startTime = System.currentTimeMillis();
		RemoteNode root = createTree(depth, numChildren);
		root.reindex(0, 1);
		System.out.println(System.currentTimeMillis() - startTime + " ms");
		System.out.println(nodeCount + " nodes");
		
		persist(root);
		
		//pull the root back out
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		
		System.out.println("unpersisting root...");
		startTime = System.currentTimeMillis();
		TypedQuery<RemoteNode> query = entityManager.createQuery("SELECT n FROM RemoteNode n WHERE n.id = :id ", RemoteNode.class)
				.setParameter("id", root.getId());
		
		@SuppressWarnings("unused")
		RemoteNode copyOfRoot = query.getSingleResult();
		System.out.println(System.currentTimeMillis() - startTime + " ms");
		
		entityManager.close();
		
		assertTrue(true);
		
	}

	private void persist(RemoteNode root)
	{
		long startTime;
		//persist the root
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		System.out.println("persisting tree...");
		startTime = System.currentTimeMillis();
		entityManager.persist(root);
		entityManager.getTransaction().commit();
		System.out.println(System.currentTimeMillis() - startTime + " ms");
		
		entityManager.close();
	}
	
	private RemoteNode get(int id, EntityManager entityManager)
	{
		//pull the root back out of persistence
		entityManager.getTransaction().begin();
		Query query = entityManager.createQuery("SELECT n FROM RemoteNode n WHERE n.id = :id")
				.setParameter("id", id);

		RemoteNode unfrozenCavemanLawyer = (RemoteNode) query.getSingleResult();
		
		return unfrozenCavemanLawyer;
	}
	

	private RemoteNode createTree(int depth, int numChildren)
	{
		RemoteNode node = new RemoteNode("");
		nodeCount++;
		
		createTree(node, depth, numChildren);
		
		return node;
	}

	private void createTree(RemoteNode node, int depth, int numChildren)
	{
		if (depth == 0) {
			return;
		} else {
			List<RemoteNode> children = createChildren(numChildren);
			node.setChildren(children);
			
			for (RemoteNode child : children) {
				createTree(child, depth - 1, numChildren);
			}
		}
	}

	private List<RemoteNode> createChildren(int numChildren)
	{
		ArrayList<RemoteNode> children = new ArrayList<RemoteNode>();
		RemoteNode child;
		for (int i = 0; i < numChildren; i++) {
			child = new RemoteNode("");
			nodeCount++;
			children.add(child);
		}
		
		return children;
	}
	

}

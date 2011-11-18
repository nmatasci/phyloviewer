package org.iplantc.phyloviewer.viewer.server.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.junit.Test;

public class RemoteTreePersistTest extends PersistenceTest
{
	@Test
	public void test()
	{
		//make a little tree
		RemoteNode root = new RemoteNode();
		root.addChild(new RemoteNode());
		RemoteTree tree = new RemoteTree();
		tree.setRootNode(root);
		EntityManager em = entityManagerFactory.createEntityManager();
		persist(tree, em);
		
		//pull it back out and compare
		RemoteTree tree2 = getTreeAttached(tree.getId(), em);
		assertEquals(tree, tree2);
		assertEquals(tree.getRootNode(), tree2.getRootNode());
		
		em.close();
	}
	
	@Test
	public void testManyTreesOneRoot()
	{
		EntityManager em = entityManagerFactory.createEntityManager();
		
		//make a little tree
		RemoteNode root = new RemoteNode();
		root.addChild(new RemoteNode());
		
		//make and persist a tree
		RemoteTree tree = new RemoteTree();
		tree.setRootNode(root);
		persist(tree, em);
		em.close();
		
		//pull that tree out and create another tree with the same root node
		em = entityManagerFactory.createEntityManager();
		RemoteTree treeOut = getTreeAttached(tree.getId(), em);
		RemoteTree tree2 = new RemoteTree();
		tree2.setRootNode(treeOut.getRootNode());
		persist(tree2, em);
		RemoteTree tree2Out = getTreeAttached(tree2.getId(), em);
		
		assertEquals(tree, treeOut);
		assertEquals(tree2, tree2Out);
		assertFalse(treeOut == tree2Out);
		assertFalse(treeOut.equals(tree2Out));
		
		assertEquals(tree.getRootNode(), tree2.getRootNode());
		
		em.close();
	}
	
	private void persist(RemoteTree tree, EntityManager entityManager)
	{
		entityManager.getTransaction().begin();
		
		entityManager.persist(tree);
		entityManager.getTransaction().commit();
	}
	
	private RemoteTree getTreeAttached(int id, EntityManager entityManager) {
		//pull the tree back out of persistence
		entityManager.getTransaction().begin();
		Query query = entityManager.createQuery("SELECT n FROM RemoteTree n WHERE n.id = :id")
				.setParameter("id", id);

		RemoteTree tree = (RemoteTree) query.getSingleResult();
		entityManager.getTransaction().commit();
		
		return tree;
	}
	
	
}

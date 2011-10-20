package org.iplantc.phyloviewer.viewer.server.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;

import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.junit.BeforeClass;
import org.junit.Test;

public class UnpersistTreeDataTest extends PersistenceTest
{
	static RemoteTree[] trees;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		trees = new RemoteTree[3];
		PersistTreeData out = new PersistTreeData(entityManagerFactory);
		trees[0] = out.importFromNewick("(A,(B,C)D)root1;", "test");
		trees[1] = out.importFromNewick("(A,(B,C)D)root2;", "test2"); //a duplicate tree will create a tree record, but no node records
		trees[2] = out.importFromNewick("(child1, child2)root3;", "test3");
	}

	@Test
	public void testGetRootNode() throws TreeDataException
	{
		UnpersistTreeData in = new UnpersistTreeData(entityManagerFactory);
		RemoteNode root = in.getRootNode(trees[0].getId());
		assertEquals("root1", root.getLabel());
	}

	@Test
	public void testGetTrees() throws TreeDataException
	{
		UnpersistTreeData in = new UnpersistTreeData(entityManagerFactory);
		List<ITree> treesRetrieved = in.getTrees();
		
		for (RemoteTree tree : trees) {
			assertTrue(treesRetrieved.contains(tree));
		}
	}

	@Test
	public void testGetChildren() throws TreeDataException
	{
		UnpersistTreeData in = new UnpersistTreeData(entityManagerFactory);
		List<RemoteNode> children = in.getChildren(trees[2].getRootNode().getId());
		assertEquals(2, children.size());
		assertEquals("child1", children.get(0).getLabel());
		assertEquals("child2", children.get(1).getLabel());
	}

}

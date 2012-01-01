package org.iplantc.phyloviewer.viewer.server.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.iplantc.phyloviewer.viewer.server.NewickUtil;
import org.junit.BeforeClass;
import org.junit.Test;

public class UnpersistTreeDataTest extends PersistenceTest
{
	static RemoteTree[] trees;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		trees = new RemoteTree[3];
		
		trees[0] = NewickUtil.treeFromNewick("(A,(B,C)D)root1;", "test");
		trees[0].setHash(new byte[] {1,1,1,1,1,1,1,1});
		trees[1] = NewickUtil.treeFromNewick("(A,(B,C)D)root2;", "test2"); //a duplicate tree will create a tree record, but no node records
		trees[1].setHash(new byte[] {2,2,2,2,2,2,2,2});
		trees[2] = NewickUtil.treeFromNewick("(child1, child2)root3;", "test3");
		trees[1].setHash(new byte[] {3,3,3,3,3,3,3,3});
		
		PersistTreeData out = new PersistTreeData(entityManagerFactory);
		out.importTree(trees[0]);
		out.importTree(trees[1]);
		out.importTree(trees[2]);
	}

	@Test
	public void testGetRootNode() throws TreeDataException
	{
		UnpersistTreeData in = new UnpersistTreeData(entityManagerFactory);
		RemoteNode root = in.getRootNode(trees[0].getHash());
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

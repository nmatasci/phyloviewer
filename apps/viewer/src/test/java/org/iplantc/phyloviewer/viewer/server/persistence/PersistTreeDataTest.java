package org.iplantc.phyloviewer.viewer.server.persistence;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.junit.Test;

public class PersistTreeDataTest extends PersistenceTest
{
	@Test
	public void testImportFromNewick() throws Exception
	{
		PersistTreeData out = new PersistTreeData(entityManagerFactory);
		RemoteTree tree = out.importFromNewick("(Protomyces_inouyei,(Taphrina_wiesneri,Taphrina_deformans));", "test");
		
		UnpersistTreeData in = new UnpersistTreeData(entityManagerFactory);
		EntityManager em = entityManagerFactory.createEntityManager();
		RemoteNode root = in.getRootNode(tree.getId(), em);
		
		RemoteNode node = (RemoteNode) root.getChild(0);
		assertEquals("Protomyces_inouyei", node.getLabel());
		
		node = (RemoteNode) root.getChild(1).getChild(0);
		assertEquals("Taphrina_wiesneri", node.getLabel());
		
		node = (RemoteNode) root.getChild(1).getChild(1);
		assertEquals("Taphrina_deformans", node.getLabel());
		
		em.close();
	}
	
	@Test
	public void testDuplicateTree() throws Exception
	{
		PersistTreeData out = new PersistTreeData(entityManagerFactory);
		int treeID = out.importFromNewick("(Protomyces_inouyei,(Taphrina_wiesneri,Taphrina_deformans));", "test").getId();
		int treeID2 = out.importFromNewick("(Protomyces_inouyei,(Taphrina_wiesneri,Taphrina_deformans));", "duplicate").getId();
		
		UnpersistTreeData in = new UnpersistTreeData(entityManagerFactory);
		RemoteNode root = in.getRootNode(treeID);
		RemoteNode root2 = in.getRootNode(treeID2);
		assertEquals("not the same root node", root, root2);
	}
	
	@Test
	public void testHashTree() {
		RemoteTree[] trees = new RemoteTree[5];
		byte[][] hashes = new byte[trees.length][16];
		
		//root, 2 children, empty labels
		RemoteNode node = new RemoteNode("");
		node.addChild(new RemoteNode(""));
		node.addChild(new RemoteNode(""));
		node.reindex();
		trees[0] = new RemoteTree();
		trees[0].setRootNode(node);
		
		//root, 2 children, with labels
		node = new RemoteNode("A");
		node.addChild(new RemoteNode("B"));
		node.addChild(new RemoteNode("C"));
		node.reindex();
		trees[1] = new RemoteTree();
		trees[1].setRootNode(node);
		
		//root, 2 children swapped, with labels
		node = new RemoteNode("A");
		node.addChild(new RemoteNode("C"));
		node.addChild(new RemoteNode("B"));
		node.reindex();
		trees[2] = new RemoteTree();
		trees[2].setRootNode(node);
		
		//root, 2 children, empty labels, with different branch lengths
		node = new RemoteNode("");
		node.setBranchLength(2.0);
		RemoteNode child = new RemoteNode("");
		child.setBranchLength(42.0);
		node.addChild(child);
		child = new RemoteNode("");
		child.setBranchLength(1.0);
		node.addChild(child);
		node.reindex();
		trees[3] = new RemoteTree();
		trees[3].setRootNode(node);
		
		//root, child, grandchild
		node = new RemoteNode("");
		child = new RemoteNode("");
		node.addChild(child);
		child.addChild(new RemoteNode(""));
		node.reindex();
		trees[4] = new RemoteTree();
		trees[4].setRootNode(node);
		
		for (int t = 0; t < trees.length; t++) {
			hashes[t] = PersistTreeData.hashTree(trees[t]);
		}
		
		//loop over hashes
		for (int i = 0; i < hashes.length; i++) {
			//loop over other hashes
			for (int j = i + 1; j < hashes.length; j++) {
				boolean same = true;
				//loop over bytes
				for (int b = 0; b < hashes[i].length; b++) {
					same &= hashes[i][b] == hashes[j][b];
				}
				Assert.assertFalse("hashes " + i + " and " + j + " are the same: \n" + Arrays.toString(hashes[i]) + "\n" + Arrays.toString(hashes[j]), same);
			}
		}
	}

}

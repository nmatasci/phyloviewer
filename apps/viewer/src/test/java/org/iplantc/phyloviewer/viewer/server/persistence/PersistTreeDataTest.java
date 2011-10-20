package org.iplantc.phyloviewer.viewer.server.persistence;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;

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

}

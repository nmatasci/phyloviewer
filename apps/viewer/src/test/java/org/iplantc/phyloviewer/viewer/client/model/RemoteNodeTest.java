package org.iplantc.phyloviewer.viewer.client.model;

import static org.iplantc.phyloviewer.viewer.client.model.RemoteNode.rn;
import static org.junit.Assert.assertEquals;

import org.iplantc.phyloviewer.viewer.server.persistence.PersistenceTest;
import org.junit.Test;

public class RemoteNodeTest extends PersistenceTest
{
	
	@Test
	public void testReindex()
	{
		//single node tree
		RemoteNode root = new RemoteNode();
		root.setLabel("root");
		
		root.reindex();
		NodeTopology topology = root.getTopology();
		
		assertEquals("root", topology.getAltLabel());
		assertEquals(0.0, topology.getBranchLengthHeight(), 0.0);
		assertEquals(topology.getDepth(), 0);
		assertEquals(topology.getHeight(), 0);
		assertEquals(topology.getLeftIndex(), 1);
		assertEquals(topology.getNumChildren(), 0);
		assertEquals(topology.getNumLeaves(), 1);
		assertEquals(topology.getNumNodes(), 1);
		assertEquals(topology.getRightIndex(), 2);
		assertEquals(topology.getRootNode(), root);
		
		//small tree
		root = rn(null, 0.0,
				rn("Protomyces_inouyei", 1.0),
				rn(null, 2.0,
					rn("Taphrina_wiesneri", 3.0),
					rn("Taphrina_deformans", 4.0)
				)
			);
		
		root.reindex();
		
		//check the root node
		topology = root.getTopology();
		assertEquals("Protomyces_inouyei", topology.getAltLabel());
		assertEquals(6.0, topology.getBranchLengthHeight(), 0.0);
		assertEquals(topology.getDepth(), 0);
		assertEquals(topology.getHeight(), 2);
		assertEquals(topology.getLeftIndex(), 1);
		assertEquals(topology.getNumChildren(), 2);
		assertEquals(topology.getNumLeaves(), 3);
		assertEquals(topology.getNumNodes(), 5);
		assertEquals(topology.getRightIndex(), 10);
		assertEquals(topology.getRootNode(), root);
		
		//check the unnamed internal node
		topology = ((RemoteNode)root.getChild(1)).getTopology();
		assertEquals("Taphrina_wiesneri", topology.getAltLabel());
		assertEquals(4.0, topology.getBranchLengthHeight(), 0.0);
		assertEquals(topology.getDepth(), 1);
		assertEquals(topology.getHeight(), 1);
		assertEquals(topology.getLeftIndex(), 4);
		assertEquals(topology.getNumChildren(), 2);
		assertEquals(topology.getNumLeaves(), 2);
		assertEquals(topology.getNumNodes(), 3);
		assertEquals(topology.getRightIndex(), 9);
		assertEquals(topology.getRootNode(), root);
	}
	
	@Test
	public void testPersistRemoteNode() throws SecurityException, NoSuchMethodException
	{
		RemoteNode node = new RemoteNode();
		node.setBranchLength(42.0);
		node.setLabel("label");
		
		testPersist(node, RemoteNode.class.getMethod("getId"));
	}
}

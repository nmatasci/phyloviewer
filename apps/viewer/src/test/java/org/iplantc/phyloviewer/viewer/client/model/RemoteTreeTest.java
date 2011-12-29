package org.iplantc.phyloviewer.viewer.client.model;

import org.iplantc.phyloviewer.viewer.server.persistence.PersistenceTest;
import org.junit.Test;

public class RemoteTreeTest extends PersistenceTest
{

	@Test
	public void testPersistRemoteTree() throws SecurityException, NoSuchMethodException
	{
		RemoteTree tree = new RemoteTree();
		tree.setHash(new byte[] {42, 42, 42, 42, 42, 42, 42, 42});
		tree.setName("test");
		
		testPersist(tree, RemoteTree.class.getMethod("getId"));
	}

}

package org.iplantc.phyloviewer.viewer.server;

import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;

/**
 * An interface for tree and node data persistence objects.
 */
public interface IImportTreeData {

	/**
	 * Import the nodes of the given tree.
	 * @throws ImportException if the tree cannot be imported
	 */
	void importTree(RemoteTree tree) throws ImportException;
	
	/**
	 * Set the import complete flag on the given tree, indicating that it is ready for client use (i.e.
	 * has generated and imported layouts and overview images)
	 */
	void setImportComplete(RemoteTree tree);
}

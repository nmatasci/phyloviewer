package org.iplantc.phyloviewer.viewer.server;

import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;

public interface IImportTreeData {

	void importTree(RemoteTree tree) throws ImportException;
	void setImportComplete(RemoteTree tree);
}

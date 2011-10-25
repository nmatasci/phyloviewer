package org.iplantc.phyloviewer.viewer.server;

import java.util.List;

import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;

public interface IImportTreeData {

	RemoteTree importFromNewick(String newick, String name) throws Exception;
	List<RemoteTree> importFromNexml(String nexml) throws Exception;
}

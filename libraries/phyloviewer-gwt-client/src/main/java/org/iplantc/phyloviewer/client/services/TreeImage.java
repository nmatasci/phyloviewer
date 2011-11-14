package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.shared.model.ITree;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("treeImage")
public interface TreeImage extends RemoteService {

	/*
	 * Get the url to download the overview image for the tree and layout.
	 */
	String getTreeImageURL(ITree tree, String layoutID, int width, int height);
}

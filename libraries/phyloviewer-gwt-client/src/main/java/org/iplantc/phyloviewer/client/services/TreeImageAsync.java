package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.shared.model.ITree;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TreeImageAsync {

	void getTreeImageURL(ITree tree, String layoutID, int width, int height, AsyncCallback<String> callback);
}

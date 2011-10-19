package org.iplantc.phyloviewer.viewer.client.services;

import java.util.List;

import org.iplantc.phyloviewer.shared.model.ITree;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TreeListServiceAsync {

	void getTreeList(AsyncCallback<List<ITree>> callback);
}

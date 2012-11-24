package org.iplantc.phyloviewer.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Automatically generated GWT client interface for {@link TreeIntersectService}
 */
public interface TreeIntersectServiceAsync {

	void intersectTree(int rootNodeId, String layoutID, double x, double y, AsyncCallback<String> callback);
}

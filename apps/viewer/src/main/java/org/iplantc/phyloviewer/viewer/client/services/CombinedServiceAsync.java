package org.iplantc.phyloviewer.viewer.client.services;

import org.iplantc.phyloviewer.viewer.client.services.CombinedService.CombinedResponse;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.NodeResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CombinedServiceAsync
{
	void getRootNode(int treeId, String layoutID, AsyncCallback<NodeResponse> callback);
	
	void getChildrenAndLayout(int parentID, String layoutID,
			AsyncCallback<CombinedResponse> callback);

	void getChildrenAndLayout(int[] parentIDs, String[] layoutIDs,
			AsyncCallback<CombinedResponse[]> callback);
}

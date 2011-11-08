package org.iplantc.phyloviewer.viewer.client.services;

import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.CombinedResponse;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.NodeResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CombinedServiceAsync
{
	void getRootNode(byte[] rootID, String layoutID, AsyncCallback<NodeResponse> callback);
	
	void getChildrenAndLayout(int parentID, String layoutID,
			AsyncCallback<CombinedResponse> callback);

	void getChildrenAndLayout(int[] parentIDs, String[] layoutIDs,
			AsyncCallback<CombinedResponse[]> callback);

	void getLayout(RemoteNode node, String layoutID, AsyncCallback<LayoutResponse> callback);
}

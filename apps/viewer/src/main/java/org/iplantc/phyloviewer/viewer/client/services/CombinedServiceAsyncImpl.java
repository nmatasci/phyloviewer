package org.iplantc.phyloviewer.viewer.client.services;

import java.util.ArrayList;
import java.util.HashMap;

import org.iplantc.phyloviewer.viewer.client.services.CombinedService.CombinedResponse;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.NodeResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Client side proxy for the CombinedService.  
 * 
 * Gets nodes and layouts in one RPC call and caches the layout response until the client needs it.
 */
public class CombinedServiceAsyncImpl implements CombinedServiceAsync
{
	private CombinedServiceAsync service;
	private boolean defer = true;
	private BatchRequestCommand nextRequestCommand;
	
	public CombinedServiceAsyncImpl() {
		service = GWT.create(CombinedService.class);
	}

	@Override
	public void getRootNode(int treeId, String layoutID, AsyncCallback<NodeResponse> callback)
	{
		service.getRootNode(treeId, layoutID, callback);
	}

	@Override
	public void getChildrenAndLayout(int parentID, String layoutID,
			AsyncCallback<CombinedResponse> callback)
	{
		if (defer)
		{
			addDeferredRequest(parentID, layoutID, callback);
		}
		else
		{
			service.getChildrenAndLayout(parentID, layoutID, callback);
		}
	}

	@Override
	public void getChildrenAndLayout(int[] parentIDs, String[] layoutIDs,
			AsyncCallback<CombinedResponse[]> callback)
	{
		service.getChildrenAndLayout(parentIDs, layoutIDs, callback);
	}
	
	private void addDeferredRequest(int parentID, String layoutID,
			AsyncCallback<CombinedResponse> callback)
	{
		if (nextRequestCommand == null) {
			nextRequestCommand = new BatchRequestCommand();
			Scheduler.get().scheduleDeferred(nextRequestCommand);
		}
		
		nextRequestCommand.addRequest(parentID, layoutID, callback);
	}
	
	private class BatchRequestCommand implements ScheduledCommand {
		ArrayList<Integer> parentList = new ArrayList<Integer>();
		ArrayList<String> layoutIDList = new ArrayList<String>();
		HashMap<Integer, AsyncCallback<CombinedResponse>> callbacks = new HashMap<Integer, AsyncCallback<CombinedResponse>>();
		
		public void addRequest(int parentID, String layoutID, AsyncCallback<CombinedResponse> callback) {
			parentList.add(parentID);
			layoutIDList.add(layoutID);
			callbacks.put(parentID, callback);
		}

		@Override
		public void execute()
		{
			CombinedServiceAsyncImpl.this.nextRequestCommand = null;
			
			int[] parentIDs = new int[parentList.size()];
			for(int i = 0; i < parentList.size(); ++i) {
				parentIDs[i] = parentList.get(i);
			}
			
			String[] layoutIDs = layoutIDList.toArray(new String[layoutIDList.size()]);
			
			service.getChildrenAndLayout(parentIDs, layoutIDs, new AsyncCallback<CombinedResponse[]>()
			{

				@Override
				public void onFailure(Throwable arg0)
				{
					for (AsyncCallback<CombinedResponse> callback : callbacks.values())
					{
						callback.onFailure(arg0);
					}
				}

				@Override
				public void onSuccess(CombinedResponse[] responses)
				{
					for (CombinedResponse response : responses) 
					{
						AsyncCallback<CombinedResponse> callback = callbacks.get(response.parentID);
						callback.onSuccess(response);
					}
				}

			});
		}
	}
}

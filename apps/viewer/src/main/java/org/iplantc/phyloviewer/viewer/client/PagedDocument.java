package org.iplantc.phyloviewer.viewer.client;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.iplantc.phyloviewer.client.events.RenderEvent;
import org.iplantc.phyloviewer.shared.layout.LayoutStorage;
import org.iplantc.phyloviewer.shared.model.Document;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.CombinedResponse;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.viewer.client.services.CombinedServiceAsync;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PagedDocument extends Document implements Serializable
{
	private static final long serialVersionUID = -1787841507150753540L;
	
	LayoutStorage remoteLayout = new LayoutStorage();
	
	private String layoutID;
	
	private transient CombinedServiceAsync combinedService;
	private transient EventBus eventBus;
	private transient Set<Integer> pendingRequests = new HashSet<Integer>();

	public PagedDocument(RemoteTree tree, LayoutResponse rootLayout)
	{
		super();

		this.setTree(tree);

		int numberOfNodes = tree.getNumberOfNodes();

		remoteLayout = new LayoutStorage();
		remoteLayout.init(numberOfNodes);
		remoteLayout.setPositionAndBounds(rootLayout.nodeID, rootLayout.position,
				rootLayout.boundingBox);
		this.setLayout(remoteLayout);
		
		this.layoutID = rootLayout.layoutID;
	}

	@Override
	public boolean checkForData(final INode node)
	{
		// Return now if we are waiting for a response from the server.
		if(pendingRequests.contains(node.getId()))
		{
			return false;
		}

		final LayoutStorage rLayout = remoteLayout;
		if(node instanceof RemoteNode)
		{
			final RemoteNode rNode = (RemoteNode)node;

			if(rNode.getChildren() == null)
			{
				pendingRequests.add(rNode.getId());

				getCombinedService().getChildrenAndLayout(rNode.getId(), layoutID,
						new AsyncCallback<CombinedResponse>()
						{

							@Override
							public void onFailure(Throwable arg0)
							{
								pendingRequests.remove(rNode.getId());
							}

							@Override
							public void onSuccess(CombinedResponse response)
							{
								pendingRequests.remove(rNode.getId());

								rNode.setChildren(response.nodes);

								for(LayoutResponse layoutResponse : response.layouts)
								{
									rLayout.setPositionAndBounds(layoutResponse.nodeID,
											layoutResponse.position, layoutResponse.boundingBox);
								}

								if(getEventBus() != null)
								{
									getEventBus().fireEvent(new RenderEvent());
								}
							}

						});

				return false;

			}
		}

		return true;
	}

	@Override
	public String getLabel(INode node)
	{
		String label = node.getLabel();
		if (node instanceof RemoteNode && (label == null || label.length() == 0)) {
			label = ((RemoteNode)node).getTopology().getAltLabel();
		}
		
		return label;
	}

	public EventBus getEventBus()
	{
		return eventBus;
	}

	public void setEventBus(EventBus eventBus)
	{
		this.eventBus = eventBus;
	}

	public CombinedServiceAsync getCombinedService()
	{
		return combinedService;
	}

	public void setCombinedService(CombinedServiceAsync combinedService)
	{
		this.combinedService = combinedService;
	}

	@Override
	public RemoteTree getTree()
	{
		return (RemoteTree)super.getTree();
	}

	public String getLayoutID()
	{
		return layoutID;
	}

	public void setLayoutID(String layoutID)
	{
		this.layoutID = layoutID;
	}
	
	
}

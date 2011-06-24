package org.iplantc.phyloviewer.viewer.server;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.iplantc.phyloviewer.viewer.client.services.TreeNotAvailableException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CombinedServiceImpl extends RemoteServiceServlet implements CombinedService
{
	private static final long serialVersionUID = 2839219371009200675L;
	
	private ITreeData getTreeData() {
		return (ITreeData) this.getServletContext().getAttribute(Constants.TREE_DATA_KEY);
	}
	
	private ILayoutData getLayoutData() {
		return (ILayoutData) this.getServletContext().getAttribute(Constants.LAYOUT_DATA_KEY);
	}

	public RemoteNode[] getChildren(int parentID) throws TreeDataException {
		return this.getTreeData().getChildren(parentID);
	}

	@Override
	public NodeResponse getRootNode(int treeId) throws TreeDataException 
	{
		ITreeData treeData = this.getTreeData();
		RemoteNode node = treeData.getRootNode(treeId);
		
		NodeResponse response = new NodeResponse();
		response.node = node;
		response.layout = this.getLayout(node);
		return response;
	}

	public LayoutResponse getLayout(INode node) {		
		return this.getLayoutData().getLayout(node);
	}
	
	public LayoutResponse[] getLayout(INode[] nodes) {
		LayoutResponse[] response = new LayoutResponse[nodes.length];
		
		for (int i = 0; i < nodes.length; i++) {
			response[i] = getLayout(nodes[i]);
		}
		
		return response;
	}

	@Override
	public CombinedResponse getChildrenAndLayout(int parentID) throws TreeDataException
	{
		CombinedResponse response = new CombinedResponse();
	
		response.parentID = parentID;
		response.nodes = getChildren(parentID);
		response.layouts = getLayout(response.nodes);
		
		return response;
	}

	@Override
	public CombinedResponse[] getChildrenAndLayout(int[] parentIDs) throws TreeDataException
	{
		CombinedResponse[] responses = new CombinedResponse[parentIDs.length];
		for (int i = 0; i < parentIDs.length; i++) {
			responses[i] = getChildrenAndLayout(parentIDs[i]);
		}
		return responses;
	}

}

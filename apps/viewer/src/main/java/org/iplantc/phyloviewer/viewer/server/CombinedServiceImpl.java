package org.iplantc.phyloviewer.viewer.server;

import java.util.List;

import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.iplantc.phyloviewer.viewer.server.persistence.Constants;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of CombinedService.
 */
public class CombinedServiceImpl extends RemoteServiceServlet implements CombinedService
{
	private static final long serialVersionUID = 2839219371009200675L;
	
	private ITreeData getTreeData() {
		return (ITreeData) this.getServletContext().getAttribute(Constants.TREE_DATA_KEY);
	}
	
	private ILayoutData getLayoutData() {
		return (ILayoutData) this.getServletContext().getAttribute(Constants.LAYOUT_DATA_KEY);
	}

	private List<RemoteNode> getChildren(int parentID) throws TreeDataException {
		return this.getTreeData().getChildren(parentID);
	}

	@Override
	public NodeResponse getRootNode(byte[] rootID, String layoutID) throws TreeDataException 
	{
		ITreeData treeData = this.getTreeData();
		RemoteNode node = treeData.getRootNode(rootID);
		
		NodeResponse response = new NodeResponse();
		response.node = node;
		response.layout = this.getLayout(node, layoutID);
		return response;
	}

	@Override
	public LayoutResponse getLayout(RemoteNode node, String layoutID) {		
		return this.getLayoutData().getLayout(node, layoutID);
	}
	
	private LayoutResponse[] getLayout(List<RemoteNode> nodes, String layoutID) {
		LayoutResponse[] response = new LayoutResponse[nodes.size()];
		
		for (int i = 0; i < nodes.size(); i++) {
			response[i] = getLayout(nodes.get(i), layoutID);
		}
		
		return response;
	}

	@Override
	public CombinedResponse getChildrenAndLayout(int parentID, String layoutID) throws TreeDataException
	{
		CombinedResponse response = new CombinedResponse();
	
		response.parentID = parentID;
		response.nodes = getChildren(parentID);
		response.layouts = getLayout(response.nodes, layoutID);
		
		return response;
	}

	@Override
	public CombinedResponse[] getChildrenAndLayout(int[] parentIDs, String[] layoutIDs) throws TreeDataException
	{
		CombinedResponse[] responses = new CombinedResponse[parentIDs.length];
		for (int i = 0; i < parentIDs.length; i++) {
			responses[i] = getChildrenAndLayout(parentIDs[i], layoutIDs[i]);
		}
		return responses;
	}
}

package org.iplantc.phyloviewer.viewer.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;

public class ImportRemoteNodeTree extends ImportTree<RemoteNode>
{
	public ImportRemoteNodeTree(Connection conn, ExecutorService executor) throws SQLException
	{
		super(conn, executor);
	}

	public ImportRemoteNodeTree(Connection conn) throws SQLException
	{
		super(conn);
	}
	
	protected void addSubtree(RemoteNode node, Integer parentId) throws SQLException
	{
		addTopology(parentId, node);
		
		if (node.getChildren() != null && node.getChildren().length > 0)
		{
			for (RemoteNode child : node.getChildren())
			{
				addNode(child);
				addSubtree(child, node.getId());
			}
		}
	}

	private void addTopology(Integer parentID, RemoteNode child) throws SQLException
	{
		treeWriter.addTopologyToBatch(parentID, child.getId(), child.getNumberOfNodes(), 
				child.getNumberOfLeafNodes(), child.findMaximumDepthToLeaf(), 
				child.getLeftIndex(), child.getRightIndex(), child.getDepth(), 
				child.getNumberOfChildren());
	}
}

package org.iplantc.phyloviewer.viewer.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Tree;

public abstract class ImportTree<N extends INode>
{
	private ExecutorService executor;
	protected DatabaseTreeDataWriter treeWriter;

	public ImportTree(Connection conn) throws SQLException
	{
		this(conn, Executors.newSingleThreadExecutor());
	}
	
	public ImportTree(Connection conn, ExecutorService executor) throws SQLException
	{
		treeWriter = new DatabaseTreeDataWriter(conn);
		this.executor = executor;
	}

	public Future<Void> addTreeAsync(final Tree tree, final String name, byte[] hash) throws SQLException
	{	
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Adding tree and root node to database");
		//do the minimum necessary to set the tree ID
		initTree(tree, name, hash);
		
		//Finish importing the tree in another thread
		Callable<Void> task = new Callable<Void>()
		{
			@Override
			public Void call() throws SQLException
			{
				try
				{
					Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Adding remaining nodes and topology to database");
					addSubtree((N) tree.getRootNode(), null);
					
					treeWriter.executeTopologyBatch();
				}
				catch(SQLException e)
				{
					throw e;
				}
				finally
				{
					treeWriter.close();
				}
				
				return null;
			}
		};
		
		return executor.submit(task);		
	}

	public void addTree(Tree tree, String name, byte[] hash) throws SQLException
	{
		try
		{
			initTree(tree, name, hash);
			
			addSubtree((N) tree.getRootNode(), null);
			
			treeWriter.executeTopologyBatch();
		}
		finally
		{
			treeWriter.close();
		}
	}

	public void close()
	{
		treeWriter.close();
	}
	
	private void initTree(Tree tree, String treeName, byte[] hash) throws SQLException
	{
		INode root = tree.getRootNode();
		int[] ids = treeWriter.initTree(treeName, root.getLabel(), hash);
		tree.setId(ids[0]);
		root.setId(ids[1]);
	}

	/**
	 * @param node the root node of the subtree. This node is expected to already have been inserted into
	 *            the nodes table (but not its topology fields)
	 * @param parentId the node id of its parent. null for root nodes.
	 * @throws SQLException
	 */
	protected abstract void addSubtree(N node, Integer parentId) throws SQLException;
	
	protected void addNode(INode node) throws SQLException
	{
		String label =  node.getLabel();
		
		int id = treeWriter.addNode(label);
		
		if (label == null) {
			String altLabel = node.findLabelOfFirstLeafNode();
			treeWriter.addAltLabel(id, altLabel);
		}
		
		node.setId(id);
	}
}
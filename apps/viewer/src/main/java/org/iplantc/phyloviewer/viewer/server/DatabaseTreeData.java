package org.iplantc.phyloviewer.viewer.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.iplantc.phyloviewer.shared.model.Tree;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.iplantc.phyloviewer.viewer.client.services.TreeImportInProgressException;
import org.iplantc.phyloviewer.viewer.client.services.TreeNotAvailableException;
import org.iplantc.phyloviewer.viewer.server.db.ConnectionUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseTreeData implements ITreeData

{
	public static final int SUBTREE_QUERY_THRESHOLD = 4; //recalculated for NCBI tree on postgres database (at depth = 4, getSubtreeInTwoQueries becomes faster than getSubtreeRecursive)
	
	private DataSource pool;
	private final String getChildren = "select * from node natural join topology where parent_id = ?";

	public DatabaseTreeData(DataSource pool) {
		this.pool = pool;
	}
	
	@Override
	public RemoteNode getSubtree(int rootID, int depth) throws TreeDataException
	{
		if (depth >= SUBTREE_QUERY_THRESHOLD) 
		{
			return getSubtreeInTwoQueries(rootID, depth);
		} 
		else 
		{
			return getSubtreeRecursive(rootID, depth);
		}
		
	}
	
	public RemoteNode getSubtreeRecursive(int rootID, int depth) throws TreeDataException
	{
		RemoteNode node = null;
		Connection conn = null;
		PreparedStatement rootNodeStmt = null;
		PreparedStatement getChildrenStmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = pool.getConnection();
			rootNodeStmt = conn.prepareStatement("select * from node natural join topology where node.node_id = ?");
			getChildrenStmt = conn.prepareStatement(getChildren);
			
			rootNodeStmt.setInt(1, rootID);
			
			rs = rootNodeStmt.executeQuery();
			
			if (rs.next()) 
			{
				node = createNode(rs,pool,true);
			
				if (depth > 0 && node.getNumberOfChildren() > 0) {
					RemoteNode[] children = getChildren(node.getId(), depth - 1, getChildrenStmt);
					node.setChildren(children);
				}
			}
		}
		catch(SQLException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, e.getMessage(), e);
			throw new TreeDataException(e);
		}
		finally
		{
			ConnectionUtil.close(rootNodeStmt);
			ConnectionUtil.close(getChildrenStmt);
			ConnectionUtil.close(rs);
			ConnectionUtil.close(conn);
		}
		
		return node;
	}

	public RemoteNode getSubtreeInTwoQueries(int rootID, int depth) throws TreeDataException
	{
		RemoteNode subtree = null;
		Connection conn = null;
		PreparedStatement getRoot = null;
		PreparedStatement getSubtree = null;
		ResultSet rootRS = null;
		ResultSet subtreeRS = null;
		
		try
		{
			//using some extra topology metadata to get the entire subtree in two queries instead of recursively querying for children
			conn = pool.getConnection();
			String sql = "select * from topology where node_id = ?";
			getRoot = conn.prepareStatement(sql);
			getRoot.setInt(1, rootID);
			rootRS = getRoot.executeQuery();
			
			if (rootRS.next()) {
				int maxDepth = rootRS.getInt("Depth") + depth;
				
				sql = "select * " + 
					" from node natural join topology " + 
					" where LeftNode >= ? and RightNode <= ? and Depth <= ? and tree_id = ?" + 
					" order by Depth desc ";
				
				getSubtree = conn.prepareStatement(sql);
				getSubtree.setInt(1, rootRS.getInt("LeftNode"));
				getSubtree.setInt(2, rootRS.getInt("RightNode"));
				getSubtree.setInt(3, maxDepth);
				getSubtree.setInt(4, rootRS.getInt("tree_id"));
				
				subtreeRS = getSubtree.executeQuery();
				
				subtree = buildTree(subtreeRS);
			}
		}
		catch(SQLException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, e.getMessage(), e);
			throw new TreeDataException(e);
		}
		finally
		{
			ConnectionUtil.close(rootRS);
			ConnectionUtil.close(subtreeRS);
			ConnectionUtil.close(getRoot);
			ConnectionUtil.close(getSubtree);
			ConnectionUtil.close(conn);
		}
		
		return subtree;
	}

	@Override
	public RemoteNode[] getChildren(int parentID) throws TreeDataException
	{
		RemoteNode[] children = null;
		Connection conn = null;
		PreparedStatement getChildrenStmt = null;
		
		try
		{
			conn = pool.getConnection();
			getChildrenStmt = conn.prepareStatement(getChildren);
			int depth = 0;
			children = getChildren(parentID, depth, getChildrenStmt);
		}
		catch(SQLException e) 
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, e.getMessage(), e);
			throw new TreeDataException(e);
		}
		finally 
		{
			ConnectionUtil.close(conn);
			ConnectionUtil.close(getChildrenStmt);
		}
		
		return children;
	}
	
	public RemoteNode[] getChildren(int parentID, int depth, PreparedStatement getChildrenStmt) throws SQLException
	{
		ArrayList<RemoteNode> children = new ArrayList<RemoteNode>();
		getChildrenStmt.setInt(1, parentID);
		
		ResultSet rs = getChildrenStmt.executeQuery();
		
		while (rs.next()) {
			RemoteNode child = createNode(rs,pool,true);
			children.add(child);
		}
		
		ConnectionUtil.close(rs);
		
		if (depth > 0)
		{
			for (RemoteNode child : children)
			{
				if (child.getNumberOfChildren() > 0) 
				{
					child.setChildren(getChildren(child.getId(), depth - 1, getChildrenStmt));
				}
			}
		}
		
		if (children.size() > 0) {
			return children.toArray(new RemoteNode[children.size()]);
		} else {
			return null;
		}
	}
	

	@Override
	public RemoteNode getRootNode(int treeId) throws TreeDataException
	{
		Tree tree = this.getTree(treeId,0);
		return (RemoteNode) tree.getRootNode();
	}

	public Tree getTree(int id, int depth) throws TreeDataException
	{
		Tree tree = null;
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
	
		try
		{
			conn = pool.getConnection();
			String sql = "select * from tree where tree_id = ?";
			statement = conn.prepareStatement(sql);
			statement.setInt(1,id);
			
			rs = statement.executeQuery();
			if (rs.next()) {
				if (!rs.getBoolean("import_complete")) 
				{
					throw new TreeImportInProgressException(id);
				}
				
				tree = new Tree();
				tree.setId(rs.getInt("tree_id"));
				int rootId = rs.getInt("root_id");
				RemoteNode node = getSubtree(rootId, depth);
				tree.setRootNode(node);
			}
			else 
			{
				throw new TreeNotAvailableException(id);
			}
		}
		catch(SQLException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, e.getMessage(), e);
			throw new TreeDataException(e);
		}
		finally 
		{
			ConnectionUtil.close(rs);
			ConnectionUtil.close(statement);
			ConnectionUtil.close(statement);
			ConnectionUtil.close(conn);
		}
		
		return tree;
	}
	
	/**
	 * Gets the tree id for the given tree hash value.
	 * @param hash
	 * @return the tree id. -1 if the hash value is not found.
	 * @throws SQLException 
	 */
	public int getTreeId(byte[] hash) throws TreeDataException
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		int treeId = -1;
		
		try
		{
			connection = pool.getConnection();
			statement = connection.prepareStatement("select * from tree where hash = ?");
			statement.setBytes(1, hash);
			statement.execute();
			rs = statement.getResultSet();
			
			if (rs.next())
			{
				treeId = rs.getInt("tree_id");
			}
		}
		catch(SQLException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, e.getMessage(), e);
			throw new TreeDataException(e);
		}
		finally
		{
			ConnectionUtil.close(rs);
			ConnectionUtil.close(statement);
			ConnectionUtil.close(connection);
		}
		
		return treeId;
	}

	/**
	 * Takes the ResultSet from getSubtree and builds a subtree from it. 
	 * 
	 * The nodes in subtreeRS must be sorted by Depth from largest to smallest.
	 */
	private RemoteNode buildTree(ResultSet subtreeRS) throws SQLException
	{
		HashMap<Integer,List<RemoteNode>> childrenLists = new HashMap<Integer,List<RemoteNode>>();
		RemoteNode node = null;

		while(subtreeRS.next())
		{
			//create the node
			node = createNode(subtreeRS,pool,true);
			
			List<RemoteNode> childrenList = childrenLists.get(node.getId());
			if(childrenList != null)
			{
				RemoteNode[] children = childrenList.toArray(new RemoteNode[childrenList.size()]);
				node.setChildren(children);
			}
			
			//add the node to its parent's childrenList (creating the list first if it doesn't already exist)
			int parentID = subtreeRS.getInt("parent_id");
			if(!childrenLists.containsKey(parentID))
			{
				childrenLists.put(parentID, new ArrayList<RemoteNode>());
			}
			
			childrenLists.get(parentID).add(node);
		}
		
		return node; //the last row of the resultset was the root node 
	}
	
	/**
	 * Creates a RemoteNode from the current row of a ResultSet.
	 * 
	 * @param rs A ResultSet containing the columns of the node and topology tables. The state of the
	 *            ResultSet (current row, etc) should not be altered by this method
	 * @throws SQLException 
	 */
	public static RemoteNode createNode(ResultSet rs,DataSource pool,boolean addAltLabel) throws SQLException
	{
		int id = rs.getInt("node_id");
		String label = rs.getString("Label");
		int numNodes = rs.getInt("NumNodes");
		int numLeaves = rs.getInt("NumLeaves");
		int height = rs.getInt("Height");
		int depth = rs.getInt("Depth");
		int numChildren = rs.getInt("NumChildren");
		int leftIndex = rs.getInt("LeftNode");
		int rightIndex = rs.getInt("RightNode");
		
		if (addAltLabel && label==null) {
			Connection conn = null;
			PreparedStatement statement = null;
			ResultSet rs2 = null;
		
			conn = pool.getConnection();
			String sql = "select * from node_label_lookup where node_id = ?";
			statement = conn.prepareStatement(sql);
			statement.setInt(1,id);
			
			rs2 = statement.executeQuery();
			if (rs2.next()) {
				label = rs2.getString("alt_label");
			}
			
			ConnectionUtil.close(rs2);
			ConnectionUtil.close(statement);
			ConnectionUtil.close(conn);
		}

		return new RemoteNode(id, label, numChildren, numNodes, numLeaves, depth, height, leftIndex, rightIndex);
	}
	

	@Override
	public String getTrees() throws TreeDataException 
	{
		JSONObject result = new JSONObject();
		JSONArray trees = new JSONArray();
		
		Connection conn;
		try {
			conn = pool.getConnection();
		
			PreparedStatement statement = conn.prepareStatement("select * from tree");
			
			ResultSet rs = statement.executeQuery();
			
			while (rs.next()) {
				
				int uuid = rs.getInt("tree_id");
				String name = rs.getString("Name");
				boolean complete = rs.getBoolean("import_complete");
				
				trees.put(buildJSONForTree(uuid, name, complete));
	
			}
			
			result.put("trees", trees);
			
			ConnectionUtil.close(statement);
			ConnectionUtil.close(conn);
			ConnectionUtil.close(rs);
			
		} 
		catch(SQLException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, e.getMessage(), e);
			throw new TreeDataException(e);
		}
		catch(JSONException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, e.getMessage(), e);
			throw new TreeDataException(e);
		}
		
		return result.toString();
	}
	
	private JSONObject buildJSONForTree(int id, String name, boolean complete) throws JSONException {
		JSONObject tree = new JSONObject();
		tree.put("id", id);
		tree.put("name", name);
		tree.put("importComplete", complete);
		return tree;
	}
}

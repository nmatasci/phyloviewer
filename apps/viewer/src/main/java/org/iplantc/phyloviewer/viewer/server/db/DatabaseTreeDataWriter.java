package org.iplantc.phyloviewer.viewer.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class DatabaseTreeDataWriter {
	private Connection connection;
	private PreparedStatement addTreeStmt;
	private PreparedStatement addNodeStmt;
	private PreparedStatement addChildStmt;
	private PreparedStatement addAltLabelStmt;
	
	private int treeId;
	
	public DatabaseTreeDataWriter(Connection conn) throws SQLException {
		this.connection = conn;
	
		// prepare insert statements
		addNodeStmt = conn.prepareStatement("insert into node(Label) values (?)", Statement.RETURN_GENERATED_KEYS);
		addChildStmt = conn.prepareStatement("insert into topology (node_id, parent_id, tree_id, NumNodes, NumLeaves, Height, LeftNode, RightNode, Depth, NumChildren) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		addTreeStmt = conn.prepareStatement("insert into tree(root_id,Name) values(?, ?)");
		addAltLabelStmt = conn.prepareStatement("insert into node_label_lookup(node_id,alt_label) values(?, ?)");	
	}
	
	/**
	 * Adds the tree and root node records. Must be done before adding other nodes, topologies.
	 * @param treeName
	 * @param rootLabel
	 * @return IDs for the inserted tree and node: [treeId, rootId]
	 * @throws SQLException
	 */
	public int[] initTree(String treeName, String rootLabel) throws SQLException
	{
		int rootId = addNode(rootLabel);
		treeId = addTreeRoot(rootId, treeName);
		
		return new int[] {treeId, rootId};
	}
	
	public void close() {
		ConnectionUtil.close(addTreeStmt);
		ConnectionUtil.close(addNodeStmt);
		ConnectionUtil.close(addChildStmt);
		ConnectionUtil.close(addAltLabelStmt);
	}
	
	public int addNode(String label) throws SQLException
	{
		int id = -1;
		
		addNodeStmt.setString(1, label);
		addNodeStmt.execute();
		
		ResultSet rs = addNodeStmt.getGeneratedKeys();
		if (rs.next()) {
			id = rs.getInt("node_id");
		}
		
		if (id == -1)
		{
			throw new SQLException("unable to retrieve generated node ID");
		}
		
		return id;
	}
	
	public void addAltLabel(int nodeId, String altLabel) throws SQLException
	{
		addAltLabelStmt.setInt(1, nodeId);
		addAltLabelStmt.setString(2, altLabel);
		addAltLabelStmt.execute();
	}
	
	/** Adds a child to the current batch.  The batch is not executed until executeBatch() is called. */ 
	public void addTopologyToBatch(Integer parentID, int childId, int numNodes, int numLeaves, int height, int left, int right, int depth, int numChildren) throws SQLException
	{
		addChildStmt.setInt(1, childId);
		
		if (parentID != null)
		{
			addChildStmt.setInt(2, parentID);
		}
		else
		{
			addChildStmt.setNull(2, Types.INTEGER);
		}
		
		//param 3 (treeID) is already set by addTreeRoot
		addChildStmt.setInt(4, numNodes);
		addChildStmt.setInt(5, numLeaves);
		addChildStmt.setInt(6, height);
		addChildStmt.setInt(7, left);
		addChildStmt.setInt(8, right);
		addChildStmt.setInt(9, depth);
		addChildStmt.setInt(10, numChildren);
	
		addChildStmt.addBatch();
	}
	
	public void executeTopologyBatch() throws SQLException
	{
		addChildStmt.executeBatch();
	}
	
	/**
	 * Adds a record to the trees table. This has to be done after the root node is added and before any topology is added.
	 * @param rootId the root node ID
	 * @param name the name of the tree
	 * @return the tree ID
	 * @throws SQLException
	 */
	private int addTreeRoot(int rootId, String name) throws SQLException
	{
		int id; 
		
		addTreeStmt.setInt(1, rootId);
		addTreeStmt.setString(2, name != null ? name : "No name");
		addTreeStmt.execute();
		
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("select currval('trees_tree_id') as result" );
		if (rs.next()) {
			id = rs.getInt("result");
		}
		else 
		{
			throw new SQLException("unable to get generated tree id");
		}
		
		this.treeId = id;
		addChildStmt.setInt(3, id);
		
		return id;
	}
	
	public int getTreeId()
	{
		return this.treeId;
	}
}

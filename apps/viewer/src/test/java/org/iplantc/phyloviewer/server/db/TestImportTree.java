package org.iplantc.phyloviewer.server.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.block.Block;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.iplantc.phyloviewer.viewer.client.model.PhyloparserTreeAdapter;
import org.iplantc.phyloviewer.viewer.server.db.ImportNodeTree;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestImportTree
{
	static final String DB = "testdb";
	Tree tree1;
	Tree tree2;
	
	@BeforeClass
	public static void classSetUp() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		Connection conn = DriverManager.getConnection("jdbc:postgresql:phyloviewer", "phyloviewer", "phyloviewer");
		conn.createStatement().execute("DROP DATABASE IF EXISTS " + DB);
		conn.createStatement().execute("CREATE DATABASE " + DB + " WITH TEMPLATE phyloviewer_template;");
		
		
	}
	
	@AfterClass
	public static void classTearDown() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:postgresql:phyloviewer", "phyloviewer", "phyloviewer");
		conn.createStatement().execute("DROP DATABASE IF EXISTS " + DB);
	}
	
	@Before
	public void setUp() throws SQLException {
		tree1 = new PhyloparserTreeAdapter(parseNewick("A;"));
		tree2 = new PhyloparserTreeAdapter(parseNewick("((A,B),((C,D),E));"));
	}
	
	@Test
	public void testAddTree() throws SQLException
	{
		Connection conn = getConnection();
		
		//tree1
		ImportNodeTree it = new ImportNodeTree(conn);
		it.addTree(tree1, "name1");
		it.close();
	
		ResultSet rs = conn.createStatement().executeQuery("select * from tree");
		assertTrue(rs.next());
		assertEquals(1, rs.getInt("tree_id"));
		assertEquals(1, rs.getInt("root_id"));
		assertFalse(rs.next());
		
		rs = conn.createStatement().executeQuery("select * from node");
		assertTrue(rs.next());
		assertEquals(1, rs.getInt("node_id"));
		assertFalse(rs.next());
		
		rs = conn.createStatement().executeQuery("select * from topology");
		assertTrue(rs.next());
		assertEquals(1, rs.getInt("node_id"));
		assertEquals(0, rs.getInt("parent_id")); //getInt returns 0 for values that are null in the db
		assertTrue(rs.wasNull());
		assertEquals(1, rs.getInt("tree_id"));
		assertEquals(1, rs.getInt("LeftNode"));
		assertEquals(2, rs.getInt("RightNode"));
		assertEquals(0, rs.getInt("Depth"));
		assertEquals(0, rs.getInt("Height"));
		assertEquals(0, rs.getInt("NumChildren"));
		assertEquals(1, rs.getInt("NumLeaves"));
		assertEquals(1, rs.getInt("NumNodes"));
		assertFalse(rs.next());
		
		//tree2
		it = new ImportNodeTree(conn);
		it.addTree(tree2, "name2");
		it.close();
		
		rs = conn.createStatement().executeQuery("select * from tree order by tree_id");
		assertTrue(rs.next() && rs.next());
		assertEquals(2, rs.getInt("tree_id"));
		
		//tree2 root
		int treeId = 2;
		int rootId = 2;
		rs = conn.createStatement().executeQuery("select * from topology where tree_id = " + treeId + " and node_id = " + rootId);
		assertTrue(rs.next());
		assertEquals(0, rs.getInt("parent_id")); //getInt returns 0 for values that are null in the db
		assertTrue(rs.wasNull());
		assertEquals(1, rs.getInt("LeftNode"));
		assertEquals(18, rs.getInt("RightNode"));
		assertEquals(0, rs.getInt("Depth"));
		assertEquals(3, rs.getInt("Height"));
		assertEquals(2, rs.getInt("NumChildren"));
		assertEquals(5, rs.getInt("NumLeaves"));
		assertEquals(9, rs.getInt("NumNodes"));
		
		//tree2 root child 0
		int nodeId = 3;
		rs = conn.createStatement().executeQuery("select * from topology where tree_id = " + treeId + " and node_id = " + nodeId);
		assertTrue(rs.next());
		assertEquals(rootId, rs.getInt("parent_id"));
		assertEquals(2, rs.getInt("LeftNode"));
		assertEquals(7, rs.getInt("RightNode"));
		assertEquals(1, rs.getInt("Depth"));
		assertEquals(1, rs.getInt("Height"));
		assertEquals(2, rs.getInt("NumChildren"));
		assertEquals(2, rs.getInt("NumLeaves"));
		assertEquals(3, rs.getInt("NumNodes"));
		
		conn.close();
	}
	
	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:postgresql:" + DB, "phyloviewer", "phyloviewer");
	}
	
	private static org.iplantc.phyloparser.model.Tree parseNewick(String newick) 
	{
		org.iplantc.phyloparser.parser.NewickParser parser = new org.iplantc.phyloparser.parser.NewickParser();
		FileData data = null;
		try {
			data = parser.parse(newick);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		org.iplantc.phyloparser.model.Tree tree = null;
		
		List<Block> blocks = data.getBlocks();
		for ( Block block : blocks ) {
			if ( block instanceof TreesBlock ) {
				TreesBlock trees = (TreesBlock) block;
				tree = trees.getTrees().get( 0 );
			}
		}
		
		return tree;
	}
}

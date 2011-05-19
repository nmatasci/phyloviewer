package org.iplantc.phyloviewer.viewer.server.db;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.sql.DataSource;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.block.Block;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.iplantc.phyloviewer.server.render.ImageGraphics;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.layout.LayoutCladogram;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.model.Document;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.iplantc.phyloviewer.shared.render.RenderTreeCladogram;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.server.IImportTreeData;

public class ImportTreeData implements IImportTreeData {
	static final ExecutorService executor = Executors.newCachedThreadPool();
	private DataSource pool;
	private String imageDirectory;

	public ImportTreeData(DataSource pool,String imageDirectory) {
		this.pool = pool;
		this.imageDirectory = imageDirectory;
		
		new File(imageDirectory).mkdir();
	}
	
	public static RemoteNode rootNodeFromNewick(String newick, String name) {
		org.iplantc.phyloparser.parser.NewickParser parser = new org.iplantc.phyloparser.parser.NewickParser();
		FileData data = null;
		try {
			data = parser.parse(newick);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
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
		
		int depth = 0;
		int nextTraversalIndex = 1; //starts with 1 to avoid ambiguity, because JDBC ResultSet.getInt() returns 0 for null values 
		return convertDataModels(tree.getRoot(), depth, nextTraversalIndex);
	}
	
	private static BufferedImage renderTreeImage(Tree tree, ILayoutData layout,
			int width, int height) {

		ImageGraphics graphics = new ImageGraphics(width, height);

		RenderTreeCladogram renderer = new RenderTreeCladogram();
		renderer.getRenderPreferences().setCollapseOverlaps(false);
		renderer.getRenderPreferences().setDrawLabels(false);
		renderer.getRenderPreferences().setDrawPoints(false);

		Document document = new Document();
		document.setTree(tree);
		document.setLayout(layout);
		
		renderer.setDocument(document);
		renderer.renderTree(graphics, Matrix33.makeScale(width, height));

		return graphics.getImage();
	}
	
	public int importTreeData(RemoteNode root, String name) throws Exception {
		Connection connection = null;
		final Tree tree = new Tree();
		tree.setRootNode(root);

		try
		{
			connection = pool.getConnection();
			final Connection conn = connection; //for the Callable
			
			/* The tree and all associated data will be added in a two transactions: 
			 * First the tree and root node records, so that the tree service can 
			 * check whether the import has been started for a given tree request,
			 * then the rest of the tree and layout.
			 */
			connection.setAutoCommit(false);
			ImportTree importer = new ImportTree(connection);
			final Future<Void> futureAddTree = importer.addTreeAsync(tree, name);
			conn.commit();
			
			Callable<Void> doImportLayout = new Callable<Void>() 
			{
				@Override
				public Void call() throws SQLException, ExecutionException, InterruptedException
				{
					futureAddTree.get(); //wait for addTreeAsync thread to finish
					importLayout(conn, tree);
					conn.commit();
					ConnectionUtil.close(conn);
					return null;
				}
			};	
			
			executor.submit(doImportLayout);
		}
		catch(Exception e)
		{
			if (connection != null) {
				//rolls back entire tree transaction on exception anywhere in the tree
				ConnectionUtil.rollback(connection);
				ConnectionUtil.close(connection);
				
				//also delete the tree and root node records, which were already committed.  
				deleteTree(tree);
				
				throw(e);
			}
		}
		
		return tree.getId();
	}

	private void importLayout(Connection connection, Tree tree) throws SQLException {
		ImportLayout layoutImporter = null;
		
		try
		{
			layoutImporter = new ImportLayout(connection);

			LayoutCladogram cladogramLayout = new LayoutCladogram(0.8,1.0);
			cladogramLayout.layout(tree);

			String uuid = "LAYOUT_TYPE_CLADOGRAM";
			layoutImporter.addLayout(uuid, cladogramLayout, tree);
			
			BufferedImage image = renderTreeImage(tree,cladogramLayout,256,1024);
			ImportTreeData.this.putOverviewImage(connection,tree.getId(), uuid, image);
		}
		catch(SQLException e)
		{
			throw e;
		}
		finally
		{
			if(layoutImporter != null) {
				layoutImporter.close();
			}
		}
	}
	
	private void putOverviewImage(Connection connection, int treeId, String layoutId,
			BufferedImage image) throws SQLException {
		
		PreparedStatement statement = null;
		
		try { 
			String filename = UUID.randomUUID().toString();
			String path = imageDirectory+"/"+filename+".png";
		
			File file = new File(path);
			try {
				ImageIO.write(image, "png", file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			statement = connection.prepareStatement("insert into overview_images (tree_id,layout_id,image_width,image_height,image_path) values (?,?,?,?,?)");
			statement.setInt(1, treeId);
			statement.setString(2, layoutId);
			statement.setInt(3, image.getWidth());
			statement.setInt(4, image.getHeight());
			statement.setString(5, path);
			
			statement.executeUpdate();
			ConnectionUtil.close(statement);
		}
		finally
		{
			ConnectionUtil.close(statement);
		}
	}
	
	private static RemoteNode convertDataModels(org.iplantc.phyloparser.model.Node node, int depth, int nextTraversalIndex) {
		
		List<Node> myChildren = node.getChildren();
		
		int len = myChildren.size();
		RemoteNode[] children = new RemoteNode[len];
		int numNodes = 1;
		int maxChildHeight = -1;
		int numLeaves = len == 0 ? 1 : 0;
		int leftIndex = nextTraversalIndex;
		nextTraversalIndex++;
		
		for (int i = 0; i < len; i++) {
			Node myChild = myChildren.get(i);
			
			RemoteNode child = convertDataModels(myChild, depth + 1, nextTraversalIndex);
			children[i] = child;
			
			//note: numNodes, height and numLeaves are fields in RemoteNode, so the tree is not actually traversed again for each of these.
			maxChildHeight = Math.max(maxChildHeight, child.findMaximumDepthToLeaf()); 
			numLeaves += child.getNumberOfLeafNodes();
			numNodes += child.getNumberOfNodes();
			nextTraversalIndex = child.getRightIndex() + 1;
		}
		
		//create a RemoteNode for the current node
		String label = node.getName();
		
		int height = maxChildHeight + 1;
		int id = -1;  //id will be assigned when this node is inserted in the DB
		int numChildren = children.length;
		RemoteNode rNode = new RemoteNode(id, label, numChildren, numNodes, numLeaves, depth, height, leftIndex, nextTraversalIndex);
		rNode.setChildren(children);
		
		return rNode;
	}

	@Override
	public int importFromNewick(String newick, String name) throws Exception
	{
		RemoteNode root = rootNodeFromNewick(newick, name);
		return this.importTreeData(root, name);
	}
	
	/** Deletes the given tree from the database */
	private void deleteTree(Tree tree)
	{
		Connection connection = null;

		try
		{
			connection = pool.getConnection();
			connection.createStatement().execute("delete from node using topology where node.node_id = topology.node_id and topology.tree_id = " + tree.getId()); //node deletes cascade to related tables
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

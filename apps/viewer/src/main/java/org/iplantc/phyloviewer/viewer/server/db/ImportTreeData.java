package org.iplantc.phyloviewer.viewer.server.db;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.iplantc.phyloviewer.shared.render.RenderTreeCladogram;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.iplantc.phyloviewer.viewer.server.DatabaseTreeData;
import org.iplantc.phyloviewer.viewer.server.HashTree;
import org.iplantc.phyloviewer.viewer.server.IImportTreeData;
import org.iplantc.phyloviewer.viewer.server.PhyloparserTreeAdapter;

public class ImportTreeData implements IImportTreeData {
	private ExecutorService executor;
	private DataSource pool;
	private String imageDirectory;
	private String treeBackupDirectory;
	private HashTree hashTree = new HashTree();
	private DatabaseTreeData treeDataReader;

	public ImportTreeData(DataSource pool, String imageDirectory, String treeBackupDirectory) {
		this.pool = pool;
		this.imageDirectory = imageDirectory;
		this.treeBackupDirectory = treeBackupDirectory;
		this.treeDataReader = new DatabaseTreeData(pool);
		
		new File(imageDirectory).mkdir();
		new File(treeBackupDirectory).mkdir();
		
		executor = Executors.newSingleThreadExecutor();
	}
	
	public static RemoteNode rootNodeFromNewick(String newick, String name) throws ParserException {
		org.iplantc.phyloparser.model.Tree tree = treeFromNewick(newick, name);
		
		int depth = 0;
		int nextTraversalIndex = 1; //starts with 1 to avoid ambiguity, because JDBC ResultSet.getInt() returns 0 for null values 
		return convertDataModels(tree.getRoot(), depth, nextTraversalIndex);
	}
	
	public static org.iplantc.phyloparser.model.Tree treeFromNewick(String newick, String name) throws ParserException
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Parsing newick string");
		org.iplantc.phyloparser.parser.NewickParser parser = new org.iplantc.phyloparser.parser.NewickParser();
		FileData data = null;
		try {
			data = parser.parse(newick);
		} catch (IOException e) {
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "IOException parsing tree string: " + newick);
		} catch (ParserException e) {
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "ParserException parsing tree string: " + newick);
			throw e;
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
	
	/**
	 * Imports a tree, returning the id immediately and finishing the import in another thread. Future
	 * imports will check the hash value to avoid importing the same tree twice.
	 * 
	 * @return the id given to the tree.  (The input tree will also have its id set to this value.)
	 * @throws SQLException
	 */
	public int importTreeData(final Tree tree, final String name, byte[] hash) throws SQLException
	{
		final Connection connection;
		final Future<Void> futureAddTree;
		
		try
		{
			connection = pool.getConnection();
		}
		catch(SQLException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Unable to open database connection.", e);
			throw(e);
		}
		
		DatabaseTreeDataWriter writer = new DatabaseTreeDataWriter(connection);
		ImportTree<? extends INode> importer = ImportTree.create(tree, writer, executor);
		
		/* The tree and all associated data will be added in a two transactions: 
		 * First the tree and root node records, so that the tree service can 
		 * check whether the import has been started for a given tree request,
		 * then the rest of the tree and layout.
		 */
		try
		{
			connection.setAutoCommit(false);
			futureAddTree = importer.addTreeAsync(tree, name, hash);
			connection.commit();
		}
		catch(SQLException e)
		{
			ConnectionUtil.rollback(connection);
			ConnectionUtil.close(connection);
			
			throw(e);
		}
		 
		Runnable doImportLayout = new Runnable() 
		{
			@Override
			public void run()
			{
				try
				{
					futureAddTree.get(); //wait for addTreeAsync thread to finish
					importLayout(connection, tree);
					connection.createStatement().execute("update tree set import_complete=TRUE where tree_id=" + tree.getId());
					connection.commit();
					Logger.getLogger("org.iplantc.phyloviewer").log(Level.INFO, "Completed import of tree name: " + name + ", id: " + tree.getId());
				}
				catch(Exception e)
				{
					ConnectionUtil.rollback(connection);
					deleteTree(tree.getId());
					
					Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Exception in ImportTreeData.importTreeData(). Unable to complete import.  Rolling back.", e);
				}
				finally
				{
					ConnectionUtil.close(connection);
				}
			}
		};	
			
		executor.submit(doImportLayout);

		return tree.getId();
	}

	private void importLayout(Connection connection, Tree tree) throws SQLException {
		ImportLayout layoutImporter = null;
		
		try
		{
			layoutImporter = new ImportLayout(connection);

			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Doing layout");
			LayoutCladogram cladogramLayout = new LayoutCladogram(0.8,1.0);
			cladogramLayout.layout(tree);

			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Writing layout to DB");
			String uuid = "LAYOUT_TYPE_CLADOGRAM";
			layoutImporter.addLayout(uuid, cladogramLayout, tree);
			
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Rendering overview image");
			BufferedImage image = renderTreeImage(tree,cladogramLayout,256,1024);
			ImportTreeData.this.putOverviewImage(connection,tree.getId(), uuid, image);
		}
		catch(SQLException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Exception in ImportTreeData.importLayout()", e);
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
	public int importFromNewick(String newick, String name) throws ParserException, SQLException, TreeDataException
	{
		byte[] hash = hashTree.hash(newick);
		int id = treeDataReader.getTreeId(hash);
		if (id != -1)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Hash of newick string found.  Returning ID of existing tree.");
			return id;
		}
		
		/*
		 * TODO treeFromNewick is taking about 2.5 seconds for the ncbi tree, and the parseTree call
		 * doesn't return until it's done. Move the parse to after a dummy tree/root insert? Would need
		 * to go back and update the root node label when the real import happens.
		 */
		org.iplantc.phyloparser.model.Tree tree = treeFromNewick(newick, name); 
		
		if (tree == null)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.INFO, "No trees found in newick string: " + newick);
			throw new ParserException("No trees found");
		}
		
		PhyloparserTreeAdapter adaptedTree = new PhyloparserTreeAdapter(tree);
		id = importTreeData(adaptedTree, name, hash);
		
		saveNewickBackup(id, name, newick);
		
		return id;
	}

	private void saveNewickBackup(int id, String name, String newick)
	{
		String path = treeBackupDirectory + "/" + id + "/";
		new File(path).mkdir();
		File file = new File(path + name);
		
		try
		{
			Writer writer = new BufferedWriter(new FileWriter(file));
			writer.write(newick);
			writer.close();
		}
		catch(IOException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Unable to save backup of newick string to file system", e);
		}
	}
	
//	public String loadNewickBackup(int id) throws IOException
//	{
//		String newick = "";
//		String path = treeBackupDirectory + "/" + id;
//		File file = new File(path);
//		
//		try
//		{
//			BufferedReader reader = new BufferedReader(new FileReader(file));
//			while (reader.ready())
//			{
//				newick += reader.readLine();
//			}
//
//		}
//		catch(FileNotFoundException e)
//		{
//			Logger.getLogger("org.iplantc.phyloviewer").log(Level.INFO, "Backup file not found for tree id " + id, e);
//			throw e;
//		}
//		catch(IOException e)
//		{
//			Logger.getLogger("org.iplantc.phyloviewer").log(Level.INFO, "IOException for tree id " + id, e);
//			throw e;
//		}
//		
//		return newick;
//	}

	/** Deletes the given tree from the database */
	private void deleteTree(int treeId)
	{
		Connection connection = null;

		try
		{
			connection = pool.getConnection();
			connection.createStatement().execute("delete from node using topology where node.node_id = topology.node_id and topology.tree_id = " + treeId); //node deletes cascade to related tables
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package org.iplantc.phyloviewer.viewer.server.db;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sql.DataSource;

import org.iplantc.phyloviewer.shared.layout.LayoutCladogram;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.iplantc.phyloviewer.viewer.server.ImportTreeUtil;

public class ImportTreeLayout {
	private String imageDirectory;

	public ImportTreeLayout(DataSource pool, String imageDirectory, String treeBackupDirectory) {
		this.imageDirectory = imageDirectory;
		
		new File(imageDirectory).mkdir();
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
			String layoutID = "LAYOUT_TYPE_CLADOGRAM"; //TODO should probably have these IDs in a Enum in the shared package.
			layoutImporter.addLayout(layoutID, cladogramLayout, tree);
			
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Rendering overview image");
			BufferedImage image = ImportTreeUtil.renderTreeImage(tree,cladogramLayout,256,1024);
			putOverviewImage(connection,tree.getId(), layoutID, image);
			
			//TODO check if the tree actually has any branch lengths first
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Doing layout with branch lengths");
			cladogramLayout.init(tree.getNumberOfNodes());
			cladogramLayout.setUseBranchLengths(true);
			cladogramLayout.layout(tree);

			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Writing layout with branch lengths to DB");
			layoutID = "LAYOUT_TYPE_PHYLOGRAM"; //TODO should probably have these IDs in a Enum in the shared package.
			layoutImporter.addLayout(layoutID, cladogramLayout, tree);
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
}

package org.iplantc.phyloviewer.viewer.server.persistence;

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

import org.iplantc.phyloviewer.server.render.ImageGraphics;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.layout.LayoutCladogram;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.model.Document;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.iplantc.phyloviewer.shared.render.RenderTreeCladogram;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;

public class ImportTreeLayout {
	private File imageDirectory;
	private DataSource pool;

	public ImportTreeLayout(DataSource pool) {
		this.pool = pool;
		this.imageDirectory = new File(".");
	}

	public void importLayouts(RemoteTree tree) throws SQLException {
		Connection connection = pool.getConnection();
		ImportLayout layoutImporter = new ImportLayout(connection);
		
		try
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Doing layout");
			LayoutCladogram cladogramLayout = new LayoutCladogram(0.8,1.0);
			cladogramLayout.layout(tree);

			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Writing layout to DB");
			String layoutID = "LAYOUT_TYPE_CLADOGRAM"; //TODO should probably have these IDs in a Enum in the shared package.
			layoutImporter.addLayout(layoutID, cladogramLayout, tree);
			
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Rendering overview image");
			BufferedImage image = renderTreeImage(tree,cladogramLayout,256,1024);
			putOverviewImage(connection, tree, layoutID, image);
			
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
			
			connection.close();
		}
	}
	
	private void putOverviewImage(Connection connection, RemoteTree tree, String layoutId,
			BufferedImage image) throws SQLException {
		
		PreparedStatement statement = null;
		
		try { 
			String filename = UUID.randomUUID().toString() + ".png";
		
			File file = new File(this.imageDirectory, filename);
			try {
				ImageIO.write(image, "png", file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			statement = connection.prepareStatement("insert into overview_images (tree_id,layout_id,image_width,image_height,image_path) values (?,?,?,?,?)");
			statement.setBytes(1, tree.getHash());
			statement.setString(2, layoutId);
			statement.setInt(3, image.getWidth());
			statement.setInt(4, image.getHeight());
			statement.setString(5, file.getAbsolutePath());
			
			statement.executeUpdate();
			ConnectionUtil.close(statement);
		}
		finally
		{
			ConnectionUtil.close(statement);
		}
	}

	public File getImageDirectory()
	{
		return imageDirectory;
	}

	public void setImageDirectory(String imageDirectory)
	{
		this.imageDirectory = new File(imageDirectory);
		this.imageDirectory.mkdir();
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.INFO, "Created overview image directory at " + this.imageDirectory.getAbsolutePath());
	}
	
	public static BufferedImage renderTreeImage(Tree tree, ILayoutData layout,
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
}

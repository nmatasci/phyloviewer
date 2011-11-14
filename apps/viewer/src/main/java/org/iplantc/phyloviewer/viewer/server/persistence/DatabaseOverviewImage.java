package org.iplantc.phyloviewer.viewer.server.persistence;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sql.DataSource;

import org.iplantc.phyloviewer.viewer.server.IOverviewImageData;

public class DatabaseOverviewImage implements IOverviewImageData {

	private DataSource pool;

	public DatabaseOverviewImage(DataSource pool) {
		this.pool = pool;
	}
	
	@Override
	public BufferedImage getOverviewImage(byte[] treeId, String layoutId) {
		
		BufferedImage image = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			connection = pool.getConnection();
		
			String sql = "Select * from overview_images where tree_id=?";
			statement = connection.prepareCall(sql);
			statement.setBytes(1, treeId);
			
			rs = statement.executeQuery();
			
			if(rs.next()) {
				String path = rs.getString("image_path");
				File file = new File(path);
				if (file.exists()) {
					image = ImageIO.read(file);
				} else {
					Logger.getLogger("org.iplantc.phyloviewer").log(Level.INFO, "Overview image file not found: " + file.getAbsolutePath());
				}
			} else {
				Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "No overview image image_path for treeID=" + treeId);
			}
		} catch (SQLException e) {
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, e.getMessage());
		} catch (IOException e) {
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.INFO, e.getMessage());
		}
		finally {
			ConnectionUtil.close(connection);
			ConnectionUtil.close(statement);
			ConnectionUtil.close(rs);
		}
		
		return image;
	}
}

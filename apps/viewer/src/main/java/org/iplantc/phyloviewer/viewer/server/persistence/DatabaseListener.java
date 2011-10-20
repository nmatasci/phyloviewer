package org.iplantc.phyloviewer.viewer.server.persistence;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.iplantc.phyloviewer.viewer.server.IImportTreeData;
import org.iplantc.phyloviewer.viewer.server.ITreeData;
import org.postgresql.ds.PGPoolingDataSource;

public class DatabaseListener implements ServletContextListener
{
	PGPoolingDataSource pool;
	EntityManagerFactory emf;

	@Override
	public void contextInitialized(ServletContextEvent contextEvent)
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "contextInitialized: Getting database connection pool");
		ServletContext servletContext = contextEvent.getServletContext();
		
		String server = servletContext.getInitParameter("db.server");
		String database = servletContext.getInitParameter("db.database");
		String user = servletContext.getInitParameter("db.user");
		String password = servletContext.getInitParameter("db.password");
		
		pool = new PGPoolingDataSource();
		pool.setServerName(server);
		pool.setDatabaseName(database);
		pool.setUser(user);
		pool.setPassword(password);
		pool.setMaxConnections(10);
		
		servletContext.setAttribute("db.connectionPool", pool);
		
		emf = Persistence.createEntityManagerFactory("org.iplantc.phyloviewer");
		
		ITreeData treeData = new UnpersistTreeData(emf);
		servletContext.setAttribute(Constants.TREE_DATA_KEY, treeData);
		
		DatabaseLayoutData layoutData = new DatabaseLayoutData(pool);
		servletContext.setAttribute(Constants.LAYOUT_DATA_KEY, layoutData);
		
		DatabaseOverviewImage overviewData = new DatabaseOverviewImage(pool);
		servletContext.setAttribute(Constants.OVERVIEW_DATA_KEY, overviewData);
		
		IImportTreeData importer = new PersistTreeData(emf);
		//TODO do tree file backup in PersistTreeData
		//String treeBackupPath = servletContext.getInitParameter("treefile.path");
		//treeBackupPath = servletContext.getRealPath(treeBackupPath);
		servletContext.setAttribute(Constants.IMPORT_TREE_DATA_KEY, importer);
		
		{
			//temporary.  TODO: Hope to be moving layouts to hibernate too.
			String imagePath = servletContext.getInitParameter("image.path");
			imagePath = servletContext.getRealPath(imagePath);
			ImportTreeLayout layoutImporter = new ImportTreeLayout(pool);
			layoutImporter.setImageDirectory(imagePath);
			((PersistTreeData)importer).setLayoutImporter(layoutImporter);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "contextDestroyed: Closing database connection pool");
		pool.close();
		emf.close();
	}
}

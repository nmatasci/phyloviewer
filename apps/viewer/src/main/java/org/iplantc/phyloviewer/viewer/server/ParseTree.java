package org.iplantc.phyloviewer.viewer.server;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Hex;
import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.server.persistence.ImportTreeLayout;
import org.iplantc.phyloviewer.viewer.server.persistence.PersistTreeData;
import org.nexml.model.Document;
import org.nexml.model.Edge;
import org.nexml.model.Tree;
import org.postgresql.ds.PGPoolingDataSource;
import org.xml.sax.SAXException;

/**
 * Performs all of the non-HTTPServlet-related functions of the ParseTreeService. Also exposes methods to
 * reload trees from backup files.
 */
public class ParseTree
{
	private File treeBackupDir = new File(".");
	private IImportTreeData importer;
	private ImportTreeLayout layoutImporter;
	
	/**
	 * Creates a new ParseTree that uses the given tree and layout importers to save the tree data.
	 */
	public ParseTree(IImportTreeData importer, ImportTreeLayout layoutImporter) {
		this.importer = importer;
		this.layoutImporter = layoutImporter;
	}
	
	/**
	 * Set the object used to persist tree and node data
	 */
	public void setImporter(IImportTreeData importer)
	{
		this.importer = importer;
	}

	/**
	 * Set the object used to persist tree layout data
	 */
	public void setLayoutImporter(ImportTreeLayout layoutImporter)
	{
		this.layoutImporter = layoutImporter;
	}

	/**
	 * Set the directory used to store request backup files. If the database is later cleared, e.g. for
	 * schema changes, the requests can be replayed from this directory using replayBackups(). The
	 * default is the current working directory.
	 * 
	 * @see ParseTree#replayBackups()
	 * @see ParseTree#main(String[])
	 */
	public void setTreeBackupDir(String treeBackupPath)
	{
		treeBackupDir = new File(treeBackupPath); 
		treeBackupDir.mkdir();
	}
	
	/**
	 * @return the tree backup directory
	 */
	public File getTreeBackupDir()
	{
		return treeBackupDir;
	}
	
	/**
	 * Parse the given tree request parameters and persist the tree. In cases where the request has more
	 * than one tree, it only imports a single tree.
	 * 
	 * @param parameters the HttpServletRequest parameters for one request. Maps parameter names to
	 *            values.
	 * @return the tree ID
	 */
	public String saveTree(Map<String, String[]> parameters) throws ParserException, SAXException, Exception {
		String id = importTree(parameters);
		
		if (id != null) {
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Saving request backup file");
			saveToFile(parameters);
		}
		
		return id;
	}
	
	/**
	 * Reload the tree upload requests saved in the backup directory. Used to repopulate the database.
	 */
	public void replayBackups() throws ParserException, SAXException, Exception {
		System.out.println("Replaying backups from " + treeBackupDir.getAbsolutePath());
		File[] files = treeBackupDir.listFiles();
		
		//order by modified date
		Arrays.sort(files, new Comparator<File>()
		{
			@Override
			public int compare(File o1, File o2)
			{
				Long to1 = Long.valueOf(o1.lastModified());
				Long t02 = Long.valueOf(o2.lastModified());
				return to1.compareTo(t02);
			}
		});
		
		for (File file : files) {
			System.out.println(file.getName());
			
			@SuppressWarnings("unchecked")
			Map<String,String[]> parameters = (Map<String,String[]>) getObject(file);
			importTree(parameters);
		}
		
		System.out.println("Re-imported all trees in " + treeBackupDir.getAbsolutePath());
	}
	
	private void saveToFile(byte[] data, String fileName)
	{
		try
		{
			fileName += fileName.endsWith(".gz") ? "" : ".gz";
			File file = new File(treeBackupDir, fileName);
			
			if (file.createNewFile()) {
				OutputStream out = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
				out.write(data);
				out.flush();
				out.close();
				Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Saving request parameters to backup location " + file.getAbsolutePath());
			}
		}
		catch(IOException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Unable to save backup of newick string to file system", e);
		}
	}

	private void saveToFile(Object o)
	{
		byte[][] bytesAndDigest = getByteArrayWithDigest(o);
		byte[] data = bytesAndDigest[0];
		byte[] digest = bytesAndDigest[1];
		String fileName = Hex.encodeHexString(digest);
		saveToFile(data, fileName);
	}
	
	private byte[][] getByteArrayWithDigest(Object o)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			ByteArrayOutputStream sink = new ByteArrayOutputStream();
			DigestOutputStream dos = new DigestOutputStream(sink, digest);
			ObjectOutputStream out = new ObjectOutputStream(dos);
			
			out.writeObject(o);
			out.flush();
			out.close();
			
			byte[] data = sink.toByteArray();
			byte[] hash = dos.getMessageDigest().digest();
			return new byte[][] {data, hash};
		}
		catch(Exception e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Unable to save backup of newick string to file system", e);
		}
		
		return null;
	}
	
	private Object getObject(File file) {
		Object o = null;
		
		try
		{
			ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
			
			o = in.readObject();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return o;
	}
	
	private String importTree(Map<String, String[]> parameters) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, ParserException, ImportException, SQLException {
		RemoteTree tree = null;
		byte[] hash = getByteArrayWithDigest(parameters)[1]; //same request should always get the same hash.  (for re-importing in replayBackups)
		
		if (parameters.containsKey("newickData")) 
		{
			String[] newicks = parameters.get("newickData");
			String[] names = parameters.get("name");
			
			int i = 0; //only get the first newickData and name

			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Importing newick string");
			String newick = newicks[i];
			String name = "unnamed";
			if (names != null && names.length > i) 
			{
				name = names[i];
			}
			

			tree = NewickUtil.treeFromNewick(newick, name);

		}
		
		else if (parameters.containsKey("nexml")) 
		{
			String nexml = parameters.get("nexml")[0];

			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Importing nexml");
			Document document = NexmlUtil.parse(nexml);
			Tree<Edge> nexmlTree = NexmlUtil.getFirstTree(document);
			tree = NexmlUtil.convertDataModels(nexmlTree);
		}
		
		tree.setHash(hash);
		importer.importTree(tree);

		layoutImporter.importLayouts(tree);
		importer.setImportComplete(tree);
		
		String id = Hex.encodeHexString(tree.getHash());
		return id;
	}

	/**
	 * Run from the command line to repopulate the database from saved backups.
	 * @param args if args[0] is replay, it reloads the backups.
	 */
	public static void main(String[] args) throws ParserException, SAXException, Exception {
		if (args.length > 0 && args[0].equals("replay")) {
			String server = "localhost";
			String database = "phyloviewer";
			String user = "phyloviewer";
			String password = "phyloviewer";
			String backupPath = "tree-uploads";
			String imagePath = "images";
			String persistenceUnitName = "org.iplantc.phyloviewer";
			
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
			
			PGPoolingDataSource pool = new PGPoolingDataSource();
			pool.setServerName(server);
			pool.setDatabaseName(database);
			pool.setUser(user);
			pool.setPassword(password);
			pool.setMaxConnections(10);
			
			ImportTreeLayout layoutImporter = new ImportTreeLayout(pool);
			layoutImporter.setImageDirectory(imagePath);
			
			PersistTreeData importer = new PersistTreeData(emf);
		
			ParseTree pt = new ParseTree(importer, layoutImporter);
			pt.setTreeBackupDir(backupPath);
			
			pt.replayBackups();
		}
	}
}

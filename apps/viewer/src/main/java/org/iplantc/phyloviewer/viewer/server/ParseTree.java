package org.iplantc.phyloviewer.viewer.server;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.codec.binary.Hex;
import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.server.persistence.ImportTreeLayout;
import org.iplantc.phyloviewer.viewer.server.persistence.PersistTreeData;
import org.postgresql.ds.PGPoolingDataSource;
import org.xml.sax.SAXException;

/**
 * Performs all of the non-HTTPServlet-related functions of the ParseTreeService. Also exposes methods to
 * reload trees from backup files.
 */
public class ParseTree
{
	File treeBackupDir = new File(".");
	private IImportTreeData importer;
	
	public ParseTree(IImportTreeData importer) {
		this.importer = importer;
	}
	
	public void setImporter(IImportTreeData importer)
	{
		this.importer = importer;
	}
	
	public void setTreeBackupDir(String treeBackupPath)
	{
		treeBackupDir = new File(treeBackupPath); 
		treeBackupDir.mkdir();
	}
	
	public File getTreeBackupDir()
	{
		return treeBackupDir;
	}
	
	public List<String> saveTrees(Map<String, String[]> parameters) throws ParserException, SAXException, Exception {
		List<String> ids = loadTrees(parameters);
		
		if (ids.size() > 0) {
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Saving request backup file");
			saveToFile(parameters);
		}
		
		return ids;
	}
	
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
			loadTrees(parameters);
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
			String fileName = Hex.encodeHexString(hash);
			saveToFile(data, fileName);
		}
		catch(NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Object getObject(File file) {
		Object o = null;
		
		try
		{
			ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
			
			o = in.readObject();
		}
		catch(FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return o;
	}
	
	private List<String> loadTrees(Map<String, String[]> parameters) throws ParserException, SAXException, Exception {
		List<String> ids = new ArrayList<String>();
		
		if (parameters.containsKey("newickData")) 
		{
			String[] newicks = parameters.get("newickData");
			String[] names = parameters.get("name");
			for (int i = 0; i < newicks.length; i++)
			{
				Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Importing newick string");
				String newick = newicks[i];
				String name = "unnamed";
				if (names != null && names.length > i) 
				{
					name = names[i];
				}
				
				RemoteTree tree = importer.importFromNewick(newick, name);
				String hash = Hex.encodeHexString(tree.getHash());
				ids.add(hash);
			}
		}
		
		if (parameters.containsKey("nexml")) 
		{
			for (String nexml : parameters.get("nexml"))
			{
				Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Importing nexml");
				List<RemoteTree> trees = importer.importFromNexml(nexml);
				for (RemoteTree tree : trees) {
					String hash = Hex.encodeHexString(tree.getHash());
					ids.add(hash);
				}
			}
		}
		
		return ids;
	}

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
			importer.setLayoutImporter(layoutImporter);
		
			ParseTree pt = new ParseTree(importer);
			pt.setTreeBackupDir(backupPath);
			
			pt.replayBackups();
		}
	}
}

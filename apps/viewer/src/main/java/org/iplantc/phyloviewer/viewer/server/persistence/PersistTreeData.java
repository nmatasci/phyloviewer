package org.iplantc.phyloviewer.viewer.server.persistence;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.iplantc.phyloviewer.viewer.server.IImportTreeData;
import org.iplantc.phyloviewer.viewer.server.ImportTreeUtil;
import org.nexml.model.Document;
import org.nexml.model.DocumentFactory;
import org.nexml.model.Edge;
import org.nexml.model.Network;
import org.nexml.model.Tree;
import org.nexml.model.TreeBlock;
import org.xml.sax.SAXException;

public class PersistTreeData implements IImportTreeData
{	
	private final EntityManagerFactory emf;
	private ImportTreeLayout layoutImporter;
	private String treeBackupPath;
	
	public PersistTreeData(EntityManagerFactory emf)
	{
		this.emf = emf;
	}
	
	public void setTreeBackupPath(String treeBackupPath) {
		this.treeBackupPath = treeBackupPath;
	}
	
	@Override
	public RemoteTree importFromNewick(String newick, String name) throws ParserException, SQLException, TreeDataException
	{	
		EntityManager em = emf.createEntityManager();
		byte[] hash = DigestUtils.md5(newick);
		RemoteNode root = null;
		RemoteTree existingTree = getExistingTree(hash, em);
		boolean doLayout;
		
		if (existingTree != null)
		{
			if (existingTree.getName().equals(name)) {
				return existingTree;
			}
			
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "A tree matching the given newick string was found, but with a different name. Creating a new tree with the existing nodes.");
			root = existingTree.getRootNode();
			doLayout = false;
		}
		else
		{
			root = ImportTreeUtil.rootNodeFromNewick(newick, name);
			saveBackupFile(hash, newick);
			doLayout = true;
		}
		
		RemoteTree tree = new RemoteTree(name);
		tree.setRootNode(root);
		
		if (hash != null) {
			tree.setHash(hash);
		}
		
		em.getTransaction().begin();
		em.persist(tree);
		em.getTransaction().commit();
		
		if(doLayout)
		{
			//TODO doing this inside PersistTreeData seems unnecessary and is probably temporary.  call this from outside.
			if(layoutImporter != null) 
			{
				layoutImporter.importLayouts(tree);
			}
		}
		
		em.close();
		
		return tree;
	}
	
	@Override
	public List<RemoteTree> importFromNexml(String nexml) throws ParserConfigurationException, SAXException, IOException, SQLException
	{
		byte[] hash = DigestUtils.md5(nexml);
		
		EntityManager em = emf.createEntityManager();
		InputStream stream = new ByteArrayInputStream(nexml.getBytes("UTF-8"));
		Document document = DocumentFactory.parse(stream);
		TreeBlock treeBlock = document.getTreeBlockList().get(0);
		
		List<RemoteTree> trees = new ArrayList<RemoteTree>();
		if (treeBlock != null) {
			for (Network<?> network : treeBlock) {
				if (network instanceof Tree) {
					@SuppressWarnings("unchecked")
					Tree<Edge> nexmlTree = (Tree<Edge>) network;
					RemoteTree tree = ImportTreeUtil.convertDataModels(nexmlTree);
					tree.setHash(hash);
					trees.add(tree);
					
					em.getTransaction().begin();
					em.persist(tree);
					em.getTransaction().commit();
					
					if(layoutImporter != null) 
					{
						layoutImporter.importLayouts(tree);
					}
				}
			}
		}
		
		saveBackupFile(hash, nexml);
		
		em.close();
		
		return trees;
	}
	
	private RemoteTree getExistingTree(byte[] hash, EntityManager em) {
		RemoteTree matchingTree = null;
		if (hash != null)
		{
			em.getTransaction().begin();
			TypedQuery<RemoteTree> query = em.createQuery("SELECT t FROM RemoteTree t WHERE t.hash = :hash", RemoteTree.class);
			query.setParameter("hash", hash);
			List<RemoteTree> results = query.getResultList();
			if (!results.isEmpty()) {
				matchingTree = results.get(0);
			}
			
			em.getTransaction().commit();
		}
		
		return matchingTree;
	}

	public ImportTreeLayout getLayoutImporter()
	{
		return layoutImporter;
	}

	public void setLayoutImporter(ImportTreeLayout layoutImporter)
	{
		this.layoutImporter = layoutImporter;
	}
	
	private void saveBackupFile(byte[] hash, String data)
	{
		if(this.treeBackupPath != null)
		{
			try
			{
				String name = Hex.encodeHexString(hash);
				new File(treeBackupPath).mkdir();
				File file = new File(treeBackupPath + name);
				
				Writer writer = new BufferedWriter(new FileWriter(file));
				writer.write(data);
				writer.close();
			}
			catch(IOException e)
			{
				Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Unable to save backup of newick string to file system", e);
			}
		}
	}
}

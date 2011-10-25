package org.iplantc.phyloviewer.viewer.server.persistence;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.xml.parsers.ParserConfigurationException;

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
	static final String encoding = "UTF-8";
	static final String algorithm = "MD5";
	
	private final EntityManagerFactory emf;
	private ImportTreeLayout layoutImporter;
	
	public PersistTreeData(EntityManagerFactory emf)
	{
		this.emf = emf;
	}
	
	@Override
	public RemoteTree importFromNewick(String newick, String name) throws ParserException, SQLException, TreeDataException
	{	
		EntityManager em = emf.createEntityManager();
		byte[] hash = getHash(newick);
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
		
		//TODO save newick to disk as backup
		
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
		
		//TODO save nexml to disk as backup
		
		em.close();
		
		return trees;
	}
	
	private byte[] getHash(String newick)
	{
		byte[] hash = null;
		
		try
		{
			hash = HashTree.hash(newick, encoding, algorithm);
		}
		catch(Exception e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Unable to create hash value for tree using " + algorithm + " & " + encoding, e);
		}

		return hash;
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
}

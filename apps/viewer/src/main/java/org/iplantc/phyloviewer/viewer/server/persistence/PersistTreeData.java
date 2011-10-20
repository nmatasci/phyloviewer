package org.iplantc.phyloviewer.viewer.server.persistence;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.iplantc.phyloviewer.viewer.server.IImportTreeData;
import org.iplantc.phyloviewer.viewer.server.ImportTreeUtil;

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
		
		if (existingTree != null)
		{
			if (existingTree.getName().equals(name)) {
				return existingTree;
			}
			
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "A tree matching the given newick string was found, but with a different name. Creating a new tree with the existing nodes.");
			root = existingTree.getRootNode();
		}
		else
		{
			root = ImportTreeUtil.rootNodeFromNewick(newick, name);
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

package org.iplantc.phyloviewer.viewer.server.db;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.iplantc.phyloviewer.viewer.server.HashTree;
import org.iplantc.phyloviewer.viewer.server.IImportTreeData;

public class PersistTreeData implements IImportTreeData
{	
	private final EntityManagerFactory emf;
	
	public PersistTreeData(EntityManagerFactory emf)
	{
		this.emf = emf;
	}
	
	@Override
	public int importFromNewick(String newick, String name) throws ParserException, SQLException, TreeDataException
	{	
		int id = getExistingTreeID(newick);
		if (id != -1)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "A tree matching the given newick string was found.  Returning ID of existing tree.");
			return id;
		}
		
		RemoteNode root = ImportTreeData.rootNodeFromNewick(newick, name);
		persist(root);
		id = root.getId();
		
		//TODO add the hash of the tree to the database
		//TODO save newick to disk as backup
		
		return id;
	}

	private void persist(RemoteNode root)
	{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(root);
		em.getTransaction().commit();
		em.close();
	}
	
	private int getExistingTreeID(String newick)
	{
		String encoding = "UTF-8";
		String algorithm = "MD5";
		int id = -1;

		try
		{
			byte[] hash = HashTree.hash(newick, encoding, algorithm);
		}
		catch(Exception e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Unable to create hash value for tree using " + algorithm + " & " + encoding, e);
		}
		
		//TODO id = find tree by hash
		
		return id;
	}

}

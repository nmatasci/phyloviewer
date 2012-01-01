package org.iplantc.phyloviewer.viewer.server.persistence;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.server.IImportTreeData;
import org.iplantc.phyloviewer.viewer.server.ImportException;

public class PersistTreeData implements IImportTreeData
{	
	private final EntityManagerFactory emf;
	
	public PersistTreeData(EntityManagerFactory emf)
	{
		this.emf = emf;
	}

	@Override
	public void importTree(RemoteTree tree) throws ImportException
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Persisting tree");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		
		tx.begin();
		try
		{
			em.persist(tree);
			tx.commit();
		}
		catch(PersistenceException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Exception persisting tree " + tree.getName());
			throw new ImportException(e);
		}

		em.detach(tree);
		em.close();
	}

	@Override
	public void setImportComplete(RemoteTree tree)
	{
		EntityManager em = emf.createEntityManager();
		
		tree.setImportComplete(true);
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.merge(tree);
		tx.commit();
		em.close();
	}
}

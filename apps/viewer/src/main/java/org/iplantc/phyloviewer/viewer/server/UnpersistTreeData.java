package org.iplantc.phyloviewer.viewer.server;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;

public class UnpersistTreeData implements ITreeData
{
	private final EntityManagerFactory emf;
	
	public UnpersistTreeData(EntityManagerFactory emf)
	{
		this.emf = emf;
	}

	@Override
	public RemoteNode getRootNode(int treeID) throws TreeDataException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ITree> getTrees() throws TreeDataException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemoteNode getSubtree(int rootID, int depth) throws TreeDataException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RemoteNode> getChildren(int parentID) throws TreeDataException
	{
		EntityManager em = emf.createEntityManager();
		
		//pull the root back out of persistence
		em.getTransaction().begin();
		Query query = em.createQuery("SELECT n FROM RemoteNode n WHERE n.id = :id")
				.setParameter("id", parentID);

		RemoteNode parent = (RemoteNode) query.getSingleResult();
		List<RemoteNode> children = parent.getChildren();
		em.close();
		
		return children;
	}

}

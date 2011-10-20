package org.iplantc.phyloviewer.viewer.server.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.iplantc.phyloviewer.viewer.server.ITreeData;

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
		EntityManager em = emf.createEntityManager();
		
		RemoteNode root = getRootNode(treeID, em);
		em.detach(root);
		em.close();
		
		return root;
	}
	
	/**
	 * @return the tree's root node.  Still attached to the persistence context so, for example, children can be lazily fetched by calling node.getChild().
	 */
	public RemoteNode getRootNode(int treeID, EntityManager em) throws TreeDataException
	{
		em.getTransaction().begin();
		TypedQuery<RemoteNode> query = em.createQuery("SELECT n FROM RemoteTree t JOIN t.rootNode n WHERE t.id = :id", RemoteNode.class)
				.setParameter("id", treeID);

		RemoteNode root = query.getSingleResult();

		em.getTransaction().commit();
		
		return root;
	}

	@Override
	public List<ITree> getTrees() throws TreeDataException
	{
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin();
		TypedQuery<ITree> query = em.createQuery("SELECT t FROM RemoteTree t", ITree.class);

		List<ITree> trees = query.getResultList();

		em.getTransaction().commit();
		em.close();
		
		return trees;
	}

	@Override
	public List<RemoteNode> getChildren(int parentID) throws TreeDataException
	{
		EntityManager em = emf.createEntityManager();
		
		//pull the root back out of persistence
		em.getTransaction().begin();
		TypedQuery<RemoteNode> query = em.createQuery("SELECT n FROM RemoteNode n WHERE n.id = :id", RemoteNode.class)
				.setParameter("id", parentID);

		List<RemoteNode> children = query.getSingleResult().getChildren();
		children.isEmpty(); //force lazy fetch of children
		
		em.getTransaction().commit();
		em.close();
		
		return children;
	}

}

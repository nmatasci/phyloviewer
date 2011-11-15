package org.iplantc.phyloviewer.viewer.server.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.viewer.client.model.NodeTopology;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
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
	public RemoteNode getRootNode(byte[] rootID) throws TreeDataException
	{
		EntityManager em = emf.createEntityManager();
		
		RemoteNode root = getRootNode(rootID, em);
		em.detach(root);
		em.close();
		
		return clone(root);
	}
	
	/**
	* @return the tree's root node.  Still attached to the persistence context so, for example, children can be lazily fetched by calling node.getChild().
	*/
	public RemoteNode getRootNode(byte[] rootID, EntityManager em) throws TreeDataException
	{
		em.getTransaction().begin();
		TypedQuery<RemoteNode> query = em.createQuery("SELECT n FROM RemoteTree t JOIN t.rootNode n WHERE t.hash = :id", RemoteNode.class)
				.setParameter("id", rootID);

		RemoteNode node;
		try
		{
			node = query.getSingleResult();
			em.getTransaction().commit();
		}
		catch(NoResultException e)
		{
			throw new TreeDataException("Tree not found for ID " + Arrays.toString(rootID));
		}
		
		return node;
	}

	@Override
	public List<ITree> getTrees() throws TreeDataException
	{
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin();
		TypedQuery<RemoteTree> query = em.createQuery("SELECT t FROM RemoteTree t", RemoteTree.class);

		List<RemoteTree> results = query.getResultList();
		List<ITree> trees = new ArrayList<ITree>(results.size());
		for(RemoteTree tree : results) {
			em.detach(tree);
			trees.add(clone(tree));
		}
		
		em.getTransaction().commit();
		em.close();

		return trees;
	}

	@Override
	public List<RemoteNode> getChildren(int parentID) throws TreeDataException
	{
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin();
		TypedQuery<RemoteNode> query = em.createQuery("SELECT n FROM RemoteNode n WHERE n.id = :id", RemoteNode.class)
				.setParameter("id", parentID);

		RemoteNode parent = query.getSingleResult();
		parent.getChildren().isEmpty(); //force lazy fetch of children
		
		List<RemoteNode> children = clone(parent).getChildren();
		em.detach(parent);
		
		em.getTransaction().commit();
		em.close();
		
		return children;
	}
	
	public static RemoteNode clone(RemoteNode node) {
		RemoteNode clone = new RemoteNode(node);
		
		NodeTopology topology = new NodeTopology(node.getTopology());
		topology.setRootNode(null);
		clone.setTopology(topology);
		
		List<RemoteNode> children = node.getChildren();
		clone.setChildren(null);
		if (children != null && Persistence.getPersistenceUtil().isLoaded(node, "children")) {
			for (RemoteNode child : children) {
				clone.addChild(clone(child));
			}
		}
		
		return clone;
	}
	
	public static RemoteTree clone(RemoteTree tree) {
		RemoteTree clone = new RemoteTree(tree);
		clone.setRootNode(clone(tree.getRootNode()));
		
		return clone;
	}
}

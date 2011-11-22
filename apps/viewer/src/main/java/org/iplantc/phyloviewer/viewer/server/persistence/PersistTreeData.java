package org.iplantc.phyloviewer.viewer.server.persistence;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
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
	private static final String hashAlgorithm = "MD5";
	private final EntityManagerFactory emf;
	private ImportTreeLayout layoutImporter;
	
	public PersistTreeData(EntityManagerFactory emf)
	{
		this.emf = emf;
	}
	
	@Override
	public RemoteTree importFromNewick(String newick, String name) throws ParserException, SQLException, TreeDataException
	{	
		RemoteNode root = ImportTreeUtil.rootNodeFromNewick(newick, name);
		byte[] hash = hashTree(root);
		newick = null;
		
		RemoteTree tree = new RemoteTree(name);
		tree.setRootNode(root);
		tree.setHash(hash);
		tree.setName(name);
		
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Checking for existing tree match");
		RemoteTree existingTree = getExistingTree(hash, em);
		
		boolean doLayout;
		
		if (existingTree != null) {
			if (existingTree.getName().equals(name)) {
				Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Existing tree match found.  Skipping import.");
				return existingTree; //same tree, same name, nothing to do
			} 
			
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "A tree matching the given newick string was found, but with a different name. Creating a new tree with the existing nodes.");
			root = null;
			tree.setRootNode(existingTree.getRootNode());
			doLayout = false;
		} else {
			tree.setRootNode(root);
			doLayout = true;
		}

		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Persisting tree");
		tx.begin();
		em.persist(tree);
		tx.commit();

		//TODO doing this inside PersistTreeData seems unnecessary and is probably temporary.  call this from outside.
		if(doLayout && layoutImporter != null) 
		{
			layoutImporter.importLayouts(tree);
		}
		
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Updating importComplete flag");
		tree.setImportComplete(true);
		tx.begin();
		em.merge(tree);
		tx.commit();
		
		em.detach(tree);
		
		em.close();
		
		return tree;
	}
	
	@Override
	public List<RemoteTree> importFromNexml(String nexml) throws ParserConfigurationException, SAXException, IOException, SQLException
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		InputStream stream = new ByteArrayInputStream(nexml.getBytes("UTF-8"));
		
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Parsing nexml");
		Document document = DocumentFactory.parse(stream);
		stream.close();
		
		TreeBlock treeBlock = document.getTreeBlockList().get(0);
		List<RemoteTree> trees = new ArrayList<RemoteTree>();
		if (treeBlock != null) {
			for (Network<?> network : treeBlock) {
				if (network instanceof Tree) {
					@SuppressWarnings("unchecked")
					Tree<Edge> nexmlTree = (Tree<Edge>) network;
					RemoteTree tree = ImportTreeUtil.convertDataModels(nexmlTree);
					byte[] hash = hashTree(tree.getRootNode());
					
					Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Checking for existing tree match");
					RemoteTree existingTree = getExistingTree(hash, em);
					
					if (existingTree != null) {
						Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Existing tree match found.  Skipping import.");
						trees.add(existingTree);
						//FIXME: this duplicate tree could have different node annotations.  If so, find some way to persist them.
						continue;
					}
					
					tree.setHash(hash);
					trees.add(tree);
					
					Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Persisting tree");
					tx.begin();
					try
					{
						em.persist(tree);
						tx.commit();
					}
					catch(RollbackException e)
					{
						//FIXME: strange hibernate behavior.  It's assigning ids that have already been used. Always near id# 6050
						Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Exception persisting tree " + tree.getName());
						continue; //TODO Just fail altogether?
					}
					
					if(layoutImporter != null) 
					{
						layoutImporter.importLayouts(tree);
					}
					
					Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Updating importComplete flag");
					tree.setImportComplete(true);
					tx.begin();
					em.merge(tree);
					tx.commit();
					
					em.detach(tree);
				}
			}
		}

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
	
	public static byte[] hashTree(RemoteNode root) {
		byte[] hash = null;
		
		try
		{
			MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);
			hashNode(root, digest);
			hash = digest.digest();
		}
		catch(NoSuchAlgorithmException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, e.getMessage());
		}
		
		return hash;
	}

	public static void hashNode(RemoteNode node, MessageDigest digest) {
		if (node.getNumberOfChildren() > 0) {
			for (RemoteNode child : node.getChildren()) {
				hashNode(child, digest);
			}
		}
		
		String label = node.getLabel();
		if (label != null) {
			digest.update(label.getBytes());
		}
		
		Double branchLength = node.getBranchLength();
		byte[] bytes = new byte[8];
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		buf.putDouble(branchLength);
		digest.update(bytes);
		
		bytes = new byte[4];
		buf = ByteBuffer.wrap(bytes);
		buf.putInt(node.getTopology().getRightIndex());
		digest.update(bytes);
	}
}

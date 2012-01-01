package org.iplantc.phyloviewer.viewer.server.persistence;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.iplantc.phyloviewer.viewer.client.model.AnnotatedNode;
import org.iplantc.phyloviewer.viewer.client.model.AnnotatedTree;
import org.iplantc.phyloviewer.viewer.client.model.LiteralMetaAnnotation;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.client.model.ResourceMetaAnnotation;
import org.iplantc.phyloviewer.viewer.server.ImportException;
import org.iplantc.phyloviewer.viewer.server.NewickUtil;
import org.junit.Test;

public class PersistTreeDataTest extends PersistenceTest
{
	@Test
	public void testImportRemoteTree() throws ImportException
	{
		RemoteNode root = rn(null,
			rn("Protomyces_inouyei"),
			rn(null, 
				rn("Taphrina_wiesneri"),
				rn("Taphrina_deformans")
			)
		);
		
		RemoteTree tree = new RemoteTree();
		tree.setHash(new byte[] {42, 42, 42, 42, 42, 42, 42, 42});
		tree.setName("test");
		
		tree.setRootNode(root);
		
		PersistTreeData out = new PersistTreeData(entityManagerFactory);
		out.importTree(tree);

		EntityManager em = entityManagerFactory.createEntityManager();
		
		Object found = em.find(RemoteTree.class, tree.getId());
		assertNotNull(found);
		
		found = em.find(RemoteNode.class, root.getId());
		assertNotNull(found);
		
		 //imported tree was detached from the persistence context
		assertFalse(em.contains(tree));
		assertFalse(em.contains(root));
	}
	
	@Test
	public void testImportAnnotatedTree() throws ImportException
	{
		LiteralMetaAnnotation literal = new LiteralMetaAnnotation();
		literal.setPredicateNamespace("predicateNamespace");
		literal.setDatatype("datatype");
		literal.setProperty("property");
		literal.setValue("value");
		
		ResourceMetaAnnotation resource = new ResourceMetaAnnotation();
		resource.setRel("rel");
		
		ResourceMetaAnnotation nestedResource = new ResourceMetaAnnotation();
		nestedResource.setHref("href");
		nestedResource.setRel("rel");
		resource.addAnnotation(nestedResource);
		
		AnnotatedNode node = new AnnotatedNode();
		node.addAnnotation(literal);
		
		AnnotatedTree tree = new AnnotatedTree();
		tree.setRootNode(node);
		tree.addAnnotation(resource);
		
		PersistTreeData out = new PersistTreeData(entityManagerFactory);
		out.importTree(tree);

		EntityManager em = entityManagerFactory.createEntityManager();
		
		Object found = em.find(RemoteTree.class, tree.getId());
		assertNotNull(found);
		
		found = em.find(RemoteNode.class, node.getId());
		assertNotNull(found);
		
		found = em.find(LiteralMetaAnnotation.class, literal.getId());
		assertNotNull(found);
		
		found = em.find(ResourceMetaAnnotation.class, resource.getId());
		assertNotNull(found);
		
		found = em.find(ResourceMetaAnnotation.class, nestedResource.getId());
		assertNotNull(found);
	}

	@Test
	public void testDuplicateTree() throws Exception
	{
		String newick = "(Protomyces_inouyei,(Taphrina_wiesneri,Taphrina_deformans));";

		PersistTreeData out = new PersistTreeData(entityManagerFactory);
		
		RemoteTree tree = NewickUtil.treeFromNewick(newick, "test");
		byte[] treeID = {4,4,4,4,4,4,4,4};
		tree.setHash(treeID);
		out.importTree(tree);
		
		tree = NewickUtil.treeFromNewick(newick, "duplicate");
		byte[] treeID2 = {5,5,5,5,5,5,5,5};
		tree.setHash(treeID2);
		out.importTree(tree);
		
		UnpersistTreeData in = new UnpersistTreeData(entityManagerFactory);
		RemoteTree tree1 = in.getTree(treeID);
		RemoteTree tree2 = in.getTree(treeID2);
		
		assertNotSame(tree1, tree2);
		assertNotSame(tree1.getRootNode(), tree2.getRootNode());
	}
	
	private RemoteNode rn(String label, RemoteNode... children) 
	{
		RemoteNode node = new RemoteNode();

		if (label != null)
		{
			node.setLabel(label);
		}
		
		if (children != null)
		{
			node.setChildren(Arrays.asList(children));
		}
		
		return node;
	}
	
	//@Test
	public void testBigTree()
	{
		int depth = 10;
		int numChildren = 2;
		long startTime;
		
		System.out.print("building tree...");
		startTime = System.currentTimeMillis();
		RemoteNode root = createTree(depth, numChildren);
		root.reindex();
		System.out.println(System.currentTimeMillis() - startTime + " ms");
		
		System.out.print("persisting tree...");
		startTime = System.currentTimeMillis();
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(root);
		entityManager.getTransaction().commit();
		entityManager.close();
		System.out.println(System.currentTimeMillis() - startTime + " ms");
		
		//pull the root back out
		entityManager = entityManagerFactory.createEntityManager();
		
		System.out.print("unpersisting root...");
		startTime = System.currentTimeMillis();
		TypedQuery<RemoteNode> query = entityManager.createQuery("SELECT n FROM RemoteNode n WHERE n.id = :id ", RemoteNode.class)
				.setParameter("id", root.getId());
		
		@SuppressWarnings("unused")
		RemoteNode copyOfRoot = query.getSingleResult();
		System.out.println(System.currentTimeMillis() - startTime + " ms");
		
		entityManager.close();
		
		assertTrue(true);
		
	}
	
	private RemoteNode createTree(int depth, int numChildren)
	{
		RemoteNode node = new RemoteNode();
		
		createTree(node, depth, numChildren);
		
		return node;
	}

	private void createTree(RemoteNode node, int depth, int numChildren)
	{
		if (depth == 0) {
			return;
		} else {
			List<RemoteNode> children = createChildren(numChildren);
			node.setChildren(children);
			
			for (RemoteNode child : children) {
				createTree(child, depth - 1, numChildren);
			}
		}
	}

	private List<RemoteNode> createChildren(int numChildren)
	{
		ArrayList<RemoteNode> children = new ArrayList<RemoteNode>();
		RemoteNode child;
		for (int i = 0; i < numChildren; i++) {
			child = new RemoteNode();
			children.add(child);
		}
		
		return children;
	}
}

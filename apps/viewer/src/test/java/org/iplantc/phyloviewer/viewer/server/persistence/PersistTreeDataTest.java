package org.iplantc.phyloviewer.viewer.server.persistence;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.junit.Test;

public class PersistTreeDataTest extends PersistenceTest
{
	String newick = "(Protomyces_inouyei,(Taphrina_wiesneri,Taphrina_deformans));";
	String nexml = "<nex:nexml> <otus id=\"otus26\"> <otu id=\"otu27\" label=\"Eurysphindus\"/> </otus> <trees about=\"#trees22\" id=\"trees22\" otus=\"otus26\"> <meta content=\"117855\" datatype=\"xsd:integer\" id=\"meta24\" property=\"dcterms:identifier\" xsi:type=\"nex:LiteralMeta\"/> <meta href=\"http://tolweb.org/117855\" id=\"meta25\" rel=\"owl:sameAs\" xsi:type=\"nex:ResourceMeta\"/> <tree id=\"tree1\" xsi:type=\"nex:IntTree\"> <node about=\"#node2\" id=\"node2\" root=\"true\"> <meta content=\"117851\" datatype=\"xsd:integer\" id=\"meta21\" property=\"tba:ID\" xsi:type=\"nex:LiteralMeta\"/> </node> <node about=\"#node3\" id=\"node3\" label=\"Eurysphindus\" otu=\"otu27\"> <meta content=\"\" datatype=\"xsd:string\" id=\"meta4\" property=\"dc:description\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"Leconte\" datatype=\"xsd:string\" id=\"meta5\" property=\"tbe:AUTHORITY\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"1878\" datatype=\"xsd:integer\" id=\"meta6\" property=\"tbe:AUTHDATE\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"117851\" datatype=\"xsd:integer\" id=\"meta7\" property=\"tba:ANCESTORWITHPAGE\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta8\" property=\"tba:CHILDCOUNT\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"null\" datatype=\"xsd:string\" id=\"meta9\" property=\"tba:COMBINATION_DATE\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta10\" property=\"tba:CONFIDENCE\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta11\" property=\"tba:EXTINCT\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"1\" datatype=\"xsd:integer\" id=\"meta12\" property=\"tba:HASPAGE\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"117855\" datatype=\"xsd:integer\" id=\"meta13\" property=\"tba:ID\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta14\" property=\"tba:INCOMPLETESUBGROUPS\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta15\" property=\"tba:IS_NEW_COMBINATION\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"1\" datatype=\"xsd:integer\" id=\"meta16\" property=\"tba:ITALICIZENAME\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta17\" property=\"tba:LEAF\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta18\" property=\"tba:PHYLESIS\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta19\" property=\"tba:SHOWAUTHORITY\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"1\" datatype=\"xsd:integer\" id=\"meta20\" property=\"tba:SHOWAUTHORITYCONTAINING\" xsi:type=\"nex:LiteralMeta\"/> </node> <edge id=\"edge3\" source=\"node2\" target=\"node3\"/> </tree> </trees> </nex:nexml>";
	
	@Test
	public void testImportFromNewick() throws Exception
	{
		PersistTreeData out = new PersistTreeData(entityManagerFactory);
		RemoteTree tree = out.importFromNewick(newick, "test");
		
		UnpersistTreeData in = new UnpersistTreeData(entityManagerFactory);
		EntityManager em = entityManagerFactory.createEntityManager();
		RemoteNode root = in.getRootNode(tree.getHash(), em);
		
		RemoteNode node = (RemoteNode) root.getChild(0);
		assertEquals("Protomyces_inouyei", node.getLabel());
		
		node = (RemoteNode) root.getChild(1).getChild(0);
		assertEquals("Taphrina_wiesneri", node.getLabel());
		
		node = (RemoteNode) root.getChild(1).getChild(1);
		assertEquals("Taphrina_deformans", node.getLabel());
		
		em.close();
	}
	
	@Test
	public void testImportFromNexml() throws Exception
	{
		PersistTreeData out = new PersistTreeData(entityManagerFactory);
		
		List<RemoteTree> trees = out.importFromNexml(nexml);
		assertEquals(1, trees.size());
		RemoteTree tree = trees.get(0);
		assertEquals("tree1", tree.getName());
	}
	
	@Test
	public void testDuplicateTree() throws Exception
	{
		PersistTreeData out = new PersistTreeData(entityManagerFactory);
		byte[] treeID = out.importFromNewick(newick, "test").getHash();
		byte[] treeID2 = out.importFromNewick(newick, "duplicate").getHash();
		
		UnpersistTreeData in = new UnpersistTreeData(entityManagerFactory);
		RemoteNode root = in.getRootNode(treeID);
		RemoteNode root2 = in.getRootNode(treeID2);
		assertEquals("not the same root node", root, root2);
	}
	
	@Test
	public void testHashTree() {
		RemoteTree[] trees = new RemoteTree[5];
		byte[][] hashes = new byte[trees.length][16];
		
		//root, 2 children, empty labels
		RemoteNode node = new RemoteNode();
		node.addChild(new RemoteNode());
		node.addChild(new RemoteNode());
		node.reindex();
		trees[0] = new RemoteTree();
		trees[0].setRootNode(node);
		
		//root, 2 children, with labels
		node = new RemoteNode();
		node.setLabel("A");
		RemoteNode child = new RemoteNode();
		child.setLabel("B");
		node.addChild(child);
		child = new RemoteNode();
		child.setLabel("C");
		node.addChild(child);
		node.reindex();
		trees[1] = new RemoteTree();
		trees[1].setRootNode(node);
		
		//root, 2 children swapped, with labels
		node = new RemoteNode();
		node.setLabel("A");
		child = new RemoteNode();
		child.setLabel("C");
		node.addChild(child);
		child = new RemoteNode();
		child.setLabel("B");
		node.reindex();
		trees[2] = new RemoteTree();
		trees[2].setRootNode(node);
		
		//root, 2 children, empty labels, with different branch lengths
		node = new RemoteNode();
		node.setBranchLength(2.0);
		child = new RemoteNode();
		child.setBranchLength(42.0);
		node.addChild(child);
		child = new RemoteNode();
		child.setBranchLength(1.0);
		node.addChild(child);
		node.reindex();
		trees[3] = new RemoteTree();
		trees[3].setRootNode(node);
		
		//root, child, grandchild
		node = new RemoteNode();
		child = new RemoteNode();
		node.addChild(child);
		child.addChild(new RemoteNode());
		node.reindex();
		trees[4] = new RemoteTree();
		trees[4].setRootNode(node);
		
		for (int t = 0; t < trees.length; t++) {
			hashes[t] = PersistTreeData.hashTree(trees[t].getRootNode());
		}
		
		//loop over hashes
		for (int i = 0; i < hashes.length; i++) {
			//loop over other hashes
			for (int j = i + 1; j < hashes.length; j++) {
				boolean same = true;
				//loop over bytes
				for (int b = 0; b < hashes[i].length; b++) {
					same &= hashes[i][b] == hashes[j][b];
				}
				Assert.assertFalse("hashes " + i + " and " + j + " are the same: \n" + Arrays.toString(hashes[i]) + "\n" + Arrays.toString(hashes[j]), same);
			}
		}
	}

}

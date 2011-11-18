package org.iplantc.phyloviewer.viewer.server;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloviewer.viewer.client.model.AnnotatedNode;
import org.iplantc.phyloviewer.viewer.client.model.Annotation;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.junit.Before;
import org.junit.Test;
import org.nexml.model.Document;
import org.nexml.model.DocumentFactory;
import org.nexml.model.Edge;
import org.nexml.model.Node;
import org.nexml.model.Tree;
import org.nexml.model.TreeBlock;
import org.xml.sax.SAXException;

public class ImportTreeUtilTest
{
	String newick = "(a:1,(ba:1[&&NHX:someAnnotation],bb:2)b:2)r;";
	String nexml = "<nex:nexml> <otus id=\"otus26\"> <otu id=\"otu27\" label=\"Eurysphindus\"/> </otus> <trees about=\"#trees22\" id=\"trees22\" otus=\"otus26\"> <meta content=\"117855\" datatype=\"xsd:integer\" id=\"meta24\" property=\"dcterms:identifier\" xsi:type=\"nex:LiteralMeta\"/> <meta href=\"http://tolweb.org/117855\" id=\"meta25\" rel=\"owl:sameAs\" xsi:type=\"nex:ResourceMeta\"/> <tree id=\"tree1\" xsi:type=\"nex:IntTree\"> <node about=\"#node2\" id=\"node2\" root=\"true\"> <meta content=\"117851\" datatype=\"xsd:integer\" id=\"meta21\" property=\"tba:ID\" xsi:type=\"nex:LiteralMeta\"/> </node> <node about=\"#node3\" id=\"node3\" label=\"Eurysphindus\" otu=\"otu27\"> <meta content=\"\" datatype=\"xsd:string\" id=\"meta4\" property=\"dc:description\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"Leconte\" datatype=\"xsd:string\" id=\"meta5\" property=\"tbe:AUTHORITY\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"1878\" datatype=\"xsd:integer\" id=\"meta6\" property=\"tbe:AUTHDATE\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"117851\" datatype=\"xsd:integer\" id=\"meta7\" property=\"tba:ANCESTORWITHPAGE\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta8\" property=\"tba:CHILDCOUNT\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"null\" datatype=\"xsd:string\" id=\"meta9\" property=\"tba:COMBINATION_DATE\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta10\" property=\"tba:CONFIDENCE\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta11\" property=\"tba:EXTINCT\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"1\" datatype=\"xsd:integer\" id=\"meta12\" property=\"tba:HASPAGE\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"117855\" datatype=\"xsd:integer\" id=\"meta13\" property=\"tba:ID\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta14\" property=\"tba:INCOMPLETESUBGROUPS\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta15\" property=\"tba:IS_NEW_COMBINATION\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"1\" datatype=\"xsd:integer\" id=\"meta16\" property=\"tba:ITALICIZENAME\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta17\" property=\"tba:LEAF\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta18\" property=\"tba:PHYLESIS\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"0\" datatype=\"xsd:integer\" id=\"meta19\" property=\"tba:SHOWAUTHORITY\" xsi:type=\"nex:LiteralMeta\"/> <meta content=\"1\" datatype=\"xsd:integer\" id=\"meta20\" property=\"tba:SHOWAUTHORITYCONTAINING\" xsi:type=\"nex:LiteralMeta\"/> <meta id=\"dict1\" property=\"cdao:has_tag\" content=\"true\" xsi:type=\"nex:LiteralMeta\" datatype=\"xsd:boolean\"/> <meta href=\"http://purl.org/phylo/treebase/phylows/study/TB2:As1269\" id=\"meta1841\" rel=\"tb:output.analysisstep\" xsi:type=\"nex:ResourceMeta\"/> </node> <edge id=\"edge3\" source=\"node2\" target=\"node3\"/> </tree> </trees> </nex:nexml>";
	
	@Before
	public void setUp() 
	{
		
	}

	@Test
	public void testConvertDataModelsNewick() throws ParserException
	{
		String name = "node";
		double branchLength = 42.0;
		org.iplantc.phyloparser.model.Node parserNode = new org.iplantc.phyloparser.model.Node(name, branchLength);
		
		AnnotatedNode node = ImportTreeUtil.convertDataModels(parserNode);
		assertEquals(branchLength, node.getBranchLength().doubleValue(), 0.0);
		assertEquals(name, node.getLabel());
		
		org.iplantc.phyloparser.model.Tree phyloparserTree = ImportTreeUtil.treeFromNewick(newick, null);
		
		node = ImportTreeUtil.convertDataModels(phyloparserTree.getRoot());
		assertEquals(1.0, node.getBranchLength().doubleValue(), 0.0);
		assertEquals("r", node.getLabel());
		
		node = (AnnotatedNode) node.getChild(1);
		assertEquals(2.0, node.getBranchLength().doubleValue(), 0.0);
		assertEquals("b", node.getLabel());
		
		node = (AnnotatedNode) node.getChild(0);
		assertEquals(1.0, node.getBranchLength().doubleValue(), 0.0);
		assertEquals("ba", node.getLabel());
		assertEquals("&&NHX:someAnnotation", node.getAnnotations("NHX").iterator().next().getValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertDataModelsNexml() throws SAXException, IOException, ParserConfigurationException
	{
		InputStream stream = new ByteArrayInputStream(nexml.getBytes("UTF-8"));
		Document document = DocumentFactory.parse(stream);
		stream.close();
		
		TreeBlock treeBlock = document.getTreeBlockList().get(0);
		Tree<Edge> in = (Tree<Edge>) treeBlock.iterator().next();
		
		RemoteTree out = ImportTreeUtil.convertDataModels(in);
		assertEquals(in.getId(), out.getName());
		
		Node rootIn = in.getRoot();
		RemoteNode nodeOut = out.getRootNode();
		assertEquals(rootIn.getLabel(), nodeOut.getLabel());
		
		Node childIn = in.getOutNodes(rootIn).iterator().next();
		AnnotatedNode childOut = (AnnotatedNode) nodeOut.getChild(0);
		assertEquals(childIn.getLabel(), childOut.getLabel());
		assertEquals(childIn.getAllAnnotations().size(), childOut.getAnnotations().size());
//		Edge edge = in.getEdge(rootIn, childIn);
//		assertEquals(edge.getLength().doubleValue(), nodeOut.getBranchLength().doubleValue(), 0.0); //FIXME get rid of RemoteNode default branch length = 1.0?

		Set<Annotation> annotations = childOut.getAnnotations("tbe:AUTHORITY");
		assertEquals(1, annotations.size());
		Annotation annotation = annotations.iterator().next();
		assertEquals("Leconte", annotation.getValue());
		
		annotations = childOut.getAnnotations("tba:ANCESTORWITHPAGE");
		assertEquals(1, annotations.size());
		annotation = annotations.iterator().next();
		assertEquals(BigInteger.valueOf(117851), annotation.getValue());
		
		annotations = childOut.getAnnotations("cdao:has_tag");
		assertEquals(1, annotations.size());
		annotation = annotations.iterator().next();
		assertEquals(true, annotation.getValue());
		
		annotations = childOut.getAnnotations("tb:output.analysisstep");
		assertEquals(1, annotations.size());
		annotation = annotations.iterator().next();
		assertEquals("http://purl.org/phylo/treebase/phylows/study/TB2:As1269", annotation.getValue().toString());
	}

}

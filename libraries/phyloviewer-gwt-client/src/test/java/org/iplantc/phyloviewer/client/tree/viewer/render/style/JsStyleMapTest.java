package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import static org.junit.Assert.*;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Node;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.junit.Test;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.junit.client.GWTTestCase;

public class JsStyleMapTest extends GWTTestCase
{
	//test structures from example at https://pods.iplantcollaborative.org/wiki/display/iptol/Using+Phyloviewer+GWT+client+library
	String testStyleMap = "{\"styles\":{\"style-one\":{\"id\":\"style-one\",\"nodeStyle\":{\"color\":\"#ff0000\",\"pointSize\":3,\"nodeShape\":\"circle\"},\"labelStyle\":{\"color\":\"#000000\"},\"branchStyle\":{\"strokeColor\":\"#000000\",\"lineWidth\":1},\"glyphStyle\":{\"fillColor\":\"#000000\",\"strokeColor\":\"#000000\",\"lineWidth\":1}},\"style-two\":{\"id\":\"style-two\",\"nodeStyle\":{\"color\":\"#0000ff\",\"pointSize\":10,\"nodeShape\":\"square\"},\"labelStyle\":{\"color\":\"#000000\"},\"branchStyle\":{\"strokeColor\":\"#000000\",\"lineWidth\":1},\"glyphStyle\":{\"fillColor\":\"#000000\",\"strokeColor\":\"#000000\",\"lineWidth\":1}}},\"nodeStyleMappings\":{\"1\":\"style-one\",\"2\":\"style-two\",\"5\":\"style-one\"},\"nameStyleMappings\":{\"a\":\"style-two\",\"ab\":\"style-two\"},\"branchDecorations\":{\"1\":\"triangle\",\"3\":\"triangle\"}}";
	INode testNode1 = new Node(1, "a");
	INode testNode2 = new Node(2, "");
	INode testNode3 = new Node(3, "ab");
	
	@Test
	public void testGet()
	{
		JSONObject object = (JSONObject)JSONParser.parseStrict(testStyleMap);
		JsStyleMap map = (JsStyleMap)object.getJavaScriptObject();
		
		IStyle style = map.get(testNode1);
		assertEquals("style-one", style.getId()); //the mapping of id 1 overrides the mapping of name "a"
		
		style = map.get(testNode2);
		assertEquals("style-two", style.getId());
		
		style = map.get(testNode3);
		assertEquals("style-two", style.getId()); //node 3 only has a name mapping
	}

	@Test
	public void testHasBranchDecoration()
	{
		JSONObject object = (JSONObject)JSONParser.parseStrict(testStyleMap);
		JsStyleMap map = (JsStyleMap)object.getJavaScriptObject();
		
		assertTrue(map.hasBranchDecoration(testNode1.getId()));
		assertFalse(map.hasBranchDecoration(testNode2.getId()));
	}

	@Override
	public String getModuleName()
	{
		return "org.iplantc.phyloviewer.client";
	}

}

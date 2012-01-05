package org.iplantc.phyloviewer.shared.render.style;

import static org.junit.Assert.*;

import org.iplantc.phyloviewer.shared.render.style.INodeStyle.Shape;
import org.junit.Test;

public class StyleTest
{

	@Test
	public void testToString()
	{
		Style style = new Style("foo", 
				new NodeStyle("#ff0000", 3.0, Shape.CIRCLE), 
				new LabelStyle("#000000"), 
				new GlyphStyle("#000000", "#000000", 1.0), 
				new BranchStyle("#000000", 1.0));
		
		String nodeStyleJSON = "{" +
	            "\"color\":\"#ff0000\"," + 
	            "\"pointSize\":3.0," + 
	            "\"nodeShape\":\"circle\"" + 
	        	"}";
		
		String labelStyleJSON = "{\"color\":\"#000000\"}";
		
		String branchStyleJSON = "{" + 
		        "\"strokeColor\":\"#000000\"," +
		        "\"lineWidth\":1.0" + 
		    	"}";
		
		String glyphStyleJSON = "{"+
	            "\"fillColor\":\"#000000\","+
	            "\"strokeColor\":\"#000000\","+
	            "\"lineWidth\":1.0"+
	        	"}";
		
		String expectedJSON = "{" +
				"\"id\":\"foo\","+
				"\"nodeStyle\":" + nodeStyleJSON + "," + 
				"\"labelStyle\":" + labelStyleJSON + "," + 
				"\"branchStyle\":" + branchStyleJSON + "," +
				"\"glyphStyle\":" + glyphStyleJSON + 
	        	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
	}

}

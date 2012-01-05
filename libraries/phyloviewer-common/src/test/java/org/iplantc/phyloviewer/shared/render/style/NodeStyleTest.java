package org.iplantc.phyloviewer.shared.render.style;

import static org.junit.Assert.assertEquals;

import org.iplantc.phyloviewer.shared.render.style.INodeStyle.Shape;
import org.junit.Test;

public class NodeStyleTest
{
	
	@Test
	public void testToString()
	{
		NodeStyle style = new NodeStyle("#ff0000", 3.0, Shape.CIRCLE);
		String expectedJSON = "{" +
	            "\"color\":\"#ff0000\"," + 
	            "\"pointSize\":3.0," + 
	            "\"nodeShape\":\"circle\"" + 
	        	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		//remove elements from the beginning
		style.setColor(null);
		expectedJSON = "{" +
	            "\"pointSize\":3.0," + 
	            "\"nodeShape\":\"circle\"" + 
	        	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		style.setPointSize(Double.NaN);
		expectedJSON = "{" +
	            "\"nodeShape\":\"circle\"" + 
	        	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		//remove elements from the end
		style = new NodeStyle("#ff0000", 3.0, null);
		expectedJSON = "{" +
	            "\"color\":\"#ff0000\"," + 
	            "\"pointSize\":3.0" + 
	        	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		style.setPointSize(Double.NaN);
		expectedJSON = "{" +
	            "\"color\":\"#ff0000\"" + 
	        	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		style = new NodeStyle();
		expectedJSON = "{}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
	}

}

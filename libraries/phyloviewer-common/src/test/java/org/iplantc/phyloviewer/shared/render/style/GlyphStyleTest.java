package org.iplantc.phyloviewer.shared.render.style;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GlyphStyleTest
{
	
	@Test
	public void testToString()
	{
		IGlyphStyle style = new GlyphStyle("#000000", "#000000", 1.0);
		String expectedJSON = "{"+
            "\"fillColor\":\"#000000\","+
            "\"strokeColor\":\"#000000\","+
            "\"lineWidth\":1.0"+
        	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		style = new GlyphStyle(null, "#000000", 1.0);
		expectedJSON = "{"+
            "\"strokeColor\":\"#000000\","+
            "\"lineWidth\":1.0"+
        	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		style = new GlyphStyle(null, null, 1.0);
		expectedJSON = "{"+
            "\"lineWidth\":1.0"+
        	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		style = new GlyphStyle();
		expectedJSON = "{}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		style = new GlyphStyle("#000000", "#000000", Double.NaN);
		expectedJSON = "{"+
            "\"fillColor\":\"#000000\","+
            "\"strokeColor\":\"#000000\""+
        	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		style = new GlyphStyle("#000000", null, Double.NaN);
		expectedJSON = "{"+
            "\"fillColor\":\"#000000\""+
        	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
	}

}

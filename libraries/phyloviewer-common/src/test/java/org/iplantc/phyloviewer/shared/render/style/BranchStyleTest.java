package org.iplantc.phyloviewer.shared.render.style;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BranchStyleTest
{

	@Test
	public void testToString()
	{
		IBranchStyle style = new BranchStyle("#000000", 1.0);
		String expectedJSON = "{" + 
		        "\"strokeColor\":\"#000000\"," +
		        "\"lineWidth\":1.0" + 
		    	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		style = new BranchStyle(null, 1.0);
		expectedJSON = "{" + 
		        "\"lineWidth\":1.0" + 
		    	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		style = new BranchStyle("#000000", Double.NaN);
		expectedJSON = "{" + 
		        "\"strokeColor\":\"#000000\"" +
		    	"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		style = new BranchStyle();
		expectedJSON = "{}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
	}

}

package org.iplantc.phyloviewer.shared.render.style;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LabelStyleTest
{

	@Test
	public void testToString()
	{
		ILabelStyle style = new LabelStyle("#000000");
		String expectedJSON = "{\"color\":\"#000000\"}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
		
		style.setColor(null);
		expectedJSON = "{}";
		assertEquals(expectedJSON.toLowerCase(), style.toString().toLowerCase());
	}

}

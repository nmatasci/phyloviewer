package org.iplantc.phyloviewer.viewer.client.style;

import org.iplantc.phyloviewer.client.tree.viewer.render.style.JsStyleMap;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * StyleMapFactory takes a string and tries different parsers until it gets one that can create a StyleMap from the given string
 */
public class StyleMapFactory
{
	/**
	 * Parses a style string and makes an IStyleMap
	 * @param style
	 * @return an IStyleMap for the given style.
	 * @throws StyleParseException if no appropriate parser is found
	 */
	public static IStyleMap createStyleMap(String style) throws StyleParseException {
		if (style == null || style.length() == 0) {
			throw new StyleParseException();
		}
		
		try
		{
			JSONObject object = (JSONObject)JSONParser.parseStrict(style);
			return (JsStyleMap)object.getJavaScriptObject();
		}
		catch(Exception e)
		{
			//drop, try another parser
		}
		
		try {
			return new StyleByCSV(style);
		}
		catch(Exception e)
		{
			//drop, try another parser?
		}
		
		throw new StyleParseException();
	}
	
	public static class StyleParseException extends Exception 
	{
		private static final long serialVersionUID = -5297069106778668867L;
	}
}

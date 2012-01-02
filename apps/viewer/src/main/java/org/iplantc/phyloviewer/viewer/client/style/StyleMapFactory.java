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
	 * @param json
	 * @return an IStyleMap for the given style.
	 * @throws StyleParseException if no appropriate parser is found
	 */
	public static IStyleMap parseJSON(String json) throws StyleParseException {
		try
		{
			JSONObject object = (JSONObject)JSONParser.parseStrict(json);
			return (JsStyleMap)object.getJavaScriptObject();
		}
		catch(Exception e)
		{
			throw new StyleParseException(e);
		}
	}
	
	public static class StyleParseException extends Exception 
	{
		private static final long serialVersionUID = -5297069106778668867L;

		public StyleParseException(Throwable t)
		{
			super(t);
		}
		
	}
}

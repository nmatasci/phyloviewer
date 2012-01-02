package org.iplantc.phyloviewer.viewer.client.services;

import org.iplantc.phyloviewer.client.tree.viewer.render.style.JsStyleMap;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StyleServiceClient
{	
	static final String BASE = "/style/";
	
	public static void getStyle(final String styleID, final AsyncCallback<IStyleMap> callback) {
		String url = URL.encode(BASE + styleID);
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
		
		RequestCallback rc = new RequestCallback()
		{
			@Override
			public void onResponseReceived(Request request, Response response)
			{
				if(response.getStatusCode() == 200)
				{
					String styleString = response.getText();
					IStyleMap style;
					try
					{
						style = parseJSON(styleString);
						callback.onSuccess(style);
					}
					catch(StyleServiceException e)
					{
						onError(request, e);
					}
					
				}
				else
				{
					onError(request, new StyleServiceException(response.getStatusText()));
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception)
			{
				callback.onFailure(exception);
			}
		};
		
		try
		{
			rb.sendRequest(null, rc);
		}
		catch(RequestException e)
		{
			callback.onFailure(e);
		}
	}
	
	/**
	 * Parses a style string and makes an IStyleMap
	 * @param json
	 * @return an IStyleMap for the given style.
	 * @throws StyleServiceException if no appropriate parser is found
	 */
	public static IStyleMap parseJSON(String json) throws StyleServiceException {
		try
		{
			JSONObject object = (JSONObject)JSONParser.parseStrict(json);
			return (JsStyleMap)object.getJavaScriptObject();
		}
		catch(Exception e)
		{
			throw new StyleServiceException(e);
		}
	}
	
	public static class StyleServiceException extends Exception 
	{
		private static final long serialVersionUID = -5297069106778668867L;

		public StyleServiceException(Throwable t)
		{
			super(t);
		}
		
		public StyleServiceException(String msg)
		{
			super(msg);
		}
	}
}

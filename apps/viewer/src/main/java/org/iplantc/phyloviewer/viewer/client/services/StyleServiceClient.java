package org.iplantc.phyloviewer.viewer.client.services;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StyleServiceClient
{	
	static final String BASE = "/style/";
	
	public static void getStyle(final String styleID, final AsyncCallback<String> callback) {
		String url = URL.encode(BASE + styleID);
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
		
		RequestCallback rc = new RequestCallback()
		{
			@Override
			public void onResponseReceived(Request request, Response response)
			{
				if(response.getStatusCode() == 200)
				{
					callback.onSuccess(response.getText());
				}
				else
				{
					callback.onFailure(new Exception(response.getStatusText()));
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
}

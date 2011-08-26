package org.iplantc.phyloviewer.viewer.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * Copies GET requests to the style couchdb
 */
public class StyleProxy extends HttpServlet
{
	private static final long serialVersionUID = 4733086954724400951L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String baseURL = getServletContext().getInitParameter("style.db");
		String styleID = request.getPathInfo();

		URLConnection connection = new URL(baseURL + styleID).openConnection();
		
		OutputStream out = response.getOutputStream();
		InputStream in = connection.getInputStream();
		
		IOUtils.copy(in, out);
		
		in.close();
		out.close();
	}
}

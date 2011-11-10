/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.viewer.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloviewer.viewer.server.persistence.Constants;
import org.xml.sax.SAXException;

public class ParseTreeService extends HttpServlet {
	private static final long serialVersionUID = -2532260393364629170L;
	private ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
	private ParseTree parseTree;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Received tree post request");
		
		PrintWriter writer = getWriter(response);
		
		Map<String, String[]> parameters = null;
		try
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Reading request parameters");
			parameters = getParameters(request);
		}
		catch(FileUploadException e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			writer.println(e.getMessage());
			writer.flush();
			writer.close();
			return;
		}

		try
		{
			List<String> ids = parseTree.saveTrees(parameters);
			if (ids.size() > 0) {
				Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Returning response");
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				for (String id : ids) {
					String viewURL = getViewURL(id, request);
					response.setHeader("Location", viewURL);
					writer.println(viewURL);
				}
			} 
			else 
			{
				Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "No trees found. Returning response");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writer.println("Bad request: No tree data found.");
			}
		}
		catch(ParserException e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			writer.println(e.getMessage());
			e.printStackTrace(writer);
		}
		catch(SAXException e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			writer.println(e.getMessage());
			e.printStackTrace(writer);
		}
		catch(Exception e)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writer.println(e.getMessage());
			e.printStackTrace(writer);
		}
		
		writer.flush();
		writer.close();
	}
	
	@Override
	public void init() throws ServletException
	{
		ServletContext servletContext = this.getServletContext();
		
		IImportTreeData i = (IImportTreeData) servletContext.getAttribute(Constants.IMPORT_TREE_DATA_KEY);
		
		String path = servletContext.getInitParameter("treefile.path");
		path = servletContext.getRealPath(path);
		
		parseTree = new ParseTree(i);
		parseTree.setTreeBackupDir(path);
		
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.INFO, "Setting parseTree file backup path to " + parseTree.getTreeBackupDir().getAbsolutePath());
	}

	private PrintWriter getWriter(HttpServletResponse response)
	{
		PrintWriter writer = null;
		
		try
		{
			writer = response.getWriter();
		}
		catch(IOException e1)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Unable to write response");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		return writer;
	}
	
	private Map<String, String[]> getMultipartParameters(HttpServletRequest request) throws FileUploadException
	{
		HashMap<String, List<String>> parameters = new HashMap<String, List<String>>();

		List<?> items = null;
		try
		{
			items = upload.parseRequest(request);
		}
		catch(FileUploadException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Unable to parse request", e);
			throw e;
		}
		
		for (Object obj : items)
		{
			if (obj instanceof FileItem)
			{
				FileItem item = (FileItem) obj;
				
				String name = item.getFieldName();
				if (!parameters.containsKey(name))
				{
					parameters.put(name, new ArrayList<String>());
				}
				parameters.get(name).add(item.getString());
			}
		}
		
		//convert to match return type of HttpServletRequest.getParameterMap()
		Map<String, String[]> copy = new HashMap<String, String[]>();
		for (String key: parameters.keySet())
		{
			List<String> value = parameters.get(key);
			copy.put(key, value.toArray(new String[value.size()]));
		}
		
		return copy;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String[]> getParameters(HttpServletRequest request) throws FileUploadException {
		if (ServletFileUpload.isMultipartContent(request)) 
		{
			return getMultipartParameters(request);
		}
		else 
		{
			return request.getParameterMap();
		}
	}
	
	private String getViewURL(String id, HttpServletRequest request)
	{
		String viewURL =  request.getScheme() + "://" + request.getServerName();
		
		if (request.getServerPort() != 80)
		{
			viewURL += ":" + request.getServerPort();
		}
		
		if (request.getContextPath().length() > 0) {
			viewURL += request.getContextPath();
		}
		
		viewURL += "/view/tree/" + id;
		
		return viewURL;
	}
}

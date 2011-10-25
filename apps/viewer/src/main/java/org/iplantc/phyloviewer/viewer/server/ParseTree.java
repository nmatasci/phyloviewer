/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.viewer.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.server.persistence.Constants;

public class ParseTree extends HttpServlet {
	private static final long serialVersionUID = -2532260393364629170L;
	private ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Received tree post request");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		String newick = request.getParameter("newickData");
		String nexml = request.getParameter("nexml");
		String name = request.getParameter("name");
		
		PrintWriter writer = null;
		try
		{
			writer = response.getWriter();
		}
		catch(IOException e1)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Unable to write response");
		}

		if (isMultipart) 
		{
			try
			{
				Map<String, FileItem> parameters = getParameters(request);
				
				if (parameters.containsKey("name"))
				{
					name = parameters.get("name").getString();
				}
				
				if (parameters.containsKey("newickData"))
				{
					newick = parameters.get("newickData").getString();
				}
				
				if (parameters.containsKey("nexml"))
				{
					nexml = parameters.get("nexml").getString();
				}
			}
			catch(FileUploadException e)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} 
		
		if (newick != null) 
		{
			try
			{
				int id = loadNewickString(newick, name);

				Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Returning response");
				String viewURL = getViewURL(id, request);
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				response.setHeader("Location", viewURL);
				
				writer.print(viewURL);
			}
			catch (ParserException e) 
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
		}
		else if (nexml != null) 
		{
			try
			{
				int[] ids = loadNexml(nexml);
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				for (int id : ids)
				{
					String viewURL = getViewURL(id, request);
					response.setHeader("Location", viewURL);	
					writer.println(viewURL);
				}
			}
			catch(Exception e)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writer.println(e.getMessage());
				e.printStackTrace(writer);
			}
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			writer.println("Bad request: No tree data found.");
		}
		
		writer.flush();
	}

	private int loadNewickString(String newick, String name ) throws Exception {
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Importing newick string");
		IImportTreeData importer = (IImportTreeData) this.getServletContext().getAttribute(Constants.IMPORT_TREE_DATA_KEY);
		
		if(importer != null) {
			int id = importer.importFromNewick(newick, name).getId();
			return id;
		}
		
		return -1;
	}
	
	private Map<String, FileItem> getParameters(HttpServletRequest request) throws FileUploadException
	{
		HashMap<String, FileItem> parameters = new HashMap<String, FileItem>();

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
				parameters.put(item.getFieldName(), item);
			}
		}
		
		return parameters;
	}
	
	private String getViewURL(int id, HttpServletRequest request)
	{
		String viewURL =  request.getScheme() + "://" + request.getServerName();
		
		if (request.getServerPort() != 80)
		{
			viewURL += ":" + request.getServerPort();
		}
		
//		if (request.getContextPath().length() > 0) {
//			viewURL += request.getContextPath();
//		}
		
		viewURL += "/view/treeId/" + id;
		
		return viewURL;
	}
	
	private int[] loadNexml(String nexml) throws Exception
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Importing nexml");
		IImportTreeData importer = (IImportTreeData) this.getServletContext().getAttribute(Constants.IMPORT_TREE_DATA_KEY);
		
		int[] ids = null;
		if(importer != null) {
			List<RemoteTree> trees = importer.importFromNexml(nexml);
			ids = new int[trees.size()]; 
			for (int i = 0; i < trees.size(); i++) {
				ids[i] = trees.get(i).getId();
			}
		}
		
		return ids;
	}

}

/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.viewer.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

public class ParseTree extends HttpServlet {
	private static final long serialVersionUID = -2532260393364629170L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Received tree post request");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		String newick = null;
		String name = request.getParameter("name");

		if (isMultipart) 
		{
			try
			{
				newick = getNewickFilePost(request);
			}
			catch(Exception e)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} 
		else 
		{
			newick = request.getParameter("newickData");
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
			}
			catch(Exception e)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	private int loadNewickString(String newick, String name ) throws Exception {
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Importing newick string");
		IImportTreeData importer = (IImportTreeData) this.getServletContext().getAttribute(Constants.IMPORT_TREE_DATA_KEY);
		
		if(importer != null) {
			int id = importer.importFromNewick(newick, name);
			return id;
		}
		
		return -1;
	}
	
	private String getNewickFilePost(HttpServletRequest request) throws FileUploadException, IOException
	{
		String newick = null;
		

		ServletFileUpload upload = new ServletFileUpload();
		
		FileItemIterator iter = upload.getItemIterator(request);
		while (iter.hasNext()) {
		    FileItemStream item = iter.next();
		    if (item.getFieldName().equals("newickData")) {
		    	newick = Streams.asString(item.openStream());
		    }
		}
		
		return newick;
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

}

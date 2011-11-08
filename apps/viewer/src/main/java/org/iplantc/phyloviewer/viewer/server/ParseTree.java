/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.viewer.server;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.server.persistence.Constants;
import org.xml.sax.SAXException;

public class ParseTree extends HttpServlet {
	private static final long serialVersionUID = -2532260393364629170L;
	private ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
	private String treeBackupPath = "./";

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Received tree post request");
		
		PrintWriter writer = getWriter(response);
		
		Map<String, String[]> parameters = null;
		try
		{
			parameters = getParameters(request);
		}
		catch(FileUploadException e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			writer.println(e.getMessage());
			return;
		}

		try
		{
			List<String> ids = loadTrees(parameters);
			if (ids.size() > 0) {
				Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Returning response");
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				for (String id : ids) {
					String viewURL = getViewURL(id, request);
					response.setHeader("Location", viewURL);
					writer.println(viewURL);
				}
				
				saveToFile(parameters);
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
	}
	
	@Override
	public void init() throws ServletException
	{
		ServletContext servletContext = this.getServletContext();
		String path = servletContext.getInitParameter("treefile.path");
		path = servletContext.getRealPath(path);
		this.setTreeBackupPath(path);
	}

	public String getTreeBackupPath()
	{
		return treeBackupPath;
	}

	public void setTreeBackupPath(String treeBackupPath)
	{
		this.treeBackupPath = treeBackupPath;
		new File(treeBackupPath).mkdir();
	}

	private void saveToFile(Map<String,String[]> parameters)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			ByteArrayOutputStream sink = new ByteArrayOutputStream();
			DigestOutputStream dos = new DigestOutputStream(sink, digest);
			ObjectOutputStream out = new ObjectOutputStream(dos);
			
			out.writeObject(parameters);
			out.flush();
			out.close();
			
			byte[] data = sink.toByteArray();
			byte[] hash = dos.getMessageDigest().digest();
			saveToFile(data, hash);
		}
		catch(NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveToFile(byte[] data, byte[] hash)
	{
		String fileName = Hex.encodeHexString(hash);
		
		try
		{
			File file = new File(getTreeBackupPath() + fileName);
			
			if (file.createNewFile()) {
				OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
				out.write(data);
				out.flush();
				out.close();
			}
		}
		catch(IOException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Unable to save backup of newick string to file system", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String[]> loadFromFile(File file)
	{
		Map<String,String[]> parameters = null;
		
		try
		{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			
			parameters = (Map<String,String[]>)in.readObject();
		}
		catch(FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parameters;
	}

	private List<String> loadTrees(Map<String, String[]> parameters) throws ParserException, SAXException, Exception {
		List<String> ids = new ArrayList<String>();
		
		if (parameters.containsKey("newickData")) 
		{
			String[] newicks = parameters.get("newickData");
			String[] names = parameters.get("name");
			for (int i = 0; i < newicks.length; i++)
			{
				String newick = newicks[i];
				String name = names[i];
				
				ids.add(loadNewickString(newick, name));
			}
		}
		
		if (parameters.containsKey("nexml")) 
		{
			for (String nexml : parameters.get("nexml"))
			{
				List<String> newIds = loadNexml(nexml);
				ids.addAll(newIds);
			}
		}
		
		return ids;
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

	private String loadNewickString(String newick, String name ) throws Exception {
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Importing newick string");
		IImportTreeData importer = (IImportTreeData) this.getServletContext().getAttribute(Constants.IMPORT_TREE_DATA_KEY);
		
		String id = null;
		
		if(importer != null) {
			RemoteTree tree = importer.importFromNewick(newick, name);
			id = Hex.encodeHexString(tree.getHash());
		}
		
		return id;
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
		
//		if (request.getContextPath().length() > 0) {
//			viewURL += request.getContextPath();
//		}
		
		viewURL += "/view/tree/" + id;
		
		return viewURL;
	}
	
	private List<String> loadNexml(String nexml) throws Exception
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Importing nexml");
		IImportTreeData importer = (IImportTreeData) this.getServletContext().getAttribute(Constants.IMPORT_TREE_DATA_KEY);
		
		List<String> list = new ArrayList<String>();
		if(importer != null) {
			List<RemoteTree> trees = importer.importFromNexml(nexml);
			for (RemoteTree tree : trees) {
				list.add(Hex.encodeHexString(tree.getHash()));
			}
		}
		
		return list;
	}

}

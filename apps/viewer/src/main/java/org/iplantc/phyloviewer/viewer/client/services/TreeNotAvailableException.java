package org.iplantc.phyloviewer.viewer.client.services;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TreeNotAvailableException extends TreeDataException implements IsSerializable
{
	private static final long serialVersionUID = -2930713523317228373L;
	private int treeId;
	
	public TreeNotAvailableException(int id) 
	{
		this(id, "Tree id " + id + " is not available.");
	}
	
	public TreeNotAvailableException(int id, String message) 
	{
		super(message);
		this.treeId = id; 
	}
	
	/** no-arg constructor for Serializable */
	protected TreeNotAvailableException() 
	{
	}

	public int getTreeId()
	{
		return treeId;
	}
}

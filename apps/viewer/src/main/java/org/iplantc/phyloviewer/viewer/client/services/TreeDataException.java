package org.iplantc.phyloviewer.viewer.client.services;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TreeDataException extends Exception implements IsSerializable
{
	public TreeDataException()
	{
		super();
	}

	public TreeDataException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TreeDataException(Throwable cause)
	{
		super(cause);
	}

	public TreeDataException(String message) 
	{
		super(message);
	}
	
	private static final long serialVersionUID = -2641719033996604722L;
}

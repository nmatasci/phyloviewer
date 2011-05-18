package org.iplantc.phyloviewer.viewer.client.services;

public class TreeImportInProgressException extends TreeNotAvailableException
{
	private static final long serialVersionUID = -7737774228664955001L;

	public TreeImportInProgressException(int id)
	{
		super(id, "Import still in progress for this tree (id: " + id + "). Please try again in a few minutes.");
	}
	
	protected TreeImportInProgressException()
	{
	}
}

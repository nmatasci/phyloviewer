package org.iplantc.phyloviewer.viewer.server;

/**
 * Indicates a problem that prevents a tree from being imported.
 */
public class ImportException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ImportException()
	{
		super();
	}

	public ImportException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ImportException(String message)
	{
		super(message);
	}

	public ImportException(Throwable cause)
	{
		super(cause);
	}
	
}

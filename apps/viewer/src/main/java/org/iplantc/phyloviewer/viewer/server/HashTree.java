package org.iplantc.phyloviewer.viewer.server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.shared.model.ITree;

public class HashTree
{
	private final MessageDigest md;
	public final String algorithm = "MD5";
	public final String encoding = "UTF-8";
	
	public HashTree() throws UnsupportedOperationException 
	{
		try
		{
			md = MessageDigest.getInstance(algorithm);
		}
		catch(NoSuchAlgorithmException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "NoSuchAlgorithmException for " + algorithm, e);
			throw new UnsupportedOperationException("NoSuchAlgorithmException for " + algorithm, e);
		}
	}
	
	public byte[] hash(String tree) throws UnsupportedOperationException
	{
		byte[] bytes;
		try
		{
			bytes = tree.getBytes(encoding);
			return md.digest(bytes);
		}
		catch(UnsupportedEncodingException e)
		{
			String msg = "UnsupportedEncodingException for " + encoding;
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, msg, e);
			throw new UnsupportedOperationException(msg, e);
		}
	}
	
	public byte[] hash(ITree tree) throws UnsupportedOperationException
	{
		//TODO
		throw new UnsupportedOperationException("Not yet implemented: HashTree.hash(ITree tree)");
	}
}

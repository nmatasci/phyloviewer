package org.iplantc.iptol.client.services;

import java.io.Serializable;


public class BaseServiceCallWrapper implements Serializable
{
	private static final long serialVersionUID = -7453647589756124397L;
	
	private Type type = Type.GET;
	private String address = new String();
	

	public enum Type
	{
		GET,
		PUT,
		POST,	
		DELETE
	}
	
	public BaseServiceCallWrapper()
	{
	}
	
	public BaseServiceCallWrapper(String address)
	{
		this.address = address; 
	}

	public BaseServiceCallWrapper(Type type,String address)
	{
		this(address);
		this.type = type; 
	}
	
	public Type getType()
	{
		return type;
	}
	
	public String getAddress()
	{
		return address;		
	}
}
package org.iplantc.phyloviewer.shared.model.metadata;


public class MetadataPropertyImpl implements MetadataProperty
{
	private String name;
	private Class<?> datatype;
	
	public MetadataPropertyImpl(String name, Class<?> datatype)
	{
		this.name = name;
		this.datatype = datatype;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public Class<?> getDatatype()
	{
		return datatype;
	}

}

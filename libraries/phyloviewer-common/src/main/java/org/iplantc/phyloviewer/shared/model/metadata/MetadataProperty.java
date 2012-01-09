package org.iplantc.phyloviewer.shared.model.metadata;

public interface MetadataProperty
{
	public String getName();
	
	/** can't use isAssignableFrom in GWT client code, so this should return Class<Number> for anything that should be treated as a number. */
	public Class<?> getDatatype();
}

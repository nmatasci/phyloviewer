package org.iplantc.phyloviewer.shared.model.metadata;

public interface ValueFilter<T> extends ValueMap<T, Boolean>
{
	public String getDescription();
}

package org.iplantc.phyloviewer.viewer.client.model;

public interface Annotation
{

	public abstract String getKey();

	public abstract String getPredicateNamespace();

	public abstract Object getValue();
}
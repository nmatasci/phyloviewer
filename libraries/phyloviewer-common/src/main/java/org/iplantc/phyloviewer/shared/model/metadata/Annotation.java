package org.iplantc.phyloviewer.shared.model.metadata;

public interface Annotation
{

	public abstract String getKey();

	public abstract String getPredicateNamespace();

	public abstract Object getValue();
}
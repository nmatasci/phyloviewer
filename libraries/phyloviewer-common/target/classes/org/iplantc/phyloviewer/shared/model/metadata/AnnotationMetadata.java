package org.iplantc.phyloviewer.shared.model.metadata;

/**
 * Metadata about all annotations of some type on a tree.
 */
public interface AnnotationMetadata
{
	public String getName();
	
	/** can't use isAssignableFrom in GWT client code, so this should return Class<Number> for anything that should be treated as a number. */
	public Class<?> getDatatype();
}

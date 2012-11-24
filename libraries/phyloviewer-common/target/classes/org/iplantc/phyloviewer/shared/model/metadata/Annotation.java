package org.iplantc.phyloviewer.shared.model.metadata;

/**
 * An interface for node and tree annotations. Based on org.nexml.model.Annotation.
 */
public interface Annotation
{
	/**
	 * @return the key for this annotation. May be a namespaced predicate, e.g. dc:name, or a CURIE
	 *         predicate when the value is a URI, or some other string used to group or describe
	 *         annotations.
	 */
	public abstract String getKey();

	/**
	 * Gets the namespace URI for the predicate (either the "property" 
	 * or "rel" attribute in the case of annotations with URLs or nested
	 * annotations).
	 */
	public abstract String getPredicateNamespace();

	/**
	 * @return the value of the annotation.
	 */
	public abstract Object getValue();
}
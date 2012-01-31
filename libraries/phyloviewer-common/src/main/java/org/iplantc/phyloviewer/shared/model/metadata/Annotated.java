package org.iplantc.phyloviewer.shared.model.metadata;

import java.util.Set;

/**
 * Methods for objects that have annotations
 * @see Annotation
 */
public interface Annotated
{
	/**
	 * @return all this object's annotations
	 */
	public Set<Annotation> getAnnotations();

	/**
	 * Set the annotations to the given collection.
	 */
	public void setAnnotations(Set<Annotation> annotations);
	
    /**
     * Get annotations having the given property or rel value.
     * @see org.nexml.model.Annotatable#getAnnotations(java.lang.String)
     */
    public Set<Annotation> getAnnotations(String propertyOrRel);
    
    /**
     * Adds an annotation
     */
    public void addAnnotation(Annotation annotation);
    
}

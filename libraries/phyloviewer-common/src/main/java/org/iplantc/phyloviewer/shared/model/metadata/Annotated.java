package org.iplantc.phyloviewer.shared.model.metadata;

import java.util.Set;

/**
 * Methods for objects that have annotations
 */
public interface Annotated
{
	public Set<Annotation> getAnnotations();

	public void setAnnotations(Set<Annotation> annotations);
	
    /**
     * @see org.nexml.model.Annotatable#getAnnotations(java.lang.String)
     */
    public Set<Annotation> getAnnotations(String propertyOrRel);
    
    public void addAnnotation(Annotation annotation);
    
}

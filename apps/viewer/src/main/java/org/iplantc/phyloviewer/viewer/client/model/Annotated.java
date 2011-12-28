package org.iplantc.phyloviewer.viewer.client.model;

import java.util.Set;

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

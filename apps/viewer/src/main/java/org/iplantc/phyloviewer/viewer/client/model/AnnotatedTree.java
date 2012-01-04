package org.iplantc.phyloviewer.viewer.client.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.iplantc.phyloviewer.shared.model.metadata.Annotated;
import org.iplantc.phyloviewer.shared.model.metadata.Annotation;

@SuppressWarnings("serial")
@Entity
public class AnnotatedTree extends RemoteTree implements Annotated
{
	private Set<Annotation> annotations;
	
	public AnnotatedTree()
	{
		
	}
	
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH}, targetEntity=AnnotationEntity.class)
	public Set<Annotation> getAnnotations()
	{
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations)
	{
		this.annotations = annotations;
	}

    /**
     * @see org.nexml.model.Annotatable#getAnnotations(java.lang.String)
     */
    public Set<Annotation> getAnnotations(String propertyOrRel) {
    	return AnnotationEntity.getAnnotations(propertyOrRel, this.annotations);
    }
    
    public void addAnnotation(Annotation annotation) {
    	if (this.annotations == null) {
    		this.annotations = new HashSet<Annotation>();
    	}

    	this.annotations.add(annotation);
    }
}

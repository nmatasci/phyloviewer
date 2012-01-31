package org.iplantc.phyloviewer.viewer.client.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.iplantc.phyloviewer.shared.model.metadata.Annotated;
import org.iplantc.phyloviewer.shared.model.metadata.Annotation;

/**
 * A RemoteTree with Annotations
 * @see Annotation
 */
@SuppressWarnings("serial")
@Entity
public class AnnotatedTree extends RemoteTree implements Annotated
{
	private Set<Annotation> annotations;
	
	public AnnotatedTree()
	{
		
	}
	
	@Override
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH}, targetEntity=AnnotationEntity.class)
	public Set<Annotation> getAnnotations()
	{
		return annotations;
	}

	@Override
	public void setAnnotations(Set<Annotation> annotations)
	{
		this.annotations = annotations;
	}

	@Override
    public Set<Annotation> getAnnotations(String propertyOrRel) {
    	return AnnotationEntity.getAnnotations(propertyOrRel, this.annotations);
    }
    
    @Override
    public void addAnnotation(Annotation annotation) {
    	if (this.annotations == null) {
    		this.annotations = new HashSet<Annotation>();
    	}

    	this.annotations.add(annotation);
    }
}

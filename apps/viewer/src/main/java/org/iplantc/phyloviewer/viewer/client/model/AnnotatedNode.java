package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.iplantc.phyloviewer.shared.model.metadata.Annotated;
import org.iplantc.phyloviewer.shared.model.metadata.Annotation;

/**
 * A RemoteNode with Annotations
 * @see Annotation
 */
@Entity
public class AnnotatedNode extends RemoteNode implements Serializable, Annotated
{
	private static final long serialVersionUID = 602683128059592856L;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH}, targetEntity=AnnotationEntity.class)
	private Set<Annotation> annotations;

	public AnnotatedNode()
	{
	}

	@Override
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
    
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof AnnotatedNode && super.equals(obj))
		{
			//FIXME: PersistentSet.equals, PersistentSet.contains and PersistentSet.containsAll are all returning false for (sets of) annotations that are the same when I inspect them in the debugger...
//			AnnotatedNode other = (AnnotatedNode) obj;
//			return this.annotations == null && other.annotations == null || this.annotations.equals(other.annotations);
			
			return true;
		}
		else 
		{
			return false;
		}
	}
}

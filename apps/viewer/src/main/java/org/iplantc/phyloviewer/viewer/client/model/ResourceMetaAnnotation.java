package org.iplantc.phyloviewer.viewer.client.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@SuppressWarnings("serial")
@Entity
public class ResourceMetaAnnotation extends Annotation implements Annotatable
{
	private String rel;
	
	@OneToMany(mappedBy="annotated", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH})
	private Set<Annotation> nestedAnnotations;

	/**
     * @see org.nexml.model.Annotation#getRel()
     */
    public String getRel() {
    	return rel;
    }
    
    /**
     * @see org.nexml.model.Annotation#setRel(java.lang.String)
     */
    public void setRel(String relValue) {
    	this.rel = relValue;
    }

	@Override
	public Set<Annotation> getAnnotations(String propertyOrRel)
	{
		return Annotation.getAnnotations(propertyOrRel, this.nestedAnnotations);
	}

	@Override
	public void addAnnotation(Annotation annotation)
	{
    	if (this.nestedAnnotations == null) {
    		this.nestedAnnotations = new HashSet<Annotation>();
    	}
		
		annotation.setAnnotated(this);
		this.nestedAnnotations.add(annotation);
	}

	@Override
	public String getKey()
	{
		return rel;
	}
}

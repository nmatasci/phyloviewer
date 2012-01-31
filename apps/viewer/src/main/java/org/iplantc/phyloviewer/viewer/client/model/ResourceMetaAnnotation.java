package org.iplantc.phyloviewer.viewer.client.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.iplantc.phyloviewer.shared.model.metadata.Annotated;
import org.iplantc.phyloviewer.shared.model.metadata.Annotation;

/**
 * An AnnotationEntity for nested annotations and remote resources.
 * @see http://nexml.org/nexml/html/doc/schema-1/meta/annotations/#ResourceMeta
 */
@SuppressWarnings("serial")
@Entity
public class ResourceMetaAnnotation extends AnnotationEntity implements Annotated
{
	private String rel;
	private String href;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH}, targetEntity=AnnotationEntity.class)
	@CollectionTable(name="nested_annotation")
	private Set<Annotation> annotations;

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

    /**
     * @return the href for a remote resource
     */
	public String getHref()
	{
		return href;
	}

	public void setHref(String href)
	{
		this.href = href;
	}

	@Override
	public Set<Annotation> getAnnotations()
	{
		return this.annotations;
	}

	@Override
	public void setAnnotations(Set<Annotation> annotations)
	{
		this.annotations = annotations;
	}
	
	@Override
	public Set<Annotation> getAnnotations(String propertyOrRel)
	{
		return AnnotationEntity.getAnnotations(propertyOrRel, this.annotations);
	}

	@Override
	public void addAnnotation(Annotation annotation)
	{
    	if (this.annotations == null) {
    		this.annotations = new HashSet<Annotation>();
    	}
		
		this.annotations.add(annotation);
	}

	@Override
	public String getKey()
	{
		return rel;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ResourceMetaAnnotation && super.equals(obj))
		{
			ResourceMetaAnnotation other = (ResourceMetaAnnotation) obj;
			return this.rel.equals(other.rel) &&
					this.href == null && other.href == null || this.href.equals(other.href) && 
					this.annotations.equals(other.annotations);
		}
		else
		{
			return false;
		}
	}

	@Override
	public Object getValue()
	{
		return annotations;
	}   
}

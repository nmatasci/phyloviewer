package org.iplantc.phyloviewer.viewer.client.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@SuppressWarnings("serial")
@Entity
public class ResourceMetaAnnotation extends Annotation implements Annotated
{
	private String rel;
	private String href;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH})
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
	
	public Set<Annotation> getAnnotations(String propertyOrRel)
	{
		return Annotation.getAnnotations(propertyOrRel, this.annotations);
	}

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
	public void clean()
	{
		Set<Annotation> annotations = this.annotations; //possibly a persistence collection -- not serializable. But can't check directly, since hibernate classes aren't available on the client.
		this.annotations = new HashSet<Annotation>();
	
		for (Annotation annotation : annotations) 
    	{
			annotation.clean();
    		addAnnotation(annotation);
    	}
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
}

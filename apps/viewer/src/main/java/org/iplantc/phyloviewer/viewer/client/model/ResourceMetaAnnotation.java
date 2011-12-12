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
public class ResourceMetaAnnotation extends Annotation
{
	private String rel;
	private String href;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH})
	@CollectionTable(name="nested_annotation")
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

	public String getHref()
	{
		return href;
	}

	public void setHref(String href)
	{
		this.href = href;
	}

	public Set<Annotation> getAnnotations(String propertyOrRel)
	{
		return Annotation.getAnnotations(propertyOrRel, this.nestedAnnotations);
	}

	public void addAnnotation(Annotation annotation)
	{
    	if (this.nestedAnnotations == null) {
    		this.nestedAnnotations = new HashSet<Annotation>();
    	}
		
		this.nestedAnnotations.add(annotation);
	}

	@Override
	public String getKey()
	{
		return rel;
	}

	@Override
	public void clean()
	{
		Set<Annotation> annotations = this.nestedAnnotations; //possibly a persistence collection -- not serializable. But can't check directly, since hibernate classes aren't available on the client.
		this.nestedAnnotations = new HashSet<Annotation>();
	
		for (Annotation annotation : annotations) 
    	{
			annotation.clean();
    		addAnnotation(annotation);
    	}
	}
}

package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class AnnotatedNode extends RemoteNode implements Serializable
{
	private static final long serialVersionUID = 602683128059592856L;
	
	@OneToMany(mappedBy="node", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH})
	private Set<Annotation> annotations;

	public AnnotatedNode()
	{
	}

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
    	Set<Annotation> annotations = new HashSet<Annotation>();
    	if ( propertyOrRel == null ) {
    		return annotations;
    	}
    	for ( Annotation annotation : this.annotations ) {
    		if ( propertyOrRel.equals(annotation.getProperty()) || propertyOrRel.equals(annotation.getRel()) ) {
    			annotations.add(annotation);
    		}
    	}
    	return annotations;
    }

    /**
     * For adding phyloparser NHX annotations.
     */
    public Annotation addAnnotation(String property, String value) 
    {
    	Annotation annotation = new Annotation();
    	annotation.setNode(this);
    	annotation.setProperty(property);
    	annotation.setValue(value);
    	addAnnotation(annotation);
    	
    	return annotation;
    }
    
    public void addAnnotation(Annotation annotation) {
    	if (this.annotations == null) {
    		this.annotations = new HashSet<Annotation>();
    	}
    	
    	this.annotations.add(annotation);
    }
    
    @Override
    public void clean() 
    {
    	super.clean();
    	
		Set<Annotation> annotations = this.annotations; //possibly a persistence collection -- not serializable. But can't check directly, since hibernate classes aren't available on the client.
		this.annotations = new HashSet<Annotation>();
	
		for (Annotation annotation : annotations) 
    	{
    		annotation.clean();
    	}

    }
}

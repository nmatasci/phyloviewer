package org.iplantc.phyloviewer.viewer.client.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@SuppressWarnings("serial")
public class AnnotatedTree extends RemoteTree implements Annotated
{
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH})
	private Set<Annotation> annotations;
	
	public AnnotatedTree()
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
    	return Annotation.getAnnotations(propertyOrRel, this.annotations);
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
    		addAnnotation(annotation);
    	}
	}
    
    
}

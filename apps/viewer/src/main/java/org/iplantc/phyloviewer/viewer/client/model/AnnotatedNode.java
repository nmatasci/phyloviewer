package org.iplantc.phyloviewer.viewer.client.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class AnnotatedNode extends RemoteNode
{
	private static final long serialVersionUID = 602683128059592856L;
	
	@OneToMany(mappedBy="node", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH})
	private Set<Annotation> annotations;

	public AnnotatedNode(AnnotatedNode node)
	{
		super(node);
	}
	
	/**
	 * Creates an AnnotatedNode from a nexml Node.  
	 * Creates new serializable, persistable copies of any < meta> annotations
	 * @param nexmlNode
	 */
	public AnnotatedNode(org.nexml.model.Node nexmlNode)
	{
		this.setLabel(nexmlNode.getLabel());
		
		Set<org.nexml.model.Annotation> nexmlAnnotations = nexmlNode.getAllAnnotations();
		this.annotations = new HashSet<Annotation>();
		for (org.nexml.model.Annotation nexmlAnnotation : nexmlAnnotations) 
		{
			this.annotations.add(new Annotation(nexmlAnnotation, this));
		}
	}
	
	/**
	 * Creates an AnnotatedNode from a phyloparser Node. Doesn't attempt to do additional parsing of
	 * annotations, just adds the &&NHX annotation strings from parserNode.getAnnotations(). The annotations can
	 * be retrieved with getAnnotations("NHX").
	 * 
	 * @param parserNode
	 */
	public AnnotatedNode(org.iplantc.phyloparser.model.Node parserNode)
	{
		this.setLabel(parserNode.getName());
		
		this.annotations = new HashSet<Annotation>();
		
		for (org.iplantc.phyloparser.model.Annotation annotation : parserNode.getAnnotations())
		{
			this.addAnnotation("NHX", annotation.getContent());
		}
	}

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
    	if (this.annotations == null) {
    		this.annotations = new HashSet<Annotation>();
    	}
    	
    	Annotation annotation = new Annotation();
    	annotation.setNode(this);
    	annotation.setProperty(property);
    	annotation.setValue(value);
    	this.annotations.add(annotation);
    	
    	return annotation;
    }
}

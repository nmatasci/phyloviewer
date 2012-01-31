package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.iplantc.phyloviewer.shared.model.metadata.Annotation;

/**
 * An abstract Annotation superclass for serializable, persistent annotations
 * @see Annotation
 */
@Entity
@Table(name="annotation")
public abstract class AnnotationEntity implements Serializable, Annotation
{
	private static final long serialVersionUID = 5343384601854251292L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	private String predicateNamespace;
	
	public AnnotationEntity()
	{
		
	}
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

    @Override
	public String getPredicateNamespace()
	{
		return predicateNamespace;
	}

	/**
	 * Sets the namespace URI for the predicate (either the "property" or "rel" attribute in the case of
	 * annotations with URLs or nested annotations).
	 */
	public void setPredicateNamespace(String predicateNamespace)
	{
		this.predicateNamespace = predicateNamespace;
	}
	
	/**
	 * @return all annotations in a set matching the given key.
	 */
    public static Set<Annotation> getAnnotations(String key, Set<Annotation> annotations) {
    	Set<Annotation> subset = new HashSet<Annotation>();
    	if ( key == null ) {
    		return subset;
    	}
    	for ( Annotation annotation : annotations ) {
    		if ( key.equals(annotation.getKey()) ) {
    			subset.add(annotation);
    		}
    	}
    	return subset;
    }

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Annotation)
		{
			Annotation other = (Annotation) obj;
			return this.predicateNamespace == null && other.getPredicateNamespace() == null || this.predicateNamespace.equals(other.getPredicateNamespace()) &&
					this.getValue() == null && other.getValue() == null || this.getValue().equals(other.getValue());
		}
		else
		{
			return false;
		}
	}   
}

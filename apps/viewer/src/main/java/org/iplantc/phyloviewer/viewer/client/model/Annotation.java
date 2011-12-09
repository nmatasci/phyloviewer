package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Annotation implements Serializable
{
	private static final long serialVersionUID = 5343384601854251292L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Annotatable annotated;

	private String predicateNamespace;
	
	public Annotation()
	{
		
	}
	
	public abstract String getKey();
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public Annotatable getAnnotated()
	{
		return annotated;
	}

	public void setAnnotated(Annotatable annotated)
	{
		this.annotated = annotated;
	}

    public String getPredicateNamespace()
	{
		return predicateNamespace;
	}

	public void setPredicateNamespace(String predicateNamespace)
	{
		this.predicateNamespace = predicateNamespace;
	}
	
	public void clean()
	{
		this.annotated = null;
	}
	
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
}

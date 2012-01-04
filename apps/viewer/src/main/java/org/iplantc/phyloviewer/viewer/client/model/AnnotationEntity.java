package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
	
	/* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.viewer.client.model.Annotation#getKey()
	 */
	@Override
	public abstract String getKey();
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

    /* (non-Javadoc)
	 * @see org.iplantc.phyloviewer.viewer.client.model.Annotation#getPredicateNamespace()
	 */
    @Override
	public String getPredicateNamespace()
	{
		return predicateNamespace;
	}

	public void setPredicateNamespace(String predicateNamespace)
	{
		this.predicateNamespace = predicateNamespace;
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

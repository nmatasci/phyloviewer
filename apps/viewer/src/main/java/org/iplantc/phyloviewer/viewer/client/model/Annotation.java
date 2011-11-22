package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Annotation implements Serializable
{
	private static final long serialVersionUID = 5343384601854251292L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private AnnotatedNode node;
	
	private Serializable value;
	private String property;
	private String predicateNamespace;
	private String rel;
	private String datatype;
	
	public Annotation(AnnotatedNode node, Serializable value, String property, String predicateNamespace, String rel, String datatype) {
		this.node = node;
		this.value = value;
		this.property = property;
		this.predicateNamespace = predicateNamespace;
		this.rel = rel;
		this.datatype = datatype;
	}
	
	public Annotation()
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

	public AnnotatedNode getNode()
	{
		return node;
	}

	public void setNode(AnnotatedNode node)
	{
		this.node = node;
	}

	/**
     * @see org.nexml.model.Annotation#getProperty()
     */
    public String getProperty() {
        return property;
    }
    
    /**
     * @see org.nexml.model.Annotation#setProperty(java.lang.String)
     */
    public void setProperty(String propertyValue) {
        this.property = propertyValue;
    }
    
    public String getPredicateNamespace()
	{
		return predicateNamespace;
	}

	public void setPredicateNamespace(String predicateNamespace)
	{
		this.predicateNamespace = predicateNamespace;
	}

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
     * @see org.nexml.model.Annotation#getValue()
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * @see org.nexml.model.Annotation#setValue(java.lang.Object)
     */
    public void setValue(Object value) {
    	this.value = (Serializable)value;
    }

	public String getDatatype()
	{
		return datatype;
	}

	public void setDatatype(String datatype)
	{
		this.datatype = datatype;
	}

	public void clean()
	{
		this.node = null;
	}
	
}

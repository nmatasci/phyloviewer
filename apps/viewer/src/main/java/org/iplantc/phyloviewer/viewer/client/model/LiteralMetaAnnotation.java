package org.iplantc.phyloviewer.viewer.client.model;

import javax.persistence.Entity;

/**
 * An AnnotationEntity for simple annotation values.
 * Based on nexml Annotation
 * @see http://nexml.org/nexml/html/doc/schema-1/meta/annotations/#LiteralMeta
 */
@SuppressWarnings("serial")
@Entity
public class LiteralMetaAnnotation extends AnnotationEntity
{
	private String value;
	private String property;
	private String datatype;
    
    /**
     * @see org.nexml.model.Annotation#getValue()
     */
    public String getValue() {
        return value;
    }
    
    /**
     * @see org.nexml.model.Annotation#setValue(java.lang.Object)
     */
    public void setValue(String value) {
    	this.value = value;
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

    /**
     * @return the data type of this annotation
     * @see org.nexml.model.Annotation#getXsdType()
     */
	public String getDatatype()
	{
		return datatype;
	}

	/**
	 * Set the data type of this annotation
	 * @see org.nexml.model.Annotation#getXsdType()
	 */
	public void setDatatype(String datatype)
	{
		this.datatype = datatype;
	}

	@Override
	public String getKey()
	{
		return property;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof LiteralMetaAnnotation && super.equals(obj))
		{
			LiteralMetaAnnotation other = (LiteralMetaAnnotation) obj;
			return	this.value.equals(other.value) &&
					this.property.equals(other.property) &&
					this.datatype.equals(other.datatype);
		}
		else
		{
			return false;
		}
	}   
}

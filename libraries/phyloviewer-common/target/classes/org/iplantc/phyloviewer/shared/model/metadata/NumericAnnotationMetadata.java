package org.iplantc.phyloviewer.shared.model.metadata;

/**
 * AnnotationMetadata for numeric annotations. Adds min and max values.
 */
public interface NumericAnnotationMetadata extends AnnotationMetadata
{
	/**
	 * @return the minimum value for this annotation over the whole tree
	 */
	public double getMin();
	
	/**
	 * @return the maximum value for this annotation over the whole tree
	 */
	public double getMax();
}

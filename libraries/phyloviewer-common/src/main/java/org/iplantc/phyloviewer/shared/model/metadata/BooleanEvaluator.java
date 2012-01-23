package org.iplantc.phyloviewer.shared.model.metadata;

/**
 * An AnnotationEvaluator that takes nodes and returns booleans for some annotation key
 */
public class BooleanEvaluator extends AnnotationEvaluator<Boolean>
{
	public BooleanEvaluator(String annotationKey)
	{
		super(annotationKey);
	}

	@Override
	protected Boolean parseValue(Object annotationValue)
	{
		if (annotationValue instanceof Boolean)
		{
			return (Boolean) annotationValue;
		}
		else if (annotationValue instanceof String)
		{
			return Boolean.parseBoolean((String) annotationValue);
		}
		
		return null;
	}
}

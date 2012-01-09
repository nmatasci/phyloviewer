package org.iplantc.phyloviewer.shared.model.metadata;

public class BooleanEvaluator extends AnnotationEvaluator<Boolean>
{
	public BooleanEvaluator(String annotationKey)
	{
		super(annotationKey);
	}

	@Override
	public Boolean parseValue(Object annotationValue)
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

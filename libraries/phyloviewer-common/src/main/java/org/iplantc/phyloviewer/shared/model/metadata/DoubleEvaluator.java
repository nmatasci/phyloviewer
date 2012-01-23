package org.iplantc.phyloviewer.shared.model.metadata;

/**
 * An AnnotationEvaluator that takes nodes and returns doubles for some annotation key.
 * String values will be parsed as doubles if possible.
 */
public class DoubleEvaluator extends AnnotationEvaluator<Double>
{
	public DoubleEvaluator(String annotationKey)
	{
		super(annotationKey);
	}

	protected Double parseValue(Object o)
	{
		Double value = null;
		
		if (o instanceof Number)
		{
			value = ((Number)o).doubleValue();
		}
		else if (o instanceof String)
		{
			try
			{
				value = Double.parseDouble((String)o);
			}
			catch(Exception e)
			{
				//ignore. This annotation is not a double.  value() will return null;
			}
		}
		
		return value;
	}
}

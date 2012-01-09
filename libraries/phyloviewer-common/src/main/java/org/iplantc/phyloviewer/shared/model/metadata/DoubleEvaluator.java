package org.iplantc.phyloviewer.shared.model.metadata;

public class DoubleEvaluator extends AnnotationEvaluator<Double>
{
	public DoubleEvaluator(String annotationKey)
	{
		super(annotationKey);
	}

	public Double parseValue(Object o)
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

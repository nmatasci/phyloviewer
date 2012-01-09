package org.iplantc.phyloviewer.shared.model.metadata;

import java.util.Set;

import org.iplantc.phyloviewer.shared.model.INode;

/**
 * Gets annotations matching a given key (property or rel) from nodes.  
 */
public class AnnotationEvaluator implements ValueMap<INode, Double>
{
	String annotationKey;
	
	public AnnotationEvaluator(String annotationKey)
	{
		this.annotationKey = annotationKey;
	}
	
	/**
	 * @return the double value of the annotation on the given node matching this evaluator's key
	 * 			Converts to double value if necessary (and possible). 
	 * 			Ignores any matched annotations after the first.
	 */
	@Override
	public Double get(INode node)
	{
		Double value = null;
		
		if (node instanceof Annotated)
		{
			Set<Annotation> matchedAnnotations = ((Annotated) node).getAnnotations(annotationKey);
			
			if(!matchedAnnotations.isEmpty())
			{
				Annotation annotation = matchedAnnotations.iterator().next();
				Object o = annotation.getValue();
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
			}
		}
		
		return value;
	}
	
}

package org.iplantc.phyloviewer.shared.model.metadata;

import java.util.Set;

import org.iplantc.phyloviewer.shared.model.INode;

/**
 * Gets annotations matching a given key (property or rel) from nodes.  
 */
public abstract class AnnotationEvaluator<T> implements ValueMap<INode, T>
{
	String annotationKey;
	
	/**
	 * Create a new AnnotationEvaluator that finds annotations matching a given annotationKey.
	 */
	public AnnotationEvaluator(String annotationKey)
	{
		this.annotationKey = annotationKey;
	}
	
	/**
	 * @return the value of the annotation on the given node matching this evaluator's key
	 * 			Converts to T value type if necessary (and possible). 
	 * 			Ignores any matched annotations after the first.
	 */
	@Override
	public T get(INode node)
	{
		if (node instanceof Annotated)
		{
			Set<Annotation> matchedAnnotations = ((Annotated) node).getAnnotations(annotationKey);
			
			if(!matchedAnnotations.isEmpty())
			{
				Annotation annotation = matchedAnnotations.iterator().next();
				Object o = annotation.getValue();
				return parseValue(o);
			}
		}
		
		return null;
	}
	
	protected abstract T parseValue(Object annotationValue);
	
	@Override
	public String toString()
	{
		return annotationKey;
	}
}

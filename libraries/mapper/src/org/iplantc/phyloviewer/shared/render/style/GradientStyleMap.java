package org.iplantc.phyloviewer.shared.render.style;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.metadata.ValueForNode;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

public class GradientStyleMap implements IStyleMap
{
	private IStyle minStyle;
	private IStyle maxStyle;
	
	/** @return a value [0, 1) or null for any given node */
	private ValueForNode<Double> evaluator;
	
	/**
	 * Create a new GradientStyleMap that interpolates between the given styles based on the given evaluator.
	 * @param evaluator returns a value between 0 and 1
	 * @param minStyle
	 * @param maxStyle
	 */
	public GradientStyleMap(ValueForNode<Double> evaluator, IStyle minStyle, IStyle maxStyle)
	{
		//TODO check that minStyle and maxStyle have all of the same properties set?
		this.evaluator = evaluator;
		this.minStyle = minStyle;
		this.maxStyle = maxStyle;
	}
	
	@Override
	public IStyle get(INode node)
	{
		IStyle style = null;
		Double value = evaluator.get(node);
		style = interpolate(value);

		return style;
	}
	
	/**
	 * 
	 * @param value
	 * @return the style for the given value
	 */
	private IStyle interpolate(Double value)
	{
		if (value == null)
		{
			return null;
		}
		
		//TODO confirm value in [0, 1) range
		//TODO interpolate style between minStyle and maxStyle
		//TODO check performance, cache the interpolated style if necessary
		return minStyle;
	}
}

package org.iplantc.phyloviewer.shared.render.style;

import java.util.ArrayList;

import org.iplantc.phyloviewer.shared.model.INode;

/**
 * ChainedStyleMap queries a list of IStyleMaps and returns the style provided by the last matching IStyleMap.
 */
public class ChainedStyleMap implements IStyleMap
{
	private ArrayList<IStyleMap> styleMaps;
	
	public ChainedStyleMap()
	{
		styleMaps = new ArrayList<IStyleMap>();
	}
	
	@Override
	public IStyle get(INode node)
	{
		//TODO cache the final values if performance is bad, e.g. for large styleMaps list or slow styleMap.get() implementations
		
		IStyle finalStyle = null;
		
		for (IStyleMap styleMap : styleMaps)
		{
			IStyle style = styleMap.get(node);
			if (style != null)
			{
				/*
				 * For now, just replace the previously found styles. Last mapper wins. Instead, consider
				 * doing a composite style? (E.g. one mapper adds a node color, another adds a branch
				 * width, and we want both to show up.)
				 */
				finalStyle = style;
			}
		}
		
		return finalStyle;
	}
	
	public boolean addStyleMap(IStyleMap map)
	{
		return this.styleMaps.add(map);
	}
	
	public boolean removeStyleMap(IStyleMap map)
	{
		return this.styleMaps.remove(map);
	}
}

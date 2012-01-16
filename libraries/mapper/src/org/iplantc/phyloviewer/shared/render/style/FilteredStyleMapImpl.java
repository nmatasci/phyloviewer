package org.iplantc.phyloviewer.shared.render.style;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.metadata.ValueMap;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

public class FilteredStyleMapImpl implements FilteredStyleMap
{
	ValueMap<INode, Boolean> filter;
	IStyleMap passStyleMap;
	
	public FilteredStyleMapImpl(ValueMap<INode, Boolean> filter, IStyleMap passStyleMap)
	{
		this.filter = filter;
		this.passStyleMap = passStyleMap;
	}
	
	public FilteredStyleMapImpl(ValueMap<INode, Boolean> filter, IStyle passStyle)
	{
		this.filter = filter;
		this.passStyleMap = new SingleValueStyleMap(passStyle);
	}
	
	@Override
	public IStyle get(INode node)
	{
		if (filter.get(node))
		{
			return passStyleMap.get(node);
		}
		
		return null;
	}

	@Override
	public ValueMap<INode, Boolean> getFilter()
	{
		return filter;
	}

	public IStyleMap getPassStyleMap()
	{
		return passStyleMap;
	}

}

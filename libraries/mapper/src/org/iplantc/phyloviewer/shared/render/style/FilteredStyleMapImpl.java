package org.iplantc.phyloviewer.shared.render.style;

import org.iplantc.phyloviewer.client.mapper.mocks.SingleValueStyleMap;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.metadata.ValueForNode;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

public class FilteredStyleMapImpl implements FilteredStyleMap
{
	ValueForNode<Boolean> filter;
	IStyleMap passStyleMap;
	
	public FilteredStyleMapImpl(ValueForNode<Boolean> filter, IStyleMap passStyleMap)
	{
		this.filter = filter;
		this.passStyleMap = passStyleMap;
	}
	
	public FilteredStyleMapImpl(ValueForNode<Boolean> filter, IStyle passStyle)
	{
		this.filter = filter;
		this.passStyleMap = new SingleValueStyleMap(passStyle);
	}
	
	@Override
	public IStyle get(INode node)
	{
		if (filter.value(node))
		{
			return passStyleMap.get(node);
		}
		
		return null;
	}

	@Override
	public void put(INode node, IStyle style)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasBranchDecoration(int nodeId)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getBranchLabel(INode node)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueForNode<Boolean> getFilter()
	{
		return filter;
	}

	public IStyleMap getPassStyleMap()
	{
		return passStyleMap;
	}

}

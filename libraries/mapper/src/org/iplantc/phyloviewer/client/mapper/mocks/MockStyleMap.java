package org.iplantc.phyloviewer.client.mapper.mocks;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.metadata.ValueForNode;
import org.iplantc.phyloviewer.shared.render.style.FilteredStyleMap;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

public class MockStyleMap implements FilteredStyleMap
{
	ValueForNode<Boolean> filter;
	IStyle passStyle;
	
	public MockStyleMap(ValueForNode<Boolean> filter, IStyle passStyle)
	{
		this.filter = filter;
		this.passStyle = passStyle;
	}
	
	@Override
	public IStyle get(INode node)
	{
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

	public IStyle getPassStyle()
	{
		return passStyle;
	}

}

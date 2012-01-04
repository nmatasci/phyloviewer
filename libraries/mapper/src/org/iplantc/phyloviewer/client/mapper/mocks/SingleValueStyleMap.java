package org.iplantc.phyloviewer.client.mapper.mocks;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

public class SingleValueStyleMap implements IStyleMap
{
	IStyle style;
	public SingleValueStyleMap(IStyle style)
	{
		this.style = style;
	}
	
	
	@Override
	public IStyle get(INode node)
	{
		return style;
	}

	@Override
	public void put(INode node, IStyle style)
	{
		//not implemented
	}

	@Override
	public boolean hasBranchDecoration(int nodeId)
	{
		//not implemented
		return false;
	}

	@Override
	public String getBranchLabel(INode node)
	{
		return null;
	}
	
	@Override
	public String toString()
	{
		return style.toString();
	}
}

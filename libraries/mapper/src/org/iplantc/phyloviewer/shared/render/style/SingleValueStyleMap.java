package org.iplantc.phyloviewer.shared.render.style;

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
	public String toString()
	{
		return style.toString();
	}
}

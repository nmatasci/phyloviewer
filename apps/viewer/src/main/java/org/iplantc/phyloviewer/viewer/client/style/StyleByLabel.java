package org.iplantc.phyloviewer.viewer.client.style;

import java.util.HashMap;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

/**
 * Stores node styles indexed by label
 */
public class StyleByLabel implements IStyleMap
{
	protected HashMap<String, IStyle> map = new HashMap<String,IStyle>();

	@Override
	public IStyle get(INode node)
	{
		return map.get(node.getLabel());
	}

	@Override
	public void put(INode node, IStyle style)
	{
		map.put(node.getLabel(), style);
	}

	public void clear()
	{
		map.clear();
	}
	
	@Override
	public boolean hasBranchDecoration(int nodeId)
	{
		// TODO Auto-generated method stub
		return false;
	}
}

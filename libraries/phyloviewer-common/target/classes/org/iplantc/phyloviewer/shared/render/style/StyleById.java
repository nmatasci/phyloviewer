package org.iplantc.phyloviewer.shared.render.style;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.iplantc.phyloviewer.shared.model.INode;

public class StyleById implements MutableStyleMap, HasBranchDecoration
{
	private HashMap<String,IStyle> map = new HashMap<String,IStyle>();
	private HashMap<Integer,String> nodeStyleMappings = new HashMap<Integer,String>(); 
	private Set<Integer> decoratedBranches = new HashSet<Integer>();
	private HashMap<Integer, String> branchLabels = new HashMap<Integer, String>(); 
	
	@Override
	public IStyle get(INode node)
	{
		if(node != null)
		{
			String styleId = nodeStyleMappings.get(node.getId());
			return map.get(styleId);
		}

		return null;
	}

	@Override
	public void put(INode node, IStyle style)
	{
		if(node != null && style != null)
		{
			nodeStyleMappings.put(node.getId(), style.getId());
			map.put(style.getId(), style);
		}
	}

	public void clear()
	{
		map.clear();
		nodeStyleMappings.clear();
	}

	public Set<String> getKeys()
	{
		return map.keySet();
	}
	
	@Override
	public boolean hasBranchDecoration(int nodeId)
	{
		return decoratedBranches.contains(nodeId);
	}

	public void setBranchDecoration(int nodeId, boolean value)
	{
		if(value)
		{
			decoratedBranches.add(nodeId);
		}
		else
		{
			decoratedBranches.remove(nodeId);
		}
	}

	@Override
	public String getBranchLabel(INode node)
	{
		return branchLabels.get(node.getId());
	}
	
	public void setBranchLabel(INode node, String label)
	{
		branchLabels.put(node.getId(), label);
	}
}

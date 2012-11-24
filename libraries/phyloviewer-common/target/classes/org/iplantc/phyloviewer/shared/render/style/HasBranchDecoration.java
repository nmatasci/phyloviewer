package org.iplantc.phyloviewer.shared.render.style;

import org.iplantc.phyloviewer.shared.model.INode;

public interface HasBranchDecoration
{
	//TODO should probably just move this into the IBranchStyle, but it seems more like data than style. 
	public boolean hasBranchDecoration(int nodeId);
	public String getBranchLabel(INode node);
}

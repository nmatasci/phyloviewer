package org.iplantc.phyloviewer.shared.render.style;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.metadata.ValueForNode;

public interface IStyleMap extends ValueForNode<IStyle>
{
	public void put(INode node, IStyle style);
	
	public boolean hasBranchDecoration(int nodeId);
	public String getBranchLabel(INode node);
}

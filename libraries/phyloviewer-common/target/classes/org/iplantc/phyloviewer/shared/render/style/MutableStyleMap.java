package org.iplantc.phyloviewer.shared.render.style;

import org.iplantc.phyloviewer.shared.model.INode;

public interface MutableStyleMap extends IStyleMap
{
	public void put(INode node, IStyle style);
}

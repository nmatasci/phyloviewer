package org.iplantc.phyloviewer.shared.render.style;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.metadata.ValueMap;

public interface FilteredStyleMap extends IStyleMap
{
	public IStyleMap getPassStyleMap();
	public ValueMap<INode, Boolean> getFilter();
}

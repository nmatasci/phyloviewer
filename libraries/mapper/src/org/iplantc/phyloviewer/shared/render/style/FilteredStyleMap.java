package org.iplantc.phyloviewer.shared.render.style;

import org.iplantc.phyloviewer.shared.model.metadata.ValueForNode;

public interface FilteredStyleMap extends IStyleMap
{
	public IStyleMap getPassStyleMap();
	public ValueForNode<Boolean> getFilter();
}

package org.iplantc.phyloviewer.shared.render.style;

import org.iplantc.phyloviewer.shared.model.metadata.ValueForNode;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

public interface FilteredStyleMap extends IStyleMap
{
	public IStyle getPassStyle();
	public ValueForNode<Boolean> getFilter();
}

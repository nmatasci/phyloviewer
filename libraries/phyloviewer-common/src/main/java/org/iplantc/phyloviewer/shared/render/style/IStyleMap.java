package org.iplantc.phyloviewer.shared.render.style;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.metadata.ValueMap;

public interface IStyleMap extends ValueMap<INode, IStyle>
{
	//keeping this interface around, even though it's now just a de-genericized ValueMap
}

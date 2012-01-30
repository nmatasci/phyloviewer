package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.scene.DrawableBuilderCladogram;
import org.iplantc.phyloviewer.shared.scene.LODSelectorCladogram;

/**
 * Renderer for standard 2D tree drawing.
 */
public class RenderTreeCladogram extends RenderTree
{
	//TODO there's no reason for this to be a separate class anymore.  Just pull this up into a factory method in RenderTree.
	public RenderTreeCladogram()
	{
		this.setDrawableBuilder(new DrawableBuilderCladogram());
		this.setLODSelector(new LODSelectorCladogram());
	}
}

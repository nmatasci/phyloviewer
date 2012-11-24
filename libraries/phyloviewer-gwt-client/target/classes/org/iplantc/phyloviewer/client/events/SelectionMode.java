package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView;

/**
 * An InteractionMode for tree node selection
 */
public class SelectionMode extends InteractionMode
{

	/**
	 * Creates a new SelectionMode that manipulates the given view
	 */
	public SelectionMode(DetailView detailView)
	{
		super(new SelectionKeyHandler(detailView), new SelectionMouseHandler(detailView), "selection");
	}

	@Override
	public SelectionMouseHandler getMouseHandler()
	{
		return (SelectionMouseHandler)super.getMouseHandler();
	}
}

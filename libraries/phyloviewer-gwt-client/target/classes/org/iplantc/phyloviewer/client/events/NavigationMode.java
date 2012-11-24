package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView;

/**
 * An Interaction mode for tree navigation.  
 */
public class NavigationMode extends InteractionMode
{
	
	/**
	 * Creates a new NavigationMode that manipulates the given view
	 */
	public NavigationMode(DetailView detailView)
	{
		super(new NavigationKeyHandler(detailView), new NavigationMouseHandler(detailView), "navigation");
	}

	@Override
	public NavigationMouseHandler getMouseHandler()
	{
		return (NavigationMouseHandler)super.getMouseHandler();
	}
}

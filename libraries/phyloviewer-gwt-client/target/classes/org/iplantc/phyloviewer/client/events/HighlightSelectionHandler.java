package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.client.tree.viewer.View;
import org.iplantc.phyloviewer.shared.model.INode;

/**
 * Highlights the selected nodes in a view
 */
public class HighlightSelectionHandler implements NodeSelectionHandler
{
	private final View view;

	/**
	 * Create a new HighlightSelectionHandler that highlights selected nodes in the given view
	 * @param view
	 */
	public HighlightSelectionHandler(View view)
	{
		this.view = view;
	}

	@Override
	public void onNodeSelection(NodeSelectionEvent event)
	{
		view.getRenderPreferences().clearAllHighlights();
		for(INode node : event.getSelectedNodes())
		{
			view.getRenderPreferences().highlightNode(node);
		}
		view.requestRender();
	}
}
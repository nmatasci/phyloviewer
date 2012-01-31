package org.iplantc.phyloviewer.viewer.server;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.LayoutResponse;

/**
 * An interface for tree layout data access objects.
 */
public interface ILayoutData {
	
	/**
	 * @return the layout data for the given node in the layout with the given layoutID
	 */
	public abstract LayoutResponse getLayout(INode node, String layoutID);
}

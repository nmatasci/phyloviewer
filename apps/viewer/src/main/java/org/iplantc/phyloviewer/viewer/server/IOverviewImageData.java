package org.iplantc.phyloviewer.viewer.server;

import java.awt.image.BufferedImage;

/**
 * An interface for overview image data access objects
 */
public interface IOverviewImageData {

	/**
	 * Gets or generates the overview image of the given tree with the given layout.
	 */
	public abstract BufferedImage getOverviewImage(byte[] treeId, String layoutId);
}

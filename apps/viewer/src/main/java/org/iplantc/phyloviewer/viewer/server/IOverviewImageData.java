package org.iplantc.phyloviewer.viewer.server;

import java.awt.image.BufferedImage;

public interface IOverviewImageData {

	public abstract BufferedImage getOverviewImage(byte[] treeId, String layoutId);
}

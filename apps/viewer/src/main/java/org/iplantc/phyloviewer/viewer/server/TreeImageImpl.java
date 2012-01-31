/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.viewer.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Hex;
import org.iplantc.phyloviewer.client.services.TreeImage;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of TreeImage that return URLs for the RenderTree service at /renderTree
 */
public class TreeImageImpl extends RemoteServiceServlet implements TreeImage {
	private static final long serialVersionUID = 6030698564239584673L;

	public String getTreeImageURL(ITree tree, String layoutID, int width, int height) {
		String treeID;
		
		if (tree instanceof RemoteTree) {
			treeID = Hex.encodeHexString(((RemoteTree)tree).getHash());
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "TreeImageImpl Received request for " + "treeID=" + treeID + "&layoutID=" + layoutID + "&width=" + width + "&height=" + height);
			return "/renderTree?" + "treeID=" + treeID + "&layoutID=" + layoutID + "&width=" + width + "&height=" + height;
		} else {
			throw new IllegalArgumentException("Unable to retrieve overview image for tree " + tree);
		}
	}
}

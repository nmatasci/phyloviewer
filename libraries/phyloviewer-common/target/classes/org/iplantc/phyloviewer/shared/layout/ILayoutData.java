/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.layout;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

/**
 * A data structure containing the position of a set of nodes and the bounding box of each of their
 * subtrees.
 */
public interface ILayoutData {
		
	public abstract Vector2 getPosition(INode node);
	
	public abstract Box2D getBoundingBox(INode node);
	public abstract Box2D getBoundingBox(int nodeId);
	
	/**
	 * @return true if this ILayoutData contains data for the given node.
	 */
	public abstract boolean containsNode(INode node);
}

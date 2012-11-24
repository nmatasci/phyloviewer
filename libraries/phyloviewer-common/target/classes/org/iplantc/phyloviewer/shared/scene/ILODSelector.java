package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.model.INode;

/**
 * Objects for determining if a subtree should be drawn
 */
public interface ILODSelector
{
	public enum LODLevel
	{
		LOD_LOW,
		LOD_HIGH
	}
	
	/**
	 * Check if the subtree rooted at the given node should be drawn
	 * @param node the subtree root
	 * @param layout the tree layout data
	 * @param viewMatrix the current view matrix
	 * @return LOD_LOW if the subtree should not be drawn, LOD_HIGH otherwise
	 */
	public abstract LODLevel getLODLevel(INode node, ILayoutData layout, Matrix33 viewMatrix);
}

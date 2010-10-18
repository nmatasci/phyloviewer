package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.Element;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.IElementStyle;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.core.client.GWT;

/**
 * An IStyleMap calculates per-node styling info for each graphical element of
 * the tree (nodes, branches, labels, glyphs)
 */
public abstract class IStyleMap {
	
	/** 
	 * @return a stroke style string for the given element of the given node in the format used by Canvas
	 * @see org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas#setStrokeStyle(String)
	 * @see INodeStyle.Element
	 */
	public abstract String getStrokeStyle(INodeStyle.Element element, INode node);
	
	/** 
	 * @return a fill style string for the given element of the given node in the format used by Canvas
	 * @see org.iplantc.phyloviewer.client.tree.viewer.canvas.Canvas#setFillStyle(String)
	 * @see INodeStyle.Element
	 */
	public abstract String getFillStyle(INodeStyle.Element element, INode node);
	
	/** 
	 * @return a stroke width for the given element of the given node (but mostly for branches).
	 * @see INodeStyle.Element
	 */
	public abstract double getLineWidth(INodeStyle.Element element, INode node);
	
	public void styleSubtree(INode node) {
		
		styleNode(node);
		
		//style child subtrees
		for (int i = 0, len = node.getNumberOfChildren(); i < len; i++) {
			INode child = node.getChild(i);
			if (child != null) {
				styleSubtree(child);
			}
		}
	}
	
	public void styleNode(INode node) {
		
		if ( node == null ) {
			GWT.log("Null node pointer passed to styleNode");
			return;
		}
		
		//style node elements (node, branch, label, glyph...)
		for (Element element : Element.values()) {
			IElementStyle elementStyle = node.getStyle().getElementStyle(element);
			elementStyle.setFillColor(getFillStyle(element, node));
			elementStyle.setStrokeColor(getStrokeStyle(element, node));
			elementStyle.setLineWidth(getLineWidth(element, node));
		}
	}
}

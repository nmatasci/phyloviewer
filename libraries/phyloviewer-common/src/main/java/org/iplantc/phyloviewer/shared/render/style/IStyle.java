package org.iplantc.phyloviewer.shared.render.style;

/**
 * All of the styling needed for drawing a node.
 */
public interface IStyle
{
	/**
	 * @return the id for this style.
	 */
	public abstract String getId();

	/**
	 * @return the style for the node.
	 */
	public abstract INodeStyle getNodeStyle();

	/**
	 * @return the style for the label.
	 */
	public abstract ILabelStyle getLabelStyle();

	/**
	 * @return the style for the glyph.
	 */
	public abstract IGlyphStyle getGlyphStyle();

	/**
	 * @return the style for the branch.
	 */
	public abstract IBranchStyle getBranchStyle();

	/**
	 * @return true if this style should be inherited by a node's subtree
	 */
	public abstract boolean isInheritable();
}

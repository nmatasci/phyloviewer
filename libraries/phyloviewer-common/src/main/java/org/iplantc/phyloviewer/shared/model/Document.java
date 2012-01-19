package org.iplantc.phyloviewer.shared.model;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.style.HasBranchDecoration;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;
import org.iplantc.phyloviewer.shared.render.style.StyleById;

/**
 * A basic implementation of IDocument.
 */
public class Document implements IDocument
{
	private ITree tree;
	private IStyleMap styleMap = new StyleById();
	private ILayoutData layout;

	// TODO: Add a lookup table for internal nodes, instead of setting node object's name

	public Document()
	{
	}

	@Override
	public ITree getTree()
	{
		return tree;
	}

	public void setTree(ITree tree)
	{
		this.tree = tree;
	}

	@Override
	public IStyleMap getStyleMap()
	{
		return styleMap;
	}

	public void setStyleMap(IStyleMap styleMap)
	{
		this.styleMap = styleMap;
	}

	/**
	 * A convenience method to get the style for a given node, or the default style if no style is
	 * defined for it.
	 */
	@Override
	public IStyle getStyle(INode node)
	{
		if(node != null)
		{
			if(this.styleMap != null)
			{
				IStyle style = this.styleMap.get(node);
				if(style != null)
				{
					return style;
				}
			}
		}

		// If we get here, return the default style.
		return Defaults.DEFAULT_STYLE;
	}

	@Override
	public String getLabel(INode node)
	{
		// TODO give user options on how to label internal nodes without modifying the INode itself
		return node.getLabel();
	}

	public ILayoutData getLayout()
	{
		return layout;
	}

	public void setLayout(ILayoutData layout)
	{
		this.layout = layout;
	}

	/**
	 * Check if the children of <code>node</code> are ready to be rendered (i.e. both the children and
	 * their layout are available locally.)
	 */
	@Override
	public boolean checkForData(final INode node)
	{
		//Always true for this implementation.  Subclasses may return false while they fetch the necessary data.
		return true;
	}

	/**
	 * @return true if this Document is ready to begin rendering. 
	 * 		Returns false if the tree or the layout hasn't been set. Subclasses could impose other conditions.
	 */
	@Override
	public boolean isReady()
	{
		return this.getTree() != null && this.getLayout() != null;
	}

	/**
	 * A convenience method to check if a node has been given branch decorations in the style map.
	 */
	@Override
	public boolean hasBranchDecoration(int nodeId)
	{
		if(this.styleMap != null && this.styleMap instanceof HasBranchDecoration)
		{
			return ((HasBranchDecoration)styleMap).hasBranchDecoration(nodeId);
		}
		
		return false;
	}
}

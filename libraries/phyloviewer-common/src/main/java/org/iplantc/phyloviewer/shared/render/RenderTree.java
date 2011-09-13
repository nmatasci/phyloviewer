/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.render;

import java.util.Stack;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.style.CompositeStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.scene.Drawable;
import org.iplantc.phyloviewer.shared.scene.DrawableContainer;
import org.iplantc.phyloviewer.shared.scene.IDrawableBuilder;
import org.iplantc.phyloviewer.shared.scene.ILODSelector;
import org.iplantc.phyloviewer.shared.scene.ILODSelector.LODLevel;

public abstract class RenderTree
{
	private RenderPreferences renderPreferences = new RenderPreferences();
	IDocument document;
	IDrawableBuilder builder;
	ILODSelector lodSelector;
	DrawableContainer drawableContainer = new DrawableContainer();
	Stack<Boolean> highlightSubTreeStack = new Stack<Boolean>();

	public RenderTree()
	{
	}

	public IDocument getDocument()
	{
		return document;
	}

	public void setDocument(IDocument document)
	{
		this.document = document;
		drawableContainer.clear();
	}

	protected void setDrawableBuilder(IDrawableBuilder builder)
	{
		this.builder = builder;
		drawableContainer.setBuilder(builder);
	}

	protected void setLODSelector(ILODSelector lodSelector)
	{
		this.lodSelector = lodSelector;
	}

	public DrawableContainer getDrawableContainer()
	{
		return drawableContainer;
	}

	public void renderTree(IGraphics graphics, Matrix33 viewMatrix)
	{
		ITree tree = document != null ? document.getTree() : null;
		ILayoutData layout = document != null ? document.getLayout() : null;

		if(document == null || tree == null || graphics == null || layout == null || builder == null
				|| lodSelector == null)
			return;

		INode root = tree.getRootNode();

		if(root == null)
			return;

		if(viewMatrix != null)
		{
			graphics.setViewMatrix(viewMatrix);
		}

		graphics.clear();

		highlightSubTreeStack.clear();
		highlightSubTreeStack.push(false);

		IStyle style = document.getStyle(root);
		this.renderNode(root, layout, graphics, style);
	}

	public RenderPreferences getRenderPreferences()
	{
		return renderPreferences;
	}

	public void setRenderPreferences(RenderPreferences preferences)
	{
		renderPreferences = preferences;
	}

	protected void renderNode(INode node, ILayoutData layout, IGraphics graphics, IStyle style)
	{
		if(graphics.isCulled(this.getBoundingBox(node, layout)))
		{
			return;
		}
		
		IStyle highlightedStyle = style;
		if (renderPreferences.isNodeHighlighted(node) || highlightSubTreeStack.peek()) 
		{
			highlightedStyle = addHighlight(highlightedStyle);
		}

		boolean stackNeedsPopped = false;
		if(renderPreferences.isSubTreeHighlighted(node))
		{
			highlightSubTreeStack.push(true);
			stackNeedsPopped = true;
		}

		if(renderPreferences.drawLabels() && node.isLeaf())
		{
			drawLabel(node, layout, graphics, highlightedStyle);
		}
		else if(renderPreferences.isCollapsed(node)
				|| (renderPreferences.collapseOverlaps() && LODLevel.LOD_LOW == lodSelector.getLODLevel(
						node, layout, graphics.getObjectToScreenMatrix())))
		{
			renderPlaceholder(node, layout, graphics, highlightedStyle);
		}
		else if(!document.checkForData(node))
		{
			// while checkForData gets children and layouts (async), render a subtree placeholder
			renderPlaceholder(node, layout, graphics, highlightedStyle);
		}
		else
		{
			renderChildren(node, layout, graphics, style);
		}

		if(renderPreferences.isDrawPoints())
		{
			Drawable[] drawables = drawableContainer.getNodeDrawables(node, document, layout);
			for(Drawable drawable : drawables)
			{
				drawable.draw(graphics, highlightedStyle);
			}
		}

		if(stackNeedsPopped)
		{
			highlightSubTreeStack.pop();
		}
	}

	public Box2D getBoundingBox(INode node, ILayoutData layout)
	{
		return layout.getBoundingBox(node);
	}

	protected void drawLabel(INode node, ILayoutData layout, IGraphics graphics, IStyle style)
	{
		Drawable drawable = drawableContainer.getTextDrawable(node, document, layout);
		drawable.draw(graphics, style);
	}

	protected void renderChildren(INode parent, ILayoutData layout, IGraphics graphics, IStyle parentStyle)
	{
		INode[] children = parent.getChildren();
		for(int i = 0;i < children.length;++i)
		{
			INode child = children[i];
			IStyle style = getStyle(child);
			 
			style = createComposite(parentStyle, style, Defaults.DEFAULT_STYLE);
			
			IStyle branchStyle = style;
			if (renderPreferences.isBranchHighlighted(child))
			{
				branchStyle = addHighlight(branchStyle);
			}

			Drawable[] drawables = drawableContainer.getBranchDrawables(parent, child, document, layout);
			for(Drawable drawable : drawables)
			{
				drawable.draw(graphics, branchStyle);
			}

			renderNode(child, layout, graphics, style);
		}
	}

	protected void renderPlaceholder(INode node, ILayoutData layout, IGraphics graphics, IStyle style)
	{
		Drawable[] drawables = drawableContainer.getGlyphDrawables(node, document, layout);
		for(Drawable drawable : drawables)
		{
			drawable.draw(graphics, style);
		}
	}

	private IStyle addHighlight(IStyle style)
	{
		IStyle highlightStyle = renderPreferences.getHighlightStyle();

		if(highlightStyle != null)
		{
			style = new CompositeStyle(highlightStyle, style);
		}
		
		return style;
	}
	
	/**
	 * @return the style for a node.  May return null.
	 */
	private IStyle getStyle(INode node)
	{
		IStyle style = null;
		if(node != null && document != null && document.getStyleMap() != null)
		{
			style = document.getStyleMap().get(node);
		}
		
		return style;
	}
	
	/**
	 * @param parentStyle may be null
	 * @param style may be null
	 * @param defaultStyle may not be null
	 * @return a composite of style and parentStyle if parentStyle.isInheritable().  defaultStyle if style is null and parentStyle is null or not inheritable
	 */
	private IStyle createComposite(IStyle parentStyle, IStyle style, IStyle defaultStyle) {
		if (parentStyle != null && parentStyle.isInheritable()) 
		{
			if (style != null) 
			{
				style = new CompositeStyle(style, parentStyle);
			}
			else
			{
				style = parentStyle;
			}
		} 
		else if (style == null)
		{
			style = defaultStyle;
		}
		return style;
	}
}

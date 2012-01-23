package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

/**
 * Represents tree elements that can be drawn.
 */
public abstract class Drawable
{
	/**
	 * Context for what this drawable is representing
	 * 
	 * @author adamkubach
	 * 
	 */
	public enum Context
	{
		CONTEXT_UNKNOWN, CONTEXT_NODE, CONTEXT_BRANCH, CONTEXT_GLYPH, CONTEXT_LABEL
	}
	
	public static final int TYPE_POINT = 0x01;
	public static final int TYPE_LINE = 0x02;
	public static final int TYPE_POLYGON = 0x04;
	public static final int TYPE_TEXT = 0x08;

	Box2D boundingBox = new Box2D();
	Context context = Context.CONTEXT_UNKNOWN;

	/**
	 * Constructor.
	 */
	public Drawable()
	{
	}

	/**
	 * @return the bounds of this drawable
	 */
	public Box2D getBoundingBox()
	{
		return boundingBox;
	}

	public void setBoundingBox(Box2D boundingBox)
	{
		this.boundingBox = boundingBox;
	}

	/**
	 * @return the Context of this drawable
	 */
	public Context getContext()
	{
		return context;
	}

	public void setContext(Context context)
	{
		this.context = context;
	}
	
	/**
	 * An integer indicating the type of object being drawn. Used for filtering user interface actions.
	 * 
	 * @return a bitwise or of the following flags: TYPE_POINT, TYPE_LINE, TYPE_POLYGON, TYPE_TEXT
	 */
	public abstract int getDrawableType();

	public boolean intersect(Vector2 position, double distanceSquared)
	{
		return false;
	}

	/**
	 * Draw this Drawable to the given IGraphics with the given style.
	 */
	public abstract void draw(IGraphics graphics, IStyle style);
}

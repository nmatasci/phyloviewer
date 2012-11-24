package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

/**
 * A Drawable for collapsed subtrees in circular tree drawings
 */
public class Wedge extends Drawable
{
	Vector2 center;
	Vector2 peak;
	double radius;
	double startAngle;
	double endAngle;

	/**
	 * Create a new Wedge.
	 * 
	 * @param center the center of the outer circular arc.
	 * @param peak the inner point of the wedge.
	 * @param radius the outer radius
	 * @param startAngle the start of the outer arc, in radians
	 * @param endAngle the end of the outer arc, in radians
	 */
	public Wedge(Vector2 center, Vector2 peak, double radius, double startAngle, double endAngle)
	{
		this.center = center;
		this.peak = peak;
		this.radius = radius;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
	}
	
	@Override
	public void draw(IGraphics graphics, IStyle style)
	{
		if(graphics != null)
		{
			if(style != null)
			{
				graphics.setStyle(style.getGlyphStyle());
			}
			
			graphics.drawWedge(center, peak, radius, startAngle, endAngle);
		}
	}

	@Override
	public int getDrawableType()
	{
		return TYPE_POLYGON;
	}
}

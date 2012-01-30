/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;
import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.ILabelStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle;
import org.iplantc.phyloviewer.shared.scene.Text;

/**
 * Interface for tree rendering targets. Use the setStyle methods to set the drawing style according to
 * the element type being drawn, then use the drawX methods to draw the element.
 */
public interface IGraphics
{
	public abstract int getWidth();
	public abstract int getHeight();

	/**
	 * Clear any previously drawn graphics.
	 */
	public abstract void clear();

	/**
	 * Draws a filled circle in the given position, with the current style. Typically used for drawing
	 * nodes.
	 */
	public abstract void drawPoint(Vector2 position);

	/**
	 * Draws a series of straight lines between consecutive vertices. Typically used to draw branches.
	 */
	public abstract void drawLineStrip(Vector2[] vertices);

	/**
	 * Draw a closed, filled polygon with the given vertices.  Typically used to draw glyphs and decorations.
	 */
	public abstract void drawPolygon(Vector2 vertices[]);

	/**
	 * Draws text.
	 * @param position the text baseline start position
	 * @param offset an offset from the given position (TODO: this parameter doesn't seem necessary)
	 * @param text the text to render
	 * @param angle the direction of the text baseline
	 */
	public abstract void drawText(Vector2 position, Vector2 offset, String text, double angle);

	/**
	 * Draws a filled area bounded by a circular arc and two lines starting at each end of the arc which
	 * meet at the given peak point. Used for collapsed subtrees in circular drawing.
	 * 
	 * @param center the center of the circular layout.
	 * @param peak the inner point of the wedge.
	 * @param radius the outer radius
	 * @param startAngle the start of the outer arc
	 * @param endAngle the end of the outer arc
	 */
	public abstract void drawWedge(Vector2 center, Vector2 peak, double radius, double startAngle,
			double endAngle);
	
	/**
	 * Sets the view to the given dimensions
	 */
	public void setSize(int width, int height);

	/**
	 * Sets the view transformation matrix
	 */
	public abstract void setViewMatrix(Matrix33 matrix);

	public abstract Matrix33 getViewMatrix();
	
	/**
	 * Get the matrix to convert object coordinates to screen coordinates.
	 * @return
	 */
	public Matrix33 getObjectToScreenMatrix();
	
	/**
	 * Get the matrix to convert screen coordinates to object coordinates.
	 * @return
	 */
	public Matrix33 getScreenToObjectMatrix();

	/**
	 * @return true if the given box (in object space) is visible in this IGraphics
	 */
	public abstract Boolean isCulled(Box2D iBox2D);

	/**
	 * Draws a circular arc
	 */
	public abstract void drawArc(Vector2 center, double radius, double startAngle, double endAngle);

	/**
	 * Sets the drawing style. Any element style fields that are null or NaN will leave the previous
	 * style in place for that field (so multiple styles can be 'layered').
	 */
	public abstract void setStyle(IBranchStyle style);

	/**
	 * Sets the drawing style. Any element style fields that are null or NaN will leave the previous
	 * style in place for that field (so multiple styles can be 'layered').
	 */
	public abstract void setStyle(IGlyphStyle style);

	/**
	 * Sets the drawing style. Any element style fields that are null or NaN will leave the previous
	 * style in place for that field (so multiple styles can be 'layered').
	 */
	public abstract void setStyle(ILabelStyle style);

	/**
	 * Sets the drawing style. Any element style fields that are null or NaN will leave the previous
	 * style in place for that field (so multiple styles can be 'layered').
	 */
	public abstract void setStyle(INodeStyle style);
	
	/**
	 * Calculates the dimensions of the given text with the current view matrix.
	 */
	public Box2D calculateBoundingBox(Text text);
}
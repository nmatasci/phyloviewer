package org.iplantc.phyloviewer.shared.render.style;

public interface INodeStyle
{
	public enum Shape
	{
		CIRCLE,
		SQUARE
	}
	
	/**
	 * Get the shape to use
	 * @return
	 */
	public Shape getShape();
	
	/**
	 * Get the color of the node.
	 * @return
	 */
	public abstract String getColor();

	/**
	 * Set the color
	 * @param color
	 */
	public abstract void setColor(String color);

	/**
	 * Get the point size.
	 * @return
	 */
	public abstract double getPointSize();

	/**
	 * Set the point size.
	 * @param size
	 */
	public abstract void setPointSize(double size);
}

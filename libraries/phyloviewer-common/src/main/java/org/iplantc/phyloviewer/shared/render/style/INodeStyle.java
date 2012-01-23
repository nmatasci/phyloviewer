package org.iplantc.phyloviewer.shared.render.style;

public interface INodeStyle
{
	/** Node shapes */
	public enum Shape
	{
		CIRCLE,
		SQUARE
	}
	
	/**
	 * @return the shape to use
	 */
	public Shape getShape();
	
	/**
	 * @return the color of the node.
	 */
	public abstract String getColor();

	/**
	 * Set the color
	 */
	public abstract void setColor(String color);

	/**
	 * @return the point size.
	 */
	public abstract double getPointSize();

	/**
	 * Set the point size.
	 */
	public abstract void setPointSize(double size);
}

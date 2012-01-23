package org.iplantc.phyloviewer.shared.render.style;

public interface IGlyphStyle {

	/**
	 * @return the color for the outline of the glyph
	 */
	public abstract String getStrokeColor();
	public abstract void setStrokeColor(String color);
	
	/**
	 * @return the color for the interior of the glyph
	 */
	public abstract String getFillColor();
	public abstract void setFillColor(String color);
	
	/**
	 * @return the width of the outline, in pixels
	 */
	public abstract double getLineWidth();
	public abstract void setLineWidth(double width);
}

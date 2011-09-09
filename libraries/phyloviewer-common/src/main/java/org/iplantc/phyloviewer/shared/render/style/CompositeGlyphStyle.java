package org.iplantc.phyloviewer.shared.render.style;

/**
 * An IGlyphStyle that returns default values from a base IGlyphStyle if they haven't been set.
 */
public class CompositeGlyphStyle implements IGlyphStyle
{
	private IGlyphStyle baseStyle;
	private IGlyphStyle mainStyle;
	
	public CompositeGlyphStyle(IGlyphStyle mainStyle, IGlyphStyle baseStyle)
	{
		this.mainStyle = mainStyle;
		this.baseStyle = baseStyle;
	}
	
	public String getFillColor() {
		String fillColor = mainStyle.getFillColor();
		if (fillColor != null)
		{
			return fillColor;
		}
		else
		{
			return baseStyle.getFillColor();
		}
	}

	public double getLineWidth() {
		double strokeWidth = mainStyle.getLineWidth();
		if (strokeWidth != Double.NaN)
		{
			return strokeWidth;
		}
		else
		{
			return baseStyle.getLineWidth();
		}
	}

	public String getStrokeColor() {
		String strokeColor = mainStyle.getStrokeColor();
		if (strokeColor != null)
		{
			return strokeColor;
		}
		else
		{
			return baseStyle.getStrokeColor();
		}
	}

	public void setBaseStyle(IGlyphStyle baseStyle)
	{
		this.baseStyle = baseStyle;
	}

	public IGlyphStyle getBaseStyle()
	{
		return baseStyle;
	}

	public void setFillColor(String color)
	{
		mainStyle.setFillColor(color);
	}

	public void setLineWidth(double width)
	{
		mainStyle.setLineWidth(width);
	}

	public void setStrokeColor(String color)
	{
		mainStyle.setStrokeColor(color);
	}
}

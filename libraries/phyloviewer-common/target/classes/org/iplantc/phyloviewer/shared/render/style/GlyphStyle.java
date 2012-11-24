package org.iplantc.phyloviewer.shared.render.style;

import java.io.Serializable;

public class GlyphStyle implements IGlyphStyle, Serializable
{
	private static final long serialVersionUID = 1L;
	String fillColor = null;
	String strokeColor = null;
	double strokeWidth = Double.NaN;

	public GlyphStyle(String fillColor, String strokeColor, double strokeWidth)
	{
		this.fillColor = fillColor;
		this.strokeColor = strokeColor;
		this.strokeWidth = strokeWidth;
	}

	public GlyphStyle()
	{
	}

	@Override
	public String getFillColor()
	{
		return fillColor;
	}

	@Override
	public double getLineWidth()
	{
		return strokeWidth;
	}

	@Override
	public String getStrokeColor()
	{
		return strokeColor;
	}

	@Override
	public void setFillColor(String color)
	{
		this.fillColor = color;
	}

	@Override
	public void setLineWidth(double width)
	{
		this.strokeWidth = width;
	}

	@Override
	public void setStrokeColor(String color)
	{
		this.strokeColor = color;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		boolean previous = false;
		
		sb.append("{");

		if (fillColor != null && !fillColor.isEmpty())
		{
			sb.append("\"fillColor\":\"" + fillColor + '"');
			previous = true;
		}
		
		if (strokeColor != null && !strokeColor.isEmpty())
		{
			if (previous) sb.append(",");
			sb.append("\"strokeColor\":\"" + strokeColor + '"');
			previous = true;
		}
		
		if (!Double.isNaN(strokeWidth))
		{
			if (previous) sb.append(",");
			sb.append("\"lineWidth\":" + strokeWidth);
			previous = true;
		}
		
		sb.append("}");
		
		return sb.toString();
	}
}

package org.iplantc.phyloviewer.shared.render.style;

import java.io.Serializable;

public class BranchStyle implements IBranchStyle, Serializable
{
	private static final long serialVersionUID = 1L;
	String strokeColor = null;
	double strokeWidth = Double.NaN;

	public BranchStyle(String strokeColor, double strokeWidth)
	{
		this.strokeColor = strokeColor;
		this.strokeWidth = strokeWidth;
	}

	public BranchStyle()
	{
	}

	@Override
	public String getStrokeColor()
	{
		return this.strokeColor;
	}

	@Override
	public void setStrokeColor(String color)
	{
		this.strokeColor = color;
	}

	@Override
	public double getLineWidth()
	{
		return this.strokeWidth;
	}

	@Override
	public void setLineWidth(double width)
	{
		this.strokeWidth = width;
	}
}

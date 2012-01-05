package org.iplantc.phyloviewer.shared.render.style;

import java.io.Serializable;

public class NodeStyle implements INodeStyle, Serializable
{
	private static final long serialVersionUID = 1L;
	String color = null;
	double pointSize = Double.NaN;
	private Shape shape = null;

	public NodeStyle(String color, double pointSize)
	{
		this.color = color;
		this.pointSize = pointSize;
	}
	
	public NodeStyle(String color, double pointSize, Shape shape)
	{
		this.color = color;
		this.pointSize = pointSize;
		this.shape = shape;
	}

	public NodeStyle()
	{
	}

	@Override
	public String getColor()
	{
		return this.color;
	}

	@Override
	public void setColor(String color)
	{
		this.color = color;
	}

	@Override
	public double getPointSize()
	{
		return this.pointSize;
	}

	@Override
	public void setPointSize(double size)
	{
		this.pointSize = size;
	}

	@Override
	public Shape getShape()
	{
		return shape;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		boolean previous = false;
		
		sb.append("{");

		if (color != null && !color.isEmpty())
		{
			sb.append("\"color\":\"" + color + '"');
			previous = true;
		}
		
		if (!Double.isNaN(pointSize))
		{
			if (previous) sb.append(",");
			sb.append("\"pointSize\":" + pointSize);
			previous = true;
		}
		
		if (shape != null)
		{
			if (previous) sb.append(",");
			sb.append("\"nodeShape\":\"" + shape.toString() + '"');
			previous = true;
		}
		
		sb.append("}");
		
		return sb.toString();
	}
}

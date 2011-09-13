package org.iplantc.phyloviewer.shared.render.style;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NodeStyle implements INodeStyle, IsSerializable
{
	String color = null;
	double pointSize = Double.NaN;
	private Shape shape = Shape.SHAPE_CIRCLE;

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
}

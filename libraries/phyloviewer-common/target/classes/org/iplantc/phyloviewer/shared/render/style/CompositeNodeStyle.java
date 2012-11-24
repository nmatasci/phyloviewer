package org.iplantc.phyloviewer.shared.render.style;

/**
 * An INodeStyle that returns default values from a base INodeStyle if they haven't been set.
 */
public class CompositeNodeStyle implements INodeStyle
{
	private INodeStyle baseStyle;
	private INodeStyle mainStyle;

	public CompositeNodeStyle(INodeStyle mainStyle, INodeStyle baseStyle)
	{
		this.mainStyle = mainStyle;
		this.baseStyle = baseStyle;
	}
	
	@Override
	public String getColor() {
		String color = mainStyle.getColor();
		if (color != null)
		{
			return color;
		}
		else
		{
			return baseStyle.getColor();
		}
	}

	@Override
	public double getPointSize() {
		double pointSize = mainStyle.getPointSize();
		if (!Double.isNaN(pointSize))
		{
			return pointSize;
		}
		else
		{
			return baseStyle.getPointSize();
		}
	}

	public Shape getShape()
	{
		Shape s = mainStyle.getShape();
		if (s != null) 
		{
			return s;
		}
		else
		{
			return baseStyle.getShape();
		}
	}

	public void setBaseStyle(INodeStyle baseStyle)
	{
		this.baseStyle = baseStyle;
	}

	public INodeStyle getBaseStyle()
	{
		return baseStyle;
	}

	public void setColor(String color)
	{
		mainStyle.setColor(color);
	}

	public void setPointSize(double size)
	{
		mainStyle.setPointSize(size);
	}
}

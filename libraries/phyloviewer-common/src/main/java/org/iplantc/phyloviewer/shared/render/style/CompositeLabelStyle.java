package org.iplantc.phyloviewer.shared.render.style;

/**
 * An ILabelStyle that returns default values from a base ILabelStyle if they haven't been set.
 */
public class CompositeLabelStyle implements ILabelStyle
{
	private ILabelStyle baseStyle;
	private ILabelStyle mainStyle;

	public CompositeLabelStyle(ILabelStyle mainStyle, ILabelStyle baseStyle)
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

	public void setBaseStyle(ILabelStyle baseStyle)
	{
		this.baseStyle = baseStyle;
	}

	public ILabelStyle getBaseStyle()
	{
		return baseStyle;
	}
	
	public void setColor(String color)
	{
		mainStyle.setColor(color);
	}
}

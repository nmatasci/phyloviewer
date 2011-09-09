package org.iplantc.phyloviewer.shared.render.style;

/**
 * An IBranchStyle that returns default values from a base IBranchStyle if they haven't been set.
 */
public class CompositeBranchStyle implements IBranchStyle
{
	private IBranchStyle baseStyle;
	private IBranchStyle mainStyle;
	
	public CompositeBranchStyle(IBranchStyle mainStyle, IBranchStyle baseStyle)
	{
		this.mainStyle = mainStyle;
		this.baseStyle = baseStyle;
	}
	
	@Override
	public String getStrokeColor() 
	{
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

	@Override
	public double getLineWidth() 
	{
		double strokeWidth = mainStyle.getLineWidth();
		if (!Double.isNaN(strokeWidth))
		{
			return strokeWidth;
		}
		else
		{
			return baseStyle.getLineWidth();
		}
	}

	public void setBaseStyle(IBranchStyle baseStyle)
	{
		this.baseStyle = baseStyle;
	}

	public IBranchStyle getBaseStyle()
	{
		return baseStyle;
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

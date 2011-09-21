package org.iplantc.phyloviewer.shared.render.style;

import java.io.Serializable;

public class LabelStyle implements ILabelStyle, Serializable
{
	private static final long serialVersionUID = 1L;
	String color = null;

	public LabelStyle(String color)
	{
		this.color = color;
	}

	public LabelStyle()
	{
	}

	@Override
	public String getColor()
	{
		return color;
	}

	@Override
	public void setColor(String color)
	{
		this.color = color;
	}
}

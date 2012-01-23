package org.iplantc.phyloviewer.shared.render.style;

public interface IBranchStyle {

	/**
	 * @return the branch line color
	 */
	public abstract String getStrokeColor();
	public abstract void setStrokeColor(String color);
	
	/**
	 * @return the branch line width
	 */
	public abstract double getLineWidth();
	public abstract void setLineWidth(double width);
}

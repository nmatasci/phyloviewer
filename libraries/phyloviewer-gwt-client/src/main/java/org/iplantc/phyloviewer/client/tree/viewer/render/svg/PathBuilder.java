package org.iplantc.phyloviewer.client.tree.viewer.render.svg;

import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;

public class PathBuilder
{
	private static final double TWO_PI = 2 * Math.PI;
	StringBuilder sb = new StringBuilder();
	
	public PathBuilder moveTo(double x, double y) 
	{
		sb.append("M").append(x).append(' ').append(y);
		return this;
	}
	
	public PathBuilder moveTo(Vector2 v) 
	{
		return moveTo(v.getX(), v.getY());
	}
	
	public PathBuilder lineTo(double x, double y) 
	{
		sb.append("L").append(x).append(' ').append(y);
		return this;
	}
	
	public PathBuilder lineTo(Vector2 v) 
	{
		return lineTo(v.getX(), v.getY());
	}
	
	/**
	 * Draws an elliptical arc from the current point to (x, y). The size and orientation of the ellipse
	 * are defined by two radii (rx, ry) and an xAxisRotation, which indicates how the ellipse as a
	 * whole is rotated relative to the current coordinate system. The center (cx, cy) of the ellipse is
	 * calculated automatically to satisfy the constraints imposed by the other parameters.
	 * largeArcFlag and sweepFlag contribute to the automatic calculations and help determine how the
	 * arc is drawn.
	 * 
	 * @param rx x-radius
	 * @param ry y-radius
	 * @param xAxisRotation how the ellipse as a whole is rotated relative to the current coordinate system
	 * @param largeArcFlag '0' or '1'
	 * @param sweepFlag '0' or '1'
	 * @param x the ending x coordinate
	 * @param y the ending y coordinate
	 * @return this PathBuilder
	 * 
	 * @see http://www.w3.org/TR/SVG/paths.html#PathDataEllipticalArcCommands
	 */
	public PathBuilder arc(double rx, double ry, double xAxisRotation, char largeArcFlag, char sweepFlag, double x, double y) 
	{
		sb.append("A").append(rx).append(' ').append(ry).append(' ').append(xAxisRotation)
			.append(' ').append(largeArcFlag).append(' ').append(sweepFlag)
			.append(' ').append(x).append(' ').append(y);
		return this;
	}
	
	/**
	 * For easy compatibility with canvas.arc(). Adds a circular arc to the path. If the path is not
	 * empty, appends a lineTo to the start of the arc. If the path is empty, appends a moveTo to the
	 * start of the arc.
	 * 
	 */
	public PathBuilder circularArc(double cx, double cy, double radius, double startAngle, double endAngle, boolean antiClockwise)
	{
		Vector2 center = new Vector2(cx, cy);
		Vector2 start = new PolarVector2(radius, startAngle).toCartesian(center);
		Vector2 end = new PolarVector2(radius, endAngle).toCartesian(center);
		
		if (sb.length() == 0)
		{
			moveTo(start);
		}
		else
		{
			lineTo(start);
		}
		
		char sweepFlag = antiClockwise ? '0' : '1';
		double xAxisRotation = 0;
		double magnitude = (TWO_PI + endAngle - startAngle) % TWO_PI;
		magnitude = antiClockwise ? TWO_PI - magnitude : magnitude;
		char largeArcFlag = magnitude > Math.PI ? '1' : '0';
		arc(radius, radius, xAxisRotation, largeArcFlag, sweepFlag, end.getX(), end.getY());
		
		return this;
	}
	
	public PathBuilder close()
	{
		sb.append("z");
		return this;
	}
	
	public String toString()
	{
		return sb.toString();
	}
}

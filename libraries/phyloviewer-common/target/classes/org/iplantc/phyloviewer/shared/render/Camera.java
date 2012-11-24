/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;

/**
 * Represents a pannable, zoomable viewpoint for the scene.
 */
public abstract class Camera
{
	private Matrix33 _matrix = new Matrix33();
	private boolean allowZoom = true;
	private boolean panX = true;
	private boolean panY = true;

	protected Camera()
	{
	}

	/**
	 * @return a new camera of the same type as this camera
	 */
	public abstract Camera create();

	/**
	 * Scale to fit the given rectangle
	 */
	public abstract void zoomToBoundingBox(Box2D bbox);

	/**
	 * @return the current view matrix, scaled to the given dimensions
	 */
	public Matrix33 getMatrix(int width, int height)
	{
		return Matrix33.makeScale(width, height).multiply(_matrix);
	}

	/**
	 * @return the current view matrix
	 */
	public Matrix33 getViewMatrix()
	{
		return _matrix;
	}

	public void setViewMatrix(Matrix33 matrix)
	{
		_matrix = matrix;
	}

	/**
	 * Scale around the given point (xCenter, yCenter) by the given amounts (xZoom, yZoom)
	 */
	public void zoom(double xCenter, double yCenter, double xZoom, double yZoom)
	{
		if(allowZoom)
		{
			Matrix33 T0 = Matrix33.makeTranslate(xCenter, yCenter);
			Matrix33 S = Matrix33.makeScale(xZoom, yZoom);
			Matrix33 T1 = Matrix33.makeTranslate(-xCenter, -yCenter);

			Matrix33 delta = T0.multiply(S.multiply(T1));
			Matrix33 matrix = delta.multiply(_matrix);
			this.setViewMatrix(matrix);
		}
	}

	/**
	 * Scale uniformly around the center of the camera by the given factor
	 */
	public void zoom(double factor)
	{
		zoom(0.5, 0.5, factor, factor);
	}

	/**
	 * Translate the camera
	 */
	public void pan(double x, double y)
	{
		x = isXPannable() ? x : 0.0;
		y = isYPannable() ? y : 0.0;
		
		Matrix33 matrix = _matrix.multiply(Matrix33.makeTranslate(x, y));
		this.setViewMatrix(matrix);
	}

	/**
	 * Resets the view matrix
	 */
	public void reset()
	{
		this.setViewMatrix(new Matrix33());
	}

	/**
	 * Set the zoom values and don't allow any further zooming.
	 */
	public void lockToZoom(double xZoom, double yZoom)
	{
		double x = _matrix.getTranslationX();
		double y = _matrix.getTranslationY();
		
		// Reset the matrix.
		this.reset();
		
		_matrix.setTranslationX(x);
		_matrix.setTranslationY(y);
		
		this.zoom(0.5, 0.5, xZoom, yZoom);
		
		allowZoom = false;
	}

	public boolean isAllowZoom()
	{
		return allowZoom;
	}

	/**
	 * Set whether this camera is allowed to zoom
	 */
	public void setAllowZoom(boolean allowZoom)
	{
		this.allowZoom = allowZoom;
	}
	
	/**
	 * Set whether this camera is allowed to pan in the x and y directions
	 */
	public void setPannable(boolean x, boolean y)
	{
		this.panX = x;
		this.panY = y;
	}

	/**
	 * @return true if this camera can pan in the x direction
	 */
	public boolean isXPannable()
	{
		return this.panX;
	}

	/**
	 * @return true if this camera can pan in the y direction
	 */
	public boolean isYPannable()
	{
		return this.panY;
	}
}

package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;
import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.ILabelStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle;

public abstract class Graphics implements IGraphics
{
	private Matrix33 viewMatrix = new Matrix33();
	private Matrix33 projectionMatrix = new Matrix33();
	private Viewport viewport = new Viewport();

	protected Matrix33 objectToScreenMatrix = new Matrix33();
	protected Matrix33 screenToObjectMatrix = new Matrix33();

	private Box2D screenBounds = new Box2D();
	
	protected abstract void setPointSize(double pointSize);
	protected abstract void setLineWidth(double d);
	protected abstract void setStrokeStyle(String style);
	protected abstract void setFillStyle(String style);

	private static Matrix33 createProjectionMatrix(double left, double right, double bottom, double top)
	{
		Matrix33 m = new Matrix33();

		// From OpenGL documentation.
		double tx = (-(right + left) / (right - left));
		double ty = (-(top + bottom) / (top - bottom));

		m.set(0, 0, 2 / (right - left));
		m.set(1, 1, 2 / (top - bottom));

		m.setTranslationX(tx);
		m.setTranslationY(ty);

		return m;
	}

	@Override
	public void setSize(int width, int height)
	{
		viewport.setX(0);
		viewport.setY(0);
		viewport.setWidth(width);
		viewport.setHeight(height);

		projectionMatrix = createProjectionMatrix(0, width, 0, height);

		updateMatrix();
	}

	@Override
	public int getWidth()
	{
		return viewport.getWidth();
	}

	@Override
	public int getHeight()
	{
		return viewport.getHeight();
	}

	@Override
	public Matrix33 getObjectToScreenMatrix()
	{
		return objectToScreenMatrix;
	}

	@Override
	public Matrix33 getScreenToObjectMatrix()
	{
		return screenToObjectMatrix;
	}

	/**
	 * Set the view matrix
	 */
	@Override
	public void setViewMatrix(Matrix33 matrix)
	{
		this.viewMatrix = matrix;

		updateMatrix();
	}

	protected void updateMatrix()
	{
		objectToScreenMatrix = viewMatrix.multiply(projectionMatrix.multiply(viewport
				.computeWindowMatrix()));

		try
		{
			screenToObjectMatrix = objectToScreenMatrix.inverse();
		}
		catch(Exception e)
		{
			screenToObjectMatrix = new Matrix33();
		}

		Vector2 min = new Vector2(0, 0);
		Vector2 max = new Vector2(getWidth(), getHeight());
		min = screenToObjectMatrix.transform(min);
		max = screenToObjectMatrix.transform(max);

		screenBounds.setMin(min);
		screenBounds.setMax(max);
	}

	/**
	 * Get the view matrix.
	 */
	@Override
	public Matrix33 getViewMatrix()
	{
		return viewMatrix;
	}

	/**
	 * Check to see if the given bounding box is visible.
	 */
	@Override
	public Boolean isCulled(Box2D bbox)
	{
		if(!bbox.valid())
			return false;

		return !screenBounds.intersects(bbox);
	}
	@Override
	public void setStyle(IBranchStyle style)
	{
		try
		{
			if(style != null)
			{
				if(style.getStrokeColor() != null)
				{
					setStrokeStyle(style.getStrokeColor());
				}
	
				if(!Double.isNaN(style.getLineWidth()))
				{
					setLineWidth(style.getLineWidth());
				}
			}
		}
		catch(Exception e)
		{
			setStrokeStyle(Defaults.LINE_COLOR);
			setLineWidth(1.0);
		}
	}
	@Override
	public void setStyle(IGlyphStyle style)
	{
		try
		{
			if(style != null)
			{
				if(style.getFillColor() != null)
				{
					setFillStyle(style.getFillColor());
				}
	
				if(style.getStrokeColor() != null)
				{
					setStrokeStyle(style.getStrokeColor());
				}
	
				if(!Double.isNaN(style.getLineWidth()))
				{
					setLineWidth(style.getLineWidth());
				}
			}
		}
		catch(Exception e)
		{
			setFillStyle(Defaults.TRIANGLE_FILL_COLOR);
			setStrokeStyle(Defaults.TRIANGLE_OUTLINE_COLOR);
			setLineWidth(1.0);
		}
	}
	@Override
	public void setStyle(ILabelStyle style)
	{
		try
		{
			if(style != null)
			{
				if(style.getColor() != null)
				{
					setFillStyle(style.getColor());
				}
			}
		}
		catch(Exception e)
		{
			setFillStyle(Defaults.TEXT_COLOR);
		}
	}
	@Override
	public void setStyle(INodeStyle style)
	{
		try
		{
			if(style != null)
			{
				if(style.getColor() != null)
				{
					setFillStyle(style.getColor());
					setStrokeStyle(style.getColor());
				}
	
				if(!Double.isNaN(style.getPointSize()))
				{
					setPointSize(style.getPointSize());
				}
			}
		}
		catch(Exception e)
		{
			setFillStyle(Defaults.LINE_COLOR);
			setStrokeStyle(Defaults.LINE_COLOR);
			setLineWidth(1.0);
		}
	}
	
	
}

package org.iplantc.phyloviewer.shared.scene;

import java.util.ArrayList;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

public class CompositeDrawable extends Drawable
{
	private ArrayList<Drawable> drawables;
	private int type = 0;
	
	public CompositeDrawable()
	{
		drawables = new ArrayList<Drawable>();
	}

	@Override
	public void draw(IGraphics graphics, IStyle style)
	{
		for (Drawable drawable : drawables)
		{
			drawable.draw(graphics, style);
		}
	}

	@Override
	public int getDrawableType()
	{
		return type;
	}
	
	/**
	 * Add a drawable to this composite.  Note: add order is draw order.
	 * @param d the drawable
	 * @return true if successfully added
	 */
	public boolean add(Drawable d) 
	{
		boolean success = drawables.add(d);
		this.type |= d.getDrawableType();
		
		Box2D bounds = d.getBoundingBox();
		if(bounds != null)
		{
			this.boundingBox.expandBy(bounds);
		}
		
		return success;
	}
	
	public void updateBoundingBox()
	{
		this.boundingBox = new Box2D();
		
		for (Drawable d : drawables)
		{
			Box2D bounds = d.getBoundingBox();
			if(bounds != null)
			{
				this.boundingBox.expandBy(bounds);
			}
		}
	}

	@Override
	public boolean intersect(Vector2 position, double distanceSquared)
	{
		Box2D pickBox = new Box2D(position, position);
		pickBox.expandBy(distanceSquared);
		return this.boundingBox.intersects(pickBox);
	}
	
	
}

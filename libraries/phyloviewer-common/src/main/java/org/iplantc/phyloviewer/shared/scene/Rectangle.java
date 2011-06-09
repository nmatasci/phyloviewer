package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;

public class Rectangle extends Polygon
{
	
	public Rectangle(Box2D box)
	{
		super(4);
		this.setBoundingBox(box);
	}
	
	public Rectangle()
	{
		super(4);
	}
	
	@Override
	public void setBoundingBox(Box2D boundingBox)
	{
		this.boundingBox = boundingBox;
		
		if (boundingBox != null)
		{
			vertices[0] = boundingBox.getMin();
			vertices[1] = new Vector2(boundingBox.getMax().getX(), boundingBox.getMin().getY());
			vertices[2] = boundingBox.getMax();
			vertices[3] = new Vector2(boundingBox.getMin().getX(), boundingBox.getMax().getY());
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

package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

/**
 * A Drawable for text enclosed in a rectangular border.
 */
public class Label extends CompositeDrawable
{
	private Text text;
	private Rectangle rect;
	private double leftMargin = 0.01;
	private double rightMargin = 0.01;
	private double topMargin = 0.01;
	private double bottomMargin = 0;
	
	/**
	 * @param text the label text
	 * @param position the left side of the label, at the text baseline
	 */
	public Label(String text, Vector2 position)
	{
		this.text = new Text(text, position, new Vector2(leftMargin, 0));
		this.rect = new Rectangle();
		//note: will update rect in draw, where we can calculate actual text bounds
		
		this.add(this.rect);
		this.add(this.text);
	}

	@Override
	public void draw(IGraphics graphics, IStyle style)
	{
		if (rect.boundingBox == null) 
		{
			Box2D textBounds = graphics.calculateBoundingBox(text);
			
			Vector2 min = textBounds.getMin();
			min.setX(min.getX() - leftMargin);
			min.setY(min.getY() - topMargin);
			
			Vector2 max = textBounds.getMax();
			max.setX(max.getX() + rightMargin);
			max.setY(max.getY() + bottomMargin);
			
			rect.setBoundingBox(textBounds);
			
			this.updateBoundingBox();
		}
		
		super.draw(graphics, style);
	}
}

package org.iplantc.phyloviewer.client.tree.viewer.render.svg;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.Graphics;
import org.iplantc.phyloviewer.shared.scene.Text;

/**
 * SVGGraphics... embrace the redundancy.
 */
public class SVGGraphics extends Graphics
{
	static final String PROLOG = "<?xml version=\"1.0\" standalone=\"no\"?><!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">";
	static final String[] nofill = {"fill", "none"};
	
	private StringBuilder svg;
	
	private String[] fill = {"fill", ""};
	private String[] stroke = {"stroke", ""};
	private Object[] lineWidth = {"stroke-width", 1.0};
	private String[] fontFamily = {"font-family", "Arial Unicode MS, Arial, sans-serif"};
	private String[] fontSize = {"font-size", "small"};
	private List<Box2D> drawnTextExtents = new ArrayList<Box2D>();
	
	protected double pointSize;

	public SVGGraphics()
	{
		init();
	}
	
	@Override
	public Box2D calculateBoundingBox(Text text)
	{
		return text.getBoundingBox(); //returning existing 'dirty' box for now.  FIXME
	}

	@Override
	public void clear()
	{
		init();
	}

	@Override
	public void drawArc(Vector2 center, double radius, double startAngle, double endAngle)
	{
		center = objectToScreenMatrix.transform(center);
		radius = radius * objectToScreenMatrix.getScaleY();

		PathBuilder pathBuilder = new PathBuilder();
		pathBuilder.circularArc(center.getX(), center.getY(), radius, startAngle, endAngle, false);
		Object[] path = {"d", pathBuilder.toString()};
		openAndCloseElement("path", path, stroke, lineWidth, nofill);
	}

	@Override
	public void drawLineStrip(Vector2[] vertices)
	{
		if(vertices.length < 2)
		{
			return;
		}

		PathBuilder pathBuilder = new PathBuilder();
		
		Vector2 vector = objectToScreenMatrix.transform(vertices[0]);
		pathBuilder.moveTo(vector);
		
		for(int i = 1;i < vertices.length;++i)
		{
			vector = objectToScreenMatrix.transform(vertices[i]);
			pathBuilder.lineTo(vector);
		}
		
		Object[] path = {"d", pathBuilder.toString()};
		openAndCloseElement("path", path, stroke, lineWidth, nofill);
	}

	@Override
	public void drawPoint(Vector2 position)
	{
		Vector2 p = objectToScreenMatrix.transform(position);
		
		Object[] cx = {"cx", p.getX()};
		Object[] cy = {"cy", p.getY()};
		Object[] r = {"r", pointSize / 2.0};
		
		openAndCloseElement("circle", cx, cy, r, fill);
	}

	@Override
	public void drawPolygon(Vector2[] vertices)
	{
		if(vertices.length < 3)
		{
			return;
		}
		
		PathBuilder pathBuilder = new PathBuilder();
		
		Vector2 vector = objectToScreenMatrix.transform(vertices[0]);
		pathBuilder.moveTo(vector);
		
		for(int i = 1;i < vertices.length;++i)
		{
			vector = objectToScreenMatrix.transform(vertices[i]);
			pathBuilder.lineTo(vector);
		}
		
		pathBuilder.close();
		
		Object[] path = {"d", pathBuilder.toString()};
		openAndCloseElement("path", path, stroke, fill, lineWidth);
	}

	@Override
	public void drawText(Vector2 position, Vector2 offset, String text, double angle)
	{
		if(text == null || text.equals(""))
		{
			return;
		}
	
		Vector2 p = objectToScreenMatrix.transform(position);
		p = p.add(offset);
		
		float height = 10; //FIXME get actual height
		double width = 10; //FIXME get actual width
		Box2D bbox = new Box2D(new Vector2(p.getX(), p.getY() - height / 2), new Vector2(p.getX() + width, p.getY() + height / 2));

		Object[] display = null;
		for(Box2D box : drawnTextExtents)
		{
			if(box.intersects(bbox))
			{
				display = new Object[] {"display", "none"};
			}
		}
		
		if (display == null)
		{
			drawnTextExtents.add(bbox);
		}
		
		Object[] x = {"x", p.getX()};
		Object[] y = {"y", p.getY()};
		Object[] transform = null;
		
		if (angle != 0)
		{
			double degrees = angle * 180 / Math.PI;
			String string = "rotate(" + degrees + ", " + p.getX() + "," + p.getY() + ")";
			//FIXME flip labels on left.  Needs a way to get text bounds in order to rotate about center.
			transform = new Object[] {"transform", string};
		}
		
		open("text", x, y, fill, fontFamily, fontSize, transform, display);
		svg.append(text);
		close("text");
		

	}

	@Override
	public void drawWedge(Vector2 center, Vector2 peak, double radius, double startAngle, double endAngle)
	{
		center = objectToScreenMatrix.transform(center);
		peak = objectToScreenMatrix.transform(peak);
		radius = radius * objectToScreenMatrix.getScaleY();
		
		PathBuilder pathBuilder = new PathBuilder();
		pathBuilder.moveTo(peak);
		pathBuilder.circularArc(center.getX(), center.getY(), radius, startAngle, endAngle, false);
		pathBuilder.close();
		
		Object[] path = {"d", pathBuilder.toString()};
		openAndCloseElement("path", path, stroke, fill, lineWidth);
	}

	@Override
	public String toString()
	{
		return svg.toString() + "</svg>";
	}
	
	@Override
	public void setFillStyle(String style)
	{
		fill[1] = style;
	}
	
	@Override
	public void setStrokeStyle(String style)
	{
		stroke[1] = style;
	}

	@Override
	public void setLineWidth(double d)
	{
		lineWidth[1] = d;
	}

	@Override
	public void setPointSize(double pointSize)
	{
		this.pointSize = pointSize;
	}

	public void setFontFamily(String fontFamily)
	{
		this.fontFamily[1] = fontFamily;
	}

	public String getFontFamily()
	{
		return fontFamily[1];
	}

	public void setFontSize(String fontSize)
	{
		this.fontSize[1] = fontSize;
	}
	
	public void setFontSize(double fontSize)
	{
		this.fontSize[1] = String.valueOf(fontSize);
	}

	public String getFontSize()
	{
		return fontSize[1];
	}

	private void init()
	{
		svg = new StringBuilder();
		svg.append(PROLOG);
		svg.append("<svg version='1.1' xmlns='http://www.w3.org/2000/svg'>");
	}
	
	private void open(String element, Object[] ... attributes)
	{
		svg.append("<").append(element);

		for (Object[] i : attributes)
		{
			if (i != null)
			{
				Object name = i[0];
				Object value = i[1];
				svg.append(" ").append(name).append("='").append(value).append("'");
			}
		}
		
		svg.append(">");
	}
	
	private void close(String element)
	{
		svg.append("</").append(element).append('>');
	}
	
	private void openAndCloseElement(String element, Object[] ... attributes)
	{
		open(element, attributes);
		close(element);
	}
}

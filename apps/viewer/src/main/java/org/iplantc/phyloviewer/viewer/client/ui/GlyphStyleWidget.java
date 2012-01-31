package org.iplantc.phyloviewer.viewer.client.ui;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

/**
 * An AbstractElementStyleWidget implementation that edits the IGlyphStyle.
 */
public class GlyphStyleWidget extends AbstractElementStyleWidget
{
	private static final int LABEL_COLUMN = 0;
	private static final int WIDGET_COLUMN = 1;
	
	private static final int FILL_COLOR_ROW = 0;
	private static final int STROKE_COLOR_ROW = 1;
	private static final int WIDTH_ROW = 2;
	
	private SingleValueChangeHandler<String> fillColorUpdater = new SingleValueChangeHandler<String>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node, true).getGlyphStyle().setFillColor(event.getValue());
			}
		}
	};
	
	private SingleValueChangeHandler<String> strokeColorUpdater = new SingleValueChangeHandler<String>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node, true).getGlyphStyle().setStrokeColor(event.getValue());
			}
		}
	};
	
	private SingleValueChangeHandler<Double> lineWidthUpdater = new SingleValueChangeHandler<Double>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<Double> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node, true).getGlyphStyle().setLineWidth(event.getValue());
			}
		}
	};

	/**
	 * Creates a new GlyphStyleWidget that edits styles in the given document
	 */
	public GlyphStyleWidget(IDocument document)
	{
		super(document);
		
		setText(FILL_COLOR_ROW, LABEL_COLUMN, "Glyph fill color:");
		setFillColorWidget(new TextBox());
		
		setText(STROKE_COLOR_ROW, LABEL_COLUMN, "Glyph stroke color:");
		setStrokeColorWidget(new TextBox());
		
		setText(WIDTH_ROW, LABEL_COLUMN, "Glyph outline width:");
		setLineWidthWidget(new DoubleBox());
	}
	
	/**
	 * Set the fill color editing widget.  Default is a TextBox.
	 */
	public void setFillColorWidget(HasValue<String> widget)
	{
		fillColorUpdater.attachTo(widget);
		setWidget(FILL_COLOR_ROW, WIDGET_COLUMN, widget);
	}
	
	/**
	 * Set the stroke color editing widget.  Default is a TextBox.
	 */
	public void setStrokeColorWidget(HasValue<String> widget)
	{
		strokeColorUpdater.attachTo(widget);
		setWidget(STROKE_COLOR_ROW, WIDGET_COLUMN, widget);
	}
	
	/**
	 * Set the line width editing widget.  Default is a DoubleBox.
	 */
	public void setLineWidthWidget(HasValue<Double> widget)
	{
		lineWidthUpdater.attachTo(widget);
		setWidget(WIDTH_ROW, WIDGET_COLUMN, widget);
	}
	
	@SuppressWarnings("unchecked")
	public HasValue<String> getFillColorWidget()
	{
		return (HasValue<String>)getWidget(FILL_COLOR_ROW, WIDGET_COLUMN);
	}
	
	@SuppressWarnings("unchecked")
	public HasValue<String> getStrokeColorWidget()
	{
		return (HasValue<String>)getWidget(STROKE_COLOR_ROW, WIDGET_COLUMN);
	}
	
	@SuppressWarnings("unchecked")
	public HasValue<Double> getLineWidthWidget()
	{
		return (HasValue<Double>)getWidget(WIDTH_ROW, WIDGET_COLUMN);
	}
	
	@Override
	public void updateValues(INode node)
	{
		IStyle style = getStyle(node, false);
		if (style == null)
		{
			style = Defaults.NULL_STYLE;
		}
		
		IGlyphStyle glyphStyle = style.getGlyphStyle();
		String fillColor = glyphStyle.getFillColor();
		getFillColorWidget().setValue(fillColor, true);
		
		String strokeColor = glyphStyle.getStrokeColor();
		getStrokeColorWidget().setValue(strokeColor, true);
		
		double lineWidth = glyphStyle.getLineWidth();
		getLineWidthWidget().setValue(lineWidth, true);
	}
}

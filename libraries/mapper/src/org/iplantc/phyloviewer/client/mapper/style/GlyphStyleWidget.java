package org.iplantc.phyloviewer.client.mapper.style;

import org.iplantc.phyloviewer.shared.render.style.GlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.DoubleBox;

public class GlyphStyleWidget extends Composite
{

	private static GlyphStyleWidgetUiBinder uiBinder = GWT.create(GlyphStyleWidgetUiBinder.class);
	@UiField TextBox strokeColorField;
	@UiField TextBox fillColorField;
	@UiField DoubleBox lineWidthField;

	interface GlyphStyleWidgetUiBinder extends UiBinder<Widget,GlyphStyleWidget>
	{
	}

	public GlyphStyleWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public IGlyphStyle getStyle()
	{
		IGlyphStyle style = new GlyphStyle();
		style.setFillColor(fillColorField.getValue());
		style.setStrokeColor(strokeColorField.getValue());
		
		Double width = lineWidthField.getValue();
		if (width != null)
		{
			style.setLineWidth(width);
		}
		
		return style;
	}

	public void clear()
	{
		strokeColorField.setValue(null);
		fillColorField.setValue(null);
		lineWidthField.setValue(null);
	}

}

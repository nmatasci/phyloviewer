package org.iplantc.phyloviewer.client.mapper.style;

import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.Style;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class StyleWidget extends Composite
{
	private static StyleWidgetUiBinder uiBinder = GWT.create(StyleWidgetUiBinder.class);
	@UiField NodeStyleWidget nodeWidget;
	@UiField BranchStyleWidget branchWidget;
	@UiField LabelStyleWidget labelWidget;
	@UiField GlyphStyleWidget glyphWidget;

	interface StyleWidgetUiBinder extends UiBinder<Widget,StyleWidget>
	{
	}

	public StyleWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	public StyleWidget(String firstName)
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	public IStyle getStyle()
	{
		return new Style(null, 
				nodeWidget.getStyle(), 
				labelWidget.getStyle(), 
				glyphWidget.getStyle(), 
				branchWidget.getStyle());
	}

	public void clear()
	{
		nodeWidget.clear();
		branchWidget.clear();
		labelWidget.clear();
		glyphWidget.clear();
	}
}

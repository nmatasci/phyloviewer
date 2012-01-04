package org.iplantc.phyloviewer.client.mapper;

import org.iplantc.phyloviewer.shared.render.style.ILabelStyle;
import org.iplantc.phyloviewer.shared.render.style.LabelStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;

public class LabelStyleWidget extends Composite
{

	private static LabelStyleWidgetUiBinder uiBinder = GWT.create(LabelStyleWidgetUiBinder.class);
	@UiField TextBox colorField;

	interface LabelStyleWidgetUiBinder extends UiBinder<Widget,LabelStyleWidget>
	{
	}

	public LabelStyleWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public ILabelStyle getStyle()
	{
		ILabelStyle style = new LabelStyle(colorField.getValue());
		return style;
	}

	public void clear()
	{
		colorField.setValue("");
	}

}

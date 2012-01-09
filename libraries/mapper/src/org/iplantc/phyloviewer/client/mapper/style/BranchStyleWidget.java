package org.iplantc.phyloviewer.client.mapper.style;

import org.iplantc.phyloviewer.shared.render.style.BranchStyle;
import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BranchStyleWidget extends Composite
{

	private static BranchStyleWidgetUiBinder uiBinder = GWT.create(BranchStyleWidgetUiBinder.class);
	@UiField TextBox colorField;
	@UiField DoubleBox widthField;

	interface BranchStyleWidgetUiBinder extends UiBinder<Widget,BranchStyleWidget>
	{
	}

	public BranchStyleWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public IBranchStyle getStyle()
	{
		BranchStyle style = new BranchStyle();
		style.setStrokeColor(colorField.getValue());
		
		Double width = widthField.getValue();
		if (width != null)
		{
			style.setLineWidth(width);
		}
		
		return style;
	}

	public void clear()
	{
		colorField.setValue(null);
		widthField.setValue(null);
	}

}

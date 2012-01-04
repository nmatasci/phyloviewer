package org.iplantc.phyloviewer.client.mapper;

import org.iplantc.phyloviewer.shared.render.style.INodeStyle;
import org.iplantc.phyloviewer.shared.render.style.NodeStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class NodeStyleWidget extends Composite
{
	interface NodeStyleWidgetUiBinder extends UiBinder<Widget,NodeStyleWidget>{}
	private static NodeStyleWidgetUiBinder uiBinder = GWT.create(NodeStyleWidgetUiBinder.class);

	@UiField HasValue<String> colorField;
	@UiField HasValue<Double> sizeField;
	@UiField ListBox shapeField;

	public NodeStyleWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	public INodeStyle getStyle()
	{
		NodeStyle style = new NodeStyle();
		style.setColor(colorField.getValue());
		
		Double size = sizeField.getValue();
		if (size != null)
		{
			style.setPointSize(size);
		}
		
		//TODO handle shape field. may want to change it to a valuebox with the Shape enum values in it.
		
		return style;
	}

	public void clear()
	{
		colorField.setValue(null);
		sizeField.setValue(null);
		shapeField.setSelectedIndex(0);
	}
}

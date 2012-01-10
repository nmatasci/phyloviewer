package org.iplantc.phyloviewer.client.mapper.style;

import org.iplantc.phyloviewer.shared.render.style.INodeStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle.Shape;
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
		String color = colorField.getValue();
		
		Double size = sizeField.getValue();
		if (size == null)
		{
			size = Double.NaN;
		}

		Shape shape = null;
		String shapeText = shapeField.getItemText(shapeField.getSelectedIndex());
		if (shapeText != null && !shapeText.isEmpty())
		{
			shape = Shape.valueOf(shapeText);
		}
		
		NodeStyle style = new NodeStyle(color, size, shape);
		return style;
	}

	public void clear()
	{
		colorField.setValue(null);
		sizeField.setValue(null);
		shapeField.setSelectedIndex(0);
	}
}

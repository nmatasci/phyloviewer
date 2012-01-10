package org.iplantc.phyloviewer.client.mapper.style;

import java.util.Arrays;
import java.util.List;

import org.iplantc.phyloviewer.shared.render.style.INodeStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle.Shape;
import org.iplantc.phyloviewer.shared.render.style.NodeStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class NodeStyleWidget extends Composite
{
	interface NodeStyleWidgetUiBinder extends UiBinder<Widget,NodeStyleWidget>{}
	private static NodeStyleWidgetUiBinder uiBinder = GWT.create(NodeStyleWidgetUiBinder.class);

	@UiField HasValue<String> colorField;
	@UiField HasValue<Double> sizeField;
	@UiField ValueListBox<Shape> shapeField;

	public NodeStyleWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
		shapeField.setAcceptableValues(getShapes());
	}

	public INodeStyle getStyle()
	{
		String color = colorField.getValue();
		
		Double size = sizeField.getValue();
		if (size == null)
		{
			size = Double.NaN;
		}

		Shape shape = shapeField.getValue();
		
		NodeStyle style = new NodeStyle(color, size, shape);
		return style;
	}

	public void clear()
	{
		colorField.setValue(null);
		sizeField.setValue(null);
		shapeField.setValue(null);
	}
	
	private List<Shape> getShapes()
	{
		Shape[] allShapes = INodeStyle.Shape.values();
		return Arrays.asList(allShapes);
	}
	
	@UiFactory ValueListBox<Shape> createShapeField()
	{
		Renderer<Shape> shapeRenderer = new AbstractRenderer<INodeStyle.Shape>()
		{
			@Override
			public String render(Shape shape)
			{
				if (shape == null)
				{
					return "";
				}
				
				return shape.toString();
			}
		};
		
		return new ValueListBox<Shape>(shapeRenderer);
	}
}

package org.iplantc.phyloviewer.viewer.client.ui;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

/**
 * An AbstractElementStyleWidget implementation that edits the INodeStyle.
 */
public class NodeStyleWidget extends AbstractElementStyleWidget
{
	private static final int LABEL_COLUMN = 0;
	private static final int WIDGET_COLUMN = 1;
	
	private static final int COLOR_ROW = 0;
	private static final int SIZE_ROW = 1;

	private SingleValueChangeHandler<String> colorUpdater = new SingleValueChangeHandler<String>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node, true).getNodeStyle().setColor(event.getValue());
			}
		}
	};
	
	private SingleValueChangeHandler<Double> sizeUpdater = new SingleValueChangeHandler<Double>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<Double> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node, true).getNodeStyle().setPointSize(event.getValue());
			}
		}
	};
	
	/**
	 * Creates a new NodeStyleWidget that edits styles in the given document
	 */
	public NodeStyleWidget(IDocument document)
	{
		super(document);
		setText(COLOR_ROW, LABEL_COLUMN, "Node color:");
		setColorWidget(new TextBox());
		
		setText(SIZE_ROW, LABEL_COLUMN, "Node size:");
		setSizeWidget(new DoubleBox());
	}
	
	/**
	 * Set the color editing widget.  Default is a TextBox.
	 */
	public void setColorWidget(HasValue<String> widget)
	{
		colorUpdater.attachTo(widget);
		setWidget(COLOR_ROW, WIDGET_COLUMN, widget);
	}
	
	/**
	 * Set the node size editing widget.  Default is a DoubleBox
	 */
	public void setSizeWidget(HasValue<Double> widget)
	{
		sizeUpdater.attachTo(widget);
		setWidget(SIZE_ROW, WIDGET_COLUMN, widget);
	}

	@SuppressWarnings("unchecked")
	public HasValue<String> getColorWidget()
	{
		return (HasValue<String>)getWidget(COLOR_ROW, WIDGET_COLUMN);
	}

	@SuppressWarnings("unchecked")
	public HasValue<Double> getSizeWidget()
	{
		return (HasValue<Double>)getWidget(SIZE_ROW, WIDGET_COLUMN);
	}

	@Override
	public void updateValues(INode node)
	{
		IStyle style = getStyle(node, false);
		if (style == null)
		{
			style = Defaults.NULL_STYLE;
		}
		
		INodeStyle nodeStyle = style.getNodeStyle();
		String color = nodeStyle.getColor();
		getColorWidget().setValue(color, true);
		
		double pointSize = nodeStyle.getPointSize();
		getSizeWidget().setValue(pointSize, true);
	}
}

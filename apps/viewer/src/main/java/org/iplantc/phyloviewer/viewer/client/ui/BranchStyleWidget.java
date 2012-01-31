package org.iplantc.phyloviewer.viewer.client.ui;

import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

/**
 * An AbstractElementStyleWidget implementation that edits the IBranchStyle.
 */
public class BranchStyleWidget extends AbstractElementStyleWidget
{
	private static final int LABEL_COLUMN = 0;
	private static final int WIDGET_COLUMN = 1;
	
	private static final int COLOR_ROW = 0;
	private static final int WIDTH_ROW = 1;
	
	private SingleValueChangeHandler<String> colorUpdater = new SingleValueChangeHandler<String>()
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> event)
		{
			for(INode node : getNodes())
			{
				getStyle(node, true).getBranchStyle().setStrokeColor(event.getValue());
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
				getStyle(node, true).getBranchStyle().setLineWidth(event.getValue());
			}
		}
	};
	
	/**
	 * Creates a new BranchStyleWidget that edits styles in the given document
	 */
	public BranchStyleWidget(IDocument document)
	{
		super(document);
		setText(COLOR_ROW, LABEL_COLUMN, "Branch color:");
		setStrokeColorWidget(new TextBox());
		
		setText(WIDTH_ROW, LABEL_COLUMN, "Branch width:");
		setLineWidthWidget(new DoubleBox());
	}
	
	/**
	 * Set the stroke color editing widget.  Default is a TextBox.
	 */
	public void setStrokeColorWidget(HasValue<String> widget)
	{
		colorUpdater.attachTo(widget);
		setWidget(COLOR_ROW, WIDGET_COLUMN, widget);
	}
	
	/**
	 * Set the line width editing widget.  Default is a DoubleBox
	 */
	public void setLineWidthWidget(HasValue<Double> widget)
	{
		lineWidthUpdater.attachTo(widget);
		setWidget(WIDTH_ROW, WIDGET_COLUMN, widget);
	}
	
	@SuppressWarnings("unchecked")
	public HasValue<String> getStrokeColorWidget()
	{
		return (HasValue<String>)getWidget(COLOR_ROW, WIDGET_COLUMN);
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
		
		IBranchStyle branchStyle = style.getBranchStyle();
		String color = branchStyle.getStrokeColor();
		getStrokeColorWidget().setValue(color, true);
		
		double pointSize = branchStyle.getLineWidth();
		getLineWidthWidget().setValue(pointSize, true);
	}
}

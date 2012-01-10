package org.iplantc.phyloviewer.client.mapper.filter;

import java.util.ArrayList;

import org.iplantc.phyloviewer.shared.model.metadata.DoubleFilter;
import org.iplantc.phyloviewer.shared.model.metadata.ValueFilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class NumberFilterWidget extends ValueFilterWidget<Double>
{

	private static NumberFilterWidgetUiBinder uiBinder = GWT.create(NumberFilterWidgetUiBinder.class);
	@UiField ValueListBox<DoubleFilter> filterField;
	@UiField DoubleBox minField;
	@UiField DoubleBox maxField;

	interface NumberFilterWidgetUiBinder extends UiBinder<Widget,NumberFilterWidget>
	{
	}

	public NumberFilterWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
		reset();
	}

	@Override
	public ValueFilter<Double> getSelectedFilter()
	{
		DoubleFilter filter = filterField.getValue();
		Double min = minField.getValue();
		Double max = maxField.getValue();
		if (filter != null)
		{
			if (min != null)
			{
				filter.setMinValue(min);
			}
			
			if (max != null)
			{
				filter.setMaxValue(max);
			}
		}
		
		return filter;
	}

	@Override
	public void reset()
	{
		filterField.setValue(null);
		ArrayList<DoubleFilter> filters = new ArrayList<DoubleFilter>();
		filters.add(new DoubleFilter());
		filterField.setAcceptableValues(filters);
		
		minField.setValue(null);
		maxField.setValue(null);
	}

}

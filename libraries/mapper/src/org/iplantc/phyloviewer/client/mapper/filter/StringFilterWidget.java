package org.iplantc.phyloviewer.client.mapper.filter;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.shared.model.metadata.StringFilter;
import org.iplantc.phyloviewer.shared.model.metadata.StringFilter.ContainsFilter;
import org.iplantc.phyloviewer.shared.model.metadata.StringFilter.EndsWithFilter;
import org.iplantc.phyloviewer.shared.model.metadata.StringFilter.EqualsFilter;
import org.iplantc.phyloviewer.shared.model.metadata.StringFilter.StartsWithFilter;
import org.iplantc.phyloviewer.shared.model.metadata.ValueFilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class StringFilterWidget extends ValueFilterWidget<String>
{
	private static StringFilterWidgetUiBinder uiBinder = GWT.create(StringFilterWidgetUiBinder.class);

	interface StringFilterWidgetUiBinder extends UiBinder<Widget,StringFilterWidget>
	{
	}
	
	@UiField ValueListBox<ValueFilter<String>> filterField;
	@UiField TextBox filterValueField;
	
	public StringFilterWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
		reset();
	}

	public static List<ValueFilter<String>> createFilters() 
	{
		List<ValueFilter<String>> filters = new ArrayList<ValueFilter<String>>();
		
		ValueFilter<String> filter = new EqualsFilter();
		filters.add(filter);
		
		filter = new StringFilter.Not(new EqualsFilter());
		filters.add(filter);
		
		filter = new ContainsFilter();
		filters.add(filter);
		
		filter = new StringFilter.Not(new ContainsFilter());
		filters.add(filter);
		
		filter = new StartsWithFilter();
		filters.add(filter);
		
		filter = new EndsWithFilter();
		filters.add(filter);
		
		return filters;
	}

	public void reset()
	{
		filterField.setAcceptableValues(createFilters());
		filterValueField.setText("");
	}
	
	public ValueFilter<String> getSelectedFilter()
	{
		String targetValue = filterValueField.getValue();
		ValueFilter<String> filter = filterField.getValue();
		((StringFilter)filter).setTargetValue(targetValue);
		
		return filter;
	}
}

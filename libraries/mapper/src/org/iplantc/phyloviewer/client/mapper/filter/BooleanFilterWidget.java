package org.iplantc.phyloviewer.client.mapper.filter;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.shared.model.metadata.BooleanFilter;
import org.iplantc.phyloviewer.shared.model.metadata.ValueFilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class BooleanFilterWidget extends ValueFilterWidget<Boolean>
{

	private static BooleanFilterWidgetUiBinder uiBinder = GWT.create(BooleanFilterWidgetUiBinder.class);
	private static final List<ValueFilter<Boolean>> filters;
	
	static {
		filters = new ArrayList<ValueFilter<Boolean>>();
		filters.add(new BooleanFilter(true));
		filters.add(new BooleanFilter(false));
	}

	interface BooleanFilterWidgetUiBinder extends UiBinder<Widget,BooleanFilterWidget>
	{
	}

	@UiField ValueListBox<ValueFilter<Boolean>> filterField;
	
	public BooleanFilterWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
		filterField.setAcceptableValues(filters);
	}

	@Override
	public ValueFilter<Boolean> getSelectedFilter()
	{
		return filterField.getValue();
	}

	@Override
	public void reset()
	{
		filterField.setValue(null);
	}

}

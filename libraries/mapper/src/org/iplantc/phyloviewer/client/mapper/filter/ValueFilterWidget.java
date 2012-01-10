package org.iplantc.phyloviewer.client.mapper.filter;

import java.util.List;

import org.iplantc.phyloviewer.shared.model.metadata.ValueFilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class ValueFilterWidget<I> extends Composite
{

	private static ValueFilterWidgetUiBinder uiBinder = GWT.create(ValueFilterWidgetUiBinder.class);

	@SuppressWarnings("rawtypes")
	interface ValueFilterWidgetUiBinder extends UiBinder<Widget,ValueFilterWidget>
	{
	}
	
	@UiField ValueListBox<ValueFilter<I>> filterField;
	@UiField TextBox filterValueField;
	
	private List<ValueFilter<I>> filters;
	private Renderer<ValueFilter<I>> filterRenderer = new AbstractRenderer<ValueFilter<I>>()
			{
				@Override
				public String render(ValueFilter<I> filter)
				{
					if (filter == null) {
						return "no filter";
					}
					
					return filter.toString();
				}
			};

	public ValueFilterWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * @return the currently selected value filter.  Default value is null, "no filter" is selected.
	 */
	public ValueFilter<I> getSelectedFilter()
	{
		return filterField.getValue();
	}
	
	public List<ValueFilter<I>> getFilters()
	{
		return this.filters;
	}
	
	public void setFilters(List<ValueFilter<I>> filters)
	{
		this.filters = filters;
		filterField.setAcceptableValues(getFilters());
	}
	
	public Renderer<ValueFilter<I>> getFilterRenderer()
	{
		return this.filterRenderer;
	}
	
	public void reset()
	{
		filterField.setValue(null);
	}
	
	@UiFactory ValueListBox<ValueFilter<I>> createFilterField()
	{
		ValueListBox<ValueFilter<I>> valueListBox = new ValueListBox<ValueFilter<I>>(getFilterRenderer());
		valueListBox.setAcceptableValues(getFilters()); //TODO should make sure there's an AllPassFilter and that it is the default selection
		
		return valueListBox;
	}
}

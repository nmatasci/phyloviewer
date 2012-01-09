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
import com.google.gwt.user.client.ui.ValueBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class ValueFilterWidget<I> extends Composite
{

	private static NodeFilterWidgetUiBinder uiBinder = GWT.create(NodeFilterWidgetUiBinder.class);

	@SuppressWarnings("rawtypes")
	interface NodeFilterWidgetUiBinder extends UiBinder<Widget,ValueFilterWidget>
	{
	}
	
	@UiField ValueBox<ValueFilter<I>> filterField;
	@UiField TextBox filterValueField;
	
	private List<ValueFilter<I>> filters;
	private Renderer<ValueFilter<I>> filterRenderer = new AbstractRenderer<ValueFilter<I>>()
			{
				@Override
				public String render(ValueFilter<I> filter)
				{
					return filter.getDescription();
				}
			};

	public ValueFilterWidget(List<ValueFilter<I>> filters)
	{
		this.filters = filters;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ValueFilter<I> getSelectedFilter()
	{
		return filterField.getValue();
	}
	
	public List<ValueFilter<I>> getFilters()
	{
		return this.filters;
	}
	
	public Renderer<ValueFilter<I>> getFilterRenderer()
	{
		return this.filterRenderer;
	}
	
	@UiFactory ValueListBox<ValueFilter<I>> createFilterField()
	{
		ValueListBox<ValueFilter<I>> valueListBox = new ValueListBox<ValueFilter<I>>(getFilterRenderer());
		valueListBox.setAcceptableValues(getFilters()); //TODO should make sure there's an AllPassFilter and that it is the default selection
		
		return valueListBox;
	}
}

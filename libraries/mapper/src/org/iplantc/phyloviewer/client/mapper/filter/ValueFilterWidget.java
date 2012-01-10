package org.iplantc.phyloviewer.client.mapper.filter;

import org.iplantc.phyloviewer.shared.model.metadata.ValueFilter;

import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ValueListBox;

public abstract class ValueFilterWidget<I> extends Composite
{
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
			
	/**
	 * @return the currently selected value filter.  Default value is null, "no filter" is selected.
	 */
	public abstract ValueFilter<I> getSelectedFilter();
	
	public Renderer<ValueFilter<I>> getFilterRenderer()
	{
		return this.filterRenderer;
	}
	
	public abstract void reset();
	
	@UiFactory ValueListBox<ValueFilter<I>> createFilterField()
	{
		ValueListBox<ValueFilter<I>> valueListBox = new ValueListBox<ValueFilter<I>>(getFilterRenderer());
		return valueListBox;
	}
}

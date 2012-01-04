package org.iplantc.phyloviewer.client.mapper;

import org.iplantc.phyloviewer.shared.model.metadata.ValueForNode;
import org.iplantc.phyloviewer.shared.render.style.FilteredStyleMap;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class SavedMapperView extends Composite
{

	private static SavedMapperViewUiBinder uiBinder = GWT.create(SavedMapperViewUiBinder.class);
	@UiField HorizontalPanel panel;
	@UiField InlineLabel filterField;
	@UiField InlineLabel styleField;

	interface SavedMapperViewUiBinder extends UiBinder<Widget,SavedMapperView>
	{
	}

	public SavedMapperView(FilteredStyleMap map)
	{
		initWidget(uiBinder.createAndBindUi(this));
		ValueForNode<Boolean> filter = map.getFilter();
		filterField.setText(filter.toString());
		
		IStyle style = map.getPassStyle();
		styleField.setText(style.toString());
	}

}

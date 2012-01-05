package org.iplantc.phyloviewer.client.mapper;

import org.iplantc.phyloviewer.client.mapper.mocks.MockAnnotationFilter;
import org.iplantc.phyloviewer.shared.model.metadata.AllPassFilter;
import org.iplantc.phyloviewer.shared.model.metadata.MetadataInfo;
import org.iplantc.phyloviewer.shared.model.metadata.MetadataProperty;
import org.iplantc.phyloviewer.shared.model.metadata.NumericMetadataProperty;
import org.iplantc.phyloviewer.shared.model.metadata.ValueForNode;
import org.iplantc.phyloviewer.shared.render.style.ChainedStyleMap;
import org.iplantc.phyloviewer.shared.render.style.FilteredStyleMap;
import org.iplantc.phyloviewer.shared.render.style.FilteredStyleMapImpl;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DataMapper extends Composite
{
	//TODO: add a checkbox to show/hide the second style widget.  When it is shown, keep the same tab showing on both StyleWidgets, and add some validation to check if both widgets have a value for any edited fields.
	//TODO: add remove or undo button for saved styles
	
	interface DataMapperUiBinder extends UiBinder<Widget,DataMapper> {}
	private static DataMapperUiBinder uiBinder = GWT.create(DataMapperUiBinder.class);
	@UiField PropertyListBox propertiesField;
	@UiField TextBox datatypeField;
	@UiField TextBox rangeField;
	@UiField ListBox filterField;
	@UiField TextBox filterValueField;
	@UiField Label minOrOnlyStyleLabel;
	@UiField Label maxStyleLabel;
	@UiField StyleWidget styleWidget1;
	@UiField StyleWidget styleWidget2;
	@UiField Button saveButton;
	@UiField Panel savedPanel;
	
	private MetadataInfo info;
	private ChainedStyleMap styles;

	public DataMapper(MetadataInfo info)
	{
		initWidget(uiBinder.createAndBindUi(this));
		this.info = info;
		this.styles = new ChainedStyleMap();
		
		propertiesField.setAcceptableValues(this.info.getProperties());
		minOrOnlyStyleLabel.setVisible(false);
		maxStyleLabel.setVisible(false);
		styleWidget2.setVisible(false);
	}
	
	@UiHandler("propertiesField")
	void onValueChange(ValueChangeEvent<MetadataProperty> event)
	{
		MetadataProperty value = event.getValue();
		if (value != null)
		{
			selectProperty(value);
		}
	}
	
	@UiHandler("saveButton")
	void save(ClickEvent event)
	{
		ValueForNode<Boolean> filter = null;
		MetadataProperty property = propertiesField.getValue();
		if (property != null)
		{
			MockAnnotationFilter aFilter = new MockAnnotationFilter();
			aFilter.propertyName = property.getName();
			aFilter.description = filterField.getItemText(filterField.getSelectedIndex());
			aFilter.value = filterValueField.getText();
			filter = aFilter;
		}
		else 
		{
			filter = new AllPassFilter();
		}
		
		IStyle style = styleWidget1.getStyle();
		FilteredStyleMap map = new FilteredStyleMapImpl(filter, style);
		
		//TODO check if interpolated style (numeric datatype and styleWidget2 has values set) and, if so, make a GradientStyleMap
		
		this.styles.addStyleMap(map);
		
		//display saved values
		Widget savedMapping = new SavedMapperView(map);
		savedPanel.add(savedMapping);
		this.clear();
	}
	
	public void clear()
	{
		filterValueField.setText("");
		styleWidget1.clear();
		styleWidget2.clear();
	}

	private void selectProperty(MetadataProperty property)
	{
		Class<?> datatype = property.getDatatype();
		datatypeField.setValue(datatype.toString());
		
		if (property instanceof NumericMetadataProperty)
		{
			showValueRange((NumericMetadataProperty) property);
			minOrOnlyStyleLabel.setVisible(true);
			maxStyleLabel.setVisible(true);
			styleWidget2.setVisible(true);
		}
		else
		{
			rangeField.setText(null);
			minOrOnlyStyleLabel.setVisible(false);
			maxStyleLabel.setVisible(false);
			styleWidget2.setVisible(false);
		}
		
		String[] filters = getFilters(datatype);
		filterField.clear();
		for (String text : filters)
		{
			filterField.addItem(text);
		}
	}
	
	private void showValueRange(NumericMetadataProperty property)
	{
		String min = NumberFormat.getFormat("0.####").format(property.getMin());
		String max = NumberFormat.getFormat("0.####").format(property.getMax());
		rangeField.setText(min + " to " + max);
	}

	private String[] getFilters(Class<?> datatype)
	{
		//hmm... can't use isAssignableFrom in GWT client code?  TODO: Maybe change MetadataProperty.getDatatype() to return an enum value?
		
		String name = datatype.getName();
		if (name.equals("java.lang.String"))
		{
			return new String[] {"", "equals", "does not equal", "contains", "does not contain", "starts with", "ends with"};
		}
		
		else if (datatype.getName().equals("java.lang.Number") || datatype.getName().equals("java.lang.Integer") || datatype.getName().equals("java.lang.Double"))
		{
			return new String[] {"", "equals", "greater than", "less than"};
		}
		else if (datatype.getName().equals("java.lang.Boolean"))
		{
			return new String[] {"", "is true", "is false"};
		}
		else
		{
			return new String[0];
		}
	}
}

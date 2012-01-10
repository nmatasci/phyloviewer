package org.iplantc.phyloviewer.client.mapper;

import org.iplantc.phyloviewer.client.mapper.filter.BooleanFilterWidget;
import org.iplantc.phyloviewer.client.mapper.filter.NumberFilterWidget;
import org.iplantc.phyloviewer.client.mapper.filter.StringFilterWidget;
import org.iplantc.phyloviewer.client.mapper.filter.ValueFilterWidget;
import org.iplantc.phyloviewer.client.mapper.style.StyleWidget;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.metadata.AllPassFilter;
import org.iplantc.phyloviewer.shared.model.metadata.AnnotationEvaluator;
import org.iplantc.phyloviewer.shared.model.metadata.BooleanEvaluator;
import org.iplantc.phyloviewer.shared.model.metadata.DoubleEvaluator;
import org.iplantc.phyloviewer.shared.model.metadata.MetadataInfo;
import org.iplantc.phyloviewer.shared.model.metadata.MetadataProperty;
import org.iplantc.phyloviewer.shared.model.metadata.NodeValueFilter;
import org.iplantc.phyloviewer.shared.model.metadata.NumericMetadataProperty;
import org.iplantc.phyloviewer.shared.model.metadata.ValueFilter;
import org.iplantc.phyloviewer.shared.model.metadata.ValueMap;
import org.iplantc.phyloviewer.shared.render.style.ChainedStyleMap;
import org.iplantc.phyloviewer.shared.render.style.FilteredStyleMap;
import org.iplantc.phyloviewer.shared.render.style.FilteredStyleMapImpl;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

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
	@UiField BooleanFilterWidget booleanFilterWidget;
	@UiField StringFilterWidget stringFilterWidget;
	@UiField NumberFilterWidget numberFilterWidget;
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
		
		hideFilterWidgets();
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
		ValueMap<INode, Boolean> nodeFilter = createNodeFilter();		
		IStyle style = styleWidget1.getStyle();
		FilteredStyleMap map = new FilteredStyleMapImpl(nodeFilter, style);
		
		//TODO check if interpolated style (numeric datatype and styleWidget2 has values set) and, if so, make a GradientStyleMap
		
		this.styles.addStyleMap(map);
		
		//display saved values
		Widget savedMapping = new SavedMapperView(map);
		savedPanel.add(savedMapping);
		this.clearInputs();
	}
	
	public IStyleMap getStyleMap()
	{
		return styles;
	}
	
	private ValueMap<INode, Boolean> createNodeFilter()
	{
		ValueMap<INode, Boolean> nodeFilter = new AllPassFilter();
		
		MetadataProperty property = propertiesField.getValue();
		
		if (property != null)
		{
			Class<?> datatype = property.getDatatype();
			String annotationKey = property.getName();
			
			if (datatype.equals(Boolean.class))
			{
				ValueFilter<Boolean> valueFilter = booleanFilterWidget.getSelectedFilter();
				if (valueFilter != null)
				{
					BooleanEvaluator evaluator = new BooleanEvaluator(annotationKey);
					nodeFilter = new NodeValueFilter<Boolean>(evaluator, valueFilter);
				}
			}
			else if (datatype.equals(Number.class))
			{
				ValueFilter<Double> valueFilter = numberFilterWidget.getSelectedFilter();
				if (valueFilter != null)
				{
					DoubleEvaluator evaluator = new DoubleEvaluator(annotationKey);
					nodeFilter = new NodeValueFilter<Double>(evaluator, valueFilter);
				}
			}
			else if (datatype.equals(String.class))
			{
				ValueFilter<String> valueFilter = stringFilterWidget.getSelectedFilter();
				
				ValueMap<INode, String> evaluator = new AnnotationEvaluator<String>(annotationKey)
				{
					@Override
					public String parseValue(Object annotationValue)
					{
						return annotationValue.toString();
					}
					
				};
				
				if (valueFilter != null)
				{
					nodeFilter = new NodeValueFilter<String>(evaluator, valueFilter);
				}
			}
		}
		
		return nodeFilter;
	}
	
	private void clearInputs()
	{
		resetFilterWidgets();
		styleWidget1.clear();
		styleWidget2.clear();
	}
	
	private void resetFilterWidgets()
	{
		booleanFilterWidget.reset();
		stringFilterWidget.reset();
		numberFilterWidget.reset();
	}

	private void selectProperty(MetadataProperty property)
	{
		//show the appropriate widgets for the selected property
		
		Class<?> datatype = property.getDatatype();
		datatypeField.setValue(datatype.toString());
		
		if(property instanceof NumericMetadataProperty)
		{
			showValueRange((NumericMetadataProperty)property);
			//TODO setVisible the gradient style widgets: minOrOnlyStyleLabel, maxStyleLabel, styleWidget2
		}

		hideFilterWidgets();
		ValueFilterWidget<?> currentFilterWidget = getFilterWidgetFor(datatype);
		currentFilterWidget.setVisible(true);
	}
	
	private ValueFilterWidget<?> getFilterWidgetFor(Class<?> datatype)
	{
		if (datatype.equals(Boolean.class))
		{
			return booleanFilterWidget;
		}
		else if (datatype.equals(Number.class))
		{
			return numberFilterWidget;
		}
		else if (datatype.equals(String.class))
		{
			return stringFilterWidget;
		}
		else
		{
			return null;
		}
	}
	
	private void showValueRange(NumericMetadataProperty property)
	{
		String min = NumberFormat.getFormat("0.####").format(property.getMin());
		String max = NumberFormat.getFormat("0.####").format(property.getMax());
		rangeField.setText(min + " to " + max);
	}
	
	private void hideFilterWidgets()
	{
		booleanFilterWidget.setVisible(false);
		stringFilterWidget.setVisible(false);
		numberFilterWidget.setVisible(false);
	}
}

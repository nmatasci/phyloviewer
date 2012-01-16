package org.iplantc.phyloviewer.client.mapper;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.shared.model.metadata.AnnotationMetadata;
import org.iplantc.phyloviewer.shared.model.metadata.AnnotationMetadataImpl;
import org.iplantc.phyloviewer.shared.model.metadata.NumericAnnotationMetadataImpl;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MapperWidgetTest implements EntryPoint
{
	public void onModuleLoad()
	{
		Widget mapper = new DataMapper(getProperties());
		RootLayoutPanel.get().add(mapper);
	}

	private List<AnnotationMetadata> getProperties()
	{
		ArrayList<AnnotationMetadata> properties = new ArrayList<AnnotationMetadata>();
		properties.add(new AnnotationMetadataImpl("someStringProperty", String.class));
		properties.add(new NumericAnnotationMetadataImpl("someIntegerProperty", Number.class, 0, 42));
		properties.add(new NumericAnnotationMetadataImpl("someDecimalProperty", Number.class, 0.01, 0.042));
		properties.add(new AnnotationMetadataImpl("someBooleanProperty", Boolean.class));

		return properties;
	}
}

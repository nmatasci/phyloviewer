package org.iplantc.phyloviewer.client.mapper;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.shared.model.metadata.MetadataInfo;
import org.iplantc.phyloviewer.shared.model.metadata.MetadataProperty;
import org.iplantc.phyloviewer.shared.model.metadata.MetadataPropertyImpl;
import org.iplantc.phyloviewer.shared.model.metadata.NumericMetadataPropertyImpl;

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
		MetadataInfo mockInfo = getMetadataInfo();
		
		Widget mapper = new DataMapper(mockInfo);
		RootLayoutPanel.get().add(mapper);
	}

	private MetadataInfo getMetadataInfo()
	{
		final List<MetadataProperty> properties = getProperties();
		
		MetadataInfo mockInfo = new MetadataInfo()
		{
			@Override
			public List<MetadataProperty> getProperties()
			{
				return properties;
			}
		};
		return mockInfo;
	}

	private List<MetadataProperty> getProperties()
	{
		ArrayList<MetadataProperty> properties = new ArrayList<MetadataProperty>();
		properties.add(new MetadataPropertyImpl("someStringProperty", String.class));
		properties.add(new NumericMetadataPropertyImpl("someIntegerProperty", Number.class, 0, 42));
		properties.add(new NumericMetadataPropertyImpl("someDecimalProperty", Number.class, 0.01, 0.042));
		properties.add(new MetadataPropertyImpl("someBooleanProperty", Boolean.class));

		return properties;
	}
}

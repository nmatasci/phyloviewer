package org.iplantc.phyloviewer.client.mapper;

import java.io.IOException;

import org.iplantc.phyloviewer.shared.model.metadata.MetadataProperty;

import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueListBox;

public class PropertyListBox extends ValueListBox<MetadataProperty>
{
	static final Renderer<MetadataProperty> renderer = new Renderer<MetadataProperty>()
	{

		@Override
		public String render(MetadataProperty property)
		{
			if(property == null)
			{
				return "";
			}

			return property.getName();
		}

		@Override
		public void render(MetadataProperty property, Appendable appendable) throws IOException
		{
			appendable.append(property.getName());
		}

	};
	
	public PropertyListBox()
	{
		super(renderer);
	}
}

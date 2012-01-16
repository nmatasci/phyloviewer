package org.iplantc.phyloviewer.client.mapper;

import java.io.IOException;

import org.iplantc.phyloviewer.shared.model.metadata.AnnotationMetadata;

import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueListBox;

public class PropertyListBox extends ValueListBox<AnnotationMetadata>
{
	static final Renderer<AnnotationMetadata> renderer = new Renderer<AnnotationMetadata>()
	{

		@Override
		public String render(AnnotationMetadata property)
		{
			if(property == null)
			{
				return "";
			}

			return property.getName();
		}

		@Override
		public void render(AnnotationMetadata property, Appendable appendable) throws IOException
		{
			appendable.append(property.getName());
		}

	};
	
	public PropertyListBox()
	{
		super(renderer);
	}
}

package org.iplantc.phyloviewer.client.mapper.mocks;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.metadata.ValueForNode;

public class MockAnnotationFilter implements ValueForNode<Boolean>
{
	public String propertyName;
	public String description;
	public String value;
	
	@Override
	public Boolean value(INode node)
	{
		return false;
	}

	public String toString() {
		return propertyName + " " + description + " " + value;
	}
}

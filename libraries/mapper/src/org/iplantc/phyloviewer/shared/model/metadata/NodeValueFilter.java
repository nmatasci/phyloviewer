package org.iplantc.phyloviewer.shared.model.metadata;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.metadata.ValueMap;

public class NodeValueFilter<T> implements ValueMap<INode, Boolean>
{
	public final ValueMap<INode, T> evaluator;
	public final ValueMap<T, Boolean> valueFilter;
	
	public NodeValueFilter(ValueMap<INode, T> evaluator, ValueMap<T, Boolean> valueFilter)
	{
		this.evaluator = evaluator;
		this.valueFilter = valueFilter;
	}

	@Override
	public Boolean get(INode node)
	{
		T value = evaluator.get(node);
		return valueFilter.get(value);
	}

	public String toString() {
		return evaluator.toString() + " " + valueFilter.toString();
	}
}

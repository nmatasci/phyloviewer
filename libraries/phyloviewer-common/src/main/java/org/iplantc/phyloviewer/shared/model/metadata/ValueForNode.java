package org.iplantc.phyloviewer.shared.model.metadata;

import org.iplantc.phyloviewer.shared.model.INode;

public interface ValueForNode<T>
{
	public T get(INode node);
}

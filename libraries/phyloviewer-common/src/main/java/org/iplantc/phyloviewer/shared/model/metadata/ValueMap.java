package org.iplantc.phyloviewer.shared.model.metadata;

/**
 * Just a function to map arbitrary values to values. Not necessarily a collection.
 *
 * @param <I> input value type
 * @param <O> output value type
 */
public interface ValueMap<I, O>
{
	public O get(I value);
}

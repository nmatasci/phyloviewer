package org.iplantc.phyloviewer.shared.model.metadata;


public class BooleanFilter implements ValueFilter<Boolean>
{
	public final boolean targetValue;
	
	public BooleanFilter(boolean targetValue)
	{
		this.targetValue = targetValue;
	}
	
	@Override
	public Boolean get(Boolean value)
	{
		return targetValue == value;
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(targetValue);
	}
}

package org.iplantc.phyloviewer.shared.model.metadata;

public class NumberFilter implements ValueFilter<Number>
{
	private double minValue = -Double.MAX_VALUE;
	private double maxValue = Double.MAX_VALUE;
	
	public NumberFilter(double minValue, double maxValue)
	{
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public double getMinValue()
	{
		return minValue;
	}

	public void setMinValue(double minValue)
	{
		this.minValue = minValue;
	}
	
	public double getMaxValue()
	{
		return maxValue;
	}

	public void setMaxValue(double maxValue)
	{
		this.maxValue = maxValue;
	}

	public String toString()
	{
		String desc = "value in range";
		if (minValue != -Double.MAX_VALUE || maxValue != Double.MAX_VALUE)
		{
			String minString = minValue == -Double.MAX_VALUE ? "-Inf" : String.valueOf(minValue);
			String maxString = maxValue == Double.MAX_VALUE ? "Inf" : String.valueOf(maxValue);
			desc += minString + " and " + maxString; 
		}
		
		return desc;
	}

	@Override
	public Boolean get(Number value)
	{
		int minComp = Double.compare(minValue, value.doubleValue());
		int maxComp = Double.compare(maxValue, value.doubleValue());
		return minComp <= 0 && maxComp >= 0;
	}
}

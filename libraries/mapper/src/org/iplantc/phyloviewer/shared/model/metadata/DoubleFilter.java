package org.iplantc.phyloviewer.shared.model.metadata;

public class DoubleFilter implements ValueFilter<Double>
{
	private double minValue;
	private double maxValue;
	
	public DoubleFilter(double minValue, double maxValue)
	{
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public DoubleFilter()
	{
		minValue = -Double.MAX_VALUE;
		maxValue = Double.MAX_VALUE;
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
			desc += " " + minString + " and " + maxString; 
		}
		
		return desc;
	}

	@Override
	public Boolean get(Double value)
	{
		int minComp = Double.compare(minValue, value.doubleValue());
		int maxComp = Double.compare(maxValue, value.doubleValue());
		return minComp <= 0 && maxComp >= 0;
	}
}

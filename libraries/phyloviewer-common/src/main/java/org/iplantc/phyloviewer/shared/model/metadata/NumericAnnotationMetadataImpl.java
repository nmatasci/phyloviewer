package org.iplantc.phyloviewer.shared.model.metadata;

public class NumericAnnotationMetadataImpl extends AnnotationMetadataImpl implements NumericAnnotationMetadata
{
	private double min;
	private double max;

	public NumericAnnotationMetadataImpl(String name, Class<?> datatype, double min, double max)
	{
		super(name, datatype);
		this.min = min;
		this.max = max;
	}

	@Override
	public double getMin()
	{
		return min;
	}

	@Override
	public double getMax()
	{
		return max;
	}

}

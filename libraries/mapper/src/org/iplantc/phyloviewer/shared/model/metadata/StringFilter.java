package org.iplantc.phyloviewer.shared.model.metadata;


public abstract class StringFilter implements ValueFilter<String>
{
	protected String targetValue;
	
	public void setTargetValue(String targetValue)
	{
		this.targetValue = targetValue;
	}
	
	public static class EqualsFilter extends StringFilter
	{
		@Override
		public String toString()
		{
			return "equals " + (targetValue != null ? targetValue : "");
		}

		@Override
		public Boolean get(String value)
		{
			return value.equals(targetValue);
		}
	}
	
	public static class ContainsFilter extends StringFilter
	{
		@Override
		public String toString()
		{
			return "contains " + (targetValue != null ? targetValue : "");
		}

		@Override
		public Boolean get(String value)
		{
			return targetValue == null ? false : value.contains(targetValue);
		}
	}
	
	public static class StartsWithFilter extends StringFilter
	{
		@Override
		public String toString()
		{
			return "starts with " + (targetValue != null ? targetValue : "");
		}

		@Override
		public Boolean get(String value)
		{
			return value.startsWith(targetValue);
		}
	}
	
	public static class EndsWithFilter extends StringFilter
	{
		@Override
		public String toString()
		{
			return "ends with " + (targetValue != null ? targetValue : "");
		}

		@Override
		public Boolean get(String value)
		{
			return value.endsWith(targetValue);
		}
	}
	
	public static class Not extends StringFilter
	{
		private StringFilter wrapped;
		public Not(StringFilter wrapped)
		{
			this.wrapped = wrapped;
		}
		
		@Override
		public Boolean get(String value)
		{
			return !wrapped.get(value);
		}

		@Override
		public void setTargetValue(String targetValue)
		{
			wrapped.setTargetValue(targetValue);
		}

		@Override
		public String toString()
		{
			return "not " + wrapped.toString();
		}
	}
}

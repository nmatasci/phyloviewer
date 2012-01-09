package org.iplantc.phyloviewer.shared.model.metadata;

import java.util.ArrayList;
import java.util.List;

public abstract class BooleanFilter implements ValueFilter<Boolean>
{
	public static final BooleanFilter ALL_PASS = new BooleanFilter()
	{
		@Override
		public Boolean get(Boolean value)
		{
			return true;
		}

		@Override
		public String getDescription()
		{
			return "All";
		}
	};
	
	public static final BooleanFilter TRUE = new BooleanFilter()
	{
		@Override
		public Boolean get(Boolean value)
		{
			return value;
		}

		@Override
		public String getDescription()
		{
			return "True";
		}
	};
	
	public static final BooleanFilter FALSE = new BooleanFilter()
	{
		@Override
		public Boolean get(Boolean value)
		{
			return !value;
		}

		@Override
		public String getDescription()
		{
			return "False";
		}
	};
	
	public static List<ValueFilter<Boolean>> allFilters;
	static {
		allFilters = new ArrayList<ValueFilter<Boolean>>();
		allFilters.add(ALL_PASS);
		allFilters.add(TRUE);
		allFilters.add(FALSE);
	}
}

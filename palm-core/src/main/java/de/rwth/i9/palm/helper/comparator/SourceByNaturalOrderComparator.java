package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.Source;

public class SourceByNaturalOrderComparator implements Comparator<Source>
{

	@Override
	public int compare( final Source source1, final Source source2 )
	{
		String name1 = source1.getName();
		String name2 = source2.getName();

		return name1.compareTo( name2 );
	}

}
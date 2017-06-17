package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;
import java.util.Map;

public class SimilarityComparator implements Comparator<Map<String, Object>>
{

	@Override
	public int compare( Map<String, Object> o1, Map<String, Object> o2 )
	{
		if ( o1.get( "similarity" ) == null && o2.get( "similarity" ) == null )
			return 0;
		if ( o1.get( "similarity" ) == null )
			return -1;

		if ( o2.get( "similarity" ) == null )
			return 1;

		if ( (Double) o1.get( "similarity" ) == (Double) o2.get( "similarity" ) )
			return 0;

		if ( (Double) o1.get( "similarity" ) < (Double) o2.get( "similarity" ) )
			return -1;
		else
			return 1;

	}

}

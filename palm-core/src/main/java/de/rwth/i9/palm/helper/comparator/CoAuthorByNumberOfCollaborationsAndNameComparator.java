package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;
import java.util.Map;

public class CoAuthorByNumberOfCollaborationsAndNameComparator implements Comparator<Map<String, Object>>
{

	@Override
	public int compare( Map<String, Object> author1, Map<String, Object> author2 )
	{
		if ( author1 == null && author2 == null )
			return 0;

		if ( author1 == null )
			return -1;

		if ( author2 == null )
			return 1;

		if ( author1.get( "coauthorTimes" ) == null && author2.get( "coauthorTimes" ) == null )
			return 0;

		if ( author1.get( "coauthorTimes" ) == null )
			return -1;

		if ( author2.get( "coauthorTimes" ) == null )
			return 1;

		int noCitation1 = (int) author1.get( "coauthorTimes" );
		int noCitation2 = (int) author2.get( "coauthorTimes" );

		String name1 = (String) author1.get( "name" );
		String name2 = (String) author2.get( "name" );

		if ( noCitation1 < noCitation2 )
			return 1;

		if ( noCitation1 > noCitation2 )
			return -1;

		return name1.toLowerCase().compareTo( name2.toLowerCase() );
	}

}

package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.Author;

public class AuthorByNoCitationComparator implements Comparator<Author>
{

	@Override
	public int compare( final Author author1, final Author author2 )
	{
		if ( author1 == null && author2 == null )
			return 0;

		if ( author1 == null )
			return -1;

		if ( author2 == null )
			return 1;

		int noCitation1 = author1.getCitedBy();
		int noCitation2 = author2.getCitedBy();

		if ( noCitation1 < noCitation2 )
			return 1;

		if ( noCitation1 > noCitation2 )
			return -1;

		return 0;
	}

}
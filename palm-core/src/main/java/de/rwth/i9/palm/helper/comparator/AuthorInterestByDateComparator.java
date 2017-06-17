package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.AuthorInterest;

public class AuthorInterestByDateComparator implements Comparator<AuthorInterest>
{

	@Override
	public int compare( final AuthorInterest authorInterest1, final AuthorInterest authorInterest2 )
	{
		if ( authorInterest1 == null && authorInterest2 == null )
			return 0;

		if ( authorInterest1 == null )
			return -1;

		if ( authorInterest2 == null )
			return 1;

		if ( authorInterest1.getYear().before( authorInterest2.getYear() ) )
			return -1;
		else if ( authorInterest1.getYear().after( authorInterest2.getYear() ) )
			return 1;
		else
			return 0;
	}

}
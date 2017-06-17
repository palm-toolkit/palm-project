package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.AuthorInterestProfile;

public class AuthorInterestProfileByProfileNameLengthComparator implements Comparator<AuthorInterestProfile>
{

	@Override
	public int compare( final AuthorInterestProfile authorInterestProfile1, final AuthorInterestProfile authorInterestProfile2 )
	{
		if ( authorInterestProfile1 == null && authorInterestProfile2 == null )
			return 0;

		if ( authorInterestProfile1 == null )
			return -1;

		if ( authorInterestProfile2 == null )
			return 1;

		int nameLength1 = authorInterestProfile1.getName().length();
		int nameLength2 = authorInterestProfile2.getName().length();

		if ( nameLength1 < nameLength2 )
			return 1;

		if ( nameLength1 > nameLength2 )
			return -1;

		return 0;
	}

}
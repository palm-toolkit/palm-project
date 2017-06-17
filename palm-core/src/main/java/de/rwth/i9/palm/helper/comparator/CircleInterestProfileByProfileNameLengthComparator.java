package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.CircleInterestProfile;

public class CircleInterestProfileByProfileNameLengthComparator implements Comparator<CircleInterestProfile>
{

	@Override
	public int compare( final CircleInterestProfile circleInterestProfile1, final CircleInterestProfile circleInterestProfile2 )
	{
		if ( circleInterestProfile1 == null && circleInterestProfile2 == null )
			return 0;

		if ( circleInterestProfile1 == null )
			return -1;

		if ( circleInterestProfile2 == null )
			return 1;

		int nameLength1 = circleInterestProfile1.getName().length();
		int nameLength2 = circleInterestProfile2.getName().length();

		if ( nameLength1 < nameLength2 )
			return 1;

		if ( nameLength1 > nameLength2 )
			return -1;

		return 0;
	}

}
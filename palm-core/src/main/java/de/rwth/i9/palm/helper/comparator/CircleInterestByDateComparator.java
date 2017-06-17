package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.CircleInterest;

public class CircleInterestByDateComparator implements Comparator<CircleInterest>
{

	@Override
	public int compare( final CircleInterest circleInterest1, final CircleInterest circleInterest2 )
	{
		if ( circleInterest1 == null && circleInterest2 == null )
			return 0;

		if ( circleInterest1 == null )
			return -1;

		if ( circleInterest2 == null )
			return 1;

		if ( circleInterest1.getYear().before( circleInterest2.getYear() ) )
			return -1;
		else if ( circleInterest1.getYear().after( circleInterest2.getYear() ) )
			return 1;
		else
			return 0;
	}

}
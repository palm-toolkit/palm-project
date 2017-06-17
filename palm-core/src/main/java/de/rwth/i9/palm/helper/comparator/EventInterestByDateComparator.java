package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.EventInterest;

public class EventInterestByDateComparator implements Comparator<EventInterest>
{

	@Override
	public int compare( final EventInterest eventInterest1, final EventInterest eventInterest2 )
	{
		if ( eventInterest1 == null && eventInterest2 == null )
			return 0;

		if ( eventInterest1 == null )
			return -1;

		if ( eventInterest2 == null )
			return 1;

		if ( eventInterest1.getYear().before( eventInterest2.getYear() ) )
			return -1;
		else if ( eventInterest1.getYear().after( eventInterest2.getYear() ) )
			return 1;
		else
			return 0;
	}

}
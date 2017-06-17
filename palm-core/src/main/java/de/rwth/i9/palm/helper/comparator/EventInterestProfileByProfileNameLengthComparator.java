package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.EventInterestProfile;

public class EventInterestProfileByProfileNameLengthComparator implements Comparator<EventInterestProfile>
{

	@Override
	public int compare( final EventInterestProfile eventInterestProfile1, final EventInterestProfile eventInterestProfile2 )
	{
		if ( eventInterestProfile1 == null && eventInterestProfile2 == null )
			return 0;

		if ( eventInterestProfile1 == null )
			return -1;

		if ( eventInterestProfile2 == null )
			return 1;

		int nameLength1 = eventInterestProfile1.getName().length();
		int nameLength2 = eventInterestProfile2.getName().length();

		if ( nameLength1 < nameLength2 )
			return 1;

		if ( nameLength1 > nameLength2 )
			return -1;

		return 0;
	}

}
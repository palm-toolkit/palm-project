package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.Event;

public class EventByYearComparator implements Comparator<Event>
{

	@Override
	public int compare( final Event event1, final Event event2 )
	{
		if ( event1 == null && event2 == null )
			return 0;

		if ( event1 == null )
			return 1;

		if ( event2 == null )
			return -1;

		String year1 = event1.getYear();
		String year2 = event2.getYear();

		if ( year1.equals( year2 ) )
		{
			if ( event1.getVolume() == null && event2.getVolume() == null )
				return 0;

			if ( event1.getVolume() == null )
				return 1;

			if ( event2.getVolume() == null )
				return -1;

			return event1.getVolume().compareTo( event2.getVolume() );
		}
		else
		{
			return year1.compareTo( year2 ) * -1;
		}
	}

}
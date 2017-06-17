package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.Event;

public class EventByNotationComparator implements Comparator<Event>
{

	@Override
	public int compare( final Event event1, final Event event2 )
	{
		String notation1 = event1.getEventGroup().getNotation() + event1.getYear();
		String notation2 = event2.getEventGroup().getNotation() + event2.getYear();

		return notation1.compareTo( notation2 );
	}

}
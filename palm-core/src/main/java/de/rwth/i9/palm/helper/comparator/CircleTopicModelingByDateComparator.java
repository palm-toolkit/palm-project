package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.CircleTopicModeling;

public class CircleTopicModelingByDateComparator implements Comparator<CircleTopicModeling>
{

	@Override
	public int compare( final CircleTopicModeling circleTopicModeling1, final CircleTopicModeling circleTopicModeling2 )
	{
		if ( circleTopicModeling1 == null && circleTopicModeling2 == null )
			return 0;

		if ( circleTopicModeling1 == null )
			return -1;

		if ( circleTopicModeling2 == null )
			return 1;

		if ( circleTopicModeling1.getYear().before( circleTopicModeling2.getYear() ) )
			return -1;
		else if ( circleTopicModeling1.getYear().after( circleTopicModeling2.getYear() ) )
			return 1;
		else
			return 0;
	}
}

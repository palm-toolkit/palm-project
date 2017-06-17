package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.PublicationTopic;

public class PublicationTopicByExtractionServiceTypeComparator implements Comparator<PublicationTopic>
{

	@Override
	public int compare( final PublicationTopic publicationTopic1, final PublicationTopic publicationTopic2 )
	{
		String name1 = publicationTopic1.getExtractionServiceType().toString();
		String name2 = publicationTopic2.getExtractionServiceType().toString();
		;

		return name1.compareTo( name2 );
	}

}
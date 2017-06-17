package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.PublicationSource;

public class PublicationSourceBySourceTypeComparator implements Comparator<PublicationSource>
{

	@Override
	public int compare( final PublicationSource publicationSource1, final PublicationSource publicationSource2 )
	{
		String name1 = publicationSource1.getSourceType().toString();
		String name2 = publicationSource2.getSourceType().toString();
		;

		return name1.compareTo( name2 );
	}

}
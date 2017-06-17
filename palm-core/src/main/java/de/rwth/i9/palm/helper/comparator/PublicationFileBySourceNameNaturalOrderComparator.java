package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.PublicationFile;

public class PublicationFileBySourceNameNaturalOrderComparator implements Comparator<PublicationFile>
{

	@Override
	public int compare( final PublicationFile publicationFile1, final PublicationFile publicationFile2 )
	{
		if ( publicationFile1 == null && publicationFile2 == null )
			return 0;

		if ( publicationFile1 == null )
			return -1;

		if ( publicationFile2 == null )
			return 1;

		return publicationFile1.getSourceType().toString().compareTo( publicationFile2.getSourceType().toString() );
	}

}
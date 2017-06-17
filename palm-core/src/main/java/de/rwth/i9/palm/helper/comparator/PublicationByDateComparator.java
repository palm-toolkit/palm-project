package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.Publication;

public class PublicationByDateComparator implements Comparator<Publication>
{

	@Override
	public int compare( final Publication publication1, final Publication publication2 )
	{
		if ( publication1 == null && publication2 == null )
			return 0;

		if ( publication1 == null )
			return 1;

		if ( publication2 == null )
			return -1;

		if ( publication1.getPublicationDate() == null && publication2.getPublicationDate() == null )
			return 0;

		if ( publication1.getPublicationDate() == null )
			return 1;

		if ( publication2.getPublicationDate() == null )
			return -1;

		if ( publication1.getPublicationDate().before( publication2.getPublicationDate() ) )
			return 1;
		else if ( publication1.getPublicationDate().after( publication2.getPublicationDate() ) )
			return -1;
		else
			return 0;
	}

}
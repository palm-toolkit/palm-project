package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationType;

public class PublicationByPageComparator implements Comparator<Publication>
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

		if ( publication1.getPublicationType().equals( PublicationType.EDITORSHIP ) && publication2.getPublicationType().equals( PublicationType.EDITORSHIP ) )
			return 0;

		if ( publication1.getPublicationType().equals( PublicationType.EDITORSHIP ) )
			return -1;

		if ( publication2.getPublicationType().equals( PublicationType.EDITORSHIP ) )
			return 1;

		if ( publication1.getStartPage() == 0 && publication2.getStartPage() == 0 )
			return 0;

		if ( publication1.getStartPage() == 0 )
			return 1;

		if ( publication2.getStartPage() == 0 )
			return -1;

		if ( publication1.getStartPage() > publication2.getStartPage() )
			return 1;
		else if ( publication1.getStartPage() < publication2.getStartPage() )
			return -1;
		else
			return 0;
	}

}
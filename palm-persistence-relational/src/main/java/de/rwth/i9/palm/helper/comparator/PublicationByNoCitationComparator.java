package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.Publication;

public class PublicationByNoCitationComparator implements Comparator<Publication>
{

	@Override
	public int compare( final Publication publication1, final Publication publication2 )
	{
		if ( publication1 == null && publication2 == null )
			return 0;

		if ( publication1 == null )
			return -1;

		if ( publication2 == null )
			return 1;

		int noCitation1 = publication1.getCitedBy();
		int noCitation2 = publication2.getCitedBy();

		if ( noCitation1 < noCitation2 )
			return 1;

		if ( noCitation1 > noCitation2 )
			return -1;

		return 0;
	}

}
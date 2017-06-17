package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.UserPublicationBookmark;

public class UserPublicationBookmarkByDateComparator implements Comparator<UserPublicationBookmark>
{

	@Override
	public int compare( final UserPublicationBookmark userPublicationBookmark1, final UserPublicationBookmark userPublicationBookmark2 )
	{
		if ( userPublicationBookmark1 == null && userPublicationBookmark2 == null )
			return 0;

		if ( userPublicationBookmark1 == null )
			return 1;

		if ( userPublicationBookmark2 == null )
			return -1;

		if ( userPublicationBookmark1.getBookedDate().before( userPublicationBookmark2.getBookedDate() ) )
			return 1;
		else if ( userPublicationBookmark1.getBookedDate().after( userPublicationBookmark2.getBookedDate() ) )
			return -1;
		else
			return 0;
	}

}
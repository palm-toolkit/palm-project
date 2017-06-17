package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.UserCircleBookmark;

public class UserCircleBookmarkByDateComparator implements Comparator<UserCircleBookmark>
{

	@Override
	public int compare( final UserCircleBookmark userCircleBookmark1, final UserCircleBookmark userCircleBookmark2 )
	{
		if ( userCircleBookmark1 == null && userCircleBookmark2 == null )
			return 0;

		if ( userCircleBookmark1 == null )
			return 1;

		if ( userCircleBookmark2 == null )
			return -1;

		if ( userCircleBookmark1.getBookedDate().before( userCircleBookmark2.getBookedDate() ) )
			return 1;
		else if ( userCircleBookmark1.getBookedDate().after( userCircleBookmark2.getBookedDate() ) )
			return -1;
		else
			return 0;
	}

}
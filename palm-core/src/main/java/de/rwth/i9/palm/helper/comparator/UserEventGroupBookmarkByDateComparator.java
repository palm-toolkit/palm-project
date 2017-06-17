package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.UserEventGroupBookmark;

public class UserEventGroupBookmarkByDateComparator implements Comparator<UserEventGroupBookmark>
{

	@Override
	public int compare( final UserEventGroupBookmark userEventGroupBookmark1, final UserEventGroupBookmark userEventGroupBookmark2 )
	{
		if ( userEventGroupBookmark1 == null && userEventGroupBookmark2 == null )
			return 0;

		if ( userEventGroupBookmark1 == null )
			return 1;

		if ( userEventGroupBookmark2 == null )
			return -1;

		if ( userEventGroupBookmark1.getBookedDate().before( userEventGroupBookmark2.getBookedDate() ) )
			return 1;
		else if ( userEventGroupBookmark1.getBookedDate().after( userEventGroupBookmark2.getBookedDate() ) )
			return -1;
		else
			return 0;
	}

}
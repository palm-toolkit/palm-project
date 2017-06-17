package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.UserAuthorBookmark;

public class UserAuthorBookmarkByDateComparator implements Comparator<UserAuthorBookmark>
{

	@Override
	public int compare( final UserAuthorBookmark userAuthorBookmark1, final UserAuthorBookmark userAuthorBookmark2 )
	{
		if ( userAuthorBookmark1 == null && userAuthorBookmark2 == null )
			return 0;

		if ( userAuthorBookmark1 == null )
			return 1;

		if ( userAuthorBookmark2 == null )
			return -1;

		if ( userAuthorBookmark1.getBookedDate().before( userAuthorBookmark2.getBookedDate() ) )
			return 1;
		else if ( userAuthorBookmark1.getBookedDate().after( userAuthorBookmark2.getBookedDate() ) )
			return -1;
		else
			return 0;
	}

}
package de.rwth.i9.palm.feature.user;

import org.springframework.beans.factory.annotation.Autowired;

public class UserFeatureImpl implements UserFeature
{

	@Autowired( required = false )
	private UserBookmark userBookmark;
	@Autowired( required = false )
	private UserPublication userPublication;

	@Override
	public UserBookmark getUserBookmark()
	{
		if ( this.userBookmark == null )
			this.userBookmark = new UserBookmarkImpl();

		return this.userBookmark;
	}
	
	public UserPublication getUserPublication(){
		if ( this.userPublication == null)
			this.userPublication = new UserPublicationImpl();
		return this.userPublication;
	}

}

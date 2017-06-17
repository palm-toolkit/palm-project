package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserCircleBookmark;
import de.rwth.i9.palm.persistence.UserCircleBookmarkDAO;

public class UserCircleBookmarkDAOHibernate extends GenericDAOHibernate<UserCircleBookmark> implements UserCircleBookmarkDAO
{

	public UserCircleBookmarkDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public UserCircleBookmark getByUserAndCircle( User user, Circle circle )
	{
		if ( user == null || circle == null )
			return null;

		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT ucb FROM User user " );
		queryString.append( "JOIN user.userCircleBookmarks ucb " );
		queryString.append( "WHERE user = :user " );
		queryString.append( "AND ucb.circle = :circle" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "user", user );
		query.setParameter( "circle", circle );

		@SuppressWarnings( "unchecked" )
		List<UserCircleBookmark> userCircleBookmarks = query.list();

		if ( userCircleBookmarks == null || userCircleBookmarks.size() == 0 )
			return null;

		return userCircleBookmarks.get( 0 );
	}

}

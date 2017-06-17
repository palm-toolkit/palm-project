package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserPublicationBookmark;
import de.rwth.i9.palm.persistence.UserPublicationBookmarkDAO;

public class UserPublicationBookmarkDAOHibernate extends GenericDAOHibernate<UserPublicationBookmark> implements UserPublicationBookmarkDAO
{

	public UserPublicationBookmarkDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public UserPublicationBookmark getByUserAndPublication( User user, Publication publication )
	{
		if ( user == null || publication == null )
			return null;

		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT upb FROM User user " );
		queryString.append( "JOIN user.userPublicationBookmarks upb " );
		queryString.append( "WHERE user = :user " );
		queryString.append( "AND upb.publication = :publication" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "user", user );
		query.setParameter( "publication", publication );

		@SuppressWarnings( "unchecked" )
		List<UserPublicationBookmark> userPublicationBookmarks = query.list();

		if ( userPublicationBookmarks == null || userPublicationBookmarks.size() == 0 )
			return null;

		return userPublicationBookmarks.get( 0 );
	}

}

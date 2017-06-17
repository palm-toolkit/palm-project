package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.RequestType;
import de.rwth.i9.palm.model.UserRequest;
import de.rwth.i9.palm.persistence.UserRequestDAO;

public class UserRequestDAOHibernate extends GenericDAOHibernate<UserRequest> implements UserRequestDAO
{

	public UserRequestDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public UserRequest getByTypeAndQuery( RequestType requestType, String queryString )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "FROM UserRequest " );
		stringBuilder.append( "WHERE requestType = :requestType " );
		stringBuilder.append( "AND queryString = :queryString" );

		Query query = getCurrentSession().createQuery( stringBuilder.toString() );
		query.setParameter( "requestType", requestType );
		query.setParameter( "queryString", queryString );

		@SuppressWarnings( "unchecked" )
		List<UserRequest> userRequests = query.list();

		if ( userRequests == null || userRequests.isEmpty() )
			return null;

		return userRequests.get( 0 );
	}

}

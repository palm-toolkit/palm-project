package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Interest;
import de.rwth.i9.palm.persistence.InterestDAO;

public class InterestDAOHibernate extends GenericDAOHibernate<Interest>implements InterestDAO
{

	public InterestDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public Interest getInterestByTerm( String term )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Interest " );
		queryString.append( "WHERE term = :term " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "term", term );

		@SuppressWarnings( "unchecked" )
		List<Interest> interests = query.list();

		if ( interests == null || interests.isEmpty() )
			return null;

		return interests.get( 0 );
	}

}

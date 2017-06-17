package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Country;
import de.rwth.i9.palm.persistence.CountryDAO;

public class CountryDAOHibernate extends GenericDAOHibernate<Country> implements CountryDAO
{

	public CountryDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public Country getCountryByName( String name )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Country " );
		queryString.append( "WHERE name = :name " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "name", name );

		@SuppressWarnings( "unchecked" )
		List<Country> countries = query.list();

		if ( countries == null || countries.isEmpty() )
			return null;

		return countries.get( 0 );
	}

}
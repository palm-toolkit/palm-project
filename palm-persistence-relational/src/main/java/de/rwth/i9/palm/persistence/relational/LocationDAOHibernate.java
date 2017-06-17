package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Country;
import de.rwth.i9.palm.model.Location;
import de.rwth.i9.palm.persistence.LocationDAO;

public class LocationDAOHibernate extends GenericDAOHibernate<Location> implements LocationDAO
{

	public LocationDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<Location> getByCountry( String countryName )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Location " );
		queryString.append( "WHERE country = :country " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "country", countryName );

		@SuppressWarnings( "unchecked" )
		List<Location> locations = query.list();

		if ( locations == null || locations.isEmpty() )
			return Collections.emptyList();

		return locations;
	}

	@Override
	public Location getByCountryAndCity( Country country, String city )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Location " );
		queryString.append( "WHERE country = :country " );
		queryString.append( "AND city = :city " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "country", country );
		query.setParameter( "city", city );

		@SuppressWarnings( "unchecked" )
		List<Location> locations = query.list();

		if ( locations == null || locations.isEmpty() )
			return null;

		return locations.get( 0 );
	}

}

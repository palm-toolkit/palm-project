package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.EventInterestProfile;
import de.rwth.i9.palm.persistence.EventInterestProfileDAO;

public class EventInterestProfileDAOHibernate extends GenericDAOHibernate<EventInterestProfile> implements EventInterestProfileDAO
{

	public EventInterestProfileDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<EventInterestProfile> getDefaultEventInterestProfile()
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM EventInterestProfile " );
		queryString.append( "WHERE defaultProfile IS true " );
		queryString.append( "AND valid IS true " );

		Query query = getCurrentSession().createQuery( queryString.toString() );

		@SuppressWarnings( "unchecked" )
		List<EventInterestProfile> eventInterestProfiles = query.list();

		if ( eventInterestProfiles == null || eventInterestProfiles.isEmpty() )
			return Collections.emptyList();

		return eventInterestProfiles;
	}

}

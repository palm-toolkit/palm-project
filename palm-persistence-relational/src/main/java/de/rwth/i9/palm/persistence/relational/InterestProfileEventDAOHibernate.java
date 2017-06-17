package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.InterestProfileEvent;
import de.rwth.i9.palm.model.InterestProfileType;
import de.rwth.i9.palm.persistence.InterestProfileEventDAO;

public class InterestProfileEventDAOHibernate extends GenericDAOHibernate<InterestProfileEvent> implements InterestProfileEventDAO
{

	public InterestProfileEventDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<InterestProfileEvent> getAllInterestProfiles()
	{
		Query query = getCurrentSession().createQuery( "FROM InterestProfileEvent ORDER BY interestProfileType, name" );

		@SuppressWarnings( "unchecked" )
		List<InterestProfileEvent> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public List<InterestProfileEvent> getAllActiveInterestProfile()
	{
		Query query = getCurrentSession().createQuery( "FROM InterestProfileEvent WHERE active IS true" );

		@SuppressWarnings( "unchecked" )
		List<InterestProfileEvent> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public List<InterestProfileEvent> getAllActiveInterestProfile( InterestProfileType interestProfileType )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "FROM InterestProfileEvent " );
		stringBuilder.append( "WHERE active IS true " );
		stringBuilder.append( "AND interestProfileType = :interestProfileType" );

		Query query = getCurrentSession().createQuery( stringBuilder.toString() );
		query.setParameter( "interestProfileType", interestProfileType );

		@SuppressWarnings( "unchecked" )
		List<InterestProfileEvent> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public Map<String, InterestProfileEvent> getInterestProfileMap()
	{

		Query query = getCurrentSession().createQuery( "FROM InterestProfileEvent" );

		@SuppressWarnings( "unchecked" )
		List<InterestProfileEvent> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyMap();

		Map<String, InterestProfileEvent> interestProfileMap = new HashMap<String, InterestProfileEvent>();
		for ( InterestProfileEvent interestProfile : interestProfiles )
		{
			interestProfileMap.put( interestProfile.getInterestProfileType().toString(), interestProfile );
		}

		return interestProfileMap;
	}

}

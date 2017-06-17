package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.InterestProfileCircle;
import de.rwth.i9.palm.model.InterestProfileType;
import de.rwth.i9.palm.persistence.InterestProfileCircleDAO;

public class InterestProfileCircleDAOHibernate extends GenericDAOHibernate<InterestProfileCircle> implements InterestProfileCircleDAO
{

	public InterestProfileCircleDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<InterestProfileCircle> getAllInterestProfiles()
	{
		Query query = getCurrentSession().createQuery( "FROM InterestProfileCircle ORDER BY interestProfileType, name" );

		@SuppressWarnings( "unchecked" )
		List<InterestProfileCircle> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public List<InterestProfileCircle> getAllActiveInterestProfile()
	{
		Query query = getCurrentSession().createQuery( "FROM InterestProfileCircle WHERE active IS true" );

		@SuppressWarnings( "unchecked" )
		List<InterestProfileCircle> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public List<InterestProfileCircle> getAllActiveInterestProfile( InterestProfileType interestProfileType )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "FROM InterestProfileCircle " );
		stringBuilder.append( "WHERE active IS true " );
		stringBuilder.append( "AND interestProfileType = :interestProfileType" );

		Query query = getCurrentSession().createQuery( stringBuilder.toString() );
		query.setParameter( "interestProfileType", interestProfileType );

		@SuppressWarnings( "unchecked" )
		List<InterestProfileCircle> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public Map<String, InterestProfileCircle> getInterestProfileMap()
	{

		Query query = getCurrentSession().createQuery( "FROM InterestProfileCircle" );

		@SuppressWarnings( "unchecked" )
		List<InterestProfileCircle> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyMap();

		Map<String, InterestProfileCircle> interestProfileMap = new HashMap<String, InterestProfileCircle>();
		for ( InterestProfileCircle interestProfile : interestProfiles )
		{
			interestProfileMap.put( interestProfile.getInterestProfileType().toString(), interestProfile );
		}

		return interestProfileMap;
	}

}

package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.InterestProfile;
import de.rwth.i9.palm.model.InterestProfileType;
import de.rwth.i9.palm.persistence.InterestProfileDAO;

public class InterestProfileDAOHibernate extends GenericDAOHibernate<InterestProfile>implements InterestProfileDAO
{

	public InterestProfileDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<InterestProfile> getAllInterestProfiles()
	{
		Query query = getCurrentSession().createQuery( "FROM InterestProfile ORDER BY interestProfileType, name" );

		@SuppressWarnings( "unchecked" )
		List<InterestProfile> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public List<InterestProfile> getAllActiveInterestProfile()
	{
		Query query = getCurrentSession().createQuery( "FROM InterestProfile WHERE active IS true" );

		@SuppressWarnings( "unchecked" )
		List<InterestProfile> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public List<InterestProfile> getAllActiveInterestProfile( InterestProfileType interestProfileType )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "FROM InterestProfile " );
		stringBuilder.append( "WHERE active IS true " );
		stringBuilder.append( "AND interestProfileType = :interestProfileType" );

		Query query = getCurrentSession().createQuery( stringBuilder.toString() );
		query.setParameter( "interestProfileType", interestProfileType );

		@SuppressWarnings( "unchecked" )
		List<InterestProfile> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public Map<String, InterestProfile> getInterestProfileMap()
	{

		Query query = getCurrentSession().createQuery( "FROM InterestProfile" );

		@SuppressWarnings( "unchecked" )
		List<InterestProfile> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyMap();

		Map<String, InterestProfile> interestProfileMap = new HashMap<String, InterestProfile>();
		for ( InterestProfile interestProfile : interestProfiles )
		{
			interestProfileMap.put( interestProfile.getInterestProfileType().toString(), interestProfile );
		}

		return interestProfileMap;
	}

}

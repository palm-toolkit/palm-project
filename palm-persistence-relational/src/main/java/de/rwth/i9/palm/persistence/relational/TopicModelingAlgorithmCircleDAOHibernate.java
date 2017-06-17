package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.InterestProfileType;
import de.rwth.i9.palm.model.TopicModelingAlgorithmCircle;
import de.rwth.i9.palm.persistence.TopicModelingAlgorithmCircleDAO;

public class TopicModelingAlgorithmCircleDAOHibernate extends GenericDAOHibernate<TopicModelingAlgorithmCircle> implements TopicModelingAlgorithmCircleDAO
{

	public TopicModelingAlgorithmCircleDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<TopicModelingAlgorithmCircle> getAllInterestProfiles()
	{
		Query query = getCurrentSession().createQuery( "FROM TopicModelingAlgorithmCircle ORDER BY interestProfileType, name" );

		@SuppressWarnings( "unchecked" )
		List<TopicModelingAlgorithmCircle> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public List<TopicModelingAlgorithmCircle> getAllActiveInterestProfile()
	{
		Query query = getCurrentSession().createQuery( "FROM TopicModelingAlgorithmCircle WHERE active IS true" );

		@SuppressWarnings( "unchecked" )
		List<TopicModelingAlgorithmCircle> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public List<TopicModelingAlgorithmCircle> getAllActiveInterestProfile( InterestProfileType interestProfileType )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "FROM TopicModelingAlgorithmCircle " );
		stringBuilder.append( "WHERE active IS true " );
		stringBuilder.append( "AND interestProfileType = :interestProfileType" );

		Query query = getCurrentSession().createQuery( stringBuilder.toString() );
		query.setParameter( "interestProfileType", interestProfileType );

		@SuppressWarnings( "unchecked" )
		List<TopicModelingAlgorithmCircle> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public Map<String, TopicModelingAlgorithmCircle> getInterestProfileMap()
	{

		Query query = getCurrentSession().createQuery( "FROM TopicModelingAlgorithmCircle" );

		@SuppressWarnings( "unchecked" )
		List<TopicModelingAlgorithmCircle> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyMap();

		Map<String, TopicModelingAlgorithmCircle> interestProfileMap = new HashMap<String, TopicModelingAlgorithmCircle>();
		for ( TopicModelingAlgorithmCircle interestProfile : interestProfiles )
		{
			interestProfileMap.put( interestProfile.getInterestProfileType().toString(), interestProfile );
		}

		return interestProfileMap;
	}

}

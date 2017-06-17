package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.InterestProfileType;
import de.rwth.i9.palm.model.TopicModelingAlgorithmAuthor;
import de.rwth.i9.palm.persistence.TopicModelingAlgorithmAuthorDAO;

public class TopicModelingAlgorithmAuthorDAOHibernate extends GenericDAOHibernate<TopicModelingAlgorithmAuthor> implements TopicModelingAlgorithmAuthorDAO
{

	public TopicModelingAlgorithmAuthorDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<TopicModelingAlgorithmAuthor> getAllInterestProfiles()
	{
		Query query = getCurrentSession().createQuery( "FROM TopicModelingAlgorithmAuthor ORDER BY interestProfileType, name" );

		@SuppressWarnings( "unchecked" )
		List<TopicModelingAlgorithmAuthor> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public List<TopicModelingAlgorithmAuthor> getAllActiveInterestProfile()
	{
		Query query = getCurrentSession().createQuery( "FROM TopicModelingAlgorithmAuthor WHERE active IS true" );

		@SuppressWarnings( "unchecked" )
		List<TopicModelingAlgorithmAuthor> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public List<TopicModelingAlgorithmAuthor> getAllActiveInterestProfile( InterestProfileType interestProfileType )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "FROM TopicModelingAlgorithmAuthor " );
		stringBuilder.append( "WHERE active IS true " );
		stringBuilder.append( "AND interestProfileType = :interestProfileType" );

		Query query = getCurrentSession().createQuery( stringBuilder.toString() );
		query.setParameter( "interestProfileType", interestProfileType );

		@SuppressWarnings( "unchecked" )
		List<TopicModelingAlgorithmAuthor> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyList();

		return interestProfiles;
	}

	@Override
	public Map<String, TopicModelingAlgorithmAuthor> getInterestProfileMap()
	{

		Query query = getCurrentSession().createQuery( "FROM TopicModelingAlgorithmAuthor" );

		@SuppressWarnings( "unchecked" )
		List<TopicModelingAlgorithmAuthor> interestProfiles = query.list();

		if ( interestProfiles == null )
			return Collections.emptyMap();

		Map<String, TopicModelingAlgorithmAuthor> interestProfileMap = new HashMap<String, TopicModelingAlgorithmAuthor>();
		for ( TopicModelingAlgorithmAuthor interestProfile : interestProfiles )
		{
			interestProfileMap.put( interestProfile.getInterestProfileType().toString(), interestProfile );
		}

		return interestProfileMap;
	}

}

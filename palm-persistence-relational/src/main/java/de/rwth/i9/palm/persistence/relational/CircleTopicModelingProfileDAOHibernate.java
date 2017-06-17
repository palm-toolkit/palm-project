package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.CircleTopicModelingProfile;
import de.rwth.i9.palm.persistence.CircleTopicModelingProfileDAO;

public class CircleTopicModelingProfileDAOHibernate extends GenericDAOHibernate<CircleTopicModelingProfile> implements CircleTopicModelingProfileDAO
{

	public CircleTopicModelingProfileDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<CircleTopicModelingProfile> getDefaultCircleTopicModelingProfile()
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM CircleTopicModelingProfile " );
		queryString.append( "WHERE defaultProfile IS true " );
		queryString.append( "AND valid IS true " );

		Query query = getCurrentSession().createQuery( queryString.toString() );

		@SuppressWarnings( "unchecked" )
		List<CircleTopicModelingProfile> circleTopicModelingProfiles = query.list();

		if ( circleTopicModelingProfiles == null || circleTopicModelingProfiles.isEmpty() )
			return Collections.emptyList();

		return circleTopicModelingProfiles;
	}

}

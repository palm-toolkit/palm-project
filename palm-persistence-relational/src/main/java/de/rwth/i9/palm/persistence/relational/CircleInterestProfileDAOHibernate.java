package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.CircleInterestProfile;
import de.rwth.i9.palm.persistence.CircleInterestProfileDAO;

public class CircleInterestProfileDAOHibernate extends GenericDAOHibernate<CircleInterestProfile> implements CircleInterestProfileDAO
{

	public CircleInterestProfileDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<CircleInterestProfile> getDefaultCircleInterestProfile()
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM CircleInterestProfile " );
		queryString.append( "WHERE defaultProfile IS true " );
		queryString.append( "AND valid IS true " );

		Query query = getCurrentSession().createQuery( queryString.toString() );

		@SuppressWarnings( "unchecked" )
		List<CircleInterestProfile> circleInterestProfiles = query.list();

		if ( circleInterestProfiles == null || circleInterestProfiles.isEmpty() )
			return Collections.emptyList();

		return circleInterestProfiles;
	}

}

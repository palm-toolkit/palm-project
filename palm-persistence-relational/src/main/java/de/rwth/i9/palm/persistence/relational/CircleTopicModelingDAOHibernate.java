package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.CircleTopicModeling;
import de.rwth.i9.palm.persistence.CircleTopicModelingDAO;

public class CircleTopicModelingDAOHibernate extends GenericDAOHibernate<CircleTopicModeling> implements CircleTopicModelingDAO
{

	public CircleTopicModelingDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

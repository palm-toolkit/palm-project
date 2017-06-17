package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.CircleInterest;
import de.rwth.i9.palm.persistence.CircleInterestDAO;

public class CircleInterestDAOHibernate extends GenericDAOHibernate<CircleInterest> implements CircleInterestDAO
{

	public CircleInterestDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

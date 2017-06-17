package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.InterestProfileProperty;
import de.rwth.i9.palm.persistence.InterestProfilePropertyDAO;

public class InterestProfilePropertyDAOHibernate extends GenericDAOHibernate<InterestProfileProperty>implements InterestProfilePropertyDAO
{

	public InterestProfilePropertyDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.EventInterest;
import de.rwth.i9.palm.persistence.EventInterestDAO;

public class EventInterestDAOHibernate extends GenericDAOHibernate<EventInterest> implements EventInterestDAO
{

	public EventInterestDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

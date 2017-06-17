package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.PublicationHistory;
import de.rwth.i9.palm.persistence.PublicationHistoryDAO;

public class PublicationHistoryDAOHibernate extends GenericDAOHibernate<PublicationHistory> implements PublicationHistoryDAO
{

	public PublicationHistoryDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

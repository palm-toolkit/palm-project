package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.PublicationSource;
import de.rwth.i9.palm.persistence.PublicationSourceDAO;

public class PublicationSourceDAOHibernate extends GenericDAOHibernate<PublicationSource> implements PublicationSourceDAO
{

	public PublicationSourceDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

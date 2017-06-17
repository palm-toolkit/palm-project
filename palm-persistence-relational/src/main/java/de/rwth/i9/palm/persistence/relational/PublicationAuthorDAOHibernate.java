package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.PublicationAuthor;
import de.rwth.i9.palm.persistence.PublicationAuthorDAO;

public class PublicationAuthorDAOHibernate extends GenericDAOHibernate<PublicationAuthor> implements PublicationAuthorDAO
{

	public PublicationAuthorDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

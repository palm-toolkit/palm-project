package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.AuthorSource;
import de.rwth.i9.palm.persistence.AuthorSourceDAO;

public class AuthorSourceDAOHibernate extends GenericDAOHibernate<AuthorSource> implements AuthorSourceDAO
{

	public AuthorSourceDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

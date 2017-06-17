package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.AuthorAlias;
import de.rwth.i9.palm.persistence.AuthorAliasDAO;

public class AuthorAliasDAOHibernate extends GenericDAOHibernate<AuthorAlias>implements AuthorAliasDAO
{

	public AuthorAliasDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

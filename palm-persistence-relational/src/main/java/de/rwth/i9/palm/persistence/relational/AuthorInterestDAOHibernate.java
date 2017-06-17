package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.AuthorInterest;
import de.rwth.i9.palm.persistence.AuthorInterestDAO;

public class AuthorInterestDAOHibernate extends GenericDAOHibernate<AuthorInterest>implements AuthorInterestDAO
{

	public AuthorInterestDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

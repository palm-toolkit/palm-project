package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.AuthorTopicModeling;
import de.rwth.i9.palm.persistence.AuthorTopicModelingDAO;

public class AuthorTopicModelingDAOHibernate extends GenericDAOHibernate<AuthorTopicModeling> implements AuthorTopicModelingDAO
{

	public AuthorTopicModelingDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

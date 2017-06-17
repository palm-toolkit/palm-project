package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.SourceProperty;
import de.rwth.i9.palm.persistence.SourcePropertyDAO;

public class SourcePropertyDAOHibernate extends GenericDAOHibernate<SourceProperty>implements SourcePropertyDAO
{

	public SourcePropertyDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

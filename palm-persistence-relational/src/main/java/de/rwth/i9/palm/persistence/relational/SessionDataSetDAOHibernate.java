package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.SessionDataSet;
import de.rwth.i9.palm.persistence.SessionDataSetDAO;

public class SessionDataSetDAOHibernate extends GenericDAOHibernate<SessionDataSet> implements SessionDataSetDAO
{

	public SessionDataSetDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public SessionDataSet getByUsername( String userName )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
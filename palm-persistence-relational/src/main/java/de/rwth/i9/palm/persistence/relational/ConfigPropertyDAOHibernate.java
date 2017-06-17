package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.ConfigProperty;
import de.rwth.i9.palm.persistence.ConfigPropertyDAO;

public class ConfigPropertyDAOHibernate extends GenericDAOHibernate<ConfigProperty> implements ConfigPropertyDAO
{

	public ConfigPropertyDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}
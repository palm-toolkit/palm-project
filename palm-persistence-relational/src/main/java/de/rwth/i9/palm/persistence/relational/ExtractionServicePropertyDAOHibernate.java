package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.ExtractionServiceProperty;
import de.rwth.i9.palm.persistence.ExtractionServicePropertyDAO;

public class ExtractionServicePropertyDAOHibernate extends GenericDAOHibernate<ExtractionServiceProperty>implements ExtractionServicePropertyDAO
{

	public ExtractionServicePropertyDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

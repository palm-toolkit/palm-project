package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.PublicationFile;
import de.rwth.i9.palm.persistence.PublicationFileDAO;

public class PublicationFileDAOHibernate extends GenericDAOHibernate<PublicationFile>implements PublicationFileDAO
{

	public PublicationFileDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

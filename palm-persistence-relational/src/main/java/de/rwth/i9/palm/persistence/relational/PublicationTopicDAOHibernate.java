package de.rwth.i9.palm.persistence.relational;

import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.PublicationTopic;
import de.rwth.i9.palm.persistence.PublicationTopicDAO;

public class PublicationTopicDAOHibernate extends GenericDAOHibernate<PublicationTopic> implements PublicationTopicDAO
{

	public PublicationTopicDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

}

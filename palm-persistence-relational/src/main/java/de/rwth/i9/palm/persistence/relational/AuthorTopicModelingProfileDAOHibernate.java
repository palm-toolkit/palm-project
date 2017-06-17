package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.AuthorTopicModelingProfile;
import de.rwth.i9.palm.persistence.AuthorTopicModelingProfileDAO;

public class AuthorTopicModelingProfileDAOHibernate extends GenericDAOHibernate<AuthorTopicModelingProfile> implements AuthorTopicModelingProfileDAO
{

	public AuthorTopicModelingProfileDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<AuthorTopicModelingProfile> getDefaultAuthorTopicModelingProfile()
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM AuthorTopicModelingProfile " );
		queryString.append( "WHERE defaultProfile IS true " );
		queryString.append( "AND valid IS true " );

		Query query = getCurrentSession().createQuery( queryString.toString() );

		@SuppressWarnings( "unchecked" )
		List<AuthorTopicModelingProfile> authorTopicModelingProfiles = query.list();

		if ( authorTopicModelingProfiles == null || authorTopicModelingProfiles.isEmpty() )
			return Collections.emptyList();

		return authorTopicModelingProfiles;
	}

}

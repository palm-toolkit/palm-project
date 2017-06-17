package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.AuthorInterestProfile;
import de.rwth.i9.palm.persistence.AuthorInterestProfileDAO;

public class AuthorInterestProfileDAOHibernate extends GenericDAOHibernate<AuthorInterestProfile>implements AuthorInterestProfileDAO
{

	public AuthorInterestProfileDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<AuthorInterestProfile> getDefaultAuthorInterestProfile()
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM AuthorInterestProfile " );
		queryString.append( "WHERE defaultProfile IS true " );
		queryString.append( "AND valid IS true " );

		Query query = getCurrentSession().createQuery( queryString.toString() );

		@SuppressWarnings( "unchecked" )
		List<AuthorInterestProfile> authorInterestProfiles = query.list();

		if ( authorInterestProfiles == null || authorInterestProfiles.isEmpty() )
			return Collections.emptyList();

		return authorInterestProfiles;
	}

}

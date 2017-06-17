package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Subject;
import de.rwth.i9.palm.persistence.SubjectDAO;

public class SubjectDAOHibernate extends GenericDAOHibernate<Subject> implements SubjectDAO
{

	public SubjectDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public Subject getSubjectByLabel( String label )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Subject " );
		queryString.append( "WHERE label = :label " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "label", label );

		@SuppressWarnings( "unchecked" )
		List<Subject> subjects = query.list();

		if ( subjects == null || subjects.isEmpty() )
			return null;

		return subjects.get( 0 );
	}

}

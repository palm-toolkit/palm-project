package de.rwth.i9.palm.persistence.relational;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Role;
import de.rwth.i9.palm.persistence.RoleDAO;

public class RoleDAOHibernate extends GenericDAOHibernate<Role> implements RoleDAO
{

	public RoleDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public Role getRoleByName( String roleName )
	{
		if ( roleName.equalsIgnoreCase( "" ) )
			return null;

		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Role r " );
		queryString.append( "WHERE r.name = :roleName " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "roleName", roleName );

		@SuppressWarnings( "unchecked" )
		List<Role> roles = query.list();

		if ( roles == null || roles.isEmpty() )
			return null;

		return roles.get( 0 );
	}

}

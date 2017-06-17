package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import de.rwth.i9.palm.model.Function;
import de.rwth.i9.palm.model.Role;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.persistence.UserDAO;

public class UserDAOHibernate extends GenericDAOHibernate<User> implements UserDAO
{

	public UserDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public boolean isAuthorizedForFunction( User user, String functionName )
	{
		if ( user == null || StringUtils.isEmpty( functionName ) )
			return false;

		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT fn FROM User user " );
		queryString.append( "JOIN user.functions fn " );
		queryString.append( "WHERE user = :user " );
		queryString.append( "AND fn.name = :fnName" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "user", user );
		query.setParameter( "fnName", functionName );

		@SuppressWarnings( "unchecked" )
		List<Function> functions = query.list();

		if ( functions == null || functions.size() == 0 )
			return false;

		return true;
	}

	@Override
	public User getByUsername( String username )
	{

		if ( username.equalsIgnoreCase( "" ) )
			return null;

		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM User u " );
		queryString.append( "WHERE u.username = :username " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "username", username );

		@SuppressWarnings( "unchecked" )
		List<User> users = query.list();

		if ( users == null || users.isEmpty() )
			return null;

		return users.get( 0 );
	}

	@Override
	public List<User> getByName( String name )
	{

		if ( name.equalsIgnoreCase( "" ) )
			return Collections.emptyList();

		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM User u " );
		queryString.append( "WHERE u.name LIKE :name " );
		queryString.append( "ORDER BY u.name ASC" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "name", "%" + name + "%" );

		@SuppressWarnings( "unchecked" )
		List<User> users = query.list();

		if ( users == null || users.isEmpty() )
			return Collections.emptyList();

		return users;
	}

	@Override
	public User touch( User user )
	{
		if ( user == null )
			return null;

		user.setLastLogin( DateTime.now().toDate() );

		return this.persist( user );
	}

	@Override
	public boolean isAuthorizedForRole( User user, String roleName )
	{
		if ( user == null || StringUtils.isEmpty( roleName ) )
			return false;

		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT role FROM User user " );
		queryString.append( "JOIN user.role role " );
		queryString.append( "WHERE user = :user " );
		queryString.append( "AND role.name = :rlName" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "user", user );
		query.setParameter( "rlName", roleName );

		@SuppressWarnings( "unchecked" )
		List<Role> roles = query.list();

		if ( roles == null || roles.size() == 0 )
			return false;

		return true;
	}

	@Override
	public boolean isUsernameExist( String username )
	{
		if ( username == null || username.isEmpty() )
			return false;

		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM User " );
		queryString.append( "WHERE username = :username " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "username", username );

		@SuppressWarnings( "unchecked" )
		List<User> users = query.list();

		if ( users == null || users.size() == 0 )
			return false;

		return true;
	}

	@Override
	public List<User> allUsers()
	{

		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM User u " );
		queryString.append( "ORDER BY u.name ASC" );

		Query query = getCurrentSession().createQuery( queryString.toString() );

		@SuppressWarnings( "unchecked" )
		List<User> users = query.list();

		if ( users == null || users.isEmpty() )
			return Collections.emptyList();

		return users;
	}

}

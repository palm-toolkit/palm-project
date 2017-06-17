package de.rwth.i9.palm.persistence;

import java.util.List;

import de.rwth.i9.palm.model.User;

public interface UserDAO extends GenericDAO<User>, InstantiableDAO
{
	/**
	 * @param user
	 * @param functionName
	 * @return
	 */
	public boolean isAuthorizedForFunction( final User user, final String functionName );

	/**
	 * Get user object by username
	 * 
	 * @param username
	 * @return
	 */
	public User getByUsername( final String username );

	/**
	 * Get user object by name
	 * 
	 * @param username
	 * @return
	 */
	public List<User> getByName( final String name );

	/**
	 * @param user
	 */
	public User touch( final User user );

	public boolean isAuthorizedForRole( User user, String roleName );

	public boolean isUsernameExist( String username );

	public List<User> allUsers();
}

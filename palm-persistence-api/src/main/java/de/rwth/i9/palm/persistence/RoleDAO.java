package de.rwth.i9.palm.persistence;

import de.rwth.i9.palm.model.Role;

public interface RoleDAO extends GenericDAO<Role>, InstantiableDAO
{
	public Role getRoleByName( String roleName );
}

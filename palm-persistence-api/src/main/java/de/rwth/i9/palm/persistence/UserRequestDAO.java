package de.rwth.i9.palm.persistence;

import de.rwth.i9.palm.model.RequestType;
import de.rwth.i9.palm.model.UserRequest;

public interface UserRequestDAO extends GenericDAO<UserRequest>, InstantiableDAO
{
	public UserRequest getByTypeAndQuery( RequestType requestType, String queryString );
}

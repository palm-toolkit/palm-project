package de.rwth.i9.palm.persistence;

import java.util.List;

import de.rwth.i9.palm.model.AuthorInterestProfile;

public interface AuthorInterestProfileDAO extends GenericDAO<AuthorInterestProfile>, InstantiableDAO
{
	List<AuthorInterestProfile> getDefaultAuthorInterestProfile();
}

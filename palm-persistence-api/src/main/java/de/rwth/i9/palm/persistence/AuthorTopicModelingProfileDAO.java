package de.rwth.i9.palm.persistence;

import java.util.List;

import de.rwth.i9.palm.model.AuthorTopicModelingProfile;

public interface AuthorTopicModelingProfileDAO extends GenericDAO<AuthorTopicModelingProfile>, InstantiableDAO
{
	List<AuthorTopicModelingProfile> getDefaultAuthorTopicModelingProfile();
}

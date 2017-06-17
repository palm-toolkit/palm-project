package de.rwth.i9.palm.persistence;

import java.util.List;

import de.rwth.i9.palm.model.CircleTopicModelingProfile;

public interface CircleTopicModelingProfileDAO extends GenericDAO<CircleTopicModelingProfile>, InstantiableDAO
{
	List<CircleTopicModelingProfile> getDefaultCircleTopicModelingProfile();
}

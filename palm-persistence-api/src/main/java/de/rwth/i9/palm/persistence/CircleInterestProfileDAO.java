package de.rwth.i9.palm.persistence;

import java.util.List;

import de.rwth.i9.palm.model.CircleInterestProfile;

public interface CircleInterestProfileDAO extends GenericDAO<CircleInterestProfile>, InstantiableDAO
{
	List<CircleInterestProfile> getDefaultCircleInterestProfile();
}

package de.rwth.i9.palm.persistence;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.InterestProfile;
import de.rwth.i9.palm.model.InterestProfileType;

public interface InterestProfileDAO extends GenericDAO<InterestProfile>, InstantiableDAO
{
	public List<InterestProfile> getAllInterestProfiles();

	public List<InterestProfile> getAllActiveInterestProfile();

	public List<InterestProfile> getAllActiveInterestProfile( InterestProfileType interestProfileType );

	public Map<String, InterestProfile> getInterestProfileMap();
}

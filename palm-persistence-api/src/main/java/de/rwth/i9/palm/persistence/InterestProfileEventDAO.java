package de.rwth.i9.palm.persistence;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.InterestProfileEvent;
import de.rwth.i9.palm.model.InterestProfileType;

public interface InterestProfileEventDAO extends GenericDAO<InterestProfileEvent>, InstantiableDAO
{
	public List<InterestProfileEvent> getAllInterestProfiles();

	public List<InterestProfileEvent> getAllActiveInterestProfile();

	public List<InterestProfileEvent> getAllActiveInterestProfile( InterestProfileType interestProfileType );

	public Map<String, InterestProfileEvent> getInterestProfileMap();
}

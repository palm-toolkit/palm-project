package de.rwth.i9.palm.persistence;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.InterestProfileCircle;
import de.rwth.i9.palm.model.InterestProfileType;

public interface InterestProfileCircleDAO extends GenericDAO<InterestProfileCircle>, InstantiableDAO
{
	public List<InterestProfileCircle> getAllInterestProfiles();

	public List<InterestProfileCircle> getAllActiveInterestProfile();

	public List<InterestProfileCircle> getAllActiveInterestProfile( InterestProfileType interestProfileType );

	public Map<String, InterestProfileCircle> getInterestProfileMap();
}

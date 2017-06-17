package de.rwth.i9.palm.persistence;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.InterestProfileType;
import de.rwth.i9.palm.model.TopicModelingAlgorithmCircle;

public interface TopicModelingAlgorithmCircleDAO extends GenericDAO<TopicModelingAlgorithmCircle>, InstantiableDAO
{
	public List<TopicModelingAlgorithmCircle> getAllInterestProfiles();

	public List<TopicModelingAlgorithmCircle> getAllActiveInterestProfile();

	public List<TopicModelingAlgorithmCircle> getAllActiveInterestProfile( InterestProfileType interestProfileType );

	public Map<String, TopicModelingAlgorithmCircle> getInterestProfileMap();
}

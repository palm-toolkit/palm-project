package de.rwth.i9.palm.persistence;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.InterestProfileType;
import de.rwth.i9.palm.model.TopicModelingAlgorithmAuthor;

public interface TopicModelingAlgorithmAuthorDAO extends GenericDAO<TopicModelingAlgorithmAuthor>, InstantiableDAO
{
	public List<TopicModelingAlgorithmAuthor> getAllInterestProfiles();

	public List<TopicModelingAlgorithmAuthor> getAllActiveInterestProfile();

	public List<TopicModelingAlgorithmAuthor> getAllActiveInterestProfile( InterestProfileType interestProfileType );

	public Map<String, TopicModelingAlgorithmAuthor> getInterestProfileMap();
}

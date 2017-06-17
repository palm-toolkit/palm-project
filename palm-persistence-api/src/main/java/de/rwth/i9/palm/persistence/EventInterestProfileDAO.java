package de.rwth.i9.palm.persistence;

import java.util.List;

import de.rwth.i9.palm.model.EventInterestProfile;

public interface EventInterestProfileDAO extends GenericDAO<EventInterestProfile>, InstantiableDAO
{
	List<EventInterestProfile> getDefaultEventInterestProfile();
}

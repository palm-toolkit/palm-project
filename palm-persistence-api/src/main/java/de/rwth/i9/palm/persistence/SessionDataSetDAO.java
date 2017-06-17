package de.rwth.i9.palm.persistence;

import de.rwth.i9.palm.model.SessionDataSet;

public interface SessionDataSetDAO extends GenericDAO<SessionDataSet>, InstantiableDAO
{
	public SessionDataSet getByUsername( String userName );
}

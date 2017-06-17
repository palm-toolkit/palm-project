package de.rwth.i9.palm.persistence;

import de.rwth.i9.palm.model.Subject;

public interface SubjectDAO extends GenericDAO<Subject>, InstantiableDAO
{
	public Subject getSubjectByLabel( String label );
}

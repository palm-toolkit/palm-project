package de.rwth.i9.palm.persistence;

import java.util.List;

import de.rwth.i9.palm.model.Institution;

public interface InstitutionDAO extends GenericDAO<Institution>, InstantiableDAO
{

	Institution getByUri( String institutionUrl );

	List<Institution> getByName( String name );

	List<Institution> getWithFullTextSearch( String label );

	void doReindexing() throws InterruptedException;
}

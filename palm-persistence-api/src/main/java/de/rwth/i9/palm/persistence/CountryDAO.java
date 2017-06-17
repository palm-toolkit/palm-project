package de.rwth.i9.palm.persistence;

import de.rwth.i9.palm.model.Country;

public interface CountryDAO extends GenericDAO<Country>, InstantiableDAO
{
	public Country getCountryByName( String name );
}

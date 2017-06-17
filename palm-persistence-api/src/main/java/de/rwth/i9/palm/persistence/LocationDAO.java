package de.rwth.i9.palm.persistence;

import java.util.List;

import de.rwth.i9.palm.model.Country;
import de.rwth.i9.palm.model.Location;

public interface LocationDAO extends GenericDAO<Location>, InstantiableDAO
{
	public List<Location> getByCountry( String countryName );

	public Location getByCountryAndCity( Country country, String city );
}

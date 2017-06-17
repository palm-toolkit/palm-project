package de.rwth.i9.palm.persistence;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.Config;

public interface ConfigDAO extends GenericDAO<Config>, InstantiableDAO
{
	public Config getConfigByName( String configType );

	public List<Config> getAllConfig();

	public Map<String, Config> getConfigMap();

}

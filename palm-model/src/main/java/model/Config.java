package de.rwth.i9.palm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "config" )
public class Config extends PersistableResource
{
	@Column( nullable = false, unique = true )
	private String name;

	@Column( columnDefinition = "bit default 1" )
	private boolean active = true;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "config", orphanRemoval = true )
	private List<ConfigProperty> configProperties;

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive( boolean active )
	{
		this.active = active;
	}

	public List<ConfigProperty> getConfigProperties()
	{
		return configProperties;
	}

	public void setConfigProperties( List<ConfigProperty> configProperties )
	{
		if ( this.configProperties == null )
			this.configProperties = new ArrayList<ConfigProperty>();
		this.configProperties.clear();
		this.configProperties.addAll( configProperties );
	}

	public Config addConfigProperty( ConfigProperty configProperty )
	{
		if ( this.configProperties == null )
			this.configProperties = new ArrayList<ConfigProperty>();
		this.configProperties.add( configProperty );
		return this;
	}

	public ConfigProperty getConfigPropertyByIdentifiers( String identifier, String identifier2 )
	{
		if ( this.configProperties != null )
		{
			for ( ConfigProperty configProperty : this.configProperties )
			{
				if ( configProperty.getMainIdentifier().equals( identifier ) && configProperty.getSecondaryIdentifier().equals( identifier2 ) )
					return configProperty;
			}
		}
		return null;
	}

	public String getConfigPropertyValueByIdentifiers( String identifier, String identifier2 )
	{
		if ( this.configProperties != null )
		{
			for ( ConfigProperty configProperty : this.configProperties )
			{
				if ( configProperty.getMainIdentifier().equals( identifier ) && configProperty.getSecondaryIdentifier().equals( identifier2 ) )
					return configProperty.getValue();
			}
		}
		return null;
	}

	public Map<String, String> getValidConfigPropertyListByMainIdentifierMap( String identifier )
	{
		Map<String, String> validConfigProperties = new HashMap<String, String>();
		if ( this.configProperties != null )
		{
			for ( ConfigProperty configProperty : this.configProperties )
			{
				if ( configProperty.getMainIdentifier().equals( identifier ) )
				{
					validConfigProperties.put( configProperty.getSecondaryIdentifier(), configProperty.getValue() );
				}
			}
		}
		return validConfigProperties;
	}

}

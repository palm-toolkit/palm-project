package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "country" )
public class Country extends PersistableResource
{
	@Column( length = 2 )
	private String iso2;

	@Column( length = 3 )
	private String iso3;

	@Column
	private String name;

	public String getIso2()
	{
		return iso2;
	}

	public void setIso2( String iso2 )
	{
		this.iso2 = iso2;
	}

	public String getIso3()
	{
		return iso3;
	}

	public void setIso3( String iso3 )
	{
		this.iso3 = iso3;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}
}

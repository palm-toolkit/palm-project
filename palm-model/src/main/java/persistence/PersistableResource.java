package de.rwth.i9.palm.persistence;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class PersistableResource extends PersistableType
{

	// properties
	@Column( unique = true )
	private String uri;

	// getter / setter

	public PersistableResource()
	{
		super();
	}

	public String getURI()
	{
		return uri;
	}

	public void setURI( final String uri )
	{
		this.uri = uri;
	}

}

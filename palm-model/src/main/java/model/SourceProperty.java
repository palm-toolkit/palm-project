package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "source_property" )
public class SourceProperty extends PersistableResource
{
	public SourceProperty()
	{
	}
	public SourceProperty( String mainIdentifier, String secondaryIdentifier, String value )
	{
		this.mainIdentifier = mainIdentifier;
		this.secondaryIdentifier = secondaryIdentifier;
		this.value = value;
	}

	@Column( length = 50 )
	private String mainIdentifier;

	@Column( length = 50 )
	private String secondaryIdentifier;

	@Column
	@Lob
	private String value;

	@Column
	private java.sql.Timestamp lastModified;

	@Column
	private String expiredEvery;

	@Column( columnDefinition = "bit default 1" )
	private boolean valid = true;

	// relation
	@ManyToOne
	@JoinColumn( name = "source_id" )
	private Source source;

	public String getMainIdentifier()
	{
		return mainIdentifier;
	}

	public void setMainIdentifier( String mainIdentifier )
	{
		this.mainIdentifier = mainIdentifier;
	}

	public String getSecondaryIdentifier()
	{
		return secondaryIdentifier;
	}

	public void setSecondaryIdentifier( String secondaryIdentifier )
	{
		this.secondaryIdentifier = secondaryIdentifier;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue( String value )
	{
		this.value = value;
	}

	public java.sql.Timestamp getLastModified()
	{
		return lastModified;
	}

	public void setLastModified( java.sql.Timestamp lastModified )
	{
		this.lastModified = lastModified;
	}

	public String getExpiredEvery()
	{
		return expiredEvery;
	}

	public void setExpiredEvery( String expiredEvery )
	{
		this.expiredEvery = expiredEvery;
	}

	public Source getSource()
	{
		return source;
	}

	public void setSource( Source source )
	{
		this.source = source;
	}

	public boolean isValid()
	{
		return valid;
	}

	public void setValid( boolean valid )
	{
		this.valid = valid;
	}

}

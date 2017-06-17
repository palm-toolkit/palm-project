package de.rwth.i9.palm.persistence;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.search.annotations.DocumentId;

import de.rwth.i9.palm.util.IdentifierFactory;

/**
 * The most general class for types that should be persistable in the context of
 * this model package. It contains only an id of length of 40 characters. The id
 * is a UUID which is set while instantiating of the class.
 */
@MappedSuperclass
public abstract class PersistableType
{
	// properties
	@Id
	@DocumentId
	@Column( length = 40, unique = true, nullable = false )
	private String id;

	public PersistableType()
	{
		id = IdentifierFactory.getNextDefaultIdentifier();
	}

	// getter / setter

	public String getId()
	{
		return id;
	}

	public void setId( final String id )
	{
		this.id = id;
	}
}

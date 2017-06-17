package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "nodes" )
public class Nodes extends PersistableResource
{
	@Column( nullable = false )
	// @GeneratedValue( strategy = GenerationType.AUTO )
	private String Id;

	@Column
	private String label;

	public String getId()
	{
		return Id;
	}

	public void setId( String id )
	{
		this.Id = id;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel( String label )
	{
		this.label = label;
	}

}

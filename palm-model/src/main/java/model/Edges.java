package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "edges" )
public class Edges extends PersistableResource
{
	@Column( nullable = false )
	private String Id;

	@Column
	private String source;

	@Column
	private String target;

	@Column
	private String label;

	@Column
	private String weight;

	public String getId()
	{
		return Id;
	}

	public void setId( String id )
	{
		this.Id = id;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource( String source )
	{
		this.source = source;
	}

	public String getTarget()
	{
		return target;
	}

	public void setTarget( String target )
	{
		this.target = target;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel( String label )
	{
		this.label = label;
	}

	public String getWeight()
	{
		return weight;
	}

	public void setWeight( String weight )
	{
		this.weight = weight;
	}


}

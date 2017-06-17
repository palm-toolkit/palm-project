package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "function" )
public class Function extends PersistableResource
{
	@Enumerated( EnumType.STRING )
	@Column( length = 16 )
	private FunctionType functionType;

	@Column
	private String name;

	@Column
	private String comment;

	@Column( columnDefinition = "VARCHAR(16) DEFAULT 'DEFAULT'" )
	private String grantType;

	// getter / setter

	public FunctionType getFunctionType()
	{
		return functionType;
	}

	public void setFunctionType( final FunctionType functionType )
	{
		this.functionType = functionType;
	}

	public String getName()
	{
		return name;
	}

	public void setName( final String name )
	{
		this.name = name;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment( String comment )
	{
		this.comment = comment;
	}

	public String getGrantType()
	{
		return grantType;
	}

	public void setGrantType( String grantType )
	{
		this.grantType = grantType;
	}
}

package de.rwth.i9.palm.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "role" )
public class Role extends PersistableResource
{
	@Column
	private String name;

	// relation
	@ManyToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	@JoinTable(
			name = "Role_Function",
			joinColumns = @JoinColumn( name = "role_id" ),
			inverseJoinColumns = @JoinColumn( name = "function_id" ) )
	private List<Function> functions;

	// getter setter
	public String getName()
	{
		return name;
	}

	public void setName( final String name )
	{
		this.name = name;
	}

	public List<Function> getFunctions()
	{
		return functions;
	}

	public void setFunctions( final List<Function> function )
	{
		this.functions = function;
	}

	public Role addFunction( final Function function )
	{
		if ( this.functions == null )
			this.functions = new ArrayList<Function>();

		this.functions.add( function );

		return this;
	}
}

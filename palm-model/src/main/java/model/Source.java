package de.rwth.i9.palm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "source" )
public class Source extends PersistableResource
{
	@Column( nullable = false, unique = true )
	private String name;
	
	@Column
	@Lob
	private String description;

	@Enumerated( EnumType.STRING )
	@Column( length = 16, nullable = false, unique = true )
	private SourceType sourceType;

	@Enumerated( EnumType.STRING )
	@Column( length = 16, nullable = false )
	private SourceMethod sourceMethod;

	@Column( columnDefinition = "bit default 1" )
	private boolean active = true;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "source", orphanRemoval = true )
	private List<SourceProperty> sourceProperties;

	public void setDescription( String description )
	{
		this.description = description;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public SourceType getSourceType()
	{
		return this.sourceType;
	}

	public void setSourceType( SourceType sourceType )
	{
		this.sourceType = sourceType;
	}

	public SourceMethod getSourceMethod()
	{
		return sourceMethod;
	}

	public void setSourceMethod( SourceMethod sourceMethod )
	{
		this.sourceMethod = sourceMethod;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive( boolean active )
	{
		this.active = active;
	}

	public List<SourceProperty> getSourceProperties()
	{
		return sourceProperties;
	}

	public void setSourceProperties( List<SourceProperty> sourceProperties )
	{
		if ( this.sourceProperties == null )
			this.sourceProperties = new ArrayList<SourceProperty>();
		this.sourceProperties.clear();
		this.sourceProperties.addAll( sourceProperties );
	}

	public Source addSourceProperty( SourceProperty sourceProperty )
	{
		if ( this.sourceProperties == null )
			this.sourceProperties = new ArrayList<SourceProperty>();
		this.sourceProperties.add( sourceProperty );
		return this;
	}

	public SourceProperty getSourcePropertyByIdentifiers( String identifier, String identifier2 )
	{
		if ( this.sourceProperties != null )
		{
			for ( SourceProperty sourceProperty : this.sourceProperties )
			{
				if ( sourceProperty.getMainIdentifier().equals( identifier ) && sourceProperty.getSecondaryIdentifier().equals( identifier2 ) )
					return sourceProperty;
			}
		}
		return null;
	}

	public Map<String, String> getValidSourcePropertyListByMainIdentifierMap( String identifier )
	{
		Map<String, String> validSourceProperties = new HashMap<String, String>();
		if ( this.sourceProperties != null )
		{
			for ( SourceProperty sourceProperty : this.sourceProperties )
			{
				if ( sourceProperty.getMainIdentifier().equals( identifier ) && sourceProperty.isValid() )
				{
					validSourceProperties.put( sourceProperty.getSecondaryIdentifier(), sourceProperty.getValue() );
				}
			}
		}
		return validSourceProperties;
	}

}

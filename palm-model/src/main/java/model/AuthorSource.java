package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "author_source" )
public class AuthorSource extends PersistableResource
{
	@Column( nullable = false )
	private String name;
	
	@Column( nullable = false )
	private String sourceUrl;

	@Enumerated( EnumType.STRING )
	@Column( length = 16 )
	private SourceType SourceType;

	@ManyToOne
	@JoinColumn( name = "author_id" )
	private Author author;

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public SourceType getSourceType()
	{
		return SourceType;
	}

	public void setSourceType( SourceType sourceType )
	{
		SourceType = sourceType;
	}

	public String getSourceUrl()
	{
		return sourceUrl;
	}

	public void setSourceUrl( String sourceUrl )
	{
		this.sourceUrl = sourceUrl;
	}

	public Author getAuthor()
	{
		return author;
	}

	public void setAuthor( Author author )
	{
		this.author = author;
	}

}

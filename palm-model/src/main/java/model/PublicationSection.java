package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "publication_section" )
public class PublicationSection extends PersistableResource
{
	@Column
	private String name;

	@Column
	@Lob
	private String content;

	@Column( length = 50 )
	private String headerFont;

	@Column( columnDefinition = "Decimal(3,3) default '0.000'" )
	private double headerHeight;

	@Column
	private int pageNumber;

	@Column
	private int position_;

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent( String content )
	{
		this.content = content;
	}

	public String getHeaderFont()
	{
		return headerFont;
	}

	public void setHeaderFont( String headerFont )
	{
		this.headerFont = headerFont;
	}

	public double getHeaderHeight()
	{
		return headerHeight;
	}

	public void setHeaderHeight( double headerHeight )
	{
		this.headerHeight = headerHeight;
	}

	public int getPageNumber()
	{
		return pageNumber;
	}

	public void setPageNumber( int pageNumber )
	{
		this.pageNumber = pageNumber;
	}

	public int getPosition_()
	{
		return position_;
	}

	public void setPosition_( int position_ )
	{
		this.position_ = position_;
	}

}

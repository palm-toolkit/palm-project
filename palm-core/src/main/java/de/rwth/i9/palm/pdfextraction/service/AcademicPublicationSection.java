package de.rwth.i9.palm.pdfextraction.service;

import java.util.ArrayList;
import java.util.List;

/**
 * This code is not used
 * 
 * @author Sigit
 *
 */
public class AcademicPublicationSection
{
	private String header;
	private float headerFontHeight;
	private String headerFontType;
	private List<String> contents;
	private float contentFontHeight;
	private String contentFontType;
	private float startX;
	private float startY;
	private int pageNumber;
	private String status;

	public String getHeader()
	{
		return header;
	}

	public void setHeader( String header )
	{
		this.header = header;
	}

	public float getContentFontHeight()
	{
		return contentFontHeight;
	}

	public void setContentFontHeight( float contentFontHeight )
	{
		this.contentFontHeight = contentFontHeight;
	}

	public float getStartX()
	{
		return startX;
	}

	public void setStartX( float startX )
	{
		this.startX = startX;
	}

	public float getStartY()
	{
		return startY;
	}

	public void setStartY( float startY )
	{
		this.startY = startY;
	}

	public int getPageNumber()
	{
		return pageNumber;
	}

	public void setPageNumber( int pageNumber )
	{
		this.pageNumber = pageNumber;
	}

	public float getHeaderFontHeight()
	{
		return headerFontHeight;
	}

	public void setHeaderFontHeight( float headerFontHeight )
	{
		this.headerFontHeight = headerFontHeight;
	}

	public List<String> getContents()
	{
		return contents;
	}

	public void setContents( List<String> contents )
	{
		this.contents = contents;
	}

	public AcademicPublicationSection addContent( String content )
	{
		if ( this.contents == null )
			this.contents = new ArrayList<String>();
		this.contents.add( content );
		return this;
	}

	public AcademicPublicationSection appendCurrentContent( String content )
	{
		if ( this.contents == null )
			this.contents = new ArrayList<String>();
		else
			// append to last list
			this.contents.set( this.contents.size() - 1, this.contents.get( this.contents.size() - 1 ) + content );
		return this;
	}

	public String getStatus()
	{
		return status;
	}

	public AcademicPublicationSection setStatus( String status )
	{
		this.status = status;
		return this;
	}

	public String getHeaderFontType()
	{
		return headerFontType;
	}

	public void setHeaderFontType( String headerFontType )
	{
		this.headerFontType = headerFontType;
	}

	public String getContentFontType()
	{
		return contentFontType;
	}

	public void setContentFontType( String contentFontType )
	{
		this.contentFontType = contentFontType;
	}
}

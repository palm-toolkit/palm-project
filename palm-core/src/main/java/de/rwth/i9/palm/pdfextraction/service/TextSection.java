package de.rwth.i9.palm.pdfextraction.service;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.pdf.parser.Vector;

public class TextSection
{
	private float fontHeight;
	private String name;
	private String fontType;
	private String content;
	private List<String> contentlines;
	private Vector topLeftBoundary = new Vector( 0f, 0f, 1f );
	private Vector bottomRightBoundary = new Vector( 0f, 0f, 1f );
	private float indentStart;
	private float indentEnd;
	private int pageNumber;

	public float getFontHeight()
	{
		return fontHeight;
	}

	public void setFontHeight( float fontHeight )
	{
		this.fontHeight = fontHeight;
	}

	public String getFontType()
	{
		return fontType;
	}

	public void setFontType( String fontType )
	{
		this.fontType = fontType;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent( String content )
	{
		this.content = content;
	}

	public List<String> getContentlines()
	{
		return contentlines;
	}

	public void setContentlines( List<String> contentlines )
	{
		this.contentlines = contentlines;
	}

	public TextSection addContentLine( String contentLine )
	{
		if ( this.contentlines == null )
			this.contentlines = new ArrayList<String>();
		this.contentlines.add( contentLine );
		return this;
	}

	public Vector getTopLeftBoundary()
	{
		return topLeftBoundary;
	}

	public void setTopLeftBoundary( Vector topLeftBoundary )
	{
		this.topLeftBoundary = topLeftBoundary;
	}

	public Vector getBottomRightBoundary()
	{
		return bottomRightBoundary;
	}

	public void setBottomRightBoundary( Vector bottomRightBoundary )
	{
		this.bottomRightBoundary = bottomRightBoundary;
	}

	public float getIndentStart()
	{
		return indentStart;
	}

	public void setIndentStart( float indentStart )
	{
		this.indentStart = indentStart;
	}

	public float getIndentEnd()
	{
		return indentEnd;
	}

	public void setIndentEnd( float indentEnd )
	{
		this.indentEnd = indentEnd;
	}

	public int getPageNumber()
	{
		return pageNumber;
	}

	public void setPageNumber( int pageNumber )
	{
		this.pageNumber = pageNumber;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

}

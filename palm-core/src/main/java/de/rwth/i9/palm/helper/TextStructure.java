package de.rwth.i9.palm.helper;

class TextStructure
{
	private String name;
	private float fontHeight;
	private float startX;
	private float startY;
	private int pageNumber;

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public float getFontHeight()
	{
		return fontHeight;
	}

	public void setFontHeight( float fontHeight )
	{
		this.fontHeight = fontHeight;
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
}
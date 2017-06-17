package de.rwth.i9.palm.model;

public enum Color
{
	GRAY("#D2D6DE"), 
	GREEN("#00A65A"), 
	YELLOW("#F39C12"), 
	RED("#DD4B39"), 
	BLUE("#3C8DBC"),
	SOLID("#EEEEEE");
	
	private String value;

	private Color( String value )
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}

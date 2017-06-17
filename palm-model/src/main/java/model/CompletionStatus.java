package de.rwth.i9.palm.model;

public enum CompletionStatus
{
	NOT_COMPLETE("not complete"), PARTIALLY_COMPLETE("partially complete"), COMPLETE("complete");

	private String value;

	private CompletionStatus( String value )
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}

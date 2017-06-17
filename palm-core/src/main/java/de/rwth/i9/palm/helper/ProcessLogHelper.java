package de.rwth.i9.palm.helper;

public class ProcessLogHelper
{
	private String logMessage;

	public void appendLogMessage( String logText )
	{
		this.logMessage += logText;
	}

	public void setLogMessge( String logText )
	{
		this.logMessage = logText;
	}

	public String getLogMessage()
	{
		return this.logMessage;
	}
}

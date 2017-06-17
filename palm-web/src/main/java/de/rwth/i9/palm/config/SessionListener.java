package de.rwth.i9.palm.config;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener
{
	private static int totalActiveSessions;

	public static int getTotalActiveSession()
	{
		return totalActiveSessions;
	}

	@Override
	public void sessionCreated( HttpSessionEvent event )
	{
		totalActiveSessions++;
		System.out.println( "sessionCreated - add one session into counter, current session " + totalActiveSessions );
	}

	@Override
	public void sessionDestroyed( HttpSessionEvent event )
	{
		totalActiveSessions--;
		System.out.println( "sessionDestroyed - deduct one session from counter, current session " + totalActiveSessions );
	}
}
package de.rwth.i9.palm.helper;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class RequestContextHelper
{

	/**
	 * Helper method to access session-scoped attributes saved in the session.
	 * Returns the object saved under the given <i>name</i>.
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	public static <T> T getSessionAttribute( final String name )
	{
		if ( StringUtils.isEmpty( name ) )
			return null;

		return (T) RequestContextHolder.getRequestAttributes().getAttribute( name, RequestAttributes.SCOPE_SESSION );
	}

	/**
	 * @param name
	 * @param object
	 */
	public static <T> void setSessionAttribute( final String name, final T object )
	{
		RequestContextHolder.getRequestAttributes().setAttribute( name, object, RequestAttributes.SCOPE_SESSION );
	}

	/**
	 * @param name
	 */
	public static <T> void expireAttribute( final String name )
	{
		RequestContextHolder.getRequestAttributes().setAttribute( name, null, RequestAttributes.SCOPE_SESSION );
	}

	/**
	 * @return
	 */
	public static String getSessionId()
	{
		return RequestContextHolder.getRequestAttributes().getSessionId();
	}
}

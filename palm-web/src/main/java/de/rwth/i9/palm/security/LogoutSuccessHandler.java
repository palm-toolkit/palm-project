package de.rwth.i9.palm.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.persistence.UserDAO;

public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler
{
	private static final Logger log = LoggerFactory.getLogger( LogoutSuccessHandler.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	public LogoutSuccessHandler( final String defaultTargetUrl )
	{
		this.setDefaultTargetUrl( defaultTargetUrl );
	}

	@Override
	@Transactional
	public void onLogoutSuccess( final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication ) throws IOException, ServletException
	{
		if ( authentication != null )
		{
			UserDAO userDao = persistenceStrategy.getUserDAO();

			User user = userDao.getByUsername( authentication.getName() );

			if ( user == null )
				log.warn( "user not found" );
			else
			{
				Date now = DateTime.now().toDate();

				log.info( "USER_LOGOUT | {} | {}", user.getUsername(), user.getSessionId() );
				log.debug( "USER_LOGOUT_TIME | {} | {} | {}", user.getUsername(), user.getSessionId(), now );

				user.setLastLogout( now );
				user.setSessionId( null );

				userDao.persist( user );
			}

			super.onLogoutSuccess( request, response, authentication );
		}
	}
}


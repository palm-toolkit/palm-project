package de.rwth.i9.palm.controller.user;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Controller
@RequestMapping( value = "/userapi" )
public class UserApiController
{

	
	@Autowired
	private PersistenceStrategy persistenceStrategy;
	
	/**
	 * Check whether username is already exist
	 * 
	 * @param username
	 * @param response
	 * @return
	 */
	@RequestMapping( value = "/isUsernameNotExist", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Boolean isUsernameNotExist( 
			@RequestParam( value = "username", required = false ) final String username, 
			final HttpServletResponse response) 
	{
		boolean isUsernameNotExist = true;
		boolean isUsernameExist = persistenceStrategy.getUserDAO().isUsernameExist( username );
		if ( isUsernameExist )
			isUsernameNotExist = false;
		return isUsernameNotExist;
	}

}
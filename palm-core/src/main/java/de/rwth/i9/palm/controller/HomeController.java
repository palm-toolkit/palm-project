package de.rwth.i9.palm.controller;


import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Controller
public class HomeController
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	public Properties config;

	@RequestMapping( value = "/home", method = RequestMethod.GET )
	@Transactional
	public ModelAndView home( @RequestParam( value = "sessionid", required = false ) final String sessionId, final HttpServletResponse response )
	{
		ModelAndView mav = new ModelAndView( "home", "link", "home" );

		if ( sessionId != null && sessionId.equals( "0" ) )
			response.setHeader( "SESSION_INVALID", "yes" );

		return mav;
	}

	@RequestMapping( "/" )
	public ModelAndView root()
	{
		return new ModelAndView( "home", "link", "home" );
	}

	@RequestMapping( "" )
	public ModelAndView rootBlank()
	{
		return new ModelAndView( "home", "link", "home" );
	}
}

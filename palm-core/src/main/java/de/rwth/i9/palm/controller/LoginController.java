package de.rwth.i9.palm.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping( value = "/login" )
public class LoginController
{

	@RequestMapping( method = RequestMethod.GET )
	public ModelAndView login(
			@RequestParam(value="form", required = false) String formMode,
			@RequestParam(value="auth", required = false) String auth,
			@RequestParam(value="info", required = false) String info)
	{
		ModelAndView mav = null;
		if ( formMode != null && formMode.equals( "true" ) )
			mav = new ModelAndView( "loginForm" );
		else
			mav = new ModelAndView( "login" );
		
		if( auth != null)
			mav.addObject( "auth", auth );
		if( info != null)
			mav.addObject( "info", info );
		
		return mav;
	}
}

package de.rwth.i9.palm.controller.administration;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.helper.TemplateHelper;
import de.rwth.i9.palm.service.SecurityService;

@Controller
@RequestMapping( value = "/admin" )
public class AdministrationController
{
	private static final String LINK_NAME = "administration";

	@Autowired
	private SecurityService securityService;

	@RequestMapping( method = RequestMethod.GET )
	public ModelAndView landing( 
			@RequestParam( value = "page", required = false ) final String page,
			final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = null;

		if ( !securityService.isAuthorizedForRole( "ADMIN" ) )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "administration", LINK_NAME );

		if( page != null)
			model.addObject( "activeMenu", page );
		else
			model.addObject( "activeMenu", "config-researcher" );

		return model;
	}

}
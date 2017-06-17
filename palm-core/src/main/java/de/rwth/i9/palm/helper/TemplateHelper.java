package de.rwth.i9.palm.helper;


import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.model.SessionDataSet;

public class TemplateHelper
{

	public static ModelAndView createViewWithSessionDataSet( final String viewName, final String linkName, final SessionDataSet sessionDataSet )
	{
		ModelAndView model = new ModelAndView( viewName );

		model.addObject( sessionDataSet );
		model.addObject( "link", linkName );

		return model;
	}

	/**
	 * @param viewName
	 * @param linkName
	 * @param datapoint
	 * @return
	 */
	public static ModelAndView createViewWithLink( final String viewName, final String linkName )
	{
		ModelAndView model = new ModelAndView();

		model.setViewName( viewName );
		model.addObject( "link", linkName );

		return model;
	}
}

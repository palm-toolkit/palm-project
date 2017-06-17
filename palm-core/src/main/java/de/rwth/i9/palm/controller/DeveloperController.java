package de.rwth.i9.palm.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.helper.TemplateHelper;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Controller
@RequestMapping( value = "/developer" )
public class DeveloperController
{
	private static final String LINK_NAME = "administration";

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	/**
	 * Load the architecture overview page
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/architecture", method = RequestMethod.GET )
	public ModelAndView getArchitectureOverviewPage( @RequestParam( value = "mode", required = false ) final String mode, HttpServletResponse response)
	{
		ModelAndView model = null;

		// iframe or normal view
		if ( mode != null && mode.equals( "iframe" ) )
			model = TemplateHelper.createViewWithLink( "dialogIframeLayout", LINK_NAME );
		else
			model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );

		// get widget
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "developer-architecture" );

		// assign the model
		model.addObject( "widgets", widgets );

		return model;
	}

	/**
	 * Load technologies overview page
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/technology", method = RequestMethod.GET )
	public ModelAndView getTechnologiesOverviewPage( @RequestParam( value = "mode", required = false ) final String mode, HttpServletResponse response)
	{
		ModelAndView model = null;

		// iframe or normal view
		if ( mode != null && mode.equals( "iframe" ) )
			model = TemplateHelper.createViewWithLink( "dialogIframeLayout", LINK_NAME );
		else
			model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );

		// get widget
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "developer-technology" );

		// assign the model
		model.addObject( "widgets", widgets );

		return model;
	}

	/**
	 * Load documentation overview page
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/documentation", method = RequestMethod.GET )
	public ModelAndView getDocumentationOverviewPage( @RequestParam( value = "mode", required = false ) final String mode, HttpServletResponse response)
	{
		ModelAndView model = null;

		// iframe or normal view
		if ( mode != null && mode.equals( "iframe" ) )
			model = TemplateHelper.createViewWithLink( "dialogIframeLayout", LINK_NAME );
		else
			model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );

		// get widget
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "developer-documentation" );

		// assign the model
		model.addObject( "widgets", widgets );

		return model;
	}

	/**
	 * Load credits page
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/credit", method = RequestMethod.GET )
	public ModelAndView getCreditOverviewPage( @RequestParam( value = "mode", required = false ) final String mode, HttpServletResponse response)
	{
		ModelAndView model = null;

		// iframe or normal view
		if ( mode != null && mode.equals( "iframe" ) )
			model = TemplateHelper.createViewWithLink( "dialogIframeLayout", LINK_NAME );
		else
			model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );

		// get widget
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "developer-credit" );

		// assign the model
		model.addObject( "widgets", widgets );

		return model;
	}

}
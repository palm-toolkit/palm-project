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
@RequestMapping( value = "/extraction" )
public class ExtractionController
{
	private static final String LINK_NAME = "administration";

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	/**
	 * Load Webpage/HTML extraction page, for testing publication information extraction results.
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/html", method = RequestMethod.GET )
	public ModelAndView getExtractionHtmlPage( @RequestParam( value = "mode", required = false ) final String mode, HttpServletResponse response)
	{
		ModelAndView model = null;

		// iframe or normal view
		if ( mode != null && mode.equals( "iframe" ) )
			model = TemplateHelper.createViewWithLink( "dialogIframeLayout", LINK_NAME );
		else
			model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );

		// get widget
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "extraction-html" );

		// assign the model
		model.addObject( "widgets", widgets );

		return model;
	}

	/**
	 * Load PDF extraction page, for testing publication information extraction results.
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/pdf", method = RequestMethod.GET )
	public ModelAndView getExtractionPdfPage( @RequestParam( value = "mode", required = false ) final String mode, HttpServletResponse response)
	{
		ModelAndView model = null;

		// iframe or normal view
		if ( mode != null && mode.equals( "iframe" ) )
			model = TemplateHelper.createViewWithLink( "dialogIframeLayout", LINK_NAME );
		else
			model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );

		// get widget
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "extraction-pdf" );

		// assign the model
		model.addObject( "widgets", widgets );

		return model;
	}

}
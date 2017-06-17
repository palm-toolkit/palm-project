package de.rwth.i9.palm.controller.administration;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.helper.TemplateHelper;
import de.rwth.i9.palm.helper.comparator.SourceByNaturalOrderComparator;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;
import de.rwth.i9.palm.service.SecurityService;

@Controller
@RequestMapping( value = "/api" )
public class ManageApiController
{
	private static final String LINK_NAME = "administration";

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private SecurityService securityService;

	/**
	 * Load the source detail form
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/{apiType}", method = RequestMethod.GET )
	public ModelAndView getSources( @PathVariable String apiType, final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = null;

		model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "api-" + apiType );

		// get list of sources and sort
		List<Source> sources = persistenceStrategy.getSourceDAO().getAllSource();
		Collections.sort( sources, new SourceByNaturalOrderComparator() );

		// assign the model
		model.addObject( "widgets", widgets );
		model.addObject( "header", "api" + apiType );

		return model;
	}

}
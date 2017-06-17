package de.rwth.i9.palm.controller.administration;

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
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.SecurityService;

@Controller
@RequestMapping( value = "/admin/data" )
public class ManageDataController
{
	private static final String LINK_NAME = "administration";

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private SecurityService securityService;

	/**
	 * Load Model and View for data
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/{pageType}", method = RequestMethod.GET )
	public ModelAndView manageDataModelAndView( 
			@PathVariable String pageType, 
			final HttpServletResponse response 
			) throws InterruptedException
	{
		ModelAndView model = null;

		if ( !securityService.isAuthorizedForRole( "ADMIN" ) )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "data-" + pageType );

		// assign the model
		model.addObject( "widgets", widgets );
		model.addObject( "header", pageType );

		return model;
	}
}
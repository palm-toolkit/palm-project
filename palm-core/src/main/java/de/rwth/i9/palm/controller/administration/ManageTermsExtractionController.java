package de.rwth.i9.palm.controller.administration;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.helper.TemplateHelper;
import de.rwth.i9.palm.model.ExtractionService;
import de.rwth.i9.palm.model.ExtractionServiceProperty;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;
import de.rwth.i9.palm.service.SecurityService;
import de.rwth.i9.palm.wrapper.ExtractionServiceListWrapper;

@Controller
@SessionAttributes( "extractionServiceListWrapper" )
@RequestMapping( value = "/admin/termextraction" )
public class ManageTermsExtractionController
{
	private static final String LINK_NAME = "administration";

	@Autowired
	private SecurityService securityService;

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private ApplicationService applicationService;

	/**
	 * Load the extractionService detail form
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( method = RequestMethod.GET )
	public ModelAndView getExtractionService( final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = null;

		if ( !securityService.isAuthorizedForRole( "ADMIN" ) )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "termextraction" );

		// get list of extractionService and sort
		List<ExtractionService> extractionServices = persistenceStrategy.getExtractionServiceDAO().getAllExtractionServices();
		
		// put it into wrapper class
		ExtractionServiceListWrapper extractionServiceListWrapper = new ExtractionServiceListWrapper();
		extractionServiceListWrapper.setExtractionServices( extractionServices );

		// assign the model
		model.addObject( "widgets", widgets );
		model.addObject( "extractionServiceListWrapper", extractionServiceListWrapper );

		return model;
	}

	/**
	 * Save changes from ExtractionService detail, via Spring binding
	 * 
	 * @param extractionServiceListWrapper
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> saveExtractionService( @ModelAttribute( "extractionServiceListWrapper" ) ExtractionServiceListWrapper extractionServiceListWrapper, 
			final HttpServletResponse response ) throws InterruptedException
	{
		// persist extractionService from model attribute
		for ( ExtractionService extractionService : extractionServiceListWrapper.getExtractionServices() )
		{
			if ( extractionService.getExtractionServiceProperties() != null || !extractionService.getExtractionServiceProperties().isEmpty() )
			{
				Iterator<ExtractionServiceProperty> iteratorExtractionServiceProperty = extractionService.getExtractionServiceProperties().iterator();
				while ( iteratorExtractionServiceProperty.hasNext() )
				{
					ExtractionServiceProperty extractionServiceProperty = iteratorExtractionServiceProperty.next();
					// check for invalid extractionServiceProperty object to be
					// removed
					if ( extractionServiceProperty.getMainIdentifier().equals( "" ) || extractionServiceProperty.getValue().equals( "" ) )
					{
						iteratorExtractionServiceProperty.remove();
						// delete extractionServiceProperty on database
						persistenceStrategy.getExtractionServicePropertyDAO().delete( extractionServiceProperty );
					}
					else
					{
						extractionServiceProperty.setExtractionService( extractionService );
					}
				}

			}
			persistenceStrategy.getExtractionServiceDAO().persist( extractionService );
		}

		// set response
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		responseMap.put( "status", "ok" );
		responseMap.put( "format", "json" );

		// update application service extractionService cache
		applicationService.updateExtractionServicesCache();

		return responseMap;
	}

}
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
import de.rwth.i9.palm.model.InterestProfile;
import de.rwth.i9.palm.model.InterestProfileProperty;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;
import de.rwth.i9.palm.service.SecurityService;
import de.rwth.i9.palm.wrapper.InterestProfileListWrapper;

@Controller
@SessionAttributes( "interestProfileListWrapper" )
@RequestMapping( value = "/admin/termweighting" )
public class ManageTermsWeightingController
{
	private static final String LINK_NAME = "administration";

	@Autowired
	private SecurityService securityService;

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private ApplicationService applicationService;

	/**
	 * Load the interestProfile detail form
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( method = RequestMethod.GET )
	public ModelAndView getInterestProfile( final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = null;

		if ( !securityService.isAuthorizedForRole( "ADMIN" ) )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "termweighting" );

		// get list of interestProfile and sort
		List<InterestProfile> interestProfiles = persistenceStrategy.getInterestProfileDAO().getAllInterestProfiles();
		
		// put it into wrapper class
		InterestProfileListWrapper interestProfileListWrapper = new InterestProfileListWrapper();
		interestProfileListWrapper.setInterestProfiles( interestProfiles );

		// assign the model
		model.addObject( "widgets", widgets );
		model.addObject( "interestProfileListWrapper", interestProfileListWrapper );

		return model;
	}

	/**
	 * Save changes from InterestProfile detail, via Spring binding
	 * 
	 * @param interestProfileListWrapper
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> saveInterestProfile( 
			@ModelAttribute( "interestProfileListWrapper" ) InterestProfileListWrapper interestProfileListWrapper, 
			final HttpServletResponse response ) throws InterruptedException
	{
		// persist interestProfile from model attribute
		for ( InterestProfile interestProfile : interestProfileListWrapper.getInterestProfiles() )
		{
			if ( interestProfile.getInterestProfileProperties() != null || !interestProfile.getInterestProfileProperties().isEmpty() )
			{
				Iterator<InterestProfileProperty> iteratorInterestProfileProperty = interestProfile.getInterestProfileProperties().iterator();
				while ( iteratorInterestProfileProperty.hasNext() )
				{
					InterestProfileProperty interestProfileProperty = iteratorInterestProfileProperty.next();
					// check for invalid interestProfileProperty object to be
					// removed
					if ( interestProfileProperty.getMainIdentifier().equals( "" ) || interestProfileProperty.getValue().equals( "" ) )
					{
						iteratorInterestProfileProperty.remove();
						// delete interestProfileProperty on database
						persistenceStrategy.getInterestProfilePropertyDAO().delete( interestProfileProperty );
					}
					else
					{
						interestProfileProperty.setInterestProfile( interestProfile );
					}
				}

			}
			persistenceStrategy.getInterestProfileDAO().persist( interestProfile );
		}

		// set response
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		responseMap.put( "status", "ok" );
		responseMap.put( "format", "json" );

		// update application service interestProfile cache
		applicationService.updateInterestProfilesCache();

		return responseMap;
	}

}
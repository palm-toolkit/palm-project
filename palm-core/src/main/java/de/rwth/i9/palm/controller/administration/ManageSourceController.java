package de.rwth.i9.palm.controller.administration;

import java.util.Collections;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.helper.TemplateHelper;
import de.rwth.i9.palm.helper.comparator.SourceByNaturalOrderComparator;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceProperty;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;
import de.rwth.i9.palm.service.SecurityService;
import de.rwth.i9.palm.wrapper.SourceListWrapper;

@Controller
@SessionAttributes( "sourceListWrapper" )
@RequestMapping( value = "/admin/source" )
public class ManageSourceController
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
	@RequestMapping( method = RequestMethod.GET )
	public ModelAndView getSources( @RequestParam( value = "sessionid", required = false ) final String sessionId, final HttpServletResponse response) throws InterruptedException
	{
		ModelAndView model = null;

		if ( !securityService.isAuthorizedForRole( "ADMIN" ) )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "source" );

		// get list of sources and sort
		List<Source> sources = persistenceStrategy.getSourceDAO().getAllSource();
		Collections.sort( sources, new SourceByNaturalOrderComparator() );
		
		// put it into wrapper class
		SourceListWrapper sourceListWrapper = new SourceListWrapper();
		sourceListWrapper.setSources( sources );

		// assign the model
		model.addObject( "widgets", widgets );
		model.addObject( "sourceListWrapper", sourceListWrapper );

		return model;
	}

	/**
	 * Save changes from Source detail, via Spring binding
	 * 
	 * @param sourceListWrapper
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> saveSources( 
			@ModelAttribute( "sourceListWrapper" ) SourceListWrapper sourceListWrapper, 
			final HttpServletResponse response ) throws InterruptedException
	{
		// persist sources from model attribute
		for ( Source source : sourceListWrapper.getSources() )
		{
			if ( source.getSourceProperties() != null || !source.getSourceProperties().isEmpty() )
			{
				Iterator<SourceProperty> iteratorSourceProperty = source.getSourceProperties().iterator();
				while ( iteratorSourceProperty.hasNext() )
				{
					SourceProperty sourceProperty = iteratorSourceProperty.next();
					// check for invalid sourceProperty object to be removed
					if ( sourceProperty.getMainIdentifier().equals( "" ) || sourceProperty.getValue().equals( "" ) )
					{
						iteratorSourceProperty.remove();
						// delete sourceProperty on database
						persistenceStrategy.getSourcePropertyDAO().delete( sourceProperty );
					}
					else
					{
						sourceProperty.setSource( source );
					}
				}

			}
			persistenceStrategy.getSourceDAO().persist( source );
		}

		// set response
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		responseMap.put( "status", "ok" );
		responseMap.put( "format", "json" );

		// update application service source cache
		applicationService.updateAcademicNetworkSourcesCache();

		return responseMap;
	}

}
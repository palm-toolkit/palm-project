package de.rwth.i9.palm.controller.administration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.helper.TemplateHelper;
import de.rwth.i9.palm.helper.comparator.ConfigPropertyByPositionComparator;
import de.rwth.i9.palm.helper.comparator.SourceByNaturalOrderComparator;
import de.rwth.i9.palm.model.Config;
import de.rwth.i9.palm.model.ConfigProperty;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;
import de.rwth.i9.palm.service.SecurityService;

@Controller
@SessionAttributes( "config" )
@RequestMapping( value = "/admin/config" )
public class ManageConfigController
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
	@RequestMapping( value = "/{configType}", method = RequestMethod.GET )
	public ModelAndView getSources( @PathVariable String configType, final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = null;

		if ( !securityService.isAuthorizedForRole( "ADMIN" ) )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "config" );

		// get list of sources and sort
		List<Source> sources = persistenceStrategy.getSourceDAO().getAllSource();
		Collections.sort( sources, new SourceByNaturalOrderComparator() );

		// get properties of config based on name
		Config config = persistenceStrategy.getConfigDAO().getConfigByName( configType );

		// not found then return 404
		if ( config == null )
		{
			model = TemplateHelper.createViewWithLink( "404", "error" );
			return model;
		}

		// sort config.configproperties based on position
		if ( config.getConfigProperties() != null && !config.getConfigProperties().isEmpty() )
			Collections.sort( config.getConfigProperties(), new ConfigPropertyByPositionComparator() );

		// assign the model
		model.addObject( "widgets", widgets );
		model.addObject( "config", config );
		model.addObject( "header", configType );

		return model;
	}

	/**
	 * Save changes from Source detail, via Spring binding
	 * 
	 * @param config
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> saveSources( 
			@ModelAttribute( "config" ) Config config,
			@RequestParam(  value ="resetDafault", required = false ) String resetDafault, 
			final HttpServletResponse response ) throws InterruptedException
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		if ( config == null )
		{
			responseMap.put( "status", "error" );
			return responseMap;
		}

		// reset if reset button pressed
		if ( resetDafault != null && resetDafault.equals( "reset" ) )
			for ( ConfigProperty cProp : config.getConfigProperties() )
				cProp.setValue( cProp.getDefaultValue() );

		// set response
		persistenceStrategy.getConfigDAO().persist( config );

		responseMap.put( "status", "ok" );
		responseMap.put( "format", "json" );

		// update application config cache
		applicationService.updateConfigCache();

		return responseMap;
	}

}
package de.rwth.i9.palm.controller.administration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import de.rwth.i9.palm.model.Color;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetSource;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.model.WidgetWidth;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.SecurityService;
import de.rwth.i9.palm.wrapper.WidgetWrapper;

@Controller
@SessionAttributes( { "widgetsWrapper" } )
@RequestMapping( value = "/admin/widget" )
public class ManageWidgetController
{
	private static final String LINK_NAME = "administration";

	@Autowired
	private SecurityService securityService;

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Transactional
	@RequestMapping( value = "/overview", method = RequestMethod.GET )
	public ModelAndView overviewWidget( final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = null;

		if ( !securityService.isAuthorizedForRole( "ADMIN" ) )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "add" );

		// get all widget enums
		List<WidgetType> widgetTypes = new ArrayList<WidgetType>( Arrays.asList( WidgetType.values() ) );
		List<WidgetSource> widgetSources = new ArrayList<WidgetSource>( Arrays.asList( WidgetSource.values() ) );
		List<WidgetWidth> widgetWidths = new ArrayList<WidgetWidth>( Arrays.asList( WidgetWidth.values() ) );

		// TODO: widget group based on widgetTypes

		model.addObject( "widgets", widgets );
		model.addObject( "widgetTypes", widgetTypes );
		model.addObject( "widgetSources", widgetSources );
		model.addObject( "widgetWidths", widgetWidths );

		return model;
	}

	@Transactional
	@RequestMapping( value = "/add", method = RequestMethod.GET )
	public ModelAndView addWidget( 
			final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = null;

		if ( !securityService.isAuthorizedForRole( "ADMIN" ) )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "add" );
		
		// get all widget enums
		List<WidgetType> widgetTypes = new ArrayList<WidgetType>( Arrays.asList( WidgetType.values() ) );
		List<WidgetSource> widgetSources = new ArrayList<WidgetSource>( Arrays.asList( WidgetSource.values() ) );
		List<WidgetWidth> widgetWidths = new ArrayList<WidgetWidth>( Arrays.asList( WidgetWidth.values() ) );
		List<WidgetStatus> widgetStatuss = new ArrayList<WidgetStatus>( Arrays.asList( WidgetStatus.values() ) );
		List<Color> colors = new ArrayList<Color>( Arrays.asList( Color.values() ) );

		// TODO: widget group based on widgetTypes

		// assign the model
		model.addObject( "widgets" , widgets );
		model.addObject( "widgetTypes", widgetTypes );
		model.addObject( "widgetSources", widgetSources );
		model.addObject( "widgetWidths", widgetWidths );
		model.addObject( "widgetStatuss", widgetStatuss );
		model.addObject( "widgetColors", colors );

		return model;
	}

	@Transactional
	@RequestMapping( value = "/add", method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> saveNewWidget( 
			@RequestParam( value="widgetTitle" ) String widgetTitle,
			@RequestParam( value="widgetUniqueName" ) String widgetUniqueName,
			@RequestParam( value="widgetType" ) String widgetType,
			@RequestParam( value="widgetGroup" ) String widgetGroup,
			@RequestParam( value="widgetSource" ) String widgetSource,
			@RequestParam( value="widgetSourcePath" ) String widgetSourcePath,
			@RequestParam( value="widgetWidth" ) String widgetWidth,
			@RequestParam( value="widgetColor" ) String widgetColor,
			@RequestParam( value="widgetInfo" ) String widgetInfo,
			@RequestParam( value="widgetClose" ) boolean widgetClose,
			@RequestParam( value="widgetMinimize" ) boolean widgetMinimize,
			@RequestParam( value="widgetMoveable" ) boolean widgetMoveable,
			@RequestParam( value="widgetStatus" ) String widgetStatus,
			@RequestParam( value="headerVisible" ) boolean headerVisible,
 @RequestParam( value = "position" ) String position,
			final HttpServletResponse response 
			)
	{
		// create new widget object and set its attributes
		Widget widget = new Widget();
		widget.setTitle( widgetTitle );
		widget.setUniqueName( widgetUniqueName );
		widget.setWidgetType( WidgetType.valueOf( widgetType ) );
		widget.setWidgetGroup( widgetGroup );
		widget.setWidgetSource( WidgetSource.valueOf( widgetSource ) );
		widget.setSourcePath( widgetSourcePath );
		widget.setWidgetWidth( WidgetWidth.valueOf( widgetWidth ) );
		widget.setColor( Color.valueOf( widgetColor ) );
		widget.setInformation( widgetInfo );
		widget.setCloseEnabled( widgetClose );
		widget.setMinimizeEnabled( widgetMinimize );
		widget.setMoveableEnabled( widgetMoveable );
		widget.setHeaderVisible( headerVisible );
		widget.setWidgetStatus( WidgetStatus.valueOf( widgetStatus ) );
		try
		{
			widget.setPosition( Integer.parseInt( position ) );
		}
		catch ( Exception e )
		{
			widget.setPosition( 999 );
		}
		// position
		if ( widget.getWidgetStatus().equals( WidgetStatus.NONACTIVE ) || !widget.isMoveableEnabled() )
			widget.setPosition( 999 );
		// save into database
		persistenceStrategy.getWidgetDAO().persist( widget );

		// create JSON mapper for response
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put( "status", "ok" );
		responseMap.put( "format", "json" );
		responseMap.put( "result", "success" );

		return responseMap;
	}
	
	@Transactional
	@RequestMapping( value = "/edit", method = RequestMethod.GET )
	public ModelAndView editWidget( @RequestParam( value = "id", required = false ) final String id, 
			final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = null;

		if ( !securityService.isAuthorizedForRole( "ADMIN" ) )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "dialogIframeLayout", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "admin-widget-edit" );

		// get widgetTarget
		Widget widget = persistenceStrategy.getWidgetDAO().getById( id );

		if ( widget == null )
		{
			model = TemplateHelper.createViewWithLink( "404", "error" );
			return model;
		}
		// get all widget enums
		List<WidgetType> widgetTypes = new ArrayList<WidgetType>( Arrays.asList( WidgetType.values() ) );
		List<WidgetSource> widgetSources = new ArrayList<WidgetSource>( Arrays.asList( WidgetSource.values() ) );
		List<WidgetWidth> widgetWidths = new ArrayList<WidgetWidth>( Arrays.asList( WidgetWidth.values() ) );
		List<WidgetStatus> widgetStatuss = new ArrayList<WidgetStatus>( Arrays.asList( WidgetStatus.values() ) );
		List<Color> colors = new ArrayList<Color>( Arrays.asList( Color.values() ) );


		// assign the model
		model.addObject( "widgets", widgets );
		model.addObject( "targetWidget", widget );
		model.addObject( "widgetTypes", widgetTypes );
		model.addObject( "widgetSources", widgetSources );
		model.addObject( "widgetWidths", widgetWidths );
		model.addObject( "widgetStatuss", widgetStatuss );
		model.addObject( "widgetColors", colors );

		return model;
	}
	
	@Transactional
	@RequestMapping( value = "/edit", method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> saveEditWidget( 
			@RequestParam( value="widgetId" ) String id,
			@RequestParam( value="widgetTitle" ) String widgetTitle,
			@RequestParam( value="widgetUniqueName" ) String widgetUniqueName,
			@RequestParam( value="widgetType" ) String widgetType,
			@RequestParam( value="widgetGroup" ) String widgetGroup,
			@RequestParam( value="widgetSource" ) String widgetSource,
			@RequestParam( value="widgetSourcePath" ) String widgetSourcePath,
			@RequestParam( value="widgetWidth" ) String widgetWidth,
			@RequestParam( value="widgetColor" ) String widgetColor,
			@RequestParam( value="widgetInfo" ) String widgetInfo,
			@RequestParam( value="widgetClose" ) boolean widgetClose,
			@RequestParam( value="widgetMinimize" ) boolean widgetMinimize,
			@RequestParam( value="widgetMoveable" ) boolean widgetMoveable,
			@RequestParam( value="widgetStatus" ) String widgetStatus,
			@RequestParam( value="headerVisible" ) boolean headerVisible,
 @RequestParam( value = "position" ) String position,
			final HttpServletResponse response 
			)
	{
		// create new widget object and set its attributes
		Widget widget = persistenceStrategy.getWidgetDAO().getById( id );
		widget.setTitle( widgetTitle );
		widget.setUniqueName( widgetUniqueName );
		widget.setWidgetType( WidgetType.valueOf( widgetType ) );
		widget.setWidgetGroup( widgetGroup );
		widget.setWidgetSource( WidgetSource.valueOf( widgetSource ) );
		widget.setSourcePath( widgetSourcePath );
		widget.setWidgetWidth( WidgetWidth.valueOf( widgetWidth ) );
		widget.setColor( Color.valueOf( widgetColor ) );
		widget.setInformation( widgetInfo );
		widget.setCloseEnabled( widgetClose );
		widget.setMinimizeEnabled( widgetMinimize );
		widget.setMoveableEnabled( widgetMoveable );
		widget.setHeaderVisible( headerVisible );
		widget.setWidgetStatus( WidgetStatus.valueOf( widgetStatus ) );
		try
		{
			widget.setPosition( Integer.parseInt( position ) );
		}
		catch ( Exception e )
		{
			widget.setPosition( 999 );
		}
		// position
		if ( widget.getWidgetStatus().equals( WidgetStatus.NONACTIVE ) || !widget.isMoveableEnabled() )
			widget.setPosition( 999 );
		// save into database
		persistenceStrategy.getWidgetDAO().persist( widget );

		// create JSON mapper for response
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put( "status", "ok" );
		responseMap.put( "format", "json" );
		responseMap.put( "result", "success" );

		return responseMap;
	}

	/**
	 * Load user widgets
	 * 
	 */
	@Transactional
	@RequestMapping( value = "/manage/{widgetType}", method = RequestMethod.GET )
	public ModelAndView getWidgets( 
			@PathVariable String widgetType,
			final HttpServletResponse response 
			)
	{
		ModelAndView model = null;

		if ( !( widgetType.equals( "researcher" ) || widgetType.equals( "publication" ) || 
				widgetType.equals( "venue" ) || widgetType.equals( "circle" ) ||
				widgetType.equals( "administration" ) || widgetType.equals( "user" ) || widgetType.equals( "menu" ) ) )
		{
			model = TemplateHelper.createViewWithLink( "404", "error" );
			return model;
		}
		if(  widgetType.equals( "venue" ) )
			widgetType = "conference";

		User user = securityService.getUser();
		if ( user == null )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "admin-widget-manage" );

		// get list of widgets
		List<Widget> targetwidgets = persistenceStrategy.getWidgetDAO().getWidgetByWidgetType( WidgetType.valueOf( widgetType.toUpperCase() ) );

		// put into wrapper
		WidgetWrapper widgetsWrapper = new WidgetWrapper();
		widgetsWrapper.setWidgets( targetwidgets );

		// assign the model
		model.addObject( "widgetsWrapper", widgetsWrapper );
		model.addObject( "widgetType", widgetType );
		model.addObject( "widgets", widgets );

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
	@RequestMapping( value = "/manage", method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> saveWidgets( 
 @ModelAttribute( "widgetsWrapper" ) WidgetWrapper widgetsWrapper,
			final HttpServletResponse response ) throws InterruptedException
	{
		// set response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		User user = securityService.getUser();

		if ( user == null || widgetsWrapper == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "unable to save changes due to session expired" );
			return responseMap;
		}


		for ( Widget widget : widgetsWrapper.getWidgets() )
		{
			widget.setWidgetWidth( WidgetWidth.valueOf( widget.getWidth().toUpperCase() ) );
			widget.setWidgetStatus( WidgetStatus.valueOf( widget.getStatus().toUpperCase() ) );
			if ( !( widget.getWidgetStatus().equals( WidgetStatus.NONACTIVE ) || !widget.isMoveableEnabled() ) )
				widget.setPosition( widget.getPos() );
			persistenceStrategy.getWidgetDAO().persist( widget );
		}

		responseMap.put( "status", "ok" );

		return responseMap;
	}

}
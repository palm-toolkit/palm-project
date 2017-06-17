package de.rwth.i9.palm.controller.administration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import de.rwth.i9.palm.model.Role;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceProperty;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;
import de.rwth.i9.palm.service.SecurityService;
import de.rwth.i9.palm.wrapper.SourceListWrapper;

@Controller
@SessionAttributes( "sourceListWrapper" )
@RequestMapping( value = "/admin/user" )
public class ManageUserController
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
	public ModelAndView getUser( 
			@RequestParam( value = "name", required = false ) final String name, 
			final HttpServletResponse response)
	{
		ModelAndView model = null;

		if ( !securityService.isAuthorizedForRole( "ADMIN" ) )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.ADMINISTRATION, "admin-manage-user" );
		// assign the model
		model.addObject( "widgets", widgets );
		if ( name != null )
			model.addObject( "targetName", name );

		return model;
	}

	@Transactional
	@RequestMapping( value = "/search", method = RequestMethod.GET )
	public @ResponseBody Map<String, Object> getAuthorList( 
			@RequestParam( value = "name", required = false ) String name, 
			HttpServletRequest request, HttpServletResponse response)
	{
		/* == Set Default Values== */
		if ( name == null )
			name = "";

		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		responseMap.put( "query", name );

		List<User> users = persistenceStrategy.getUserDAO().getByName( name );

		if ( !users.isEmpty() )
		{
			responseMap.put( "count", users.size() );
			responseMap.put( "users", printUserAsJSON( users ) );
			return responseMap;
		}
		else
		{
			responseMap.put( "count", 0 );
			return responseMap;
		}
	}

	private Object printUserAsJSON( List<User> users )
	{
		// Get necessary information from users
		List<Object> userList = new ArrayList<Object>();
		// preparing data format
		DateFormat dateFormat = new SimpleDateFormat( "yyyy/M/d", Locale.ENGLISH );

		for ( User user : users )
		{
			Map<String, Object> userMap = new LinkedHashMap<String, Object>();
			userMap.put( "id", user.getId() );
			userMap.put( "name", user.getName() );
			userMap.put( "joinDate", dateFormat.format( user.getJoinDate() ) );
			if ( user.getRole().getName().equals( "ADMIN" ) )
				userMap.put( "isAdmin", true );
			else
				userMap.put( "isAdmin", false );
			if ( user.getAuthor() != null )
			{
				Map<String, Object> authorMap = new LinkedHashMap<String, Object>();
				authorMap.put( "id", user.getAuthor().getId() );
				authorMap.put( "name", user.getAuthor().getName() );
				authorMap.put( "photo", user.getAuthor().getPhotoUrl() );
				userMap.put( "author", authorMap );
			}

			userList.add( userMap );
		}
		return userList;
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

	@Transactional
	@RequestMapping( value = "/grantAdmin", method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> grantAdminRigths( @RequestParam( value = "id" ) final String id, HttpServletRequest request, HttpServletResponse response )
	{
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		if ( id == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "user id missing" );
			return responseMap;
		}

		User user = persistenceStrategy.getUserDAO().getById( id );

		if ( user == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "user not found" );
			return responseMap;
		}

		if ( !( securityService.isAuthorizedForRole( "ADMIN" ) ) )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "error 401 - not authorized" );
			return responseMap;
		}

		// add admin rights
		Role role = persistenceStrategy.getRoleDAO().getRoleByName( "ADMIN" );
		if ( role != null )
		{
			user.setRole( role );
			persistenceStrategy.getUserDAO().persist( user );

			responseMap.put( "status", "ok" );
			responseMap.put( "statusMessage", "User now is administrator" );
		}
		else
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "Admin role not found" );
		}
		return responseMap;
	}


}
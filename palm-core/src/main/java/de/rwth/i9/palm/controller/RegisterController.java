package de.rwth.i9.palm.controller;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.model.Function;
import de.rwth.i9.palm.model.FunctionType;
import de.rwth.i9.palm.model.Role;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserWidget;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Controller
@SessionAttributes( "user" )
@RequestMapping( value = "/register" )
public class RegisterController
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	public Properties config;

	@RequestMapping( method = RequestMethod.GET )
	public ModelAndView getRegistrationForm( 
			final HttpServletResponse response )
	{
		ModelAndView mav = new ModelAndView( "register", "link", "register" );

		// create blank user
		User user = new User();
		mav.addObject( "user", user );

		return mav;
	}
	
	@Transactional
	@RequestMapping( method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> submitRegistrationForm( 
			@ModelAttribute( "user" ) User user, 
			final HttpServletResponse response )
	{
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		if ( user == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "failed to save, expired session or invalid input" );
			return responseMap;
		}

		if ( user.getUsername() == null || user.getUsername().isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "failed to save, invalid input, username is empty" );
			return responseMap;
		}

		if ( user.getName() == null || user.getName().isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "failed to save, invalid input, name is empty" );
			return responseMap;
		}

		if ( user.getPassword() == null || user.getPassword().isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "failed to save, invalid input, password is empty" );
			return responseMap;
		}

		/* store the password encrypted */
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword( passwordEncoder.encode( user.getPassword() ) );

		/* get role and function */
		// get role "USER"
		Role role = persistenceStrategy.getRoleDAO().getRoleByName( "USER" );
		if ( role != null )
			user.setRole( role );

		// get function from role
		List<Function> functions = persistenceStrategy.getFunctionDAO().getFunctionByFunctionTypeAndGrantType( FunctionType.valueOf( role.getName() ), "DEFAULT" );

		if ( !functions.isEmpty() )
			user.setFunctions( functions );

		// set join date
		// get current timestamp
		java.util.Date date = new java.util.Date();
		Timestamp currentTimestamp = new Timestamp( date.getTime() );
		user.setJoinDate( currentTimestamp );

		// list od default widget
		List<Widget> defaultWidgets = new ArrayList<>();
		// get default researcher widget
		defaultWidgets.addAll( persistenceStrategy.getWidgetDAO().getWidget( WidgetType.RESEARCHER, WidgetStatus.DEFAULT ) );
		// get default researcher widget
		defaultWidgets.addAll( persistenceStrategy.getWidgetDAO().getWidget( WidgetType.PUBLICATION, WidgetStatus.DEFAULT ) );
		// get default conference widget
		defaultWidgets.addAll( persistenceStrategy.getWidgetDAO().getWidget( WidgetType.CONFERENCE, WidgetStatus.DEFAULT ) );
		// get default circle widget
		defaultWidgets.addAll( persistenceStrategy.getWidgetDAO().getWidget( WidgetType.CIRCLE, WidgetStatus.DEFAULT ) );
		// get default explore widget
		defaultWidgets.addAll( persistenceStrategy.getWidgetDAO().getWidget( WidgetType.EXPLORE, WidgetStatus.DEFAULT ) );

		// assign all default widget to user
		for ( Widget eachWidget : defaultWidgets )
		{
			UserWidget userWidget = new UserWidget();
			userWidget.setWidget( eachWidget );
			userWidget.setWidgetStatus( WidgetStatus.ACTIVE );
			userWidget.setWidgetColor( eachWidget.getColor() );
			userWidget.setWidgetWidth( eachWidget.getWidgetWidth() );
			userWidget.setPosition( eachWidget.getPosition() );
			if ( eachWidget.getWidgetHeight() != null )
				userWidget.setWidgetHeight( eachWidget.getWidgetHeight() );

			user.addUserWidget( userWidget );
		}

		// persist user at the end
		persistenceStrategy.getUserDAO().persist( user );

		// if status ok then open login popup
		responseMap.put( "status", "ok" );

		return responseMap;
	}


}

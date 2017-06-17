package de.rwth.i9.palm.controller;

import java.util.Iterator;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.helper.TemplateHelper;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserWidget;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.model.WidgetWidth;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.SecurityService;
import de.rwth.i9.palm.wrapper.UserWidgetWrapper;
import de.rwth.i9.palm.wrapper.WidgetWrapper;

@Controller
@SessionAttributes( { "userWidgetsWrapper", "notInstalledWidgetsWrapper" } )
@RequestMapping( value = "/widget" )
public class ManageUserWidgetController
{
	private static final String LINK_NAME = "user";

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private SecurityService securityService;

	/**
	 * Load user widgets
	 * 
	 */
	@Transactional
	@RequestMapping( value = "/{widgetType}", method = RequestMethod.GET )
	public ModelAndView getUserWidgets( 
			@PathVariable String widgetType,
			final HttpServletResponse response 
			)
	{
		ModelAndView model = null;

		if ( !( widgetType.equals( "researcher" ) || widgetType.equals( "publication" ) || widgetType.equals( "venue" ) || widgetType.equals( "circle" ) ) )
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

		model = TemplateHelper.createViewWithLink( "dialogIframeLayout", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.USER, "widget" );

		// get list of user widget and sort
		List<UserWidget> userWidgets = persistenceStrategy.getUserWidgetDAO().getWidget( user, WidgetType.valueOf( widgetType.toUpperCase() ), "content", WidgetStatus.ACTIVE, WidgetStatus.NONACTIVE );
		List<Widget> notInstalledWidgets = persistenceStrategy.getWidgetDAO().getWidget( WidgetType.valueOf( widgetType.toUpperCase() ), "content", WidgetStatus.DEFAULT, WidgetStatus.ACTIVE );

		// disabled widget
		if ( userWidgets != null && !userWidgets.isEmpty() )
		{
			for ( Iterator<Widget> i = notInstalledWidgets.iterator(); i.hasNext(); )
			{
				Widget eachWidget = i.next();
				boolean removeWidget = false;
				// check for already installed widgets
				for ( UserWidget userWidget : userWidgets )
				{
					if ( eachWidget.equals( userWidget.getWidget() ) )
					{
						removeWidget = true;
						break;
					}
				}
				// remove installed widget
				if ( removeWidget )
					i.remove();
			}
		}
		// put into wrapper
		UserWidgetWrapper userWidgetsWrapper = new UserWidgetWrapper();
		userWidgetsWrapper.setUserWidgets( userWidgets );

		WidgetWrapper notInstalledWidgetsWrapper = new WidgetWrapper();
		notInstalledWidgetsWrapper.setWidgets( notInstalledWidgets );

		// assign the model
		model.addObject( "userWidgetsWrapper", userWidgetsWrapper );
		model.addObject( "notInstalledWidgetsWrapper", notInstalledWidgetsWrapper );
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
	@RequestMapping( method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> saveUserWidgets( 
			@ModelAttribute( "userWidgetsWrapper" ) UserWidgetWrapper userWidgetsWrapper,
			@ModelAttribute( "notInstalledWidgetsWrapper" ) WidgetWrapper notInstalledWidgetsWrapper, 
			final HttpServletResponse response ) throws InterruptedException
	{
		// set response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		User user = securityService.getUser();

		if ( user == null || userWidgetsWrapper == null || notInstalledWidgetsWrapper == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "unable to save changes due to session expired" );
			return responseMap;
		}

		for ( UserWidget userWidget : userWidgetsWrapper.getUserWidgets() )
		{
			if ( userWidget.getWidget().getStatus().toUpperCase().equals( "ACTIVE" ) )
			{
				userWidget.setPosition( userWidget.getWidget().getPos() );
				userWidget.setWidgetWidth( WidgetWidth.valueOf( userWidget.getWidget().getWidth().toUpperCase() ) );
				if ( userWidget.getWidget().getHeight() != null )
					userWidget.setWidgetHeight( userWidget.getWidget().getHeight() );
				userWidget.setWidgetStatus( WidgetStatus.ACTIVE );
			}
			else
			{
				userWidget.setWidgetStatus( WidgetStatus.NONACTIVE );
			}
			persistenceStrategy.getUserWidgetDAO().persist( userWidget );
		}

		for ( Widget widget : notInstalledWidgetsWrapper.getWidgets() )
		{
			if ( widget.getStatus().toUpperCase().equals( "ACTIVE" ) )
			{
				UserWidget userWidget = new UserWidget();
				userWidget.setWidget( widget );
				userWidget.setWidgetStatus( WidgetStatus.ACTIVE );
				userWidget.setWidgetColor( widget.getColor() );
				userWidget.setWidgetWidth( WidgetWidth.valueOf( widget.getWidth().toUpperCase() ) );
				userWidget.setPosition( widget.getPos() );
				if ( widget.getHeight() != null )
					userWidget.setWidgetHeight( widget.getHeight() );

				user.addUserWidget( userWidget );
			}
		}

		// at the end persist
		persistenceStrategy.getUserDAO().persist( user );

		responseMap.put( "status", "ok" );

		return responseMap;
	}

}
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.helper.TemplateHelper;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.CircleWidget;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.model.WidgetWidth;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.SecurityService;
import de.rwth.i9.palm.wrapper.CircleWidgetWrapper;
import de.rwth.i9.palm.wrapper.WidgetWrapper;

@Controller
@SessionAttributes( { "circleWidgetsWrapper", "notInstalledWidgetsWrapper" } )
@RequestMapping( value = "/circle/widget" )
public class ManageCircleWidgetController
{
	private static final String LINK_NAME = "user";

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private SecurityService securityService;

	/**
	 * Load Circle widget
	 * 
	 * @param circleId
	 * @param response
	 * @return
	 */
	@Transactional
	@RequestMapping( method = RequestMethod.GET )
	public ModelAndView getCircleWidgets(
			@RequestParam( value = "circleId" ) final String circleId,
			final HttpServletResponse response 
			)
	{
		ModelAndView model = null;


		User user = securityService.getUser();
		if ( user == null )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		Circle circle = persistenceStrategy.getCircleDAO().getById( circleId );

		if ( circle == null )
		{
			model = TemplateHelper.createViewWithLink( "404", "error" );
			model.addObject( "erorMessage", "Circle with id " + circleId + " not found" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "dialogIframeLayout", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.CIRCLE, "widget" );

		// get list of user widget and sort
		List<CircleWidget> circleWidgets = persistenceStrategy.getCircleWidgetDAO().getWidget( circle, WidgetType.CIRCLE, "content", WidgetStatus.ACTIVE, WidgetStatus.NONACTIVE );
		List<Widget> notInstalledWidgets = persistenceStrategy.getWidgetDAO().getWidget( WidgetType.CIRCLE, "content", WidgetStatus.DEFAULT, WidgetStatus.ACTIVE );

		// disabled widget
		if ( circleWidgets != null && !circleWidgets.isEmpty() )
		{
			for ( Iterator<Widget> i = notInstalledWidgets.iterator(); i.hasNext(); )
			{
				Widget eachWidget = i.next();
				boolean removeWidget = false;
				// check for already installed widgets
				for ( CircleWidget circleWidget : circleWidgets )
				{
					if ( eachWidget.equals( circleWidget.getWidget() ) )
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
		CircleWidgetWrapper circleWidgetsWrapper = new CircleWidgetWrapper();
		circleWidgetsWrapper.setCircleWidgets( circleWidgets );

		WidgetWrapper notInstalledWidgetsWrapper = new WidgetWrapper();
		notInstalledWidgetsWrapper.setWidgets( notInstalledWidgets );

		// assign the model
		model.addObject( "circleId", circleId );
		model.addObject( "circleWidgetsWrapper", circleWidgetsWrapper );
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
	public @ResponseBody Map<String, Object> saveCircleWidgets( @ModelAttribute( "circleWidgetsWrapper" ) CircleWidgetWrapper circleWidgetsWrapper,
			@RequestParam( value = "circleId" ) final String circleId,
			@ModelAttribute( "notInstalledWidgetsWrapper" ) WidgetWrapper notInstalledWidgetsWrapper, 
			final HttpServletResponse response ) throws InterruptedException
	{
		// set response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		User user = securityService.getUser();

		if ( user == null || circleWidgetsWrapper == null || notInstalledWidgetsWrapper == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "unable to save changes due to session expired" );
			return responseMap;
		}

		Circle circle = persistenceStrategy.getCircleDAO().getById( circleId );

		if ( circle == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "erorMessage", "Circle with id " + circleId + " not found" );
			return responseMap;
		}

		for ( CircleWidget circleWidget : circleWidgetsWrapper.getCircleWidgets() )
		{
			if ( circleWidget.getWidget().getStatus().toUpperCase().equals( "ACTIVE" ) )
			{
				circleWidget.setPosition( circleWidget.getWidget().getPos() );
				circleWidget.setWidgetWidth( WidgetWidth.valueOf( circleWidget.getWidget().getWidth().toUpperCase() ) );
				if ( circleWidget.getWidget().getHeight() != null )
					circleWidget.setWidgetHeight( circleWidget.getWidget().getHeight() );
				circleWidget.setWidgetStatus( WidgetStatus.ACTIVE );
			}
			else
			{
				circleWidget.setWidgetStatus( WidgetStatus.NONACTIVE );
			}
			persistenceStrategy.getCircleWidgetDAO().persist( circleWidget );
		}

		// if ( notInstalledWidgetsWrapper.getWidgets() != null &&
		// !notInstalledWidgetsWrapper.getWidgets().isEmpty() )
		// {
			for ( Widget widget : notInstalledWidgetsWrapper.getWidgets() )
			{
				if ( widget.getStatus().toUpperCase().equals( "ACTIVE" ) )
				{
					CircleWidget circleWidget = new CircleWidget();
					circleWidget.setWidget( widget );
					circleWidget.setWidgetStatus( WidgetStatus.ACTIVE );
					circleWidget.setWidgetColor( widget.getColor() );
					circleWidget.setWidgetWidth( WidgetWidth.valueOf( widget.getWidth().toUpperCase() ) );
					circleWidget.setPosition( widget.getPos() );
					if ( widget.getHeight() != null )
						circleWidget.setWidgetHeight( widget.getHeight() );

					circleWidget.setCircle( circle );
					circle.addCircleWidget( circleWidget );
				}
			}

			// at the end persist
			persistenceStrategy.getCircleDAO().persist( circle );
		// }

		responseMap.put( "status", "ok" );

		return responseMap;
	}

}
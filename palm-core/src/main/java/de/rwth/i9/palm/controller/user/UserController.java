package de.rwth.i9.palm.controller.user;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.feature.user.UserFeature;
import de.rwth.i9.palm.helper.TemplateHelper;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.SecurityService;

@Controller
@RequestMapping( value = "/user" )
public class UserController
{
	private static final String LINK_NAME = "user";
	
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private UserFeature userFeature;

	@Autowired
	private SecurityService securityService;

	@RequestMapping( method = RequestMethod.GET )
	public ModelAndView userPage( 
			@RequestParam( value = "page", required = false ) final String page,
			final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = TemplateHelper.createViewWithLink( "user", LINK_NAME );

		if( page != null)
			model.addObject( "activeMenu", page );
		else
			model.addObject( "activeMenu", "profile" );

		return model;
	}

	/**
	 * Get user's bookmark for researcher, publication, conference or circle
	 * 
	 * @param bookmarkType
	 * @param userId
	 * @param bookId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/book/{bookmarkType}", method = RequestMethod.GET )
	public ModelAndView getBookmarkPage( 
			@PathVariable String bookmarkType, 
			final HttpServletResponse response )
	{
		// set model and view
		ModelAndView model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );
		List<Widget> widgets = null;

		if ( bookmarkType.equals( "author" ) )
			widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.USER, "user-bookmark-author" );
		else if ( bookmarkType.equals( "publication" ) )
			widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.USER, "user-bookmark-publication" );
		else if ( bookmarkType.equals( "conference" ) )
			widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.USER, "user-bookmark-eventGroup" );
		else if ( bookmarkType.equals( "circle" ) )
			widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.USER, "user-bookmark-circle" );
		
		System.out.println("PERSISTECE : " + persistenceStrategy);
		// assign the model
		model.addObject( "widgets", widgets );

		return model;
	}

	/**
	 * Get user's bookmark for researcher, publication, conference or circle
	 * 
	 * @param bookmarkType
	 * @param userId
	 * @param bookId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/bookmark/{bookmarkType}", method = RequestMethod.GET )
	public @ResponseBody Map<String, Object> getBookmark( 
			@PathVariable String bookmarkType, 
			final HttpServletResponse response)
	{
		User user = securityService.getUser();
		if ( user == null )
		{
			Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "not logged" );
			return responseMap;
		}

		return userFeature.getUserBookmark().getUserBookmark( bookmarkType, user );
	}

	/**
	 * Add user's bookmark for researcher, publication, conference or circle
	 * 
	 * @param bookmarkType
	 * @param userId
	 * @param bookId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/bookmark/{bookmarkType}", method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> bookmarkAdd( 
			@PathVariable String bookmarkType, 
			@RequestParam( value = "userId" ) final String userId, 
			@RequestParam( value = "bookId" ) final String bookId, 
			final HttpServletResponse response)
	{
		return userFeature.getUserBookmark().addUserBookmark( bookmarkType, userId, bookId );
	}

	/**
	 * Add user's bookmark for researcher, publication, conference or circle
	 * 
	 * @param bookmarkType
	 * @param userId
	 * @param bookId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/bookmark/remove/{bookmarkType}", method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> bookmarkRemove( @PathVariable String bookmarkType, @RequestParam( value = "userId" ) final String userId, @RequestParam( value = "bookId" ) final String bookId, final HttpServletResponse response)
	{
		return userFeature.getUserBookmark().removeUserBookmark( bookmarkType, userId, bookId );
	}
	
	@Transactional
	@RequestMapping( value = "/publicationList", method = RequestMethod.GET )
	public @ResponseBody Map<String, Object> getPublicationList( 
			@RequestParam( value = "id", required = false ) final String authorId,
			@RequestParam( value = "query", required = false ) String query, 
			@RequestParam( value = "year", required = false ) String year,
			@RequestParam( value = "orderBy", required = false ) String orderBy,
			@RequestParam( value = "startPage", required = false ) Integer startPage, 
			@RequestParam( value = "maxresult", required = false ) Integer maxresult,
			final HttpServletResponse response)
	{
		if ( year == null )				year = "all";
		if ( query == null )			query = "";
		if ( orderBy == null )			orderBy = "date";
		
		return userFeature.getUserPublication().getPublicationListByAuthorId( authorId, query, year, startPage, maxresult, orderBy );
	}

}
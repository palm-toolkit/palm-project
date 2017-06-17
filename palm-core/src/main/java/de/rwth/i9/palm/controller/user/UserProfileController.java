package de.rwth.i9.palm.controller.user;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.text.WordUtils;
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
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.SecurityService;

@Controller
@SessionAttributes( { "user" } )
@RequestMapping( value = "/user/profile" )
public class UserProfileController
{
	private static final String LINK_NAME = "user";

	@Autowired
	private PersistenceStrategy persistenceStrategy;

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
	public ModelAndView getUserProfilePage( 
			final HttpServletResponse response) throws InterruptedException
	{
		// set model and view
		ModelAndView model = TemplateHelper.createViewWithLink( "widgetLayoutAjax", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.USER, "user-profile" );

		// assign the model
		model.addObject( "widgets", widgets );
		User user = securityService.getUser();
		model.addObject( "user", user );
		if ( user.getAuthor() != null )
		{
			Author researcher = user.getAuthor();
			/* get necessary author detail here */
			Map<String, Object> researcherMap = new LinkedHashMap<String, Object>();
			researcherMap.put( "id", researcher.getId() );
			researcherMap.put( "name", WordUtils.capitalize( researcher.getName() ) );
			if ( researcher.getPhotoUrl() != null )
				researcherMap.put( "photo", researcher.getPhotoUrl() );
			if ( researcher.getAcademicStatus() != null )
				researcherMap.put( "status", researcher.getAcademicStatus() );
			if ( researcher.getInstitution() != null )
				researcherMap.put( "aff", researcher.getInstitution().getName() );
			if ( researcher.getCitedBy() > 0 )
				researcherMap.put( "citedBy", Integer.toString( researcher.getCitedBy() ) );

			if ( researcher.getPublicationAuthors() != null )
				researcherMap.put( "publicationsNumber", researcher.getNoPublication() );
			else
				researcherMap.put( "publicationsNumber", 0 );
			String otherDetail = "";
			if ( researcher.getOtherDetail() != null )
				otherDetail += researcher.getOtherDetail();
			if ( researcher.getDepartment() != null )
				otherDetail += ", " + researcher.getDepartment();
			if ( !otherDetail.equals( "" ) )
				researcherMap.put( "detail", otherDetail );

			researcherMap.put( "isAdded", researcher.isAdded() );

			model.addObject( "authorMap", researcherMap );
		}

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
	public @ResponseBody Map<String, Object> saveUserAuthor( 
			@ModelAttribute( "user" ) User user, 
			@RequestParam( value = "userId", required = false ) final String userId, 
			@RequestParam( value = "authorId", required = false ) final String authorId,
			final HttpServletResponse response ) throws InterruptedException
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
				
		// persist sources from model attribute
		if( user == null && userId != null ){
			user = persistenceStrategy.getUserDAO().getById( userId );
		}
		if( user == null){
			responseMap.put( "status", "ok" );
			return responseMap;
		}
		
		Author author = null;
		if ( authorId != null )
			author = persistenceStrategy.getAuthorDAO().getById( authorId );

		if ( author != null )
		{
			user.setAuthor( author );
			persistenceStrategy.getUserDAO().persist( user );
		}
		
		responseMap.put( "status", "ok" );

		return responseMap;
	}
}
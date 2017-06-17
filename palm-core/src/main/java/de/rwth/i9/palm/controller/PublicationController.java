package de.rwth.i9.palm.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.feature.publication.PublicationFeature;
import de.rwth.i9.palm.helper.TemplateHelper;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserPublicationBookmark;
import de.rwth.i9.palm.model.UserWidget;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.SecurityService;

@Controller
@SessionAttributes( { "sessionDataSet" } )
@RequestMapping( value = "/publication" )
public class PublicationController
{
	private static final String LINK_NAME = "publication";

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private PublicationFeature publicationFeature;

	@Autowired
	private SecurityService securityService;

	/**
	 * Get the publication page
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@RequestMapping( method = RequestMethod.GET )
	@Transactional
	public ModelAndView publicationPage( 
			@RequestParam( value = "sessionid", required = false ) final String sessionId, 
			@RequestParam( value = "id", required = false ) final String publicationId, 
			@RequestParam( value = "title", required = false ) String title,
			final HttpServletResponse response ) throws InterruptedException
	{
		// set model and view
		ModelAndView model = TemplateHelper.createViewWithLink( "publication", LINK_NAME );

		List<Widget> widgets = new ArrayList<Widget>();

		User user = securityService.getUser();

		if ( user != null )
		{
			List<UserWidget> userWidgets = persistenceStrategy.getUserWidgetDAO().getWidget( user, WidgetType.PUBLICATION, WidgetStatus.ACTIVE );
			for ( UserWidget userWidget : userWidgets )
			{
				Widget widget = userWidget.getWidget();
				widget.setColor( userWidget.getWidgetColor() );
				widget.setWidgetHeight( userWidget.getWidgetHeight() );
				widget.setWidgetWidth( userWidget.getWidgetWidth() );
				widget.setPosition( userWidget.getPosition() );

				widgets.add( widget );
			}
		} else
			widgets.addAll( persistenceStrategy.getWidgetDAO().getWidget( WidgetType.PUBLICATION, WidgetStatus.DEFAULT ));
		// assign the model
		model.addObject( "widgets", widgets );

		if ( publicationId != null )
		{
			model.addObject( "targetId", publicationId );
			if ( title == null )
			{
				Publication publication = persistenceStrategy.getPublicationDAO().getById( publicationId );
				if ( publication != null )
					title = publication.getTitle();
			}
		}
		if ( title != null )
			model.addObject( "targetTitle", title );

		return model;
	}

	/**
	 * Get the list of publications based on the following parameters
	 * 
	 * @param query
	 * @param eventName
	 * @param eventId
	 * @param page
	 * @param maxresult
	 * @param response
	 * @return JSON Map
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional
	@RequestMapping( value = "/search", method = RequestMethod.GET )
	public @ResponseBody Map<String, Object> getPublicationList( 
			@RequestParam( value = "query", required = false ) String query,
			@RequestParam( value = "publicationType", required = false ) String publicationType,
			@RequestParam( value = "authorId", required = false ) String authorId,
			@RequestParam( value = "eventId", required = false ) String eventId,
			@RequestParam( value = "page", required = false ) Integer page, 
			@RequestParam( value = "maxresult", required = false ) Integer maxresult,
			@RequestParam( value = "source", required = false ) String source,
			@RequestParam( value = "fulltextSearch", required = false ) String fulltextSearch,
			@RequestParam( value = "year", required = false ) String year,
			@RequestParam( value = "orderBy", required = false ) String orderBy,
			final HttpServletResponse response )
	{
		/* == Set Default Values== */
		if ( query == null ) 			query = "";
		if ( publicationType == null ) 	publicationType = "all";
		if ( page == null )				page = 0;
		if ( maxresult == null )		maxresult = 50;
		if ( fulltextSearch == null || ( fulltextSearch != null && fulltextSearch.equals( "yes" ) ) )
			fulltextSearch = "yes";
		else							fulltextSearch = "no";
		if ( year == null || year.isEmpty() )
			year = "all";
		if ( orderBy == null )			orderBy = "citation";
		// Currently, system only provides query on internal database
		source = "internal";
			
		
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		responseMap.put( "query", query );
		if ( !publicationType.equals( "all" ) )
			responseMap.put( "publicationType", publicationType );
		if ( !year.equals( "all" ) )
			responseMap.put( "year", year );
		responseMap.put( "page", page );
		responseMap.put( "maxresult", maxresult );
		responseMap.put( "fulltextSearch", fulltextSearch );
		responseMap.put( "orderBy", orderBy );
		
		Map<String, Object> publicationMap = publicationFeature.getPublicationSearch().getPublicationListByQuery( query, publicationType, authorId, eventId, page, maxresult, source, fulltextSearch, year, orderBy );
		
		if ( (Integer) publicationMap.get( "totalCount" ) > 0 )
		{
			responseMap.put( "totalCount", (Integer) publicationMap.get( "totalCount" ) );
			return publicationFeature.getPublicationSearch().printJsonOutput( responseMap, (List<Publication>) publicationMap.get( "publications" ) );
		}
		else
		{
			responseMap.put( "totalCount", 0 );
			responseMap.put( "count", 0 );
			return responseMap;
		}
	}
	
	/**
	 * Get the list of similar/identical publications on PALM
	 * @param title
	 * @param response
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional
	@RequestMapping( value = "/similar", method = RequestMethod.GET )
	public @ResponseBody Map<String, Object> getSimilarPublication( 
			@RequestParam( value = "title", required = false ) String title,
			// list of full author names, separated with _-_
			@RequestParam( value = "authorString", required = false ) String authorString,
			final HttpServletResponse response )
	{	
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		Map<String, Object> publicationMap = publicationFeature.getPublicationSimilar().getSimilarPublication( title, authorString );
		
		if ( (Integer) publicationMap.get( "totalCount" ) > 0 )
		{
			responseMap.put( "totalCount", (Integer) publicationMap.get( "totalCount" ) );
			return publicationFeature.getPublicationSearch().printJsonOutput( responseMap, (List<Publication>) publicationMap.get( "publications" ) );
		}
		else
		{
			responseMap.put( "totalCount", 0 );
			responseMap.put( "count", 0 );
			return responseMap;
		}
	}

	/**
	 * Get details( publication content ) from a publication
	 * 
	 * @param id
	 *            of publication
	 * @param uri
	 * @param response
	 * @return JSON Map
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ExecutionException
	 */
	@RequestMapping( value = "/detail", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Map<String, Object> getPublicationDetail( 
			@RequestParam( value = "id", required = false ) final String id, 
			@RequestParam( value = "uri", required = false ) final String uri,
			@RequestParam( value = "section", required = false ) String section,
			final HttpServletResponse response) throws InterruptedException, IOException, ExecutionException
	{
		if ( section == null || ( section != null && section.isEmpty() ) )
			section = "all";
		return publicationFeature.getPublicationDetail().getPublicationDetailById( id, section );
	}
	
	/**
	 * Get the basic statistic (publication type, language, etc) from a
	 * publication
	 * 
	 * @param id
	 *            of publication
	 * @param uri
	 * @param response
	 * @return JSON Map
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ExecutionException
	 */
	@RequestMapping( value = "/basicInformation", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Map<String, Object> getPublicationBasicStatistic( 
			@RequestParam( value = "id", required = false ) final String id, 
			@RequestParam( value = "uri", required = false ) final String uri, 
			final HttpServletResponse response) throws InterruptedException, IOException, ExecutionException
	{
		Map<String, Object> responseMap = publicationFeature.getPublicationBasicStatistic().getPublicationBasicStatisticById( id );

		// check whether publication is already booked or not
		User user = securityService.getUser();
		if ( user != null )
		{
			Publication publication = persistenceStrategy.getPublicationDAO().getById( id );
			if ( publication == null )
				return responseMap;

			UserPublicationBookmark upb = persistenceStrategy.getUserPublicationBookmarkDAO().getByUserAndPublication( user, publication );
			if ( upb != null )
				responseMap.put( "booked", true );
			else
				responseMap.put( "booked", false );
		}
		return responseMap;
	}
	
	/**
	 * Get the basic statistic (publication type, language, topics etc) from a
	 * publication
	 * 
	 * @param id
	 *            of publication
	 * @param uri
	 * @param response
	 * @return JSON Map
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws URISyntaxException
	 */
	@RequestMapping( value = "/basicInformationAndTopics", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Map<String, Object> getPublicationBasicInformationAndTopics( @RequestParam( value = "id", required = false ) final String id, @RequestParam( value = "uri", required = false ) final String uri, final HttpServletResponse response ) throws InterruptedException, IOException, ExecutionException, URISyntaxException
	{

		// basic info selected paper
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		Map<String, Object> publicationMap = publicationFeature.getPublicationBasicStatistic().getPublicationBasicStatisticById( id );

		if ( publicationMap.isEmpty() )
		{
			responseMap.put( "status", "error" );
			return responseMap;
		}

		responseMap.put( "status", "ok" );
		responseMap.put( "basicinfo", publicationMap );


		// topics selected paper
		Map<String, Object> topics = publicationFeature.getPublicationMining().getPublicationExtractedTopicsById( id, null, null );
		if ( topics.get( "status" ) != "ok" )
			topics = publicationFeature.getPublicationTopicModeling().getTopicComposition( id, true );

		responseMap.put( "topics", topics );

		// publications and topics in the same venue
		List<Map<String, Object>> publicationsList = new ArrayList<Map<String, Object>>();
		Publication pub = persistenceStrategy.getPublicationDAO().getById( id );

		if ( pub.getEvent() != null )
		{
			Map<String, Object> publicationsMap = persistenceStrategy.getPublicationDAO().getPublicationWithPaging( "", "all", null, pub.getEvent(), null, 15, "all", "citation" );
			List<Publication> publications = (List<Publication>) publicationsMap.get( "publications" );

			if ( publications.isEmpty() )
				responseMap.put( "publications", new ArrayList<Publication>() );
			else
			{
				for ( Publication publ : publications )
				{
					Map<String, Object> publTopics = publicationFeature.getPublicationMining().getPublicationExtractedTopicsById( publ.getId(), null, null );
					if ( publTopics.get( "status" ) != "ok" )
						publTopics = publicationFeature.getPublicationTopicModeling().getTopicComposition( publ.getId(), true );

					Map<String, Object> publMap = publicationFeature.getPublicationSearch().printElementAsJsonOutput( publ );
					if ( !publMap.isEmpty() )
						publMap.put( "topics", publTopics );

					publicationsList.add( publMap );
				}
			}

		}
		responseMap.put( "publications", publicationsList );

		// check whether publication is already booked or not
		User user = securityService.getUser();
		if ( user != null )
		{
			Publication publication = persistenceStrategy.getPublicationDAO().getById( id );
			if ( publication == null )
				return responseMap;

			UserPublicationBookmark upb = persistenceStrategy.getUserPublicationBookmarkDAO().getByUserAndPublication( user, publication );
			if ( upb != null )
				responseMap.put( "booked", true );
			else
				responseMap.put( "booked", false );
		}
		return responseMap;
	}

	@RequestMapping( value = "/pdfHtmlExtract", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Map<String, Object> doPdfHtmlExtraction( 
			@RequestParam( value = "id", required = false ) final String id,
			@RequestParam( value = "pid", required = false ) final String pid,
			final HttpServletResponse response) throws InterruptedException, IOException, ExecutionException
	{
		return publicationFeature.getPublicationApi().extractPublicationFromPdfHtml( id, pid );
	}
	
	/**
	 * Extract scientific information from Pdf on a specific publication or given URL
	 * 
	 * @param id
	 *            of publication
	 * @param url
	 * 			PDF Url
	 * @param response
	 * @return JSON Map
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ExecutionException
	 */
	@RequestMapping( value = "/pdfExtract", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Map<String, Object> doPdfExtraction( 
			@RequestParam( value = "id", required = false ) final String id,
			@RequestParam( value = "url", required = false ) final String url, 
			final HttpServletResponse response) throws InterruptedException, IOException, ExecutionException
	{
		if( id != null )
			return publicationFeature.getPublicationManage().extractPublicationFromPdf( id );
		else
			return publicationFeature.getPublicationApi().extractPfdFile( url );
	}
	

	/**
	 * Extract scientific information from web page on a specific publication or given URL
	 * 
	 * @param id
	 * 			of publication
	 * @param url
	 * 			Digital Library URL
	 * @param response
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ExecutionException
	 */
	@RequestMapping( value = "/htmlExtract", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Map<String, String> doHtmlExtractionTest(
			@RequestParam( value = "id", required = false ) final String id,
			@RequestParam( value = "url", required = false ) final String url, 
			final HttpServletResponse response) throws InterruptedException, IOException, ExecutionException
	{
		return publicationFeature.getPublicationApi().extractHtmlFile( url );
	}

	/**
	 * Get list of PublicationTopic
	 * 
	 * @param id
	 * @param response
	 * @return
	 * @throws ExecutionException
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping( value = "/topic", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Map<String, Object> getPublicationTopic( 
			@RequestParam( value = "id", required = false ) final String id,
			@RequestParam( value = "pid", required = false ) final String pid, 
			@RequestParam( value = "maxRetrieve", required = false ) final String maxRetrieve,
			final HttpServletResponse response ) throws UnsupportedEncodingException, InterruptedException, URISyntaxException, ExecutionException
	{
		return publicationFeature.getPublicationMining().getPublicationExtractedTopicsById( id, pid, maxRetrieve );
	}
	
	@RequestMapping( value = "/topicComposition", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Map<String, Object> getPublicationTopicComposition( @RequestParam( value = "id", required = false ) final String publicationId, @RequestParam( value = "updateResult", required = false ) final String updateResult, final HttpServletResponse response)
	{
		if ( publicationId != null )
		{
			boolean isReplaceExistingResult = false;
			if ( updateResult != null && updateResult.equals( "yes" ) )
				isReplaceExistingResult = true;
			return publicationFeature.getPublicationTopicModeling().getTopicComposition( publicationId, isReplaceExistingResult );
		}
		return Collections.emptyMap();
	}

	/**
	 * 
	 * @param publicationId
	 * @param updateResult
	 * @param response
	 * @return
	 */
	@RequestMapping( value = "/topicCompositionUniCloud", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Map<String, Object> getPublicationTopicCompositionCloudUnigrams( @RequestParam( value = "id", required = false ) final String publicationId, @RequestParam( value = "updateResult", required = false ) final String updateResult, final HttpServletResponse response)
	{
		if ( publicationId != null )
		{
			boolean isReplaceExistingResult = false;
			if ( updateResult != null && updateResult.equals( "yes" ) )
				isReplaceExistingResult = true;

			return publicationFeature.getPublicationTopicModeling().getTopicModelUniCloud( publicationId, isReplaceExistingResult );
		}
		return Collections.emptyMap();
	}

	@RequestMapping( value = "/topicCompositionNCloud", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Map<String, Object> getPublicationTopicCompositionCloudNgrams( @RequestParam( value = "id", required = false ) final String publicationId, @RequestParam( value = "updateResult", required = false ) final String updateResult, final HttpServletResponse response)
	{
		if ( publicationId != null )
		{
			boolean isReplaceExistingResult = false;
			if ( updateResult != null && updateResult.equals( "yes" ) )
				isReplaceExistingResult = true;

			return publicationFeature.getPublicationTopicModeling().getTopicModelNCloud( publicationId, isReplaceExistingResult );
		}
		return Collections.emptyMap();
	}

	/**
	 * Get Similar publications of given author
	 * 
	 * @param publications
	 * @param startPage
	 * @param maxresult
	 * @param response
	 * @return
	 */
	@RequestMapping( value = "/similarPublicationsList", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Map<String, Object> getSimilarPublicationList( @RequestParam( value = "id", required = false ) final String publicationId, @RequestParam( value = "startPage", required = false ) Integer startPage, @RequestParam( value = "maxresult", required = false ) Integer maxresult, final HttpServletResponse response)
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		if ( publicationId == null || publicationId.equals( "" ) )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "publicationId null" );
			return responseMap;
		}

		if ( startPage == null )
			startPage = 0;
		if ( maxresult == null )
			maxresult = 10;

		// get publication
		Publication publication = persistenceStrategy.getPublicationDAO().getById( publicationId );

		if ( publication == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "publication not found in database" );
			return responseMap;
		}

		// get recommended publications based on calculations
		responseMap.putAll( publicationFeature.getPublicationTopicModeling().getResearcherSimilarPublicationMap( publication, startPage, maxresult ) );

		return responseMap;
	}

	/**
	 * Get bibtex modelview
	 * 
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/bibtexview", method = RequestMethod.GET )
	public ModelAndView addBibtexView( 
			@RequestParam( value = "id", required = false ) final String id, 
			final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = null;

		model = TemplateHelper.createViewWithLink( "dialogIframeLayout", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.PUBLICATION, "publication-bibtex" );

		// assign the model
		model.addObject( "widgets", widgets );
		model.addObject( "targetId", id );

		return model;
	}

	/**
	 * Get Bibtex
	 * 
	 * @param id
	 * @param response
	 * @return
	 */
	@RequestMapping( value = "/bibtex", method = RequestMethod.GET )
	@Transactional
	public @ResponseBody Map<String, Object> getPublicationBibtex( 
			@RequestParam( value = "id", required = false ) final String id, 
			@RequestParam( value = "retrieve", required = false ) String retrieve, 
			final HttpServletResponse response)
	{
		if( retrieve == null )
			retrieve = "all";
		return publicationFeature.getPublicationApi().getPublicationBibTex( id, retrieve );
	}
}
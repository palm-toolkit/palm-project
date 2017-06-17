package de.rwth.i9.palm.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

import de.rwth.i9.palm.feature.publication.PublicationFeature;
import de.rwth.i9.palm.helper.TemplateHelper;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.CompletionStatus;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.model.FileType;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationAuthor;
import de.rwth.i9.palm.model.PublicationFile;
import de.rwth.i9.palm.model.PublicationType;
import de.rwth.i9.palm.model.SourceType;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.pdfextraction.service.PdfExtractionService;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.SecurityService;

@Controller
@SessionAttributes( "publication" )
@RequestMapping( value = "/publication" )
public class ManagePublicationController
{
	private static final String LINK_NAME = "administration";

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private PdfExtractionService pdfExtractionService;

	@Autowired
	private PublicationFeature publicationFeature;

	@Autowired
	private SecurityService securityService;

	/**
	 * Load the add publication form together with publication object
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/add", method = RequestMethod.GET )
	public ModelAndView addNewPublication( final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = null;

		if ( securityService.getUser() == null )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "dialogIframeLayout", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.PUBLICATION, "add" );

		// create blank Publication
		Publication publication = new Publication();

		// assign the model
		model.addObject( "widgets", widgets );
		model.addObject( "publication", publication );

		return model;
	}

	/**
	 * Save changes from Add publication detail, via Spring binding
	 * 
	 * @param extractionServiceListWrapper
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/add", method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> saveNewPublication( 
			@ModelAttribute( "publication" ) Publication publication, 
			@RequestParam( value = "author-list-ids", required = false ) String authorListIds, 
			@RequestParam( value = "keywordList", required = false ) String keywordList, 
			@RequestParam( value = "publication-date", required = false ) String publicationDate, 
			@RequestParam( value = "venue-type", required = false ) String venueType, 
			@RequestParam( value = "venue-id", required = false ) String venueId, 
			@RequestParam( value = "volume", required = false ) String volume, 
			@RequestParam( value = "pages", required = false ) String pages, 
			@RequestParam( value = "publisher", required = false ) String publisher,
			@RequestParam( value = "newResourceSelect", required = false ) String newResourceSelect, 
			@RequestParam( value = "newResourceInput", required = false ) String newResourceInput, 
			final HttpServletResponse response) throws InterruptedException
	{
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		if ( publication == null || publication.getTitle() == null || publication.getTitle().isEmpty() || authorListIds == null || authorListIds.isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "Publication not found due to missing input or expired sission" );
		}


		/* Insert selected author into publication */
		// get author id split by "_#_"
		String[] authorIds = authorListIds.split( "_#_" );
		int authorPosition = 0;
		for ( String authorId : authorIds )
		{
			Author author = persistenceStrategy.getAuthorDAO().getById( authorId );
			if ( author == null )
				continue;

			author.setUpdateInterest( true );
			PublicationAuthor publicationAuthor = new PublicationAuthor();
			publicationAuthor.setAuthor( author );
			publicationAuthor.setPublication( publication );
			publicationAuthor.setPosition( authorPosition );
			publication.addPublicationAuthor( publicationAuthor );

			authorPosition++;
		}

		if ( publication.getPublicationAuthors() == null || publication.getPublicationAuthors().isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "Failed to save new publication, publication contain no authors" );
		}

		/* Abstract */
		if ( publication.getAbstractText().isEmpty() )
			publication.setAbstractText( null );
		else
		{
			publication.setAbstractStatus( CompletionStatus.COMPLETE );
			publication.setContentUpdated( true );
		}

		/* Insert Keyword if any */
		if ( keywordList != null && !keywordList.isEmpty() )
		{
			publication.setKeywordStatus( CompletionStatus.COMPLETE );
			publication.setKeywordText( keywordList.replace( "_#_", "," ) );
			publication.setContentUpdated( true );
		}

		/* Insert publication date - expect valid publication date */
		if ( publicationDate != null && !publicationDate.isEmpty() )
		{
			// set date format
			DateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy", Locale.ENGLISH );
			try
			{
				Date date = null;
				if ( publicationDate.startsWith( "dd/mm/" ) )
				{
					date = dateFormat.parse( "01/01/" + publicationDate.substring( 6, 10 ) );
					publication.setPublicationDateFormat( "yyyy" );
				}
				else if ( publicationDate.startsWith( "dd/" ) )
				{
					date = dateFormat.parse( "01/" + publicationDate.substring( 3, 10 ) );
					publication.setPublicationDateFormat( "yyyy/M" );
				}
				else
				{
					date = dateFormat.parse( publicationDate );
					publication.setPublicationDateFormat( "yyyy/M/d" );
				}
				publication.setPublicationDate( date );

				Calendar cal = Calendar.getInstance();
				cal.setTime( date );
				publication.setYear( Integer.toString( cal.get( Calendar.YEAR ) ) );
			}
			catch ( Exception e )
			{
			}
		}

		/* check for publication type */
		try
		{
			PublicationType publicationType = PublicationType.valueOf( venueType.toUpperCase() );
			publication.setPublicationType( publicationType );
			publication.setPublicationTypeStatus( CompletionStatus.COMPLETE );
		}
		catch ( Exception e )
		{
		}

		// check for volume
		int inputVolume = 0;
		if ( volume != null && !volume.isEmpty() )
		{
			try
			{
				inputVolume = Integer.parseInt( volume );
			}
			catch ( Exception e )
			{
			}

			if ( inputVolume > 0 )
				publication.addOrUpdateAdditionalInformation( "volume", inputVolume );
		}

		// check for venue
		if ( venueId != null && !venueId.isEmpty() )
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime( publication.getPublicationDate() );
			String inputYear = Integer.toString( cal.get( Calendar.YEAR ) );

			EventGroup eventGroup = persistenceStrategy.getEventGroupDAO().getById( venueId );

			// set event
			Event newEvent = null;
			if ( eventGroup.getEvents() != null && !eventGroup.getEvents().isEmpty() )
			{
				for ( Event eachEvent : eventGroup.getEvents() )
				{
					if ( eachEvent.getYear().equals( inputYear ) )
					{
						newEvent = eachEvent;
						break;
					}
				}
			}
			if ( newEvent == null )
			{
				newEvent = new Event();
				newEvent.setEventGroup( eventGroup );
				newEvent.setYear( inputYear );
				if ( inputVolume > 0 )
					newEvent.setVolume( Integer.toString( inputVolume ) );

				newEvent.addPublication( publication );
			}
			newEvent.setUpdateInterest( true );
			publication.setEvent( newEvent );
		}

		// pages
		try
		{
			if ( pages != null && !pages.isEmpty() )
			{
				String[] splitPages = pages.split( "-" );
				if ( splitPages.length == 2 )
				{
					publication.setStartPage( Integer.parseInt( splitPages[0].trim() ) );
					publication.setEndPage( Integer.parseInt( splitPages[1].trim() ) );
				}
				else if ( splitPages.length == 1 )
				{
					publication.setStartPage( Integer.parseInt( splitPages[0].trim() ) );
				}
			}
		}
		catch ( Exception e )
		{
			// TODO: handle exception
		}

		// publisher
		if ( publisher != null && !publisher.isEmpty() )
		{
			publication.addOrUpdateAdditionalInformation( "publisher", publisher );
		}
		// set publication updated
		publication.setContentUpdated( true );
		// at the end persist publication
		persistenceStrategy.getPublicationDAO().persist( publication );

		// update author interest flag
		List<Author> authors = publication.getAuthors();
		if ( authors != null && !authors.isEmpty() )
		{
			for ( Author author : authors )
			{
				if ( author.isAdded() )
				{
					author.setUpdateInterest( true );
					author.reCalculateNumberOfPublicationAndCitation();
					persistenceStrategy.getAuthorDAO().persist( author );
				}
			}
		}

		responseMap.put( "status", "ok" );
		responseMap.put( "statusMessage", "changes on publication saved" );

		Map<String, String> publicationMap = new LinkedHashMap<String, String>();
		publicationMap.put( "id", publication.getId() );
		publicationMap.put( "title", publication.getTitle() );
		responseMap.put( "publication", publicationMap );

		return responseMap;
	}

	/**
	 * Upload article via jquery ajax file upload saved to database
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws TikaException
	 * @throws SAXException
	 */
	@RequestMapping( value = "/upload", method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> multiUpload( MultipartHttpServletRequest request, HttpServletResponse response ) throws IOException, InterruptedException, ExecutionException
	{
		return uploadAndExtractPdf( request );
	}

	private Map<String, Object> uploadAndExtractPdf( MultipartHttpServletRequest request ) throws IOException, InterruptedException, ExecutionException
	{
		// build an iterator
		Iterator<String> itr = request.getFileNames();

		Map<String, Object> extractedPdfMap = null;

		// get each file
		while ( itr.hasNext() )
		{
			MultipartFile mpf = request.getFile( itr.next() );

			// extract pdf file
			extractedPdfMap = pdfExtractionService.extractPdfFromInputStream( mpf.getInputStream() );

			break;
		}
		return extractedPdfMap;
	}

	/**
	 * Load the add publication form together with publication object
	 * 
	 * @param sessionId
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/edit", method = RequestMethod.GET )
	public ModelAndView editPublication( @RequestParam( value = "sessionid", required = false ) final String sessionId, @RequestParam( value = "id" ) final String publicationId, final HttpServletResponse response) throws InterruptedException
	{
		ModelAndView model = null;

		if ( securityService.getUser() == null )
		{
			model = TemplateHelper.createViewWithLink( "401", "error" );
			return model;
		}

		model = TemplateHelper.createViewWithLink( "dialogIframeLayout", LINK_NAME );
		List<Widget> widgets = persistenceStrategy.getWidgetDAO().getActiveWidgetByWidgetTypeAndGroup( WidgetType.PUBLICATION, "edit" );

		// get publication
		Publication publication = persistenceStrategy.getPublicationDAO().getById( publicationId );
		publication.setAuthors();
		publication.getEvent();

		// assign the model
		model.addObject( "widgets", widgets );
		model.addObject( "publication", publication );

		return model;
	}

	/**
	 * Save changes from Add publication detail, via Spring binding
	 * 
	 * @param extractionServiceListWrapper
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@Transactional
	@RequestMapping( value = "/edit", method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> saveEditedPublication( 
			@RequestParam( value = "publicationId" ) String publicationId,
			@RequestParam( value = "title", required = false ) String title, 
			@RequestParam( value = "author-list-ids", required = false ) String authorListIds, 
			@RequestParam( value = "abstractText", required = false ) String abstractText, 
			@RequestParam( value = "keywordList", required = false ) String keywordList, 
			@RequestParam( value = "publication-date", required = false ) String publicationDate, 
			@RequestParam( value = "venue-type", required = false ) String venueType, 
			@RequestParam( value = "venue-id", required = false ) String venueId, 
			@RequestParam( value = "volume", required = false ) String volume, 
			@RequestParam( value = "pages", required = false ) String pages, 
			@RequestParam( value = "publisher", required = false ) String publisher,
			@RequestParam( value = "newResourceSelect", required = false ) String newResourceSelect, 
			@RequestParam( value = "newResourceInput", required = false ) String newResourceInput,
			final HttpServletResponse response) throws InterruptedException
	{

		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		Publication publication = persistenceStrategy.getPublicationDAO().getById( publicationId );
		if ( publication == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "Publication not found due to missing input or expired sission" );
		}
		
		if( title != null && !title.isEmpty())
			publication.setTitle( title );

		if ( authorListIds != null && !authorListIds.isEmpty() )
		{
			/* Insert selected author into publication */
			// get author id split by "_#_"
			String[] authorIds = authorListIds.split( "_#_" );

			// first remove all PublicationAuthor from publication
			for ( PublicationAuthor publicationAuthor : publication.getPublicationAuthors() )
			{
				publicationAuthor.setPublication( null );
				publicationAuthor.setAuthor( null );
				persistenceStrategy.getPublicationAuthorDAO().delete( publicationAuthor );
			}
			publication.clearPublicationAuthors();

			Set<PublicationAuthor> newAuthorPublications = new HashSet<PublicationAuthor>();
			int authorPosition = 0;
			for ( String authorId : authorIds )
			{
				Author author = persistenceStrategy.getAuthorDAO().getById( authorId );
				if ( author == null )
					continue;

				PublicationAuthor publicationAuthor = new PublicationAuthor();
				publicationAuthor.setAuthor( author );
				publicationAuthor.setPublication( publication );
				publicationAuthor.setPosition( authorPosition );
				// publication.addPublicationAuthor( publicationAuthor );
				newAuthorPublications.add( publicationAuthor );

				authorPosition++;
			}
			publication.setPublicationAuthors( newAuthorPublications );
		}
		// if ( publication.getPublicationAuthors() == null ||
		// publication.getPublicationAuthors().isEmpty() )
		// {
		// responseMap.put( "status", "error" );
		// responseMap.put( "statusMessage", "Failed to save new publication,
		// publication contain no authors" );
		// }

		/* ABstract */
		if ( abstractText != null )
		{
			if ( abstractText.isEmpty() )
				publication.setAbstractText( null );
			else
			{
				publication.setAbstractText( abstractText );
				publication.setAbstractStatus( CompletionStatus.COMPLETE );
				// set publication updated
				publication.setContentUpdated( true );
			}
			// set author update

		}

		/* Insert Keyword if any */
		if ( keywordList != null && !keywordList.isEmpty() )
		{
			publication.setKeywordStatus( CompletionStatus.COMPLETE );
			keywordList = keywordList.replace( ";", "," );
			keywordList = keywordList.replace( "_#_", "," );
			publication.setKeywordText( keywordList );
			// set publication updated
			publication.setContentUpdated( true );
		}
		else
		{
			// publication does not have keywords
			publication.setKeywordStatus( CompletionStatus.COMPLETE );
		}

		/* Insert publication date - expect valid publication date */
		if ( publicationDate != null && !publicationDate.isEmpty() )
		{
			// set date format
			DateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy", Locale.ENGLISH );
			try
			{
				Date date = null;
				if ( publicationDate.startsWith( "dd/mm/" ) )
				{
					date = dateFormat.parse( "01/01/" + publicationDate.substring( 6, 10 ) );
					publication.setPublicationDateFormat( "yyyy" );
				}
				else if ( publicationDate.startsWith( "dd/" ) )
				{
					date = dateFormat.parse( "01/" + publicationDate.substring( 3, 10 ) );
					publication.setPublicationDateFormat( "yyyy/M" );
				}
				else
				{
					date = dateFormat.parse( publicationDate );
					publication.setPublicationDateFormat( "yyyy/M/d" );
				}
				publication.setPublicationDate( date );

				Calendar cal = Calendar.getInstance();
				cal.setTime( date );
				publication.setYear( Integer.toString( cal.get( Calendar.YEAR ) ) );
			}
			catch ( Exception e )
			{
			}
		}

		/* check for publication type */
		if ( venueType != null && !venueType.isEmpty() )
		{
			try
			{
				PublicationType publicationType = PublicationType.valueOf( venueType.toUpperCase() );
				if ( !publicationType.equals( publication.getPublicationType() ) )
				{
					publication.setPublicationType( publicationType );
					publication.setPublicationTypeStatus( CompletionStatus.COMPLETE );
				}
			}
			catch ( Exception e )
			{
			}
		}

		// check for volume
		int inputVolume = 0;
		if ( volume != null && !volume.isEmpty() )
		{
			try
			{
				inputVolume = Integer.parseInt( volume );
			}
			catch ( Exception e )
			{
			}

			if ( inputVolume > 0 )
				publication.addOrUpdateAdditionalInformation( "volume", inputVolume );
		}

		// check for venue
		if ( venueId != null && !venueId.isEmpty() )
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime( publication.getPublicationDate() );
			String inputYear = Integer.toString( cal.get( Calendar.YEAR ) );

			EventGroup eventGroup = persistenceStrategy.getEventGroupDAO().getById( venueId );
			if ( publication.getEvent() == null )
			{
				Event newEvent = null;
				if ( eventGroup.getEvents() != null && !eventGroup.getEvents().isEmpty() )
				{
					for ( Event eachEvent : eventGroup.getEvents() )
					{
						if ( eachEvent.getYear().equals( inputYear ) )
						{
							newEvent = eachEvent;
							break;
						}
					}
				}
				if ( newEvent == null )
				{
					newEvent = new Event();
					newEvent.setEventGroup( eventGroup );
					newEvent.setYear( inputYear );
					if ( inputVolume > 0 )
						newEvent.setVolume( Integer.toString( inputVolume ) );

					newEvent.addPublication( publication );
				}
				newEvent.setUpdateInterest( true );
				publication.setEvent( newEvent );
			}
			// already assign to event , but somehow probably wrong
			else
			{
				// first check for eventGroup
				Event event = publication.getEvent();
				if ( event.getEventGroup().equals( eventGroup ) )
				{
					// if on save event group, check with date

					if ( !event.getYear().equals( inputYear ) )
					{
						// assign with new event, because year doesn't match

						Event newEvent = null;
						if ( eventGroup.getEvents() != null && !eventGroup.getEvents().isEmpty() )
						{
							for ( Event eachEvent : eventGroup.getEvents() )
							{
								if ( eachEvent.getYear().equals( inputYear ) )
								{
									newEvent = eachEvent;
									break;
								}
							}
						}

						if ( newEvent == null )
						{
							newEvent = new Event();
							newEvent.setEventGroup( eventGroup );
							newEvent.setYear( inputYear );
							if ( inputVolume > 0 )
								newEvent.setVolume( Integer.toString( inputVolume ) );
							newEvent.addPublication( publication );
						}
						newEvent.setUpdateInterest( true );
						publication.setEvent( newEvent );
					}
					else
					{
						// if volume added, set volume
						if ( inputVolume > 0 )
							event.setVolume( Integer.toString( inputVolume ) );
					}
				}
				// change eventgroup and event
				else
				{
					Event newEvent = null;
					if ( eventGroup.getEvents() != null && !eventGroup.getEvents().isEmpty() )
					{
						for ( Event eachEvent : eventGroup.getEvents() )
						{
							if ( eachEvent.getYear().equals( inputYear ) )
							{
								newEvent = eachEvent;
								break;
							}
						}
					}

					if ( newEvent == null )
					{
						newEvent = new Event();
						newEvent.setEventGroup( eventGroup );
						newEvent.setYear( inputYear );
						if ( inputVolume > 0 )
							newEvent.setVolume( Integer.toString( inputVolume ) );
						newEvent.addPublication( publication );
					}
					newEvent.setUpdateInterest( true );
					publication.setEvent( newEvent );
				}

			}
		}

		// pages
		try
		{
			if ( pages != null && !pages.isEmpty() )
			{
				String[] splitPages = pages.split( "-" );
				if ( splitPages.length == 2 )
				{
					publication.setStartPage( Integer.parseInt( splitPages[0].trim() ) );
					publication.setEndPage( Integer.parseInt( splitPages[1].trim() ) );
				}
				else if ( splitPages.length == 1 )
				{
					publication.setStartPage( Integer.parseInt( splitPages[0].trim() ) );
				}
			}
		}
		catch ( Exception e )
		{
		}

		// update newResourceInput
		if ( newResourceInput != null )
		{
			PublicationFile pubFile = new PublicationFile();
			pubFile.setSourceType( SourceType.USER );
			pubFile.setFileType( FileType.HTML );
			if ( newResourceSelect != null && newResourceSelect.equals( "pdf" ) )
				pubFile.setFileType( FileType.PDF );
			pubFile.setUrl( newResourceInput );
			pubFile.setPublication( publication );

			publication.addPublicationFile( pubFile );
		}

		// update interest flag
		if ( title != null || abstractText != null || keywordList != null )
		{
			// author interest flag
			List<Author> authors = publication.getAuthors();
			if ( authors != null && !authors.isEmpty() )
			{
				for ( Author author : authors )
				{
					if ( author.isAdded() )
					{
						author.setUpdateInterest( true );
						persistenceStrategy.getAuthorDAO().persist( author );
					}
				}
			}

			// circle interest flag
			Set<Circle> circles = publication.getCircles();
			if ( circles != null && !circles.isEmpty() )
			{
				for ( Circle circle : circles )
				{
					circle.setUpdateInterest( true );
					persistenceStrategy.getCircleDAO().persist( circle );
				}
			}

			// event interest flag
			if ( publication.getEvent() != null )
			{
				Event event = publication.getEvent();
				event.setUpdateInterest( true );
				persistenceStrategy.getEventDAO().persist( event );
			}
		}
		// at the end persist publication
		persistenceStrategy.getPublicationDAO().persist( publication );

		responseMap.put( "status", "ok" );
		responseMap.put( "statusMessage", "changes on publication saved" );

		Map<String, String> publicationMap = new LinkedHashMap<String, String>();
		publicationMap.put( "id", publication.getId() );
		publicationMap.put( "title", publication.getTitle() );
		responseMap.put( "publication", publicationMap );

		return responseMap;
	}

	@Transactional
	@RequestMapping( value = "/delete", method = RequestMethod.POST )
	public @ResponseBody Map<String, Object> deletePublication( @RequestParam( value = "id" ) final String id, HttpServletRequest request, HttpServletResponse response )
	{
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// Check if user is the author of publication
		boolean userIsPublicationAuthor = false;

		if ( id == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "publication id missing" );
			return responseMap;
		}

		Publication publication = persistenceStrategy.getPublicationDAO().getById( id );

		if ( publication == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "publication not found" );
			return responseMap;
		}
		User user = securityService.getUser();
		if ( user != null && user.getAuthor() != null )
			for ( Author author : publication.getAuthors() )
			{
				if ( author.equals( user.getAuthor() ) )
				{
					userIsPublicationAuthor = true;
					break;
				}
			}

		if ( !( securityService.isAuthorizedForRole( "ADMIN" ) || userIsPublicationAuthor ) )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "error 401 - not authorized" );
			return responseMap;
		}

		// remove publication
		publicationFeature.doDeletePublication().deletePublication( publication );

		responseMap.put( "status", "ok" );
		responseMap.put( "statusMessage", "Publication is deleted" );


		return responseMap;
	}

}
package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.helper.comparator.PublicationByNoCitationComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.AuthorSource;
import de.rwth.i9.palm.model.CompletionStatus;
import de.rwth.i9.palm.model.Country;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.model.FileType;
import de.rwth.i9.palm.model.Location;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationAuthor;
import de.rwth.i9.palm.model.PublicationFile;
import de.rwth.i9.palm.model.PublicationSource;
import de.rwth.i9.palm.model.PublicationType;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceMethod;
import de.rwth.i9.palm.model.SourceType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;

@Service
public class EventPublicationCollectionService
{
	private final static Logger LOGGER = LoggerFactory.getLogger( EventPublicationCollectionService.class );

	@Autowired
	private AsynchronousEventCollectionService asynchronousEventCollectionService;

	@Autowired
	private AsynchronousCollectionService asynchronousCollectionService;

	@Autowired
	private PersistenceStrategy persistenceStrategy;
	
	@Autowired
	private PalmAnalytics palmAnalitics;

	@Autowired
	private ApplicationService applicationService;

	/**
	 * Fetch publications list from DBLP venue
	 * 
	 * @param responseMap
	 * @param venue
	 * @param pid
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ParseException
	 * @throws TimeoutException
	 * @throws org.apache.http.ParseException
	 * @throws OAuthProblemException
	 * @throws OAuthSystemException
	 */
	public void collectPublicationListFromVenue( Map<String, Object> responseMap, Event event, String pid ) throws IOException, InterruptedException, ExecutionException, ParseException, TimeoutException, org.apache.http.ParseException, OAuthSystemException, OAuthProblemException
	{
		// process log
		applicationService.putProcessLog( pid, "Collecting publications list from Venue " + event.getEventGroup().getNotation() + event.getYear() + " <br>", "replace" );

		// get author sources
		if ( event.getDblpUrl() == null )
		{
			// set event added
			event.setAdded( true );
			persistenceStrategy.getEventDAO().persist( event );

			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "event doesn't have DBLP URL to crawl" );
		}

		// getSourceMap
		Map<String, Source> sourceMap = applicationService.getAcademicNetworkSources();

		// future list for publication list
		// extract publication from DBLP
		List<Future<Map<String, Object>>> eventDetailMapFutureLists = new ArrayList<Future<Map<String, Object>>>();

		if ( event.getDblpUrl() != null )
			eventDetailMapFutureLists.add( asynchronousEventCollectionService.getEventDetailfromDBLP( event.getDblpUrl(), sourceMap.get( SourceType.DBLP.toString() ) ) );

		// process log
		applicationService.putProcessLog( pid, "Done collecting publications list from Venue<br><br>", "append" );

		// merge the result
		if ( !eventDetailMapFutureLists.isEmpty() )
			this.manageEventAndPublicationInformation( eventDetailMapFutureLists.get( 0 ), event, sourceMap, pid );
	}
	
	private void manageEventAndPublicationInformation( Future<Map<String, Object>> eventDetailMapFuture, Event event, Map<String, Source> sourceMap, String pid ) throws InterruptedException, ExecutionException, ParseException, IOException, TimeoutException
	{
		Map<String, Object> eventDetailMap = eventDetailMapFuture.get();
		// set location for Conference
		if ( eventDetailMap.get( "type" ).toString().equals( "CONFERENCE" ) )
		{
			if ( eventDetailMap.get( "country" ) != null && eventDetailMap.get( "city" ) != null )
			{
				// get country
				Country country = persistenceStrategy.getCountryDAO().getCountryByName( (String) eventDetailMap.get( "country" ) );
				if ( country == null )
				{
					country = new Country();
					country.setName( (String) eventDetailMap.get( "country" ) );
					persistenceStrategy.getCountryDAO().persist( country );
				}
				// set event location
				Location location = persistenceStrategy.getLocationDAO().getByCountryAndCity( country, (String) eventDetailMap.get( "city" ) );

				if ( location == null )
				{
					location = new Location();
					location.setCity( (String) eventDetailMap.get( "city" ) );
					location.setCountry( country );

					persistenceStrategy.getLocationDAO().persist( location );
				}

				event.setLocation( location );
			}
		}
		// add main url into eventGroup
		EventGroup eventGroup = event.getEventGroup();
		if ( eventGroup.getDblpUrl() == null && eventDetailMap.get( "main-url" ) != null )
		{
			eventGroup.setDblpUrl( (String) eventDetailMap.get( "main-url" ) );
		}

		/// handling the event publication

		// list of publications on DBLP event, either from database or
		// completely new
		List<Publication> eventPublications = new ArrayList<Publication>();

		// publication map list from dblp
		@SuppressWarnings( "unchecked" )
		List<Map<String, String>> publicationDetailMapList = (List<Map<String, String>>) eventDetailMap.get( "publications" );
		// process log
		applicationService.putProcessLog( pid, "Merging " + publicationDetailMapList.size() + " publications with existing publication on Database...<br>", "append" );

		// first, construct the publication
		// get it from database or create new if it still doesn't exist
		this.constructPublicationFromDblpEvent( eventPublications, publicationDetailMapList, event );

		// To this point the publication has title and author, any additional
		// sources from Academic Networks,
		// should be implemented here

		// second, extract and combine information from multiple sources
		this.extractPublicationInformationDetailFromSources( event, eventPublications );

		// process log
		applicationService.putProcessLog( pid, "Done merging " + publicationDetailMapList.size() + " publications<br><br>", "append" );

		// check if enrichment option enable
		String enrichmentEnable = applicationService.getConfigValue( "conference", "flow", "htmlpdf" );
		if ( enrichmentEnable != null && enrichmentEnable.equals( "yes" ) )
		{
			// process log
			applicationService.putProcessLog( pid, "Enrich publication information extracting PDF and Html<br>", "append" );

			// enrich the publication information by extracting information
			// from HTML or PDF source
			this.enrichPublicationByExtractOriginalSources( eventPublications, false );

			// process log
			applicationService.putProcessLog( pid, "Done extracting publication information from PDF and Html<br><br>", "append" );
		}
		// at the end save everything
		for ( Publication publication : eventPublications )
		{
			if ( publication.getPublicationTopics() == null || publication.getPublicationTopics().isEmpty() )
				publication.setContentUpdated( true );
			persistenceStrategy.getPublicationDAO().persist( publication );
		}

		persistenceStrategy.getEventDAO().persist( event );
	}

	/**
	 * Construct the publication and publicationSources, with the data gathered
	 * from academic networks
	 * 
	 * @param eventPublications
	 * @param publicationDetailMapList
	 * @param event
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ParseException
	 */
	public void constructPublicationFromDblpEvent( List<Publication> eventPublications, List<Map<String, String>> publicationDetailMapList, Event event ) throws InterruptedException, ExecutionException, ParseException
	{
		// set date format
		DateFormat dateFormat = new SimpleDateFormat( "yyyy/M/d", Locale.ENGLISH );
		// get current timestamp
		java.util.Date date = new java.util.Date();
		Timestamp currentTimestamp = new Timestamp( date.getTime() );

		// set event crawlDate
		event.setCrawlDate( currentTimestamp );
		// set event is added true
		event.setAdded( true );

		for ( Map<String, String> publicationMap : publicationDetailMapList )
		{
			if ( publicationMap == null )
				continue;

			Publication publication = null;

			if ( publicationMap.get( "title" ) == null )
				continue;

			String publicationTitle = publicationMap.get( "title" );

			// check publication with the current selected list.
			if ( !eventPublications.isEmpty() )
			{
				for ( Publication pub : eventPublications )
				{
					if ( palmAnalitics.getTextCompare().getDistanceByLuceneLevenshteinDistance( pub.getTitle().toLowerCase(), publicationTitle.toLowerCase() ) > .9f )
					{
						if ( palmAnalitics.getTextCompare().getNumberCharacterDistanceByLevenshteinDistance( pub.getTitle().toLowerCase(), publicationTitle.toLowerCase() ) <= 5 )
						{
							publication = pub;
							break;
						}
					}
				}
			}

			// check with publication from database
			if ( publication == null )
			{
				// get the publication object
				List<Publication> fromDbPublications = persistenceStrategy.getPublicationDAO().getPublicationViaPhraseSlopQuery( publicationTitle.toLowerCase(), 2 );
				// check publication from database
				if ( !fromDbPublications.isEmpty() )
				{
					for ( Publication pub : fromDbPublications )
					{
						if ( palmAnalitics.getTextCompare().getDistanceByLuceneLevenshteinDistance( pub.getTitle().toLowerCase(), publicationTitle.toLowerCase() ) > .9f )
						{
							if ( palmAnalitics.getTextCompare().getNumberCharacterDistanceByLevenshteinDistance( pub.getTitle().toLowerCase(), publicationTitle.toLowerCase() ) <= 5 )
							{
								publication = pub;

								eventPublications.add( publication );
								if ( publication.getEvent() == null )
								{
									// create publication and event relations
									publication.setEvent( event );
									event.addPublication( publication );
								}
								break;
							}
						}
					}
				}
			}

			// if not exist any where create new publication
			if ( publication == null )
			{
				publication = new Publication();
				publication.setTitle( publicationTitle );
				publication.setAbstractStatus( CompletionStatus.NOT_COMPLETE );
				publication.setKeywordStatus( CompletionStatus.NOT_COMPLETE );
				eventPublications.add( publication );

				// create publication and event relations
				publication.setEvent( event );
				event.addPublication( publication );
			}

			// create publication sources and assign it to publication
			// DBLP publication source
			PublicationSource publicationSource = new PublicationSource();
			publicationSource.setTitle( publicationTitle );
			publicationSource.setSourceUrl( publicationMap.get( "url" ) );
			publicationSource.setSourceType( SourceType.valueOf( publicationMap.get( "source" ).toUpperCase() ) );
			publicationSource.setSourceMethod( SourceMethod.PARSEPAGE );

			if ( publicationMap.get( "type" ) == null )
				continue;
			// information inclusive to Conference
			if ( publicationMap.get( "type" ).equals( "CONFERENCE" ) )
			{
				publicationSource.setPublicationType( "CONFERENCE" );
				if ( publicationMap.get( "pages" ) != null )
					publicationSource.setPages( publicationMap.get( "pages" ) );
				if ( publicationMap.get( "conferenceTheme" ) != null )
					publicationSource.addOrUpdateAdditionalInformation( "conference theme", publicationMap.get( "conferenceTheme" ) );
			}
			else if ( publicationMap.get( "type" ).equals( "INFORMAL" ) )
			{
				publicationSource.setPublicationType( "INFORMAL" );
				if ( publicationMap.get( "pages" ) != null )
					publicationSource.setPages( publicationMap.get( "pages" ) );
				if ( publicationMap.get( "conferenceTheme" ) != null )
					publicationSource.addOrUpdateAdditionalInformation( "conference theme", publicationMap.get( "conferenceTheme" ) );
			}
			// information inclusive to Journal
			else if ( publicationMap.get( "type" ).equals( "JOURNAL" ) )
			{
				publicationSource.setPublicationType( "JOURNAL" );
				// set event date
				if ( event.getDate() == null && publicationMap.get( "datePublished" ) != null )
				{
					String pubSourceDate = publicationMap.get( "datePublished" );
					String publicationDateFormat = "yyyy/M/d";
					if ( pubSourceDate.length() == 4 )
					{
						pubSourceDate += "/1/1";
						publicationDateFormat = "yyyy";
					}
					else if ( pubSourceDate.length() < 8 )
					{
						pubSourceDate += "/1";
						publicationDateFormat = "yyyy/M";
					}
					event.setDate( dateFormat.parse( pubSourceDate ) );
					event.setDateFormat( publicationDateFormat );
				}
				if ( publicationMap.get( "pages" ) != null )
					publicationSource.setPages( publicationMap.get( "pages" ) );

			}
			// information inclusive to Editorship
			else if ( publicationMap.get( "type" ).equals( "EDITORSHIP" ) )
			{
				publicationSource.setPublicationType( "EDITORSHIP" );
				// set event date
				if ( event.getDate() == null && publicationMap.get( "datePublished" ) != null )
				{
					event.setDate( dateFormat.parse( publicationMap.get( "datePublished" ) ) );
					event.setDateFormat( "yyyy/M/d" );
				}
				if ( publicationMap.get( "book-series" ) != null )
					publicationSource.addOrUpdateAdditionalInformation( "series", publicationMap.get( "book-series" ) );
				if ( publicationMap.get( "book-publisher" ) != null )
					publicationSource.addOrUpdateAdditionalInformation( "publisher", publicationMap.get( "book-publisher" ) );
				if ( publicationMap.get( "book-date-published" ) != null )
					publicationSource.addOrUpdateAdditionalInformation( "published", publicationMap.get( "book-date-published" ) );
				if ( publicationMap.get( "book-isbn" ) != null )
					publicationSource.addOrUpdateAdditionalInformation( "ISBN", publicationMap.get( "book-isbn" ) );
			}

			// general information available on publications
			if ( publicationMap.get( "coauthor" ) != null )
				publicationSource.setCoAuthors( publicationMap.get( "coauthor" ) );

			if ( publicationMap.get( "coauthorUrl" ) != null )
				publicationSource.setCoAuthorsUrl( publicationMap.get( "coauthorUrl" ) );

			if ( publicationMap.get( "datePublished" ) != null )
				publicationSource.setDate( publicationMap.get( "datePublished" ) );

			if ( publicationMap.get( "doc" ) != null )
				publicationSource.setMainSource( publicationMap.get( "doc" ) );

			if ( publicationMap.get( "doc_url" ) != null )
				publicationSource.setMainSourceUrl( publicationMap.get( "doc_url" ) );

			// set the relations
			publicationSource.setPublication( publication );
			publication.addPublicationSource( publicationSource );
		}
	}
	
	/**
	 * combine publication information from multiple publication sources
	 * 
	 * @param eventPublications
	 * @param sourceMap
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ParseException
	 */
	public void extractPublicationInformationDetailFromSources( Event event, List<Publication> eventPublications ) throws ParseException
	{
		DateFormat dateFormat = new SimpleDateFormat( "yyyy/M/d", Locale.ENGLISH );
		Set<String> existingMainSourceUrl = new HashSet<String>();

		Set<String> uniqueParticipantSet = new HashSet<String>();

		for ( Publication publication : eventPublications )
		{
			for ( PublicationSource pubSource : publication.getPublicationSources() )
			{
				Date publicationDate = null;
				// Get unique characteristic on each of the source
				if ( pubSource.getSourceType() == SourceType.DBLP )
				{
					// publication date
					if ( pubSource.getDate() != null && !publication.equals( "" ) )
					{
						String pubSourceDate = pubSource.getDate();
						String publicationDateFormat = "yyyy/M/d";
						if ( pubSourceDate.length() == 4 )
						{
							pubSourceDate += "/1/1";
							publicationDateFormat = "yyyy";
						}
						else if ( pubSourceDate.length() < 8 )
						{
							pubSourceDate += "/1";
							publicationDateFormat = "yyyy/M";
						}
						publicationDate = dateFormat.parse( pubSourceDate );
						publication.setPublicationDate( publicationDate );
						publication.setPublicationDateFormat( publicationDateFormat );
					}

					if ( pubSource.getPages() != null )
					{
						String[] pageSplit = pubSource.getPages().split( "-" );
						if ( pageSplit.length == 2 )
						{
							try
							{
								publication.setStartPage( Integer.parseInt( pageSplit[0] ) );
								publication.setEndPage( Integer.parseInt( pageSplit[1] ) );
							}
							catch ( Exception e )
							{
								LOGGER.error( e.getMessage() );
							}
						}
						else
						{
							try
							{
								publication.setStartPage( Integer.parseInt( pubSource.getPages() ) );
							}
							catch ( Exception e )
							{
								LOGGER.error( e.getMessage() );
							}
						}
					}

					if ( pubSource.getAdditionalInformation() != null )
						publication.setAdditionalInformation( pubSource.getAdditionalInformation() );

				}

				/// General Information

				if ( pubSource.getCoAuthors() != null )
				{
					String[] authorsArray = pubSource.getCoAuthors().split( "," );
					// for DBLP where the coauthor have a source link
					String[] authorsUrlArray = null;
					if ( pubSource.getCoAuthorsUrl() != null )
						authorsUrlArray = pubSource.getCoAuthorsUrl().split( " " );

					if ( authorsArray.length > publication.getCoAuthors().size() )
					{
						for ( int i = 0; i < authorsArray.length; i++ )
						{
							String authorString = authorsArray[i].toLowerCase().replace( ".", "" ).trim();

							if ( authorString.equals( "" ) )
								continue;

							String[] splitName = authorString.split( " " );
							String lastName = splitName[splitName.length - 1];
							String firstName = authorString.substring( 0, authorString.length() - lastName.length() ).trim();

							Author author = null;

							// first check from database by full name
							List<Author> coAuthorsDb = persistenceStrategy.getAuthorDAO().getByName( authorString );
							if( !coAuthorsDb.isEmpty() ){
								// for now just get the first author on the list
								author = coAuthorsDb.get( 0 );
								// TODO: check other properties
								// such as institution
							}
							
							// if there is no exact name, check for name
							// ambiguity,
							// by query authors with their last name
							// and then check whether there is abbreviation name on first name
							if( author == null ){
								coAuthorsDb = persistenceStrategy.getAuthorDAO().getByLastName( lastName );
								if( !coAuthorsDb.isEmpty() ){
									for ( Author coAuthorDb : coAuthorsDb )
									{
										if ( coAuthorDb.isAliasNameFromFirstName( firstName ) )
										{
											// TODO: check with institution for
											// higher accuracy
											persistenceStrategy.getAuthorDAO().persist( coAuthorDb );

											author = coAuthorDb;
											break;
										}
									}
								}
							}

							// if author null, create new one
							if( author == null ){
								// create new author
								author = new Author();
								// set for all possible name
								author.setPossibleNames( authorString );
	
								// save new author
								persistenceStrategy.getAuthorDAO().persist( author );
							}

							// make a relation between author and publication
							PublicationAuthor publicationAuthor = new PublicationAuthor();
							publicationAuthor.setPublication( publication );
							publicationAuthor.setAuthor( author );
							publicationAuthor.setPosition( i + 1 );

							publication.addPublicationAuthor( publicationAuthor );

							// assign with authorSource, if exist
							if ( authorsUrlArray != null && authorsArray.length == authorsUrlArray.length )
							{
								AuthorSource authorSource = new AuthorSource();
								authorSource.setName( author.getName() );
								authorSource.setSourceUrl( authorsUrlArray[i] );
								authorSource.setSourceType( pubSource.getSourceType() );
								authorSource.setAuthor( author );

								author.addAuthorSource( authorSource );
								// persist new source
								persistenceStrategy.getAuthorDAO().persist( author );
							}

							// sum number of participant
							uniqueParticipantSet.add( author.getName() );
						}
					}
				}

				// abstract
				// rules :
				// - check abstract if Abstract completion flag "NOT COMPLETE".
				// - get abstract if source contain 250 or more letters.
				// - update abstract if new abstract contains longer text.
				if ( !publication.getAbstractStatus().equals( CompletionStatus.COMPLETE ) && pubSource.getAbstractText() != null && pubSource.getAbstractText().length() > 250 )
				{
					if ( publication.getAbstractText() == null || publication.getAbstractText().length() < pubSource.getAbstractText().length() )
					{
						publication.setAbstractText( pubSource.getAbstractText() );
						publication.setAbstractStatus( CompletionStatus.PARTIALLY_COMPLETE );
					}
				}

				// keyword
				if ( !publication.getKeywordStatus().equals( CompletionStatus.COMPLETE ) && pubSource.getKeyword() != null )
				{
					if ( publication.getKeywordText() == null )
					{
						publication.setKeywordText( pubSource.getKeyword() );
						publication.setKeywordStatus( CompletionStatus.PARTIALLY_COMPLETE );
					}
				}

				// publication type
				if ( publication.getPublicationType() == null || pubSource.getSourceType().equals( SourceType.DBLP ) && pubSource.getPublicationType() != null )
					publication.setPublicationType( PublicationType.valueOf( pubSource.getPublicationType() ) );

				// SOME CODE COMMENTED, SINCE THE SOURCE CURRENTLY ONLY DBLP
				// set publication date
				// if ( publication.getPublicationDate() == null &&
				// publicationDate == null && pubSource.getDate() != null )
				// {
				// publicationDate = dateFormat.parse( pubSource.getDate() +
				// "/1/1" );
				// publication.setPublicationDate( publicationDate );
				// publication.setPublicationDateFormat( "yyyy" );
				// }

				// if ( pubSource.getCitedBy() > 0 && pubSource.getCitedBy() >
				// publication.getCitedBy() )
				// publication.setCitedBy( pubSource.getCitedBy() );
				// END OF COMMENTED CODE

				// original sources (PDF and WebPage)
				if ( pubSource.getMainSourceUrl() != null )
				{

					String[] mainSourceUrls = pubSource.getMainSourceUrl().split( " " );
					String[] mainSources = pubSource.getMainSource().split( "," );
					for ( int i = 0; i < mainSourceUrls.length; i++ )
					{
						// remove unsupported format
						if ( mainSourceUrls[i].toLowerCase().endsWith( ".xml" ) )
							continue;

						// put into temporary list, to prevent duplication
						if ( existingMainSourceUrl.contains( mainSourceUrls[i] ) )
							continue;
						existingMainSourceUrl.add( mainSourceUrls[i] );

						// not exist create new
						PublicationFile pubFile = new PublicationFile();
						pubFile.setSourceType( pubSource.getSourceType() );
						pubFile.setUrl( mainSourceUrls[i] );

						// if source doesn't contain any label
						if ( mainSources[i].equals( "null" ) )
							pubFile.setSource( pubSource.getSourceType().toString().toLowerCase() );
						else
							pubFile.setSource( mainSources[i] );

						// determine type of original source
						if ( mainSourceUrls[i].toLowerCase().endsWith( ".pdf" ) || mainSourceUrls[i].toLowerCase().endsWith( "pdf.php" ) || mainSources[i].toLowerCase().contains( "pdf" ) )
							pubFile.setFileType( FileType.PDF );
						else
							pubFile.setFileType( FileType.HTML );

						// assign relations
						if ( pubFile.getFileType() != null )
						{
							pubFile.setPublication( publication );
							publication.addPublicationFile( pubFile );
						}
					}
				}
			}
		}

		// set number of publications and participants
		event.setNumberPaper( eventPublications.size() );
		event.setNumberParticipant( uniqueParticipantSet.size() );

	}

	/**
	 * Extract publication information from original source either as html or
	 * pdf with asynchronous multi threads
	 * 
	 * @param publication
	 * @param pivotAuthor
	 * @param persistResult
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public void enrichPublicationByExtractOriginalSources( List<Publication> eventPublications, boolean persistResult ) throws IOException, InterruptedException, ExecutionException, TimeoutException
	{
		LOGGER.info( "Start publications enrichment by extract HTML and PDF" );
		List<Future<PublicationSource>> publicationSourceFutureList = new ArrayList<Future<PublicationSource>>();

		// get application setting whether enrichment with HTML or pdf is
		// possible
		boolean isHtmlParsingEnable = false;
		String htmlParsingEnable = applicationService.getConfigValue( "conference", "source", "html" );
		if ( htmlParsingEnable != null && htmlParsingEnable.equals( "yes" ) )
			isHtmlParsingEnable = true;

		boolean isPdfParsingEnable = false;
		String pdfParsingEnable = applicationService.getConfigValue( "conference", "source", "pdf" );
		if ( pdfParsingEnable != null && pdfParsingEnable.equals( "yes" ) )
			isPdfParsingEnable = true;

		String[] prioritizeUrls = applicationService.getConfigValue( "html", "flow", "prioritizeExtract" ).split( "," );

		String[] preventUrls = applicationService.getConfigValue( "html", "flow", "preventExtract" ).split( "," );

		int maximumBatchAllowed = 30;
		try
		{
			maximumBatchAllowed = Integer.parseInt( applicationService.getConfigValue( "html", "flow", "maximumExtract" ) );
		}
		catch ( Exception e )
		{
		}

		// sort publication based on citation
		Collections.sort( eventPublications, new PublicationByNoCitationComparator() );

		int counterProcess = 0;
		for ( Publication publication : eventPublications )
		{
			if ( publication.getAbstractStatus().equals( CompletionStatus.COMPLETE ) )
				continue;

			if ( counterProcess > maximumBatchAllowed )
				break;

			boolean isOneSourceAlreadExtracted = false;

			if ( isHtmlParsingEnable )
			{
				// get publication type webpage
				Set<PublicationFile> htmlPublicationFiles = publication.getPublicationFilesHtml();
				if ( !htmlPublicationFiles.isEmpty() )
				{
					PublicationFile htmlPublicationFileTarget = null;
					for ( PublicationFile htmlPublicationFile : htmlPublicationFiles )
					{
						// remove PublicationFile if consist the prevent URLs
						if ( !htmlPublicationFile.isPublicationFileUrlContainsUrls( preventUrls ) && !htmlPublicationFile.isChecked() )
						{
							LOGGER.info( "Extract WebPage for publication " + publication.getTitle() );
							// find the prioritize pages
							htmlPublicationFileTarget = htmlPublicationFile;
							if ( htmlPublicationFile.isPublicationFileUrlContainsUrls( prioritizeUrls ) )
								break;
						}
					}
					if ( htmlPublicationFileTarget != null )
					{
						// extract information from selected PublicationFiles
						PublicationSource publicationSource = new PublicationSource();
						publicationSource.setSourceUrl( htmlPublicationFileTarget.getUrl() );
						publicationSource.setSourceMethod( SourceMethod.EXTRACTPAGE );
						publicationSource.setSourceType( SourceType.HTML );
						publicationSource.setPublicationType( publication.getPublicationType().toString() );

						htmlPublicationFileTarget.setChecked( true );

						isOneSourceAlreadExtracted = true;

						publicationSourceFutureList.add( asynchronousCollectionService.getPublicationInfromationFromHtml( publication, publicationSource, htmlPublicationFileTarget ) );
						counterProcess++;
					}
				}

				if ( isPdfParsingEnable && !isOneSourceAlreadExtracted )
				{
					// get publication type webpage
					Set<PublicationFile> pdfPublicationFiles = publication.getPublicationFilesPdf();
					if ( !pdfPublicationFiles.isEmpty() )
					{
						PublicationFile pdfPublicationFileTarget = null;
						for ( PublicationFile pdfPublicationFile : pdfPublicationFiles )
						{
							if ( pdfPublicationFile.isChecked() )
								continue;

							PublicationSource publicationSource = new PublicationSource();
							publicationSource.setSourceUrl( pdfPublicationFile.getUrl() );
							publicationSource.setSourceMethod( SourceMethod.EXTRACTPAGE );
							publicationSource.setSourceType( SourceType.PDF );
							publicationSource.setPublicationType( publication.getPublicationType().toString() );

							pdfPublicationFile.setChecked( true );

							publicationSourceFutureList.add( asynchronousCollectionService.getPublicationInformationFromPdf( publication, publicationSource, pdfPublicationFile ) );
							counterProcess++;
							break;
						}
					}
				}
			}
		}


		// waiting until thread finished
		for ( Future<PublicationSource> publicationSourceFuture : publicationSourceFutureList )
		{
			try
			{
				publicationSourceFuture.get( 35, TimeUnit.SECONDS );
			}
			catch ( TimeoutException e )
			{
				LOGGER.error( e.getMessage() );
			}
		}
		LOGGER.info( "Done publications enrichment by extract HTML and PDF" );
	}

}

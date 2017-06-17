package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
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
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.model.FileType;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationAuthor;
import de.rwth.i9.palm.model.PublicationFile;
import de.rwth.i9.palm.model.PublicationSource;
import de.rwth.i9.palm.model.PublicationType;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceMethod;
import de.rwth.i9.palm.model.SourceProperty;
import de.rwth.i9.palm.model.SourceType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;

@Service
public class PublicationCollectionService
{
	private final static Logger log = LoggerFactory.getLogger( PublicationCollectionService.class );

	@Autowired
	private AsynchronousCollectionService asynchronousCollectionService;

	@Autowired
	private PersistenceStrategy persistenceStrategy;
	
	@Autowired
	private PalmAnalytics palmAnalitics;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private MendeleyOauth2Helper mendeleyOauth2Helper;

	/**
	 * Fetch author' publication list from academic networks
	 * 
	 * @param responseMap
	 * @param author
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
	public void collectPublicationListFromNetwork( Map<String, Object> responseMap, Author author, String pid ) throws IOException, InterruptedException, ExecutionException, ParseException, TimeoutException, org.apache.http.ParseException, OAuthSystemException, OAuthProblemException
	{
		// process log
		applicationService.putProcessLog( pid, "Collecting publications list from Academic Networks...<br>", "replace" );

		// get author sources
		Set<AuthorSource> authorSources = author.getAuthorSources();
		if ( authorSources == null )
		{
			// TODO update author sources
			responseMap.put( "result", "error" );
			responseMap.put( "reason", "no author sources found" );
		}

		// getSourceMap
		Map<String, Source> sourceMap = applicationService.getAcademicNetworkSources();

		// future list for publication list
		// extract dataset from academic network concurrently
		// Stopwatch stopwatch = Stopwatch.createStarted();

		// get from configuration
		boolean isUseGoogleScholar = true;
		boolean isUseCiteseerX = true;
		boolean isUseDblp = true;
		boolean isUseMendeley = true;
		boolean isUseMas = true;

		// alter value from configuration
		String useGoogleScholar = applicationService.getConfigValue( "publication", "source", "google scholar" );
		if ( useGoogleScholar != null && useGoogleScholar.equals( "no" ) )
			isUseGoogleScholar = false;
		String useCiteseerX = applicationService.getConfigValue( "publication", "source", "citeserx" );
		if ( useCiteseerX != null && useCiteseerX.equals( "no" ) )
			isUseCiteseerX = false;
		String useDblp = applicationService.getConfigValue( "publication", "source", "dblp" );
		if ( useDblp != null && useDblp.equals( "no" ) )
			isUseDblp = false;
		String useMendeley = applicationService.getConfigValue( "publication", "source", "mendeley" );
		if ( useMendeley != null && useMendeley.equals( "no" ) )
			isUseMendeley = false;
		String useMas = applicationService.getConfigValue( "publication", "source", "microsoft" );
		if ( useMas != null && useMas.equals( "no" ) )
			isUseMas = false;

		List<Future<List<Map<String, String>>>> publicationFutureLists = new ArrayList<Future<List<Map<String, String>>>>();

		for ( AuthorSource authorSource : authorSources )
		{
			if ( authorSource.getSourceType() == SourceType.GOOGLESCHOLAR && sourceMap.get( SourceType.GOOGLESCHOLAR.toString() ).isActive() && isUseGoogleScholar )
				publicationFutureLists.add( asynchronousCollectionService.getListOfPublicationsGoogleScholar( authorSource.getSourceUrl(), sourceMap.get( SourceType.GOOGLESCHOLAR.toString() ) ) );
			else if ( authorSource.getSourceType() == SourceType.CITESEERX && sourceMap.get( SourceType.CITESEERX.toString() ).isActive() && isUseCiteseerX )
				publicationFutureLists.add( asynchronousCollectionService.getListOfPublicationCiteseerX( authorSource.getSourceUrl(), sourceMap.get( SourceType.CITESEERX.toString() ) ) );
			else if ( authorSource.getSourceType() == SourceType.DBLP && sourceMap.get( SourceType.DBLP.toString() ).isActive() && isUseDblp )
				publicationFutureLists.add( asynchronousCollectionService.getListOfPublicationDBLP( authorSource.getSourceUrl(), sourceMap.get( SourceType.DBLP.toString() ) ) );
		}
		if ( sourceMap.get( SourceType.MENDELEY.toString() ).isActive() && isUseMendeley )
		{
			// check for token validity
			mendeleyOauth2Helper.checkAndUpdateMendeleyToken( sourceMap.get( SourceType.MENDELEY.toString() ) );
			publicationFutureLists.add( asynchronousCollectionService.getListOfPublicationDetailMendeley( author, sourceMap.get( SourceType.MENDELEY.toString() ) ) );
		}
		// for MAS since not included on author search
		if ( sourceMap.get( SourceType.MAS.toString() ).isActive() && isUseMas )
			publicationFutureLists.add( asynchronousCollectionService.getListOfPublicationDetailMicrosoftAcademicSearch( author, sourceMap.get( SourceType.MAS.toString() ) ) );

		// wait till everything complete
		for ( Future<List<Map<String, String>>> publicationFuture : publicationFutureLists )
		{
			publicationFuture.get();
		}

		// process log
		applicationService.putProcessLog( pid, "Done collecting publications list from Academic Networks<br><br>", "append" );

		// process log
		applicationService.putProcessLog( pid, "Merging publications...<br>", "append" );

		// merge the result
		this.mergePublicationInformation( publicationFutureLists, author, sourceMap, pid );
	}
	
	/**
	 * Collect publication information and combine it into publication object
	 * 
	 * @param publicationFutureLists
	 * @param author
	 * @param sourceMap
	 * @param pid
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 * @throws ParseException
	 * @throws TimeoutException
	 */
	private void mergePublicationInformation( List<Future<List<Map<String, String>>>> publicationFutureLists, Author author, Map<String, Source> sourceMap, String pid ) throws InterruptedException, ExecutionException, IOException, ParseException, TimeoutException
	{
		if ( publicationFutureLists.size() > 0 )
		{
			// list/set of selected publication, either from database or completely new 
			List<Publication> selectedPublications = new ArrayList<Publication>();

			// first, construct the publication
			// get it from database or create new if still doesn't exist
			this.constructPublicationWithSources( selectedPublications, publicationFutureLists, author, sourceMap );
			
			// process log
			applicationService.putProcessLog( pid, "Done merging " + selectedPublications.size() + " publications<br><br>", "append" );

			boolean isFirstPhaseRemoveInvalidPublicationEnable = true;
			String firstPhaseRemoveInvalidPublicationEnable = applicationService.getConfigValue( "publication", "flow", "remove1" );
			if ( firstPhaseRemoveInvalidPublicationEnable != null && firstPhaseRemoveInvalidPublicationEnable.equals( "no" ) )
				isFirstPhaseRemoveInvalidPublicationEnable = false;

			if ( isFirstPhaseRemoveInvalidPublicationEnable )
			{
				// process log
				applicationService.putProcessLog( pid, "Removing incorrect publications...<br>", "append" );

				// second, remove incorrect publication based on investigation
				this.removeIncorrectPublicationFromPublicationList( selectedPublications );

				// process log
				applicationService.putProcessLog( pid, "Done removing incorrect publications<br><br>", "append" );
			}

			// at the end save everything
			for ( Publication publication : selectedPublications )
			{
				if ( publication.getPublicationTopics() == null || publication.getPublicationTopics().isEmpty() )
					publication.setContentUpdated( true );
				persistenceStrategy.getPublicationDAO().persist( publication );
			}

			// set flag on author to indicate that publication details
			// extraction are needed
			author.setFetchPublicationDetail( true );
			if ( author.getNoPublication() < selectedPublications.size() )
				author.setNoPublication( selectedPublications.size() );

			persistenceStrategy.getAuthorDAO().persist( author );
		}

	}

	/**
	 * Extract publication details
	 * 
	 * @param author
	 * @param pid
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ParseException
	 */
	public void extractPublicationDetails( Author author, String pid ) throws IOException, InterruptedException, ExecutionException, ParseException
	{
		// getSourceMap
		Map<String, Source> sourceMap = applicationService.getAcademicNetworkSources();

		// list publications
		List<Publication> selectedPublications = new ArrayList<Publication>();
		selectedPublications.addAll( author.getPublications() );

		// Author doesn't have any publications
		if ( selectedPublications.isEmpty() )
			return;

		// process log
		applicationService.putProcessLog( pid, "Extracting publications details...<br>", "append" );

		// third, extract and combine information from multiple sources
		this.extractPublicationInformationDetailFromSources( selectedPublications, author, sourceMap );

		// process log
		applicationService.putProcessLog( pid, "Done extracting publications details<br><br>", "append" );

		boolean isSecondPhaseRemoveInvalidPublicationEnable = true;
		String secondPhaseRemoveInvalidPublicationEnable = applicationService.getConfigValue( "publication", "flow", "remove2" );
		if ( secondPhaseRemoveInvalidPublicationEnable != null && secondPhaseRemoveInvalidPublicationEnable.equals( "no" ) )
			isSecondPhaseRemoveInvalidPublicationEnable = false;

		if ( isSecondPhaseRemoveInvalidPublicationEnable )
		{
			// fourth, second checking, after the information has been
			// merged
			this.removeIncorrectPublicationPhase2FromPublicationList( selectedPublications );
		}


		// check if enrichment option enable
		String enrichmentEnable = applicationService.getConfigValue( "publication", "flow", "htmlpdf" );
		if ( enrichmentEnable != null && enrichmentEnable.equals( "yes" ) )
		{

			// process log
			applicationService.putProcessLog( pid, "Extracting publication information from PDF and Html...<br>", "append" );

			// enrich the publication information by extract information
			// from html or pdf source
			try
			{
				this.enrichPublicationByExtractOriginalSources( selectedPublications, author, false );
			}
			catch ( Exception e )
			{
				log.error( "Entrichment error " + e.getMessage() );
			}

			// process log
			applicationService.putProcessLog( pid, "Done extracting publication information from PDF and Html<br><br>", "append" );
		}

		// at the end save everything
		for ( Publication publication : selectedPublications )
		{
			if ( publication.getPublicationTopics() == null || publication.getPublicationTopics().isEmpty() )
				publication.setContentUpdated( true );
			persistenceStrategy.getPublicationDAO().persist( publication );
		}

		// recalculate citation number
		author.reCalculateNumberOfPublicationAndCitation();
		persistenceStrategy.getAuthorDAO().persist( author );
	}

	/**
	 * Remove all publication that considered incorrect.
	 * 
	 * @param selectedPublications
	 */
	private void removeIncorrectPublicationFromPublicationList( List<Publication> selectedPublications )
	{
		// get current year
		int currentYear = Calendar.getInstance().get( Calendar.YEAR );

		// get number of active sources
		int numberOfActiveSources = 0;
		// getSourceMap
		Map<String, Source> sourceMap = applicationService.getAcademicNetworkSources();

		for ( Map.Entry<String, Source> sourceEntry : sourceMap.entrySet() )
		{
			if ( sourceEntry.getValue().isActive() )
				numberOfActiveSources++;
		}

		// filter incorrect publication
		for ( Iterator<Publication> iteratorPublication = selectedPublications.iterator(); iteratorPublication.hasNext(); )
		{
			Publication publication = iteratorPublication.next();

			// The pattern of incorrect publication
			if ( publication.getPublicationSources().size() == 1 && numberOfActiveSources > 1 )
			{
				List<PublicationSource> publicationSource = new ArrayList<>( publication.getPublicationSources() );

				// For google scholar :
				// 1. the publications don't have publication date.
				// 2. No other publications cited the incorrect publications for
				// years (more then 3 years)
				// 3. The title of publication contains "special issue article"
				if ( publicationSource.get( 0 ).getSourceType().equals( SourceType.GOOGLESCHOLAR ) )
				{
					// The pattern of incorrect publication
					// For google scholar :
					// 3. The title of publication contains "special issue
					// article"
					if ( publication.getTitle().toLowerCase().contains( "special issue article" ) )
					{
						iteratorPublication.remove();
						continue;
					}

					// removing condition
					// if publication contain no date
					if ( publicationSource.get( 0 ).getDate() == null )
					{
						iteratorPublication.remove();
						continue;
					}
					else
					{
						// try to get publication year
						int publicationYear = 0;
						String publicationYearString = publicationSource.get( 0 ).getDate();
						if ( publicationYearString.length() > 4 )
							publicationYearString = publicationYearString.substring( 0, 4 );

						try
						{
							publicationYear = Integer.parseInt( publicationYearString );
						}
						catch ( Exception e )
						{
						}

						// keep recent publication, remove old publication
						if ( publicationSource.get( 0 ).getCitedBy() == 0 && currentYear - publicationYear > 2 )
						{
							iteratorPublication.remove();
							continue;
						}
					}
				}
				// The pattern of incorrect publication
				// For MAS no author name
				else if ( publicationSource.get( 0 ).getSourceType().equals( SourceType.MAS ) )
				{
					if ( publicationSource.get( 0 ).getCoAuthors() == null || publicationSource.get( 0 ).getCoAuthors().equals( "" ) )
					{
						iteratorPublication.remove();
						continue;
					}
				}

				// The pattern of incorrect publication
				// For Mendeley is master thesis also recorded
				// indicated by publication doesn't have type, and its contain
				// exact keyword "master-thesis"
				else if ( publicationSource.get( 0 ).getSourceType().equals( SourceType.MENDELEY ) )
				{
					if ( publication.getPublicationType() == null || ( publicationSource.get( 0 ).getAbstractText() != null && publicationSource.get( 0 ).getAbstractText().contains( "master thesis" ) ) )
					{
						iteratorPublication.remove();
						continue;
					}
				}

				// The pattern of incorrect publication
				// For Citeseer is incorrect publication title (only part, duplicated or incorrect at all)
				// sometimes 
//				else if ( publicationSource.get( 0 ).getSourceType().equals( SourceType.CITESEERX ) )
//				{
//					if ( publication.getPublicationType() == null)
//					{
//						iteratorPublication.remove();
//						continue;
//					}
//				}
			}
		}
	}

	/**
	 * Remove duplicated publication (have partial title)
	 * 
	 * @param selectedPublications
	 */
	private void removeIncorrectPublicationPhase2FromPublicationList( List<Publication> selectedPublications )
	{
		for ( Iterator<Publication> iteratorPublication = selectedPublications.iterator(); iteratorPublication.hasNext(); )
		{
			Publication publication = iteratorPublication.next();

			// The pattern of incorrect publication
			// For google scholar :
			// 4. sometimes the publication is duplicated,
			// the title of duplicated one is substring the correct one
			// e.g. "Teaching Collaborative Software Development"
			// actual title "Teaching collaborative software development: A case
			// study."
			// only check for title that shorter than 80 characters
			if ( publication.getTitle().length() < 80 )
			{
				if ( isPublicationDuplicated( publication, selectedPublications ) )
				{
					iteratorPublication.remove();
					continue;
				}
			}

		}
	}

	/**
	 * Check duplicated publication, compare publication to each other
	 * 
	 * @param publication
	 * @param selectedPublications
	 * @param maxLenghtToCompare
	 * @return
	 */
	private boolean isPublicationDuplicated( Publication publication, List<Publication> selectedPublications )
	{
		int lengthOfComparedTitleText = 40; 
		int lengthOfComparedAbstractText = 40;
		for ( Publication eachPublication : selectedPublications )
		{
			if ( eachPublication.getTitle().length() > publication.getTitle().length() )
			{
				// check title
				if( publication.getTitle().length() < lengthOfComparedTitleText )
					lengthOfComparedTitleText = publication.getTitle().length();
				String compareTitle1 = publication.getTitle().substring( 0, lengthOfComparedTitleText );
				String compareTitle2 = eachPublication.getTitle().substring( 0, lengthOfComparedTitleText );
				if ( palmAnalitics.getTextCompare().getDistanceByLuceneLevenshteinDistance( compareTitle1.toLowerCase(), compareTitle2.toLowerCase() ) > .9f ){
					// check abstract
					if ( eachPublication.getAbstractText() == null || eachPublication.getAbstractText().length() < lengthOfComparedAbstractText )
						continue;

					if( publication.getAbstractText() == null || publication.getAbstractText().length() < 100 )
						// just delete publication without abstract or short abstract
						return true;
					else{
						String compareAbstract1 = publication.getAbstractText().substring( 0, lengthOfComparedAbstractText );
						String compareAbstract2 = eachPublication.getAbstractText().substring( 0, lengthOfComparedAbstractText );
						if ( palmAnalitics.getTextCompare().getDistanceByLuceneLevenshteinDistance( compareAbstract1.toLowerCase(), compareAbstract2.toLowerCase() ) > .9f )
							return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Construct the publication and publicationSources, with the data gathered
	 * from academic networks
	 * 
	 * @param selectedPublications
	 * @param publicationFutureLists
	 * @param author
	 * @param sourceMap
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void constructPublicationWithSources( List<Publication> selectedPublications, List<Future<List<Map<String, String>>>> publicationFutureLists, Author author, Map<String, Source> sourceMap ) throws InterruptedException, ExecutionException
	{
		for ( Future<List<Map<String, String>>> publicationFutureList : publicationFutureLists )
		{
			// here, if process has not been completed yet. It will wait,
			// until process complete
			List<Map<String, String>> publicationMapLists = publicationFutureList.get();
			for ( Map<String, String> publicationMap : publicationMapLists )
			{
				Publication publication = null;
				String publicationTitle = publicationMap.get( "title" );

				if ( publicationTitle == null )
					continue;

				// check publication with the current selected list.
				if ( !selectedPublications.isEmpty() )
				{
					for ( Publication pub : selectedPublications )
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
									selectedPublications.add( publication );
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
					selectedPublications.add( publication );
				}

				// create publication sources and assign it to publication
				PublicationSource publicationSource = new PublicationSource();
				publicationSource.setTitle( publicationTitle );
				publicationSource.setSourceUrl( publicationMap.get( "url" ) );

				publicationSource.setSourceType( SourceType.valueOf( publicationMap.get( "source" ).toUpperCase() ) );

				// TODO : this should be automatically signed from sources
				if ( publicationSource.getSourceType().equals( SourceType.GOOGLESCHOLAR ) || publicationSource.getSourceType().equals( SourceType.CITESEERX ) || publicationSource.getSourceType().equals( SourceType.DBLP ) )
					publicationSource.setSourceMethod( SourceMethod.PARSEPAGE );
				else if ( publicationSource.getSourceType().equals( SourceType.MENDELEY ) || publicationSource.getSourceType().equals( SourceType.MAS ) )
					publicationSource.setSourceMethod( SourceMethod.API );

				publicationSource.setPublication( publication );

				if ( publicationMap.get( "citedby" ) != null )
					publicationSource.setCitedBy( Integer.parseInt( publicationMap.get( "citedby" ) ) );

				if ( publicationMap.get( "citedbyUrl" ) != null )
					publicationSource.setCitedByUrl( publicationMap.get( "citedbyUrl" ) );

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

				if ( publicationMap.get( "type" ) != null )
					publicationSource.setPublicationType( publicationMap.get( "type" ) );

				if ( publicationMap.get( "abstract" ) != null && publicationMap.get( "abstract" ).length() > 250 )
					publicationSource.setAbstractText( publicationMap.get( "abstract" ) );

				if ( publicationMap.get( "keyword" ) != null )
					publicationSource.setKeyword( publicationMap.get( "keyword" ) );

				// add venue detail for DBLP
				if ( publicationSource.getSourceType().equals( SourceType.DBLP ) )
				{
					//log.info( "eventUrl : " + publicationMap.get( "eventUrl" ) );
					// venue url
					if ( publicationMap.get( "eventUrl" ) != null )
					{
						publicationSource.setVenueUrl( publicationMap.get( "eventUrl" ) );

						// Set publication type Workshop based on Url
						if ( publicationSource.getVenueUrl().endsWith( "w.html" ) && publicationSource.getPublicationType().equals( "CONFERENCE" ) )
							publicationSource.setPublicationType( "WORKSHOP" );

					}
					if ( publicationMap.get( "eventName" ) != null )
						publicationSource.setVenue( publicationMap.get( "eventName" ) );
					else
					{
						if ( publicationMap.get( "eventShortName" ) != null )
							publicationSource.setVenue( publicationMap.get( "eventShortName" ) );
					}

					if ( publicationMap.get( "eventVolume" ) != null )
						publicationSource.addOrUpdateAdditionalInformation( "volume", publicationMap.get( "eventVolume" ) );
					if ( publicationMap.get( "eventNumber" ) != null )
						publicationSource.addOrUpdateAdditionalInformation( "number", publicationMap.get( "eventNumber" ) );
					if ( publicationMap.get( "page" ) != null )
						publicationSource.setPages( publicationMap.get( "page" ) );

				}

				try
				{
				// assign publication authors
				this.assignAuthors( publication, publicationSource, author, sourceMap, true );
				}
				catch ( Exception e )
				{
					System.out.println( "ERROR in assignAuthors" );
					e.printStackTrace();
				}
				try
				{
				publication.addPublicationSource( publicationSource );
				}
				catch ( Exception e )
				{
					System.out.println( "ERROR in addPublicationSource" );
					e.printStackTrace();
				}

				// check print
//					for ( Entry<String, String> eachPublicationDetail : publicationMap.entrySet() )
//						System.out.println( eachPublicationDetail.getKey() + " : " + eachPublicationDetail.getValue() );
//					System.out.println();

			}
		}

	}

	/**
	 * Assign researchers to publication
	 * 
	 * @param selectedPublications
	 * @param pivotAuthor
	 * @param sourceMap
	 */
	private void assignAuthors( Publication publication, PublicationSource pubSource, Author pivotAuthor, Map<String, Source> sourceMap , boolean isBeforeMerging )
	{
		if( isBeforeMerging ){
			// new publication without any coauthors
			if( publication.getPublicationAuthors() == null || publication.getPublicationAuthors().isEmpty() ){
				// only take coauthors from DBLP
				if ( pubSource.getSourceType().equals( SourceType.DBLP )){
					this.assignEachAuthorFromSource( publication, pubSource, pivotAuthor, sourceMap );
				}
				// anything else, just assign author
				else
				{
					PublicationAuthor publicationAuthor = new PublicationAuthor();
					publicationAuthor.setPublication( publication );
					publicationAuthor.setAuthor( pivotAuthor );
					publicationAuthor.setPosition( 1 );

					publication.addPublicationAuthor( publicationAuthor );
				}
			}
		}
		else{
			boolean checkforCoAuthor = true;
			// sometimes mendeley source is not reliable
			if ( pubSource.getSourceType().equals( SourceType.MENDELEY ) && publication.getPublicationSources().size() > 1 )
				// Author for Mendeley is unreliable
				checkforCoAuthor = false;
			// no need to reinsert author from Mendeley, if the authors
			// are
			// already exist
			if ( pubSource.getSourceType().equals( SourceType.MENDELEY ) && publication.getPublicationAuthors() != null && !publication.getPublicationAuthors().isEmpty() )
				checkforCoAuthor = false;
	
			// author
			if ( pubSource.getCoAuthors() != null && checkforCoAuthor )
			{
				this.assignEachAuthorFromSource( publication, pubSource, pivotAuthor, sourceMap );
			}
		}
	}
	
	/**
	 * Assign each researcher to publication
	 * 
	 * @param selectedPublications
	 * @param pivotAuthor
	 * @param sourceMap
	 */
	private void assignEachAuthorFromSource( Publication publication, PublicationSource pubSource, Author pivotAuthor, Map<String, Source> sourceMap){

		String[] authorsArray = pubSource.getCoAuthors().split( "," );
		// for DBLP where the coauthor have a source link
//		String[] authorsUrlArray = null;
//		if ( pubSource.getCoAuthorsUrl() != null )
//			authorsUrlArray = pubSource.getCoAuthorsUrl().split( " " );

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
				if ( pivotAuthor.getName().toLowerCase().equals( authorString.toLowerCase() ) )
				{
					author = pivotAuthor;

					// create the relation with publication
					PublicationAuthor publicationAuthor = new PublicationAuthor();
					publicationAuthor.setPublication( publication );
					publicationAuthor.setAuthor( author );
					publicationAuthor.setPosition( i + 1 );

					publication.addPublicationAuthor( publicationAuthor );
				}
				else
				{
					// first check from database by full name
					List<Author> coAuthorsDb = persistenceStrategy.getAuthorDAO().getByName( authorString );
					if ( !coAuthorsDb.isEmpty() )
					{
						// TODO: check other properties
						// for now just get the first element
						// later check whether there is already
						// a connection with pivotAuthor
						// if not check institution
						author = coAuthorsDb.get( 0 );
					}

					// if there is no exact name, check for
					// ambiguity,
					// start from lastname
					// and then check whether there is
					// abbreviation name on first name
					if ( author == null )
					{
						coAuthorsDb = persistenceStrategy.getAuthorDAO().getByLastName( lastName );
						if ( !coAuthorsDb.isEmpty() )
						{
							for ( Author coAuthorDb : coAuthorsDb )
							{
								if ( coAuthorDb.isAliasNameFromFirstName( firstName ) )
								{
									// TODO: check with
									// institution for
									// higher accuracy
									persistenceStrategy.getAuthorDAO().persist( coAuthorDb );

									author = coAuthorDb;
									break;
								}
							}
						}
					}

					// if author null, create new one
					if ( author == null )
					{
						// create new author
						author = new Author();
						// set for all possible name
						author.setPossibleNames( authorString );

						// save new author
						persistenceStrategy.getAuthorDAO().persist( author );
					}

					// make a relation between author and
					// publication
					PublicationAuthor publicationAuthor = new PublicationAuthor();
					publicationAuthor.setPublication( publication );
					publicationAuthor.setAuthor( author );
					publicationAuthor.setPosition( i + 1 );

					publication.addPublicationAuthor( publicationAuthor );


				}
			}
		}
	}
	
	/**
	 * combine publication information from multiple publication sources
	 * 
	 * @param selectedPublications
	 * @param sourceMap
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ParseException
	 */
	private void extractPublicationInformationDetailFromSources( List<Publication> selectedPublications, Author pivotAuthor, Map<String, Source> sourceMap ) throws IOException, InterruptedException, ExecutionException, ParseException
	{
		int randomDelayThreshold = 0;
		Random rand = new Random();
		// get randomize delay for google scholar
		SourceProperty delayString = sourceMap.get( SourceType.GOOGLESCHOLAR.toString() ).getSourcePropertyByIdentifiers( "request", "random_delay" );
		if ( delayString != null )
		{
			try
			{
				randomDelayThreshold = Integer.parseInt( delayString.getValue() );
			}
			catch ( Exception e )
			{
			}
		}
		// multithread publication source
		List<Future<PublicationSource>> publicationSourceFutureList = new ArrayList<Future<PublicationSource>>();
		for( Publication publication : selectedPublications){
			for ( PublicationSource publicationSource : publication.getPublicationSources() )
			{
				// handling publication source ( Only for google Scholar and
				// CiteseerX)
				if ( publicationSource.getSourceMethod().equals( SourceMethod.PARSEPAGE ) )
				{
					if ( publicationSource.getSourceType().equals( SourceType.GOOGLESCHOLAR ) )
					{
						if ( randomDelayThreshold > 0 )
						{
							Thread.sleep( rand.nextInt( randomDelayThreshold ) );
						}
						publicationSourceFutureList.add( asynchronousCollectionService.getPublicationInformationFromGoogleScholar( publicationSource, sourceMap.get( SourceType.GOOGLESCHOLAR.toString() ) ) );
					}
					else if ( publicationSource.getSourceType().equals( SourceType.CITESEERX ) )
						publicationSourceFutureList.add( asynchronousCollectionService.getPublicationInformationFromCiteseerX( publicationSource, sourceMap.get( SourceType.CITESEERX.toString() ) ) );
				}
			}
		}

		// make sure everything is done
		for ( Future<PublicationSource> publicationSourceFuture : publicationSourceFutureList )
		{
			publicationSourceFuture.get();
		}

		for ( Publication selectedPublication : selectedPublications )
		{
			// combine from sources to publication
			this.mergingPublicationInformation( selectedPublication, pivotAuthor, sourceMap/* , coAuthors */ );
		}
	}

	/**
	 * 
	 * @param sourceMap 
	 * @param selectedPublications
	 * @throws ParseException
	 */
	private void mergingPublicationInformation( Publication publication,
			Author pivotAuthor, Map<String, Source> sourceMap/* , List<Author> coAuthors */ ) throws ParseException
	{
		DateFormat dateFormat = new SimpleDateFormat( "yyyy/M/d", Locale.ENGLISH );
//		Calendar calendar = Calendar.getInstance();
		Set<String> existingMainSourceUrl = new HashSet<String>();

		for ( PublicationSource pubSource : publication.getPublicationSources() )
		{
			Date publicationDate = null;
			PublicationType publicationType = null;
			// Get unique characteristic on each of the source
			if ( pubSource.getSourceType() == SourceType.GOOGLESCHOLAR )
			{
				// publication date
				if ( pubSource.getDate() != null )
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

				if ( pubSource.getAdditionalInformation() != null )
					publication.setAdditionalInformation( pubSource.getAdditionalInformation() );

			}
			else if ( pubSource.getSourceType() == SourceType.CITESEERX )
			{
				// nothing to do
			}
			else if ( pubSource.getSourceType() == SourceType.MENDELEY )
			{
				if ( !publication.getAbstractStatus().equals( CompletionStatus.COMPLETE ) && pubSource.getAbstractText() != null && pubSource.getAbstractText().length() > 250 )
				{
					publication.setAbstractText( pubSource.getAbstractText() );
					publication.setAbstractStatus( CompletionStatus.COMPLETE );
				}
				if ( !publication.getKeywordStatus().equals( CompletionStatus.COMPLETE ) && pubSource.getKeyword() != null )
				{
					// check for incorrect keyword, for abnormally a lot of
					// keyword
					if ( pubSource.getKeyword().split( "," ).length < 10 )
					{
						publication.setKeywordText( pubSource.getKeyword() );
						publication.setKeywordStatus( CompletionStatus.COMPLETE );
					}
				}

			}
			else if ( pubSource.getSourceType() == SourceType.MAS )
			{
				if ( !publication.getAbstractStatus().equals( CompletionStatus.COMPLETE ) && pubSource.getAbstractText() != null && pubSource.getAbstractText().length() > 250 )
				{ // sometimes MAS abstract is also incorrect
					if ( publication.getAbstractText() != null && publication.getAbstractText().length() < pubSource.getAbstractText().length() )
					{
						publication.setAbstractText( pubSource.getAbstractText() );
						publication.setAbstractStatus( CompletionStatus.PARTIALLY_COMPLETE );
					}
				}
				if ( !publication.getKeywordStatus().equals( CompletionStatus.COMPLETE ) && pubSource.getKeyword() != null )
				{
					publication.setKeywordText( pubSource.getKeyword() );
					publication.setKeywordStatus( CompletionStatus.PARTIALLY_COMPLETE );
				}
			}
			// for general information
			
			// assign publication authors
			this.assignAuthors( publication, pubSource, pivotAuthor, sourceMap, false );

			// abstract ( searching the longest)
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
				if ( publication.getKeywordText() == null && pubSource.getKeyword().split( "," ).length < 10 )
				{
					publication.setKeywordText( pubSource.getKeyword() );
					publication.setKeywordStatus( CompletionStatus.PARTIALLY_COMPLETE );
				}
			}

			// set publication date
			if ( publication.getPublicationDate() == null && publicationDate == null && pubSource.getDate() != null && !pubSource.getDate().equals( "" ) )
			{
				publicationDate = dateFormat.parse( pubSource.getDate() + "/1/1" );
				publication.setPublicationDate( publicationDate );
				publication.setPublicationDateFormat( "yyyy" );
			}

			if ( pubSource.getCitedBy() > 0 && pubSource.getCitedBy() > publication.getCitedBy() )
			{
				publication.setCitedBy( pubSource.getCitedBy() );
				if ( pubSource.getCitedByUrl() != null )
					publication.setCitedByUrl( pubSource.getCitedByUrl() );
			}

			// set event for conference and journal
			if ( pubSource.getPublicationType() != null )
			{
				publicationType = PublicationType.valueOf( pubSource.getPublicationType() );
				if ( publication.getPublicationTypeStatus() == null || !publication.getPublicationTypeStatus().equals( CompletionStatus.COMPLETE ) )
				{
					if ( pubSource.getSourceType().equals( SourceType.DBLP ) )
					{
						publication.setPublicationType( publicationType );
						publication.setPublicationTypeStatus( CompletionStatus.COMPLETE );
					}
					else
					{
						publication.setPublicationType( publicationType );
						publication.setPublicationTypeStatus( CompletionStatus.PARTIALLY_COMPLETE );
					}
				}


				if ( publicationType.equals( PublicationType.CONFERENCE ) || publicationType.equals( PublicationType.WORKSHOP ) || publicationType.equals( PublicationType.JOURNAL ) || publicationType.equals( PublicationType.INFORMAL ) )
				{
					if ( pubSource.getSourceType().equals( SourceType.DBLP ) && pubSource.getVenue() != null && pubSource.getVenueUrl() != null )
					{
						String eventName = pubSource.getVenue();
						if ( publicationType.equals( PublicationType.WORKSHOP ) )
						{
							int workshopStringIndex = eventName.toLowerCase().indexOf( " w" );
							if ( workshopStringIndex > 0 )
								eventName = eventName.substring( 0, workshopStringIndex ).trim();
						}

						EventGroup eventGroup = persistenceStrategy.getEventGroupDAO().getEventGroupByEventNameOrNotation( eventName );
						if ( eventGroup == null )
						{
							// create event group
							eventGroup = new EventGroup();
							eventGroup.setName( eventName );
							eventGroup.setNotation( eventName );
							eventGroup.setPublicationType( publicationType );
							// create event

							// save event group
							persistenceStrategy.getEventGroupDAO().persist( eventGroup );

							Event event = new Event();
							// event.setDate( publicationDate );
							event.setYear( pubSource.getDate() );
							event.setDblpUrl( pubSource.getVenueUrl() );
							event.setEventGroup( eventGroup );

							eventGroup.addEvent( event );
							publication.setEvent( event );

							// persistenceStrategy.getEventDAO().persist( event
							// );
						}
						else
						{
							Event event = null;
							List<Event> events = eventGroup.getEvents();
							String eventYear = pubSource.getDate();
							for ( Event eachEvent : events )
							{
								if ( eachEvent.getYear().equals( eventYear ) )
								{
									event = eachEvent;
									break;
								}
							}

							// check whether event already exist, if not create
							// new one
							if ( event == null )
							{
								event = new Event();
								// event.setDate( publicationDate );
								event.setYear( pubSource.getDate() );
								event.setDblpUrl( pubSource.getVenueUrl() );
								event.setEventGroup( eventGroup );

								eventGroup.addEvent( event );
								publication.setEvent( event );

								// persistenceStrategy.getEventDAO().persist(
								// event );
							}
							else
							{
								// event and eventgroup already exist
								// set publication with this event
								publication.setEvent( event );
							}

						}
					}
					else if ( !pubSource.getSourceType().equals( SourceType.DBLP ) && pubSource.getVenue() != null && publication.getEvent() == null )
					{
						publication.addOrUpdateAdditionalInformation( "venue", pubSource.getVenue() );
					}
				}

			}

			// set publication pages
			if ( publication.getStartPage() == 0 && pubSource.getPages() != null )
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
					}
				}
			}

			// original sources (PDF and WebPage)
			if ( pubSource.getMainSourceUrl() != null )
			{

				String[] mainSourceUrls = pubSource.getMainSourceUrl().split( " " );
				String[] mainSources = pubSource.getMainSource().split( "," );
				for ( int i = 0; i < mainSourceUrls.length; i++ )
				{
					if ( existingMainSourceUrl.contains( mainSourceUrls[i] ) )
						continue;

					existingMainSourceUrl.add( mainSourceUrls[i] );

					// not exist create new
					PublicationFile pubFile = new PublicationFile();
					pubFile.setSourceType( pubSource.getSourceType() );
					pubFile.setUrl( mainSourceUrls[i] );
					if ( mainSources[i].equals( "null" ) )
						pubFile.setSource( pubSource.getSourceType().toString().toLowerCase() );
					else
						pubFile.setSource( mainSources[i] );

					if ( mainSourceUrls[i].toLowerCase().endsWith( ".pdf" ) || mainSourceUrls[i].toLowerCase().endsWith( "pdf.php" ) || 
							mainSources[i].toLowerCase().contains( "pdf" ) || mainSourceUrls[i].contains( "download" ))
						pubFile.setFileType( FileType.PDF );
					else if ( mainSourceUrls[i].toLowerCase().endsWith( ".xml" ) )
					{
						// nothing to do
					}
					else
						pubFile.setFileType( FileType.HTML );
					pubFile.setPublication( publication );

					if ( pubFile.getFileType() != null )
						publication.addPublicationFile( pubFile );

				}
			}

		}

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
	public void enrichPublicationByExtractOriginalSources( List<Publication> selectedPublications, Author pivotAuthor, boolean persistResult ) throws IOException, InterruptedException, ExecutionException, TimeoutException
	{
		log.info( "Start publications enrichment for Auhtor " + pivotAuthor.getName() );
		List<Future<PublicationSource>> publicationSourceFutureList = new ArrayList<Future<PublicationSource>>();

		// get application setting whether enrichment with HTML or pdf is
		// possible
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
		Collections.sort( selectedPublications, new PublicationByNoCitationComparator() );

		int counterProcess = 0;
		for ( Publication publication : selectedPublications )
		{
			if ( publication.getAbstractStatus().equals( CompletionStatus.COMPLETE ) )
				continue;

			if ( counterProcess > maximumBatchAllowed )
				break;

			boolean isOneSourceAlreadExtracted = false;

			if ( isHtmlParsingEnable ){
				// get publication type webpage
				Set<PublicationFile> htmlPublicationFiles = publication.getPublicationFilesHtml();
				if ( !htmlPublicationFiles.isEmpty() && !publication.isPublicationContainSourceFrom( SourceType.HTML ) )
				{
					PublicationFile htmlPublicationFileTarget = null;
					for ( PublicationFile htmlPublicationFile : htmlPublicationFiles )
					{
						// remove PublicationFile if consist the prevent URLs
						if ( !htmlPublicationFile.isPublicationFileUrlContainsUrls( preventUrls ) )
						{

							// find the prioritize pages
							htmlPublicationFileTarget = htmlPublicationFile;
							if ( htmlPublicationFile.isPublicationFileUrlContainsUrls( prioritizeUrls ) )
								break;
						}
					}
					if ( htmlPublicationFileTarget != null )
					{
						log.info( "Extract WebPage for publication " + publication.getTitle() );
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

				if ( isPdfParsingEnable && !isOneSourceAlreadExtracted && !publication.isPublicationContainSourceFrom( SourceType.PDF ) )
				{
					// get publication type webpage
					Set<PublicationFile> pdfPublicationFiles = publication.getPublicationFilesPdf();
					if ( !pdfPublicationFiles.isEmpty() )
					{
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
				log.error( e.getMessage() );
			}
		}
		log.info( "Done publications enrichment for Auhtor " + pivotAuthor.getName() );
	}

}

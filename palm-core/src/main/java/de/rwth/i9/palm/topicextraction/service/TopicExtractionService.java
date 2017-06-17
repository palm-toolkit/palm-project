package de.rwth.i9.palm.topicextraction.service;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.rwth.i9.palm.helper.comparator.PublicationByNoCitationComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.ExtractionService;
import de.rwth.i9.palm.model.ExtractionServiceType;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationTopic;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Service
public class TopicExtractionService
{
	private final static Logger log = LoggerFactory.getLogger( TopicExtractionService.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private AsynchronousTopicExtractionService asynchronousTopicExtractionService;

	/**
	 * Extract topics from publications
	 * 
	 * @param author
	 * @throws InterruptedException
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 * @throws ExecutionException
	 */
	public void extractTopicFromPublicationByAuthor( Author author ) throws InterruptedException, UnsupportedEncodingException, URISyntaxException, ExecutionException
	{
		// container for list of publication which have abstract
		Set<Publication> publications = new HashSet<Publication>();

		List<Future<PublicationTopic>> publicationTopicFutureList = new ArrayList<Future<PublicationTopic>>();
		List<ExtractionService> extractionServices = persistenceStrategy.getExtractionServiceDAO().getAllActiveExtractionService();

		// get current date
		Calendar calendar = Calendar.getInstance();
		
		int batchSleep = 0;
		int batchCounter = 0;
		int maxBatchCounter = 100;
		
		// prepare publications
		List<Publication> authorPublications = new ArrayList<Publication>();
		authorPublications.addAll( author.getPublications() );
		// sort based on citation number
		Collections.sort( authorPublications, new PublicationByNoCitationComparator() );

		// loop through available extraction services
		int extractionServiceNumber = 0;
		for ( ExtractionService extractionService : extractionServices )
		{
			if ( !extractionService.isActive() )
				continue;
			
			try
			{
				if( extractionService.getExtractionServiceType().equals( ExtractionServiceType.OPENCALAIS )){
					batchSleep = Integer.parseInt( extractionService.getExtractionServicePropertyByIdentifiers( "flow", "delayBetweenRequest" ).getValue() );
					maxBatchCounter = Integer.parseInt( extractionService.getExtractionServicePropertyByIdentifiers( "flow", "bacthExtractMaxPublications" ).getValue() );
				}
			}
			catch ( Exception e )
			{
				log.debug( e.getMessage() );
			}

//			// this code is implementation is incorrect, since not all publication will be extracted
//			countExtractionServiceUsages( extractionService, author.getPublications().size(), calendar );
//			// if beyond limitation query perday
//			if ( extractionService.getCountQueryThisDay() > extractionService.getMaxQueryPerDay() )
//				continue;

			// publications on specific user
			for ( Publication publication : authorPublications )
			{
				if ( publication.getAbstractText() == null )
					continue;

				if ( publication.isContentUpdated() )
				{
					// add to publications hashmap for persisting
					publications.add( publication );
					// add to publications hashmap for persisting
					// // remove old extracted source
					if ( publication.getPublicationTopics() != null && extractionServiceNumber == 0 )
						publication.getPublicationTopics().clear();

					// create new publication topic
					PublicationTopic publicationTopic = new PublicationTopic();
					publicationTopic.setExtractionServiceType( extractionService.getExtractionServiceType() );
					publicationTopic.setExtractionDate( calendar.getTime() );
					publicationTopic.setPublication( publication );

					// extract topics with available services
					doAsyncronousTopicExtraction( publication, extractionService, publicationTopic, publicationTopicFutureList, batchSleep, batchCounter, maxBatchCounter );
				}
				else
				{
					// if something fails on last run
					PublicationTopic publicationTopic = null;
					for ( PublicationTopic publicationTopicEach : publication.getPublicationTopics() )
					{
						if ( publicationTopicEach.getExtractionServiceType().equals( extractionService.getExtractionServiceType() ) )
						{
							publicationTopic = publicationTopicEach;
						}
					}

					if ( publicationTopic == null )
					{
						publicationTopic = new PublicationTopic();
						publicationTopic.setExtractionServiceType( extractionService.getExtractionServiceType() );
						publicationTopic.setExtractionDate( calendar.getTime() );
						publicationTopic.setPublication( publication );
					}
					if ( publicationTopic.getTermValues() == null || publicationTopic.getTermValues().isEmpty() )
					{
						// add to publications hashmap for persisting
						publications.add( publication );

						// extract topics with available services
						doAsyncronousTopicExtraction( publication, extractionService, publicationTopic, publicationTopicFutureList, batchSleep, batchCounter, maxBatchCounter );
					}

				}

			}
			extractionServiceNumber++;

		}

		// Wait until they are all done
		for ( Future<PublicationTopic> futureList : publicationTopicFutureList )
		{
			futureList.get();
		}

		// change flag to reupdate interest calculation on author if
		// publicationTopicFutureList contain something
		if ( publicationTopicFutureList.size() > 0 )
		{
			author.setUpdateInterest( true );
			persistenceStrategy.getAuthorDAO().persist( author );
		}

		// save publications, set flag, prevent re-extract publication topic
		if ( !publications.isEmpty() )
		{
			log.info( "publication size " + publications.size() );
			for ( Publication publication : publications )
			{
				publication.setContentUpdated( false );
				persistenceStrategy.getPublicationDAO().persist( publication );
			}
		}
	}
	
	/**
	 * Extract topics from specific publications
	 * 
	 * @param publication
	 * @throws InterruptedException
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 * @throws ExecutionException
	 */
	public void extractTopicFromSpecificPublication( Publication publication ) throws InterruptedException, UnsupportedEncodingException, URISyntaxException, ExecutionException
	{
		List<Future<PublicationTopic>> publicationTopicFutureList = new ArrayList<Future<PublicationTopic>>();
		List<ExtractionService> extractionServices = persistenceStrategy.getExtractionServiceDAO().getAllActiveExtractionService();

		// get current date
		Calendar calendar = Calendar.getInstance();

		// remove old extracted source
		if ( publication.isContentUpdated() )
			if ( publication.getPublicationTopics() != null )
				publication.getPublicationTopics().clear();

		int batchSleep = 0;
		int batchCounter = 0;
		int maxBatchCounter = 100;

		// loop through available extraction services
		for ( ExtractionService extractionService : extractionServices )
		{
			if ( !extractionService.isActive() )
				continue;

			try
			{
				if ( extractionService.getExtractionServiceType().equals( ExtractionServiceType.OPENCALAIS ) )
				{
					batchSleep = Integer.parseInt( extractionService.getExtractionServicePropertyByIdentifiers( "flow", "delayBetweenRequest" ).getValue() );
					maxBatchCounter = Integer.parseInt( extractionService.getExtractionServicePropertyByIdentifiers( "flow", "bacthExtractMaxPublications" ).getValue() );
				}
			}
			catch ( Exception e )
			{
				log.debug( e.getMessage() );
			}

			// // this code is implementation is incorrect, since not all
			// publication will be extracted
			// countExtractionServiceUsages( extractionService,
			// author.getPublications().size(), calendar );
			// // if beyond limitation query perday
			// if ( extractionService.getCountQueryThisDay() >
			// extractionService.getMaxQueryPerDay() )
			// continue;

			// publications on specific user
			if ( publication.getAbstractText() == null )
				continue;

			if ( publication.isContentUpdated() )
			{

				// create new publication topic
				PublicationTopic publicationTopic = new PublicationTopic();
				publicationTopic.setExtractionServiceType( extractionService.getExtractionServiceType() );
				publicationTopic.setExtractionDate( calendar.getTime() );
				publicationTopic.setPublication( publication );

				// extract topics with available services
				doAsyncronousTopicExtraction( publication, extractionService, publicationTopic, publicationTopicFutureList, batchSleep, batchCounter, maxBatchCounter );
			}
			else
			{
				// if something fails on last run
				PublicationTopic publicationTopic = null;
				for ( PublicationTopic publicationTopicEach : publication.getPublicationTopics() )
				{
					if ( publicationTopicEach.getExtractionServiceType().equals( extractionService.getExtractionServiceType() ) )
					{
						publicationTopic = publicationTopicEach;
					}
				}

				if ( publicationTopic == null )
				{
					publicationTopic = new PublicationTopic();
					publicationTopic.setExtractionServiceType( extractionService.getExtractionServiceType() );
					publicationTopic.setExtractionDate( calendar.getTime() );
					publicationTopic.setPublication( publication );
				}
				if ( publicationTopic.getTermValues() == null || publicationTopic.getTermValues().isEmpty() )
				{
					// extract topics with available services
					doAsyncronousTopicExtraction( publication, extractionService, publicationTopic, publicationTopicFutureList, batchSleep, batchCounter, maxBatchCounter );
				}

			}

		}

		// Wait until they are all done
		for ( Future<PublicationTopic> futureList : publicationTopicFutureList )
		{
			futureList.get();
		}

		// save publication, set flag, prevent re-extract publication topic
		publication.setContentUpdated( false );
		persistenceStrategy.getPublicationDAO().persist( publication );

	}

	/**
	 * Extract topics from circle
	 * 
	 * @param circle
	 * @throws InterruptedException
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 * @throws ExecutionException
	 */
	public void extractTopicFromPublicationByCircle( Circle circle ) throws InterruptedException, UnsupportedEncodingException, URISyntaxException, ExecutionException
	{
		// container for list of publication which have abstract
		Set<Publication> publications = new HashSet<Publication>();

		List<Future<PublicationTopic>> publicationTopicFutureList = new ArrayList<Future<PublicationTopic>>();
		List<ExtractionService> extractionServices = persistenceStrategy.getExtractionServiceDAO().getAllActiveExtractionService();
		
		// get current date
		Calendar calendar = Calendar.getInstance();


		int batchSleep = 0;
		int batchCounter = 0;
		int maxBatchCounter = 100;

		// loop through available extraction services
		int extractionServiceNumber = 0;
		for ( ExtractionService extractionService : extractionServices )
		{
			if ( !extractionService.isActive() )
				continue;

			try
			{
				if ( extractionService.getExtractionServiceType().equals( ExtractionServiceType.OPENCALAIS ) )
				{
					batchSleep = Integer.parseInt( extractionService.getExtractionServicePropertyByIdentifiers( "flow", "delayBetweenRequest" ).getValue() );
					maxBatchCounter = Integer.parseInt( extractionService.getExtractionServicePropertyByIdentifiers( "flow", "bacthExtractMaxPublications" ).getValue() );
				}
			}
			catch ( Exception e )
			{
				log.debug( e.getMessage() );
			}

//			// this code is implementation is incorrect, since not all publication will be extracted
//			countExtractionServiceUsages( extractionService, circle.getPublications().size(), calendar );
//			// if beyond limitation query perday
//			if ( extractionService.getCountQueryThisDay() > extractionService.getMaxQueryPerDay() )
//				continue;
			
			// publications on specific user
			for ( Publication publication : circle.getPublications() )
			{
				if ( publication.getAbstractText() == null )
					continue;

				if ( publication.isContentUpdated() )
				{
					// add to publications hashmap for persisting
					publications.add( publication );

					if ( publication.getPublicationTopics() != null && extractionServiceNumber == 0 )
						publication.getPublicationTopics().clear();

					// create new publication topic
					PublicationTopic publicationTopic = new PublicationTopic();
					publicationTopic.setExtractionServiceType( extractionService.getExtractionServiceType() );
					publicationTopic.setExtractionDate( calendar.getTime() );
					publicationTopic.setPublication( publication );

					// extract topics with available services
					doAsyncronousTopicExtraction( publication, extractionService, publicationTopic, publicationTopicFutureList, batchSleep, batchCounter, maxBatchCounter );
				}
				else
				{
					// if something fails on last run
					PublicationTopic publicationTopic = null;
					for ( PublicationTopic publicationTopicEach : publication.getPublicationTopics() )
					{
						if ( publicationTopicEach.getExtractionServiceType().equals( extractionService.getExtractionServiceType() ) )
						{
							publicationTopic = publicationTopicEach;
						}
					}

					if ( publicationTopic == null )
					{
						publicationTopic = new PublicationTopic();
						publicationTopic.setExtractionServiceType( extractionService.getExtractionServiceType() );
						publicationTopic.setExtractionDate( calendar.getTime() );
						publicationTopic.setPublication( publication );
					}
					if ( publicationTopic.getTermValues() == null || publicationTopic.getTermValues().isEmpty() )
					{
						// add to publications hashmap for persisting
						publications.add( publication );

						// extract topics with available services
						doAsyncronousTopicExtraction( publication, extractionService, publicationTopic, publicationTopicFutureList, batchSleep, batchCounter, maxBatchCounter );
					}

				}
				
			}
			extractionServiceNumber++;
		}
		// check whether thread worker is done
//		// Wait until they are all done
//		boolean processIsDone = true;
//		do
//		{
//			processIsDone = true;
//			for ( Future<PublicationTopic> futureList : publicationTopicFutureList )
//			{
//				if ( !futureList.isDone() )
//				{
//					processIsDone = false;
//					break;
//				}
//			}
//			// 10-millisecond pause between each check
//			Thread.sleep( 10 );
//		} while ( !processIsDone );
		
		// Wait until they are all done
		for ( Future<PublicationTopic> futureList : publicationTopicFutureList )
		{
			futureList.get();
		}

		// change flag to reupdate interest calculation on author if
		// publicationTopicFutureList contain something
		if ( publicationTopicFutureList.size() > 0 )
		{
			circle.setUpdateInterest( true );
			persistenceStrategy.getCircleDAO().persist( circle );
		}

		// save publications, set flag, prevent re-extract publication topic
		if ( !publications.isEmpty() )
		{
			log.info( "publication size " + publications.size() );
			for ( Publication publication : publications )
			{
				publication.setContentUpdated( false );
				persistenceStrategy.getPublicationDAO().persist( publication );
			}
		}
	}
	
	/**
	 * Extract topics from event
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 * @throws ExecutionException
	 */
	public void extractTopicFromPublicationByEvent( Event event ) throws InterruptedException, UnsupportedEncodingException, URISyntaxException, ExecutionException
	{
		// container for list of publication which have abstract
		Set<Publication> publications = new HashSet<Publication>();

		List<Future<PublicationTopic>> publicationTopicFutureList = new ArrayList<Future<PublicationTopic>>();
		List<ExtractionService> extractionServices = persistenceStrategy.getExtractionServiceDAO().getAllActiveExtractionService();
		
		// get current date
		Calendar calendar = Calendar.getInstance();


		int batchSleep = 0;
		int batchCounter = 0;
		int maxBatchCounter = 100;

		// loop through available extraction services
		int extractionServiceNumber = 0;
		for ( ExtractionService extractionService : extractionServices )
		{
			if ( !extractionService.isActive() )
				continue;

			try
			{
				if ( extractionService.getExtractionServiceType().equals( ExtractionServiceType.OPENCALAIS ) )
				{
					batchSleep = Integer.parseInt( extractionService.getExtractionServicePropertyByIdentifiers( "flow", "delayBetweenRequest" ).getValue() );
					maxBatchCounter = Integer.parseInt( extractionService.getExtractionServicePropertyByIdentifiers( "flow", "bacthExtractMaxPublications" ).getValue() );
				}
			}
			catch ( Exception e )
			{
				log.debug( e.getMessage() );
			}

//			// this code is implementation is incorrect, since not all publication will be extracted
//			countExtractionServiceUsages( extractionService, event.getPublications().size(), calendar );
//			// if beyond limitation query perday
//			if ( extractionService.getCountQueryThisDay() > extractionService.getMaxQueryPerDay() )
//				continue;
			
			// publications on specific user
			for ( Publication publication : event.getPublications() )
			{
				if ( publication.getAbstractText() == null )
					continue;

				if ( publication.isContentUpdated() )
				{
					// add to publications hashmap for persisting
					publications.add( publication );

					if ( publication.getPublicationTopics() != null && extractionServiceNumber == 0 )
						publication.getPublicationTopics().clear();

					// create new publication topic
					PublicationTopic publicationTopic = new PublicationTopic();
					publicationTopic.setExtractionServiceType( extractionService.getExtractionServiceType() );
					publicationTopic.setExtractionDate( calendar.getTime() );
					publicationTopic.setPublication( publication );

					// extract topics with available services
					doAsyncronousTopicExtraction( publication, extractionService, publicationTopic, publicationTopicFutureList, batchSleep, batchCounter, maxBatchCounter );
				}
				else
				{
					// if something fails on last run
					PublicationTopic publicationTopic = null;
					for ( PublicationTopic publicationTopicEach : publication.getPublicationTopics() )
					{
						if ( publicationTopicEach.getExtractionServiceType().equals( extractionService.getExtractionServiceType() ) )
						{
							publicationTopic = publicationTopicEach;
						}
					}

					if ( publicationTopic == null )
					{
						publicationTopic = new PublicationTopic();
						publicationTopic.setExtractionServiceType( extractionService.getExtractionServiceType() );
						publicationTopic.setExtractionDate( calendar.getTime() );
						publicationTopic.setPublication( publication );
					}
					if ( publicationTopic.getTermValues() == null || publicationTopic.getTermValues().isEmpty() )
					{
						// add to publications hashmap for persisting
						publications.add( publication );

						// extract topics with available services
						doAsyncronousTopicExtraction( publication, extractionService, publicationTopic, publicationTopicFutureList, batchSleep, batchCounter, maxBatchCounter );
					}

				}
				
			}
			extractionServiceNumber++;
		}
		// check whether thread worker is done
//		// Wait until they are all done
//		boolean processIsDone = true;
//		do
//		{
//			processIsDone = true;
//			for ( Future<PublicationTopic> futureList : publicationTopicFutureList )
//			{
//				if ( !futureList.isDone() )
//				{
//					processIsDone = false;
//					break;
//				}
//			}
//			// 10-millisecond pause between each check
//			Thread.sleep( 10 );
//		} while ( !processIsDone );
		
		// Wait until they are all done
		for ( Future<PublicationTopic> futureList : publicationTopicFutureList )
		{
			futureList.get();
		}

		// change flag to reupdate interest calculation on author if
		// publicationTopicFutureList contain something
		if ( publicationTopicFutureList.size() > 0 )
		{
			event.setUpdateInterest( true );
			persistenceStrategy.getEventDAO().persist( event );
		}

		// save publications, set flag, prevent re-extract publication topic
		if ( !publications.isEmpty() )
		{
			log.info( "publication size " + publications.size() );
			for ( Publication publication : publications )
			{
				publication.setContentUpdated( false );
				persistenceStrategy.getPublicationDAO().persist( publication );
			}
		}
	}

	/**
	 * Count total number of topic extraction requests on specific service
	 * perday
	 * 
	 * @param extractionService
	 * @param requestsCount
	 * @param calendar
	 */
	private void countExtractionServiceUsages( ExtractionService extractionService, int requestsCount, Calendar calendar )
	{

		// check extraction service limitation (number of queries per day)
		// TODO this is still not correct
		if ( extractionService.getLastQueryDate() != null )
		{
			if ( extractionService.getLastQueryDate().equals( calendar.getTime() ) )
			{
				extractionService.setCountQueryThisDay( extractionService.getCountQueryThisDay() + requestsCount );
				persistenceStrategy.getExtractionServiceDAO().persist( extractionService );
			}
			else
			{
				extractionService.setLastQueryDate( calendar.getTime() );
				extractionService.setCountQueryThisDay( requestsCount );
				persistenceStrategy.getExtractionServiceDAO().persist( extractionService );
			}
		}
		else
		{
			extractionService.setLastQueryDate( calendar.getTime() );
			extractionService.setCountQueryThisDay( requestsCount );
			persistenceStrategy.getExtractionServiceDAO().persist( extractionService );
		}
	}

	/**
	 * Extract publication with available
	 * 
	 * @param publication
	 * @param extractionService
	 * @param publicationTopic
	 * @param publicationTopicFutureList
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 */
	private void doAsyncronousTopicExtraction( Publication publication, ExtractionService extractionService, PublicationTopic publicationTopic, List<Future<PublicationTopic>> publicationTopicFutureList, int batchSleep, int batchCounter, int maxBatchCounter) throws UnsupportedEncodingException, URISyntaxException
	{
		if ( extractionService.getExtractionServiceType().equals( ExtractionServiceType.ALCHEMY ) && !publication.isPublicationTopicEverExtractedWith( ExtractionServiceType.ALCHEMY ) )
		{
			publication.addPublicationTopic( publicationTopic );
			publicationTopicFutureList.add( asynchronousTopicExtractionService.getTopicsByAlchemyApi( publication, publicationTopic, extractionService ) );
		}
		else if ( extractionService.getExtractionServiceType().equals( ExtractionServiceType.YAHOOCONTENTANALYSIS ) && !publication.isPublicationTopicEverExtractedWith( ExtractionServiceType.YAHOOCONTENTANALYSIS ) )
		{
			publication.addPublicationTopic( publicationTopic );
			publicationTopicFutureList.add( asynchronousTopicExtractionService.getTopicsByYahooContentAnalysis( publication, publicationTopic, extractionService ) );
		}
		else if ( extractionService.getExtractionServiceType().equals( ExtractionServiceType.FIVEFILTERS ) && !publication.isPublicationTopicEverExtractedWith( ExtractionServiceType.FIVEFILTERS ) )
		{
			publication.addPublicationTopic( publicationTopic );
			publicationTopicFutureList.add( asynchronousTopicExtractionService.getTopicsByFiveFilters( publication, publicationTopic, extractionService ) );

		}
		else if ( extractionService.getExtractionServiceType().equals( ExtractionServiceType.OPENCALAIS ) && !publication.isPublicationTopicEverExtractedWith( ExtractionServiceType.OPENCALAIS ) )
		{
			// if publication language has been specified (mostly by alchemy)
			if ( publication.getLanguage() != null && !publication.getLanguage().equals( "english" ) )
				return;

			if ( extractionService.getCounter() > maxBatchCounter )
				return;

			// opencalais has limitation of 4 concurrent request every second,
			// therefore add sleep every 4 request
			// of course this will affect other extraction services
			try
			{
				Thread.sleep( batchSleep );
			}
			catch ( InterruptedException e )
			{
//				e.printStackTrace();
			}
			publication.addPublicationTopic( publicationTopic );

			// current openCalais service
			Future<PublicationTopic> openCalaisPublicationTopicFuture = asynchronousTopicExtractionService.getTopicsByOpenCalais( publication, publicationTopic, extractionService );

			publicationTopicFutureList.add( openCalaisPublicationTopicFuture );

			// prevent asynchronous call
			try
			{
				openCalaisPublicationTopicFuture.get();
			}
			catch ( InterruptedException | ExecutionException e )
			{
				e.printStackTrace();
			}

			batchCounter++;

			// put into counter
			extractionService.setCounter( extractionService.getCounter() + 1 );
		}
	}

}

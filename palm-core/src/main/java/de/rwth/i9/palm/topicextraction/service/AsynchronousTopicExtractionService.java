package de.rwth.i9.palm.topicextraction.service;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import de.rwth.i9.palm.model.ExtractionService;

//import com.google.common.base.Stopwatch;

import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationTopic;

@Service
public class AsynchronousTopicExtractionService
{
	private final static Logger log = LoggerFactory.getLogger( AsynchronousTopicExtractionService.class );

	/**
	 * Get extracted topic asynchronously via Alchemy Api
	 * 
	 * @param publication
	 * @param publicationTopic
	 * @param maxTextLength
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	@Async
	public Future<PublicationTopic> getTopicsByAlchemyApi( Publication publication, PublicationTopic publicationTopic, ExtractionService extractionService )
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();

		log.info( "AlchemyAPI extract publication " + publication.getTitle()/* + " starting" */);

		String text = getPublicationText( publication );

		// free alchemy has certain text limitation in length
		text = TopicExtractionUtils.cutTextToLength( text, extractionService.getMaxTextLength() );

		Map<String, Object> alchemyResultsMap = null;

		try
		{
			alchemyResultsMap = AlchemyAPITopicExtraction.getTextRankedKeywords( text, extractionService );
		}
		catch ( Exception e )
		{
		}

		if ( alchemyResultsMap != null && !alchemyResultsMap.isEmpty() )
		{
			if ( publication.getLanguage() == null && alchemyResultsMap.get( "language" ) != null )
				publication.setLanguage( alchemyResultsMap.get( "language" ).toString() );

			publicationTopic.setTermValues( (Map<String, Double>) alchemyResultsMap.get( "termvalue" ) );
			// filter duplicated keys, caused by accented character
			filterAlchemyResult( publicationTopic.getTermValues() );

			publicationTopic.setValid( true );
		}
		else
		{
			log.info( "Error - daily limit exceed" );
			publicationTopic.setValid( false );
		}

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );

//		log.info( "AlchemyAPI extract publication " + publication.getTitle() + " complete in " + stopwatch );
		return new AsyncResult<PublicationTopic>( publicationTopic );
	}

	/**
	 * Get extracted topic asynchronously via OpenCalais Api
	 * 
	 * @param publication
	 * @param publicationTopic
	 * @param maxTextLength
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	@Async
	public Future<PublicationTopic> getTopicsByOpenCalais( Publication publication, PublicationTopic publicationTopic, ExtractionService extractionService )
	{
		// Stopwatch stopwatch = Stopwatch.createStarted();

		log.info( "OpenCalais extract publication " + publication.getTitle()/* + " starting" */);

		String text = getPublicationText( publication );

		// just cut text according to configuration
		text = TopicExtractionUtils.cutTextToLength( text, extractionService.getMaxTextLength() );

		Map<String, Object> opencalaisResultsMap = null;

		try
		{
			opencalaisResultsMap = OpenCalaisAPITopicExtraction.getTopicsFromText( text, extractionService );
		}
		catch ( Exception e )
		{
		}

		if ( opencalaisResultsMap != null && !opencalaisResultsMap.isEmpty() )
		{
			if ( publication.getLanguage() == null && opencalaisResultsMap.get( "language" ) != null )
			{
				String language = opencalaisResultsMap.get( "language" ).toString();
				if ( language.length() > 14 )
					language = "english";

				publication.setLanguage( language );
			}

			publicationTopic.setTermValues( (Map<String, Double>) opencalaisResultsMap.get( "termvalue" ) );

			publicationTopic.setValid( true );
		}
		else if ( opencalaisResultsMap.isEmpty() ) // no topic found
			publicationTopic.setValid( true );
		else
			publicationTopic.setValid( false );

		// stopwatch.elapsed( TimeUnit.MILLISECONDS );

		// log.info( "AlchemyAPI extract publication " + publication.getTitle()
		// + " complete in " + stopwatch );
		return new AsyncResult<PublicationTopic>( publicationTopic );
	}

	/**
	 * Get extracted topic asynchronously via Yahoo Content Analysis
	 * 
	 * @param publication
	 * @param publicationTopic
	 * @param maxTextLength
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings( "unchecked" )
	@Async
	public Future<PublicationTopic> getTopicsByYahooContentAnalysis( Publication publication, PublicationTopic publicationTopic, ExtractionService extractionService ) throws UnsupportedEncodingException, URISyntaxException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();

//		log.info( "Yahoo Content Analysis extract publication " + publication.getTitle()/* + " starting" */);

		String text = getPublicationText( publication );

		text = TopicExtractionUtils.cutTextToLength( text, extractionService.getMaxTextLength() );

		Map<String, Object> ycaResultsMap = YahooContentAnalysisAPITopicExtraction.getTextContentAnalysis( text, extractionService );

		if ( ycaResultsMap != null )
		{
			//publication.setLanguage( ycaResultsMap.get( "language" ).toString() );

			publicationTopic.setTermValues( (Map<String, Double>) ycaResultsMap.get( "termvalue" ) );

			publicationTopic.setValid( true );
		}
		else
			publicationTopic.setValid( false );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );

//		log.info( "Yahoo Content Analysis extract publication " + publication.getTitle() + " complete in " + stopwatch );
		return new AsyncResult<PublicationTopic>( publicationTopic );
	}

	@SuppressWarnings( "unchecked" )
	@Async
	public Future<PublicationTopic> getTopicsByFiveFilters( Publication publication, PublicationTopic publicationTopic, ExtractionService extractionService ) throws UnsupportedEncodingException, URISyntaxException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();

//		log.info( "Five Filters extract publication " + publication.getTitle()/* + " starting" */);

		String text = getPublicationText( publication );

		text = TopicExtractionUtils.cutTextToLength( text, extractionService.getMaxTextLength() );

		Map<String, Object> fiveFiltersResultsMap = FiveFiltersAPITopicExtraction.getTextTermExtract( text, extractionService );

		if ( fiveFiltersResultsMap != null )
		{
			publicationTopic.setTermValues( (Map<String, Double>) fiveFiltersResultsMap.get( "termvalue" ) );

			// remove duplicated keys
			filterFiveFiltersResult( publicationTopic.getTermValues() );

			publicationTopic.setValid( true );
		}
		else
			publicationTopic.setValid( false );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );

//		log.info( "Five Filters extract publication " + publication.getTitle() + " complete in " + stopwatch );
		return new AsyncResult<PublicationTopic>( publicationTopic );
	}

	/**
	 * Filtering alchemyAPI result, due to accented character on terms caused
	 * error on saving process
	 * 
	 * @param alchemyResultsMap
	 */
	private void filterAlchemyResult( Map<String, Double> alchemyResultsMap )
	{
		Set<String> mapKeys = new HashSet<String>();
		if ( alchemyResultsMap != null && alchemyResultsMap.isEmpty() )
		{
			for ( Iterator<Map.Entry<String, Double>> it = alchemyResultsMap.entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry<String, Double> entry = it.next();
				// filter the accented character
				String mapKey = deAccent( entry.getKey() );
				// if there is similar key, remove resultmap
				if ( mapKeys.contains( mapKey ) )
					it.remove();
				else
					mapKeys.add( mapKey );
			}
		}
	}

	/**
	 * Filtering five filter result, due to similar term existed error on saving
	 * process
	 * 
	 * @param alchemyResultsMap
	 */
	private void filterFiveFiltersResult( Map<String, Double> fiveFiltersResultsMap )
	{
		Set<String> mapKeys = new HashSet<String>();

		for ( Iterator<Map.Entry<String, Double>> it = fiveFiltersResultsMap.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<String, Double> entry = it.next();
			// filter the accented character
			String mapKey = entry.getKey().toLowerCase();
			// if there is similar key, remove resultmap
			if ( mapKeys.contains( mapKey ) )
				it.remove();
			else
				mapKeys.add( mapKey );
		}

	}

	/**
	 * Normalize any String that contain accented character
	 * 
	 * @param str
	 * @return
	 */
	private String deAccent( String str )
	{
		String nfdNormalizedString = Normalizer.normalize( str, Normalizer.Form.NFD );
		Pattern pattern = Pattern.compile( "\\p{InCombiningDiacriticalMarks}+" );
		return pattern.matcher( nfdNormalizedString ).replaceAll( "" );
	}

	/**
	 * Construct text input for extraction from publication information ( title,
	 * abstract, keyword, content)
	 * 
	 * @param publication
	 * @return
	 */
	private String getPublicationText( Publication publication )
	{
		String text = publication.getTitle();
		if ( publication.getAbstractText() != null )
			text += ". " + publication.getAbstractText();
		if ( publication.getKeywords() != null )
			text += ". " + publication.getKeywords();
//		if ( publication.getContentText() != null )
//			text += " " + publication.getContentText();
		return text;
	}

}

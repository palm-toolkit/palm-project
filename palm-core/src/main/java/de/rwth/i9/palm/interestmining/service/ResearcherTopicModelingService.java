package de.rwth.i9.palm.interestmining.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.analytics.util.TopicMiningConstants;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.AuthorTopicModeling;
import de.rwth.i9.palm.model.AuthorTopicModelingProfile;
import de.rwth.i9.palm.model.InterestProfileType;
import de.rwth.i9.palm.model.TopicModelingAlgorithmAuthor;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Service
public class ResearcherTopicModelingService
{

	private final static Logger logger = LoggerFactory.getLogger( ResearcherTopicModelingService.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private PalmAnalytics palmAnalytics;

	private String path = TopicMiningConstants.USER_DESKTOP_PATH;

	public void calculateAuthorTopicModeling( String authorid, boolean isReplaceExistingResult, boolean Static )
	{

		Author author = persistenceStrategy.getAuthorDAO().getById( authorid );
		// First get active algorithm profile (DEFAULT & DERIVED)
		List<TopicModelingAlgorithmAuthor> activeDefaultAlgorithms = persistenceStrategy.getTopicModelingAlgorithmAuthorDAO().getAllActiveInterestProfile( InterestProfileType.DEFAULT );
		List<TopicModelingAlgorithmAuthor> activeDerivedAlgorithms = persistenceStrategy.getTopicModelingAlgorithmAuthorDAO().getAllActiveInterestProfile( InterestProfileType.DERIVED );
		
		// if no algorithms found or no topic modeling results found
		if ( ( activeDefaultAlgorithms == null || activeDefaultAlgorithms.isEmpty() ) && ( activeDerivedAlgorithms == null || activeDerivedAlgorithms.isEmpty() ) && ( author.getAuthorTopicModelingProfiles() == null && author.getAuthorTopicModelingProfiles().isEmpty() ) )
		{
			logger.warn( "status", "error - no active topic modeling algorithms found" );
		}
		
		// check whether we need to replace existing results
		if ( isReplaceExistingResult )
		{
			// First remove all results from previous calculation
			// by removing links between Author and AlgorithmProfile
			if ( author.getAuthorTopicModelingProfiles() != null && !author.getAuthorTopicModelingProfiles().isEmpty() )
			{
				// for( AuthorTopicModelingProfile atmp :
				// author.getAuthorTopicModelingProfiles() ){
				// atmp.setAuthor( null );
				// }
				author.getAuthorTopicModelingProfiles().clear();
			}

			// calculate interest with active default algorithms
			calculateDefaultTopicModelingAlgorithmAuthor( author, activeDefaultAlgorithms, Static );
		}
		else
		{
			// first time running, profile is still empty
			if ( author.getAuthorTopicModelingProfiles() == null || author.getAuthorTopicModelingProfiles().isEmpty() )
			{
				// calculate interest with active default algorithms
				calculateDefaultTopicModelingAlgorithmAuthor( author, activeDefaultAlgorithms, Static );
			}
			// else, check if there is missing profile
			else
			{
				for ( Iterator<TopicModelingAlgorithmAuthor> it = activeDefaultAlgorithms.iterator(); it.hasNext(); )
				{
					TopicModelingAlgorithmAuthor topicModelingAlgorithmAuthor = it.next();

					// check if algorithm profile is already exist on author
					boolean isAlgoritmProfileAlreadyExist = false;
					for ( AuthorTopicModelingProfile atmp : author.getAuthorTopicModelingProfiles() )
					{
						if ( atmp.getTopicModelingAlgorithmAuthor().equals( topicModelingAlgorithmAuthor ) )
						{
							isAlgoritmProfileAlreadyExist = true;
							break;
						}
					}

					// remove if algorithm is exist
					if ( isAlgoritmProfileAlreadyExist )
						it.remove();
				}

				// at the end, if there is still missing algorithm profile
				// run the calculation
				if ( !activeDefaultAlgorithms.isEmpty() )
					// calculate interest with active default algorithms
					calculateDefaultTopicModelingAlgorithmAuthor( author, activeDefaultAlgorithms, Static );
			}
		}
	}

	/**
	 * Calculate active default topic modeling from specific author
	 * 
	 * @param author
	 * @param activeDefaultAlgorithms
	 * @throws Exception
	 */
	private void calculateDefaultTopicModelingAlgorithmAuthor( Author author, List<TopicModelingAlgorithmAuthor> activeDefaultAlgorithms, boolean Static )
	{

		for ( TopicModelingAlgorithmAuthor activeDefaultAlgorithm : activeDefaultAlgorithms )
		{

			// ==Set Author TopicModeling profile==

			Calendar calendar = Calendar.getInstance();

			// default profile name [DEFAULT_PROFILENAME]
			String authorTopicModelingProfileName = activeDefaultAlgorithm.getName();

			// create new author topicModeling profile
			AuthorTopicModelingProfile authorTopicModelingProfile = new AuthorTopicModelingProfile();
			authorTopicModelingProfile.setCreated( calendar.getTime() );
			authorTopicModelingProfile.setDescription( "Topic Model mining using " + activeDefaultAlgorithm.getName() + " algorithm" );
			authorTopicModelingProfile.setName( authorTopicModelingProfileName );
			authorTopicModelingProfile.setTopicModelingAlgorithmAuthor( activeDefaultAlgorithm );

			// this is the actual implementation of NGrams N=1
			if ( activeDefaultAlgorithm.getName().toLowerCase().equals( "unigrams" ) )
			{
				calculateUnigramsAuthor( author, authorTopicModelingProfile, Static );
			}

			// calculate dummy ngram
			else if ( activeDefaultAlgorithm.getName().toLowerCase().equals( "ngrams" ) )
			{
				calculateNgramAuthor( author, authorTopicModelingProfile, Static );
			}
			// put other algorithm selection here, such as ngram etc

			// add TopicModelingProfile to author
			if ( authorTopicModelingProfile.getAuthorTopicModelings() != null && !authorTopicModelingProfile.getAuthorTopicModelings().isEmpty() )
			{
				// link author with authorTopicModelingProfile
				authorTopicModelingProfile.setAuthor( author );
				author.addAuthorTopicModelingProfiles( authorTopicModelingProfile );
			}
		}

		// at the end persist author
		persistenceStrategy.getAuthorDAO().persist( author );
	}



	
	/**
	 * Method used to get the unigrams from TopicalNgrams algorithm using N=1
	 */
	private void calculateUnigramsAuthor( Author author, AuthorTopicModelingProfile authorTopicModelingProfile, boolean Static )
	{
		// Normally, here you call palmAnalytics.getNgram....
		// to calculate the LDA and get the result as Map / List

		// get dummy results based on years cluster
		List<Object> ClusterResults = getUnigramResultsAuthor( author, Static );

		// now put this cluster list into authorTopicModeling object
		transformTopicModelingResultIntoAuthorTopicModeling( authorTopicModelingProfile, ClusterResults, Static );
	}
	
	

	/**
	 * Method used to get the unigrams from TopicalNgrams algorithm
	 */
	private void calculateNgramAuthor( Author author, AuthorTopicModelingProfile authorTopicModelingProfile, boolean Static )
	{	
		// Normally, here you call palmAnalytics.getNgram....
		// to calculate the LDA and get the result as Map / List
		
		// get dummy results based on years cluster
		List<Object> dummyClusterResults = getNgramResultsAuthor( author, Static );

		// now put this cluster list into authorTopicModeling object
		transformTopicModelingResultIntoAuthorTopicModeling( authorTopicModelingProfile, dummyClusterResults, Static );
	}

	/**
	 * Method to show how to transform topic modeling results from Collections
	 * to authorTopicModeling object
	 * 
	 */
	private void transformTopicModelingResultIntoAuthorTopicModeling( AuthorTopicModelingProfile authorTopicModelingProfile, List<Object> dummyClusterResults, boolean Static )
	{

		// loop based on clusters
		for ( Object dummyClusterResult : dummyClusterResults )
		{
			@SuppressWarnings( "unchecked" )
			Map<String, Object> clusterResultMap = (Map<String, Object>) dummyClusterResult;

			// check for result, is it empty or null?
			@SuppressWarnings( "unchecked" )
			Map<String, Double> termValueMap = (Map<String, Double>) clusterResultMap.get( "termvalues" );

			String topic = (String) clusterResultMap.get( "topic" );

			// if null or empty just skip
			if ( termValueMap == null || termValueMap.isEmpty() )
				continue;

			// get date
			// Date clusterYear = null;
			// try
			// {
			// clusterYear = dateFormat.parse( clusterResultMap.get( "year"
			// ).toString() );
			// }
			// catch ( Exception e )
			// {
			// }

			// create new AuthorTopicModeling (year cluster) and fill attributes
			AuthorTopicModeling atm = new AuthorTopicModeling();
			// atm.setYear( clusterYear );
			atm.setLanguage( clusterResultMap.get( "language" ).toString() );
			atm.setTermWeightsString( termValueMap );
			atm.setURI( topic );
			atm.setAuthorTopicModelingProfile( authorTopicModelingProfile );
			authorTopicModelingProfile.addAuthorTopicModeling( atm );
		}

	}

	/**
	 * This method uses the getTopicUnigramsDocument from TopicalNgram Algorithm
	 * 
	 * @param author
	 * @return
	 * @throws Exception
	 */
	public List<Object> getUnigramResultsAuthor( Author author, boolean Static )
	{
		// Note: usually you need author object, to get author publications, etc
		// but author object is unused in this dummy method

		// list of cluster container
		List<Object> clusterResults = new ArrayList<Object>();

		// needed for dynamic visualization
		if ( !Static )
		{
			Map<String, Double> termValueMap = new LinkedHashMap<String, Double>();

			Map<String, List<String>> topicNgrams = null;
			try
			{
				// topicNgrams =
				// palmAnalytics.getNGrams().getEvolutionofTopicOverTime( 0, 5,
				// true );
				// getTopicUnigramsDocument(i-2005,-1, 0.0,11,5,true );

				for ( Entry<String, List<String>> topicngrams : topicNgrams.entrySet() )
				{
					Map<String, Object> clusterResultMap = new LinkedHashMap<String, Object>();
					clusterResultMap.put( "language", "english" );
					for ( String topicproportion : topicngrams.getValue() )
					{
						String[] components = topicproportion.split( "_-_" );
						// termValueMap.put( "term", components[0] );
						// termValueMap.put( "value", components[1] );
						termValueMap.put( components[0], Double.parseDouble( components[1] ) );
					}
					clusterResultMap.put( "termvalues", termValueMap );
					clusterResultMap.put( "topic", topicngrams.getKey() );
					clusterResultMap.put( "color", "#ff7f0e" );
					clusterResults.add( clusterResultMap );
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}

		}

		// case when Static = True
		// prepare results for Donut :)
		else
		{
			Map<String, Object> clusterResultMap = new LinkedHashMap<String, Object>();
			clusterResultMap.put( "authorId", author.getId().toString() );
			clusterResultMap.put( "year", new SimpleDateFormat( "yyyy", Locale.ENGLISH ).format( new Date() ) );
			clusterResultMap.put( "language", "english" );
			clusterResultMap.put( "extractor", "TopicalNgram" );

			// get the results for a specific id
			Map<String, Double> termValueMap = new HashMap<String, Double>();
			Map<String, List<String>> topicNgrams;
			try
			{
				//topicNgrams = palmAnalytics.getNGrams().getTopTopicUnigramsDocument( palmAnalytics.getNGrams().maptoRealDatabaseID( author.getId().toString() ), -1, 0.0, 5, 5, true );
				topicNgrams = palmAnalytics.getNGrams().runTopicComposition( author.getId().toString(), path, "Author-Test", 20, 10, 5, false, true, true );
				
				for ( Entry<String, List<String>> topicngrams : topicNgrams.entrySet() )
			{
					for ( String topicproportion : topicngrams.getValue() )
					{
						String[] components = topicproportion.split( "_-_" );
						// termValueMap.put( "term", components[0] );
						// termValueMap.put( "value", components[1] );
						termValueMap.put( components[0], Double.parseDouble( components[1] ) );
					}
			}
		}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
			clusterResultMap.put( "termvalues", termValueMap );
			clusterResults.add( clusterResultMap );
		}

		// return dummy results
		return clusterResults;
	}

	/**
	 * This method is supposed to use the getTopicNgramsDocument from
	 * TopicalNgram Algorithm
	 * 
	 * @param author
	 * @return
	 * @throws Exception
	 */
	public List<Object> getNgramResultsAuthor( Author author, boolean Static )
	{
		// Note: usually you need author object, to get author publications, etc
		// but author object is unused in this dummy method

		// list of cluster container
		List<Object> clusterResults = new ArrayList<Object>();

		// dummy start year array
		int[] startYearArray = { 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015 };

		// dummy end year array
		int[] endYearArray = { 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015 };

		if ( !Static )
		{
			for ( int i = startYearArray[0]; i < endYearArray[endYearArray.length - 1]; i++ )
			{
				// store dummy information regarding cluster information
				Map<String, Object> clusterResultMap = new LinkedHashMap<String, Object>();

				// store dummy information regarding result information
				Map<String, Double> termValueMap = new HashMap<String, Double>();

				clusterResultMap.put( "year", i );
				clusterResultMap.put( "language", "english" );
				clusterResultMap.put( "termvalues", termValueMap );

				Map<String, List<String>> topicNgrams = null;
				try
				{
					// topicNgrams =
					// palmAnalytics.getNGrams().getTopicNgramsDocument( i -
					// 2005, -1, 0.0, 11, 5, true );

					// add into termValueMap
					// termValueMap.put(palmAnalytics.getDynamicTopicModel().getListTopics(
					// 10 ).get( j ),
					// palmAnalytics.getDynamicTopicModel().getTopicProportion2(0.0,
					// i-2005, 11, 11 ).get( j ) );//dummyWordString.toString(),
					// Math.random() );
					for ( Entry<String, List<String>> topicngrams : topicNgrams.entrySet() )
					{
						for ( String topicproportion : topicngrams.getValue() )
						{
							String[] components = topicproportion.split( "_-_ " );
							termValueMap.put( components[0], Double.parseDouble( components[1] ) );
						}
					}
				}
				catch ( Exception e )
				{
					e.printStackTrace();
				}
				clusterResultMap.put( "termvalues", termValueMap );
				clusterResults.add( clusterResultMap );
			}

		}
		else
		{
			// case when Static = True
			// prepare results for Donut :)
			Map<String, Object> clusterResultMap = new LinkedHashMap<String, Object>();
			clusterResultMap.put( "year", new SimpleDateFormat( "yyyy", Locale.ENGLISH ).format( new Date() ) );
			clusterResultMap.put( "language", "english" );
			clusterResultMap.put( "extractor", "TopicalNgram" );

			// get the results for a specific id
			// c442983a-0099-4d6d-89b1-6cfc57fa6138
			// 07397ed7-3deb-442f-a297-bdb5b476d3e6
			Map<String, Double> termValueMap = new HashMap<String, Double>();
			Map<String, List<String>> topicNgrams;
			try
				{
				// topicNgrams =
				// palmAnalytics.getNGrams().getTopTopicNgramsDocument(
				// palmAnalytics.getNGrams().maptoRealDatabaseID( author.getId()
				// ), -1, 0.0, 5, 5, true );
				topicNgrams = palmAnalytics.getNGrams().runTopicComposition( author.getId().toString(), path, "Author-Test", 20, 10, 5, false, true, false );
				for ( Entry<String, List<String>> topicngrams : topicNgrams.entrySet() )
					{
					for ( String topicproportion : topicngrams.getValue() )
					{
						String[] components = topicproportion.split( "_-_" );
						termValueMap.put( components[0], Double.parseDouble( components[1] ) );
					}
					}
				}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
			clusterResultMap.put( "termvalues", termValueMap );
			clusterResults.add( clusterResultMap );
			}

	// return results
	return clusterResults;
	}

	/**
	 * Collect the author interest result as JSON object
	 * 
	 * @param author
	 * @param responseMap
	 * @return
	 */
	public List<Object> getAuthorTopicModeliFromDatabase( Author author )
	{
		List<AuthorTopicModelingProfile> authorTopicModelingProfiles = new ArrayList<AuthorTopicModelingProfile>();
		authorTopicModelingProfiles.addAll( author.getAuthorTopicModelingProfiles() );

		// the whole result related to interest
		List<Object> authorTopicModelingResult = new ArrayList<Object>();

		for ( AuthorTopicModelingProfile authorTopicModelingProfile : authorTopicModelingProfiles )
		{
			// put profile on map
			Map<String, Object> authorTopicModelingResultProfilesMap = new HashMap<String, Object>();

			// get interest profile name and description
			String interestProfileName = authorTopicModelingProfile.getName();
			String interestProfileDescription = authorTopicModelingProfile.getDescription();

			// get authorTopicModeling set on profile
			Set<AuthorTopicModeling> authorTopicModelings = authorTopicModelingProfile.getAuthorTopicModelings();

			// if profile contain no authorTopicModeling just skip
			if ( authorTopicModelings == null || authorTopicModelings.isEmpty() )
				continue;

			// a map for storing authorInterst based on language
			Map<String, List<AuthorTopicModeling>> authorTopicModelingLanguageMap = new HashMap<String, List<AuthorTopicModeling>>();

			// split authorinterest based on language and put it on the map
			for ( AuthorTopicModeling authorTopicModeling : authorTopicModelings )
			{
				if ( authorTopicModelingLanguageMap.get( authorTopicModeling.getLanguage() ) != null )
				{
					authorTopicModelingLanguageMap.get( authorTopicModeling.getLanguage() ).add( authorTopicModeling );
				}
				else
				{
					List<AuthorTopicModeling> authorTopicModelingList = new ArrayList<AuthorTopicModeling>();
					authorTopicModelingList.add( authorTopicModeling );
					authorTopicModelingLanguageMap.put( authorTopicModeling.getLanguage(), authorTopicModelingList );
				}
			}


			// result author interest based on language
			List<Object> authorTopicModelingResultLanguageList = new ArrayList<Object>();

			// sort authorinterest based on year
			for ( Map.Entry<String, List<AuthorTopicModeling>> authorTopicModelingLanguageIterator : authorTopicModelingLanguageMap.entrySet() )
			{
				// result container
				Map<String, Object> authorTopicModelingResultLanguageMap = new LinkedHashMap<String, Object>();
				// hashmap value
				String interestLanguage = authorTopicModelingLanguageIterator.getKey();
				List<AuthorTopicModeling> interestList = authorTopicModelingLanguageIterator.getValue();

				// sort based on year
				// Collections.sort( interestList, new
				// AuthorTopicModelingByDateComparator() );

				// term values based on year result container
				List<Object> authorTopicModelingResultYearList = new ArrayList<Object>();

				// get interest year, term and value
				for ( AuthorTopicModeling authorTopicModeling : interestList )
				{
					if ( authorTopicModeling.getTermWeightsString() == null || authorTopicModeling.getTermWeightsString().isEmpty() )
						continue;

					// result container
					Map<String, Object> authorTopicModelingResultYearMap = new LinkedHashMap<String, Object>();

					// get year
					// calendar.setTime( authorTopicModeling.getYear() );
					// String year = Integer.toString( calendar.get(
					// Calendar.YEAR ) );

					List<Object> termValueResult = new ArrayList<Object>();

					// put term and value
					for ( Map.Entry<String, Double> termWeightMap : authorTopicModeling.getTermWeightsString().entrySet() )
					{
						List<Object> termWeightObjects = new ArrayList<Object>();
						termWeightObjects.add( termWeightMap.getKey() );
						termWeightObjects.add( termWeightMap.getValue() );
						termValueResult.add( termWeightObjects );
					}
					// authorTopicModelingResultYearMap.put( "year", year );
					authorTopicModelingResultYearMap.put( "termvalue", termValueResult );
					authorTopicModelingResultYearList.add( authorTopicModelingResultYearMap );
				}

				authorTopicModelingResultLanguageMap.put( "language", interestLanguage );
				authorTopicModelingResultLanguageMap.put( "interestyears", authorTopicModelingResultYearList );
				if ( interestLanguage.equals( "english" ) )
					authorTopicModelingResultLanguageList.add( 0, authorTopicModelingResultLanguageMap );
				else
					authorTopicModelingResultLanguageList.add( authorTopicModelingResultLanguageMap );
			}

			// put profile map
			authorTopicModelingResultProfilesMap.put( "profile", interestProfileName );
			authorTopicModelingResultProfilesMap.put( "description", interestProfileDescription );
			authorTopicModelingResultProfilesMap.put( "interestlanguages", authorTopicModelingResultLanguageList );
			authorTopicModelingResult.add( authorTopicModelingResultProfilesMap );
		}

		// put also publication

		return authorTopicModelingResult;
	}

	/**
	 * Collect the author interest result as JSON object
	 * 
	 * @param author
	 * @param responseMap
	 * @return
	 */
	public List<Object> getStaticAuthorTopicModelingFromDatabase( Author author )
	{
		List<AuthorTopicModelingProfile> authorTopicModelingProfiles = new ArrayList<AuthorTopicModelingProfile>();
		authorTopicModelingProfiles.addAll( author.getAuthorTopicModelingProfiles() );

		// the whole result related to interest
		List<Object> authorTopicModelingResult = new ArrayList<Object>();

		for ( AuthorTopicModelingProfile authorTopicModelingProfile : authorTopicModelingProfiles )
		{
			// put profile on map
			Map<String, Object> authorTopicModelingResultProfilesMap = new HashMap<String, Object>();

			// get interest profile name and description
			String interestProfileName = authorTopicModelingProfile.getName();
			//String interestProfileDescription = authorTopicModelingProfile.getDescription();

			// get authorTopicModeling set on profile
			Set<AuthorTopicModeling> authorTopicModelings = authorTopicModelingProfile.getAuthorTopicModelings();

			// if profile contain no authorTopicModeling just skip
			if ( authorTopicModelings == null || authorTopicModelings.isEmpty() )
				continue;

				// get interest year, term and value
			for ( AuthorTopicModeling authorTopicModeling : authorTopicModelings )
				{
					if ( authorTopicModeling.getTermWeightsString() == null || authorTopicModeling.getTermWeightsString().isEmpty() )
						continue;

					List<Object> termValueResult = new ArrayList<Object>();

					// put term and value
					for ( Map.Entry<String, Double> termWeightMap : authorTopicModeling.getTermWeightsString().entrySet() )
					{
						List<Object> termWeightObjects = new ArrayList<Object>();
						termWeightObjects.add( termWeightMap.getKey() );
						termWeightObjects.add( termWeightMap.getValue() );
						termValueResult.add( termWeightObjects );
					}
				authorTopicModelingResultProfilesMap.put( "termvalue", termValueResult );
				}

			// put profile map
			authorTopicModelingResultProfilesMap.put( "profile", interestProfileName );
			authorTopicModelingResult.add( authorTopicModelingResultProfilesMap );
		}
		return authorTopicModelingResult;
	}
}

package de.rwth.i9.palm.interestmining.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.rwth.i9.palm.helper.comparator.AuthorInterestByDateComparator;
import de.rwth.i9.palm.helper.comparator.AuthorInterestProfileByProfileNameLengthComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.AuthorInterest;
import de.rwth.i9.palm.model.AuthorInterestProfile;
import de.rwth.i9.palm.model.Interest;
import de.rwth.i9.palm.model.InterestProfile;
import de.rwth.i9.palm.model.InterestProfileType;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;

@Service
public class InterestMiningService
{

	private final static Logger logger = LoggerFactory.getLogger( InterestMiningService.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private CValueInterestProfile cValueInterestProfile;

	@Autowired
	private CorePhraseInterestProfile corePhraseInterestProfile;

	@Autowired
	private WordFreqInterestProfile wordFreqInterestProfile;

	/**
	 * Get author interests from active interest profiles
	 * 
	 * @param responseMap
	 * @param author
	 * @param updateAuthorInterest
	 * @return
	 * @throws ParseException
	 */
	public Map<String, Object> getInterestFromAuthor( Map<String, Object> responseMap, Author author, boolean updateAuthorInterest ) throws ParseException
	{
		logger.info( "start mining interest " );
		// get default interest profile
		List<InterestProfile> interestProfilesDefault = persistenceStrategy.getInterestProfileDAO().getAllActiveInterestProfile( InterestProfileType.DEFAULT );

		// get default interest profile
		List<InterestProfile> interestProfilesDerived = persistenceStrategy.getInterestProfileDAO().getAllActiveInterestProfile( InterestProfileType.DERIVED );

		if ( interestProfilesDefault.isEmpty() && interestProfilesDerived.isEmpty() )
		{
			logger.warn( "No active interest profile found" );
			return responseMap;
		}

		if ( author.getPublications() == null || author.getPublications().isEmpty() )
		{
			logger.warn( "No publication found" );
			return responseMap;
		}

		// update for all author interest profile
		// updateAuthorInterest = true;
		if ( !updateAuthorInterest )
		{
			// get interest profile from author
			Set<AuthorInterestProfile> authorInterestProfiles = author.getAuthorInterestProfiles();
			if ( authorInterestProfiles != null && !authorInterestProfiles.isEmpty() )
			{
				// check for missing default interest profile in author
				// only calculate missing one
				for ( Iterator<InterestProfile> interestProfileIterator = interestProfilesDefault.iterator(); interestProfileIterator.hasNext(); )
				{
					InterestProfile interestProfileDefault = interestProfileIterator.next();
					for ( AuthorInterestProfile authorInterestProfile : authorInterestProfiles )
					{
						if ( authorInterestProfile.getInterestProfile() != null && authorInterestProfile.getInterestProfile().equals( interestProfileDefault ) )
						{
							interestProfileIterator.remove();
							break;
						}
					}
				}

				// check for missing derivative interest profile
				for ( Iterator<InterestProfile> interestProfileIterator = interestProfilesDerived.iterator(); interestProfileIterator.hasNext(); )
				{
					InterestProfile interestProfileDerived = interestProfileIterator.next();
					for ( AuthorInterestProfile authorInterestProfile : authorInterestProfiles )
					{
						if ( authorInterestProfile.getInterestProfile() != null && authorInterestProfile.getInterestProfile().equals( interestProfileDerived ) )
						{
							interestProfileIterator.remove();
							break;
						}
					}
				}
			}
		}
		else
		{
			// clear previous results
			if ( author.getAuthorInterestProfiles() != null && !author.getAuthorInterestProfiles().isEmpty() )
			{
				author.getAuthorInterestProfiles().clear();
			}
		}

		// if defaultInterestProfile not null,
		// means interest calculation from beginning is needed
		if ( !interestProfilesDefault.isEmpty() )
		{
			// first create publication cluster
			// prepare the cluster container
			Map<String, PublicationClusterHelper> publicationClustersMap = new HashMap<String, PublicationClusterHelper>();
			// construct the cluster
			logger.info( "Construct publication cluster " );
			constructPublicationClusterByLanguageAndYear( author, publicationClustersMap );
			// cluster is ready
			if ( !publicationClustersMap.isEmpty() )
			{
				// calculate default interest profile
				calculateInterestProfilesDefault( author, publicationClustersMap, interestProfilesDefault );
			}
		}

		// check for derived interest profile
		if ( !interestProfilesDerived.isEmpty() )
		{
			// calculate derived interest profile
			calculateInterestProfilesDerived( author, interestProfilesDerived );
		}

		// get and put author interest profile into map or list
		getInterestFromDatabase( author, responseMap );

		return responseMap;
	}

	/**
	 * Calculated derived interest profile (Intersection and/or Union between
	 * interest profile) in an author
	 * 
	 * @param author
	 * @param interestProfilesDerived
	 */
	private void calculateInterestProfilesDerived( Author author, List<InterestProfile> interestProfilesDerived )
	{
		// get authorInterest set on profile
		for ( InterestProfile interestProfileDerived : interestProfilesDerived )
		{

			String[] derivedInterestProfileName = interestProfileDerived.getName().split( "\\s+" );

			// at list profile name has three segment
			if ( derivedInterestProfileName.length < 3 )
				continue;

			// prepare variables
			AuthorInterestProfile authorInterestProfile1 = null;
			AuthorInterestProfile authorInterestProfile2 = null;
			AuthorInterestProfile authorInterestProfileResult = null;
			String operationType = null;

			for ( String partOfProfileName : derivedInterestProfileName )
			{
				// ? sometimes problem on encoding
				if ( partOfProfileName.equals( "∩" ) || partOfProfileName.equals( "?" ) || partOfProfileName.equals( "+" ) || partOfProfileName.equals( "∪" ) )
				{
					if ( authorInterestProfileResult != null )
					{
						authorInterestProfile1 = authorInterestProfileResult;
						authorInterestProfileResult = null;
					}
					if ( partOfProfileName.equals( "∩" ) || partOfProfileName.equals( "?" ) || partOfProfileName.equals( "+" ) )
						operationType = "INTERSECTION";
					else
						operationType = "UNION";
				}
				else
				{
					if ( authorInterestProfile1 == null )
					{
						authorInterestProfile1 = author.getSpecificAuthorInterestProfile( partOfProfileName );

						if ( authorInterestProfile1 == null )
						{
							logger.error( "AuthorInterestProfile " + partOfProfileName + " not found" );
							// continue to next derived author profile, if exist
							break;
						}
					}
					else
					{
						authorInterestProfile2 = author.getSpecificAuthorInterestProfile( partOfProfileName );

						if ( authorInterestProfile2 == null )
						{
							logger.error( "AuthorInterestProfile " + partOfProfileName + " not found" );
							// continue to next derived author profile, if exist
							break;
						}
					}

					// calculate and persist
					if ( authorInterestProfile1 != null && authorInterestProfile2 != null && operationType != null )
					{
						if ( operationType.equals( "INTERSECTION" ) )
							authorInterestProfileResult = calculateIntersectionOfAuthorInterestProfiles( authorInterestProfile1, authorInterestProfile2, interestProfileDerived );
						else
							authorInterestProfileResult = calculateUnionOfAuthorInterestProfiles( authorInterestProfile1, authorInterestProfile2, interestProfileDerived );
					}
				}
			}
			// persist result
			if ( authorInterestProfileResult != null && ( authorInterestProfileResult.getAuthorInterests() != null && !authorInterestProfileResult.getAuthorInterests().isEmpty() ) )
			{
				authorInterestProfileResult.setAuthor( author );
				author.addAuthorInterestProfiles( authorInterestProfileResult );
				persistenceStrategy.getAuthorDAO().persist( author );

				persistenceStrategy.getAuthorInterestProfileDAO().persist( authorInterestProfileResult );
			}

		}

	}

	/**
	 * Calculate Union interest between 2 Author interest profiles
	 * 
	 * @param authorInterestProfile1
	 * @param authorInterestProfile2
	 * @param interestProfileDerived
	 * @return
	 */
	private AuthorInterestProfile calculateUnionOfAuthorInterestProfiles( AuthorInterestProfile authorInterestProfile1, AuthorInterestProfile authorInterestProfile2, InterestProfile interestProfileDerived )
	{
		Calendar calendar = Calendar.getInstance();
		AuthorInterestProfile authorInterestProfileResult = new AuthorInterestProfile();
		// set derived profile name
		String authorInterestProfileName = authorInterestProfile1.getName() + " ∪ " + authorInterestProfile2.getName();

		authorInterestProfileResult.setCreated( calendar.getTime() );
		authorInterestProfileResult.setName( authorInterestProfileName );
		authorInterestProfileResult.setDescription( "Interest mining using " + authorInterestProfileName + " algorithm" );

		Set<AuthorInterest> authorInterests1 = authorInterestProfile1.getAuthorInterests();
		Set<AuthorInterest> authorInterests2 = authorInterestProfile2.getAuthorInterests();

		for ( AuthorInterest eachAuthorInterest1 : authorInterests1 )
		{
			AuthorInterest authorInterestResult = null;
			for ( AuthorInterest eachAuthorInterest2 : authorInterests2 )
			{
				if ( eachAuthorInterest1.getLanguage().equals( eachAuthorInterest2.getLanguage() ) && eachAuthorInterest1.getYear().equals( eachAuthorInterest2.getYear() ) )
				{
					authorInterestResult = calculateUnionOfAuthorInterest( eachAuthorInterest1, eachAuthorInterest2 );
				}
			}

			if ( authorInterestResult != null && authorInterestResult.getTermWeights() != null && !authorInterestResult.getTermWeights().isEmpty() )
			{
				authorInterestResult.setAuthorInterestProfile( authorInterestProfileResult );
				authorInterestProfileResult.addAuthorInterest( authorInterestResult );
				authorInterestProfileResult.setInterestProfile( interestProfileDerived );
			}
		}

		return authorInterestProfileResult;
	}

	/**
	 * Calculate Union interest between 2 AuthorInterest
	 * 
	 * @param eachAuthorInterest1
	 * @param eachAuthorInterest2
	 * @return
	 */
	private AuthorInterest calculateUnionOfAuthorInterest( AuthorInterest eachAuthorInterest1, AuthorInterest eachAuthorInterest2 )
	{
		AuthorInterest authorInterestResult = new AuthorInterest();
		authorInterestResult.setLanguage( eachAuthorInterest1.getLanguage() );
		authorInterestResult.setYear( eachAuthorInterest1.getYear() );

		Map<Interest, Double> termsWeight1 = eachAuthorInterest1.getTermWeights();
		Map<Interest, Double> termsWeight2 = eachAuthorInterest2.getTermWeights();

		Map<Interest, Double> mergedTermWeight = new HashMap<Interest, Double>();
		mergedTermWeight.putAll( termsWeight1 );
		mergedTermWeight.putAll( termsWeight2 );

		Map<Interest, Double> termsWeightResult = new HashMap<Interest, Double>();

		for ( Map.Entry<Interest, Double> eachMergedTermWeight : mergedTermWeight.entrySet() )
		{
			Interest interstKey = eachMergedTermWeight.getKey();
			if ( termsWeight1.get( interstKey ) != null )
			{
				termsWeightResult.put( interstKey, ( eachMergedTermWeight.getValue() + termsWeight1.get( interstKey ) ) / 2 );
			}
			else
			{
				termsWeightResult.put( interstKey, eachMergedTermWeight.getValue() );
			}
		}

		if ( !termsWeightResult.isEmpty() )
			authorInterestResult.setTermWeights( termsWeightResult );

		return authorInterestResult;
	}

	/**
	 * Calculate Intersection interest between 2 Author interest profiles
	 * 
	 * @param authorInterestProfile1
	 * @param authorInterestProfile2
	 * @param interestProfileDerived
	 * @return
	 */
	private AuthorInterestProfile calculateIntersectionOfAuthorInterestProfiles( AuthorInterestProfile authorInterestProfile1, AuthorInterestProfile authorInterestProfile2, InterestProfile interestProfileDerived )
	{
		Calendar calendar = Calendar.getInstance();
		AuthorInterestProfile authorInterestProfileResult = new AuthorInterestProfile();
		// set derived profile name
		String authorInterestProfileName = authorInterestProfile1.getName() + " ∩ " + authorInterestProfile2.getName();

		authorInterestProfileResult.setCreated( calendar.getTime() );
		authorInterestProfileResult.setName( authorInterestProfileName );
		authorInterestProfileResult.setDescription( "Interest mining using " + authorInterestProfileName + " algorithm" );

		Set<AuthorInterest> authorInterests1 = authorInterestProfile1.getAuthorInterests();
		Set<AuthorInterest> authorInterests2 = authorInterestProfile2.getAuthorInterests();

		if ( authorInterests1 != null && !authorInterests1.isEmpty() )
		{
			for ( AuthorInterest eachAuthorInterest1 : authorInterests1 )
			{
				AuthorInterest authorInterestResult = null;
				for ( AuthorInterest eachAuthorInterest2 : authorInterests2 )
				{
					if ( eachAuthorInterest1.getLanguage().equals( eachAuthorInterest2.getLanguage() ) && eachAuthorInterest1.getYear().equals( eachAuthorInterest2.getYear() ) )
					{
						authorInterestResult = calculateIntersectionOfAuthorInterest( eachAuthorInterest1, eachAuthorInterest2 );
					}
				}
	
				if ( authorInterestResult != null && authorInterestResult.getTermWeights() != null && !authorInterestResult.getTermWeights().isEmpty() )
				{
					authorInterestResult.setAuthorInterestProfile( authorInterestProfileResult );
					authorInterestProfileResult.addAuthorInterest( authorInterestResult );
					authorInterestProfileResult.setInterestProfile( interestProfileDerived );
				}
			}
		}
		return authorInterestProfileResult;
	}

	/**
	 * Calculate Intersection interest between 2 AuthorInterest
	 * 
	 * @param eachAuthorInterest1
	 * @param eachAuthorInterest2
	 * @return
	 */
	private AuthorInterest calculateIntersectionOfAuthorInterest( AuthorInterest eachAuthorInterest1, AuthorInterest eachAuthorInterest2 )
	{
		AuthorInterest authorInterestResult = new AuthorInterest();
		authorInterestResult.setLanguage( eachAuthorInterest1.getLanguage() );
		authorInterestResult.setYear( eachAuthorInterest1.getYear() );

		Map<Interest, Double> termsWeight1 = eachAuthorInterest1.getTermWeights();
		Map<Interest, Double> termsWeight2 = eachAuthorInterest2.getTermWeights();
		Map<Interest, Double> termsWeightResult = new HashMap<Interest, Double>();

		for ( Map.Entry<Interest, Double> eachTermWeight1 : termsWeight1.entrySet() )
		{
			Interest interstKey = eachTermWeight1.getKey();
			if ( termsWeight2.get( interstKey ) != null )
			{
				termsWeightResult.put( interstKey, ( eachTermWeight1.getValue() + termsWeight2.get( interstKey ) ) / 2 );
			}
		}

		if ( !termsWeightResult.isEmpty() )
			authorInterestResult.setTermWeights( termsWeightResult );

		return authorInterestResult;
	}

	/**
	 * Main method to calculate default InterestProfile such as : C-Value,
	 * Corephrase and WordFreq profile
	 * 
	 * @param author
	 * @param publicationClustersMap
	 * @param interestProfilesDefault
	 */
	public void calculateInterestProfilesDefault( Author author, Map<String, PublicationClusterHelper> publicationClustersMap, List<InterestProfile> interestProfilesDefault )
	{
		// prepare the set of new interest, to prevent 
		// com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry
		Set<Interest> newInterests = new HashSet<Interest>();
		
		
		// calculate frequencies of term in cluster
		for ( Map.Entry<String, PublicationClusterHelper> publicationClusterEntry : publicationClustersMap.entrySet() )
			publicationClusterEntry.getValue().calculateTermProperties();

		// loop through all interest profiles default
		for ( InterestProfile interestProfileDefault : interestProfilesDefault )
			calculateEachInterestProfileDefault( author, newInterests, interestProfileDefault, publicationClustersMap );
		

	}

	/**
	 * Calculate each default InterestProfile
	 * 
	 * @param author
	 * @param interestProfileDefault
	 * @param publicationClustersMap
	 */
	public void calculateEachInterestProfileDefault( Author author, Set<Interest> newInterests, InterestProfile interestProfileDefault, Map<String, PublicationClusterHelper> publicationClustersMap )
	{
		// get author interest profile
		Calendar calendar = Calendar.getInstance();
		// default profile name [DEFAULT_PROFILENAME]
		String authorInterestProfileName = interestProfileDefault.getName();

		// create new author interest profile for c-value
		AuthorInterestProfile authorInterestProfile = new AuthorInterestProfile();
		authorInterestProfile.setCreated( calendar.getTime() );
		authorInterestProfile.setDescription( "Interest mining using " + interestProfileDefault.getName() + " algorithm" );
		authorInterestProfile.setName( authorInterestProfileName );
		
		// CorePhrase and WordFreq specific, according to Svetoslav Evtimov thesis
		// yearFactor Map format Map< Language-Year , value >
		// totalYearsFactor Map< Language, value >
		
		Map<String, Double> yearFactorMap = new HashMap<String, Double>();
		Map<String, Double> totalYearsFactorMap = new HashMap<String, Double>();
		
		// calculate some weighting factors
//		if ( interestProfileDefault.getName().toLowerCase().equals( "corephrase" ) ||
//				interestProfileDefault.getName().toLowerCase().equals( "wordfreq" )	)
//		{
//			yearFactorMap = CorePhraseAndWordFreqHelper.calculateYearFactor( publicationClustersMap, 0.25 );
//			totalYearsFactorMap = CorePhraseAndWordFreqHelper.calculateTotalYearsFactor( publicationClustersMap );
//		}

		// get the number of active extraction services
		int numberOfExtractionService = applicationService.getExtractionServices().size();

		// loop to each cluster and calculate default profiles
		for ( Map.Entry<String, PublicationClusterHelper> publicationClusterEntry : publicationClustersMap.entrySet() )
		{
			PublicationClusterHelper publicationCluster = publicationClusterEntry.getValue();

			if ( publicationCluster.getTermMap() == null || publicationCluster.getTermMap().isEmpty() )
				continue;

			// prepare variables
			AuthorInterest authorInterest = new AuthorInterest();

			// assign author interest method
			if ( interestProfileDefault.getName().toLowerCase().equals( "cvalue" ) )
			{
				cValueInterestProfile.doCValueCalculation( authorInterest, newInterests, publicationCluster, numberOfExtractionService );
			}
			else if ( interestProfileDefault.getName().toLowerCase().equals( "corephrase" ) )
			{
				Double yearFactor = yearFactorMap.get( publicationCluster.getLanguage() + publicationCluster.getYear() );
				Double totalYearFactor = totalYearsFactorMap.get( publicationCluster.getLanguage() );
				corePhraseInterestProfile.doCorePhraseCalculation( authorInterest, newInterests, publicationCluster, yearFactor, totalYearFactor, numberOfExtractionService );
			}
			else if ( interestProfileDefault.getName().toLowerCase().equals( "wordfreq" ) )
			{
				Double yearFactor = yearFactorMap.get( publicationCluster.getLanguage() + publicationCluster.getYear() );
				Double totalYearFactor = totalYearsFactorMap.get( publicationCluster.getLanguage() );
				wordFreqInterestProfile.doWordFreqCalculation( authorInterest, newInterests, publicationCluster, yearFactor, totalYearFactor, numberOfExtractionService );
			}
			// Put other default interest profiles
			else if ( interestProfileDefault.getName().toLowerCase().equals( "lda" ) )
			{

			}

			// at the end persist new interests
			// for ( Interest newInterest : newInterests )
			// persistenceStrategy.getInterestDAO().persist( newInterest );


			// check author interest calculation result
			if ( authorInterest.getTermWeights() != null && !authorInterest.getTermWeights().isEmpty() )
			{
				authorInterest.setAuthorInterestProfile( authorInterestProfile );
				authorInterestProfile.addAuthorInterest( authorInterest );
				authorInterestProfile.setInterestProfile( interestProfileDefault );
				//persistenceStrategy.getAuthorInterestProfileDAO().persist( authorInterestProfile );
			}
		}


		// at the end persist
		if ( authorInterestProfile.getAuthorInterests() != null && !authorInterestProfile.getAuthorInterests().isEmpty() )
		{
			authorInterestProfile.setAuthor( author );
			author.addAuthorInterestProfiles( authorInterestProfile );
			persistenceStrategy.getAuthorDAO().persist( author );
		}
	}

	/**
	 * Create a cluster for publications, based on language and year
	 * 
	 * @param author
	 * @param publicationClustersMap
	 */
	public void constructPublicationClusterByLanguageAndYear( Author author, Map<String, PublicationClusterHelper> publicationClustersMap )
	{
		// fill publication clusters
		// prepare calendar for publication year
		Calendar calendar = Calendar.getInstance();
		// get all publications from specific author and put it into cluster
		for ( Publication publication : author.getPublications() )
		{
			// only proceed publication that have date, language and abstract
			if ( publication.getAbstractText() == null || publication.getAbstractText().equals( "" ) )
				continue;
			if ( publication.getPublicationDate() == null )
				continue;
			if ( publication.getLanguage() == null )
				continue;

			// get publication year
			calendar.setTime( publication.getPublicationDate() );

			// construct clusterMap key
			String clusterMapKey = publication.getLanguage() + calendar.get( Calendar.YEAR );

			// construct publication map
			if ( publicationClustersMap.get( clusterMapKey ) == null )
			{
				// not exist create new cluster
				PublicationClusterHelper publicationCluster = new PublicationClusterHelper();
				publicationCluster.setLangauge( publication.getLanguage() );
				publicationCluster.setYear( calendar.get( Calendar.YEAR ) );
				publicationCluster.addPublicationAndUpdate( publication );

				// add into map
				publicationClustersMap.put( clusterMapKey, publicationCluster );

			}
			else
			{
				// exist on map, get the cluster
				PublicationClusterHelper publicationCluster = publicationClustersMap.get( clusterMapKey );
				publicationCluster.addPublicationAndUpdate( publication );
			}

		}
	}


	/**
	 * Collect the author interest result as JSON object
	 * 
	 * @param author
	 * @param responseMap
	 * @return
	 */
	private Map<String, Object> getInterestFromDatabase( Author author, Map<String, Object> responseMap )
	{
		// get available year
		List<String> years = persistenceStrategy.getPublicationDAO().getDistinctPublicationYearByAuthor( author, "ASC" );

		List<AuthorInterestProfile> authorInterestProfiles = new ArrayList<AuthorInterestProfile>();
		authorInterestProfiles.addAll( author.getAuthorInterestProfiles() );
		// sort based on profile length ( currently there is no attribute to
		// store position)
		Collections.sort( authorInterestProfiles, new AuthorInterestProfileByProfileNameLengthComparator() );

		// the whole result related to interest
		List<Object> authorInterestResult = new ArrayList<Object>();

		for ( AuthorInterestProfile authorInterestProfile : authorInterestProfiles )
		{
			// put profile on map
			Map<String, Object> authorInterestResultProfilesMap = new HashMap<String, Object>();

			// get interest profile name and description
			String interestProfileName = authorInterestProfile.getName();
			String interestProfileDescription = authorInterestProfile.getDescription();

			// get authorInterest set on profile
			Set<AuthorInterest> authorInterests = authorInterestProfile.getAuthorInterests();

			// if profile contain no authorInterest just skip
			if ( authorInterests == null || authorInterests.isEmpty() )
				continue;

			// a map for storing authorInterst based on language
			Map<String, List<AuthorInterest>> authorInterestLanguageMap = new HashMap<String, List<AuthorInterest>>();

			// split authorinterest based on language and put it on the map
			for ( AuthorInterest authorInterest : authorInterests )
			{
				if ( authorInterestLanguageMap.get( authorInterest.getLanguage() ) != null )
				{
					authorInterestLanguageMap.get( authorInterest.getLanguage() ).add( authorInterest );
				}
				else
				{
					List<AuthorInterest> authorInterestList = new ArrayList<AuthorInterest>();
					authorInterestList.add( authorInterest );
					authorInterestLanguageMap.put( authorInterest.getLanguage(), authorInterestList );
				}
			}

			// prepare calendar for extractind year from date
			Calendar calendar = Calendar.getInstance();

			// result author interest based on language
			List<Object> authorInterestResultLanguageList = new ArrayList<Object>();

			// sort authorinterest based on year
			for ( Map.Entry<String, List<AuthorInterest>> authorInterestLanguageIterator : authorInterestLanguageMap.entrySet() )
			{
				// result container
				Map<String, Object> authorInterestResultLanguageMap = new LinkedHashMap<String, Object>();
				// hashmap value
				String interestLanguage = authorInterestLanguageIterator.getKey();
				List<AuthorInterest> interestList = authorInterestLanguageIterator.getValue();

				// sort based on year
				Collections.sort( interestList, new AuthorInterestByDateComparator() );

				// term values based on year result container
				List<Object> authorInterestResultYearList = new ArrayList<Object>();

				// get interest year, term and value
				int indexYear = 0;
				boolean increaseIndex = true;
				for ( AuthorInterest authorInterest : interestList )
				{
					increaseIndex = true;
					// just skip if contain no term weights
					if ( authorInterest.getTermWeights() == null || authorInterest.getTermWeights().isEmpty() )
						continue;


					// get year
					calendar.setTime( authorInterest.getYear() );
					String year = Integer.toString( calendar.get( Calendar.YEAR ) );

					while ( !years.get( indexYear ).equals( year ) )
					{

						// empty result
						Map<String, Object> authorInterestResultYearMap = new LinkedHashMap<String, Object>();

						authorInterestResultYearMap.put( "year", years.get( indexYear ) );
						authorInterestResultYearMap.put( "termvalue", Collections.emptyList() );
						indexYear++;
						increaseIndex = false;

						// remove duplicated year
						if ( !authorInterestResultYearList.isEmpty() )
						{
							@SuppressWarnings( "unchecked" )
							Map<String, Object> prevAuthorInterestResultYearMap = (Map<String, Object>) authorInterestResultYearList.get( authorInterestResultYearList.size() - 1 );
							if ( prevAuthorInterestResultYearMap.get( "year" ).equals( years.get( indexYear - 1 ) ) )
								continue;
						}
						authorInterestResultYearList.add( authorInterestResultYearMap );

					}

					List<Object> termValueResult = new ArrayList<Object>();

					// put term and value
					for ( Map.Entry<Interest, Double> termWeightMap : authorInterest.getTermWeights().entrySet() )
					{
						// just remove not significant value
						if ( termWeightMap.getValue() < 0.4 )
							continue;
							
						List<Object> termWeightObjects = new ArrayList<Object>();
						termWeightObjects.add( termWeightMap.getKey().getId() );
						termWeightObjects.add( termWeightMap.getKey().getTerm() );
						termWeightObjects.add( termWeightMap.getValue() );
						termValueResult.add( termWeightObjects );
					}

					// result container
					Map<String, Object> authorInterestResultYearMap = new LinkedHashMap<String, Object>();

					authorInterestResultYearMap.put( "year", year );
					authorInterestResultYearMap.put( "termvalue", termValueResult );
					authorInterestResultYearList.add( authorInterestResultYearMap );
					if ( increaseIndex )
						indexYear++;
				}

				// continue interest year which is missing
				for ( int i = indexYear + 1; i < years.size(); i++ )
				{
					Map<String, Object> authorInterestResultYearMap = new LinkedHashMap<String, Object>();

					authorInterestResultYearMap.put( "year", years.get( i ) );
					authorInterestResultYearMap.put( "termvalue", Collections.emptyList() );
					authorInterestResultYearList.add( authorInterestResultYearMap );
				}

				authorInterestResultLanguageMap.put( "language", interestLanguage );
				authorInterestResultLanguageMap.put( "interestyears", authorInterestResultYearList );
				if ( interestLanguage.equals( "english" ) )
					authorInterestResultLanguageList.add( 0, authorInterestResultLanguageMap );
				else
					authorInterestResultLanguageList.add( authorInterestResultLanguageMap );
			}

			// put profile map
			authorInterestResultProfilesMap.put( "profile", interestProfileName );
			authorInterestResultProfilesMap.put( "description", interestProfileDescription );
			authorInterestResultProfilesMap.put( "interestlanguages", authorInterestResultLanguageList );
			authorInterestResult.add( authorInterestResultProfilesMap );
		}

		responseMap.put( "interest", authorInterestResult );

		// put also publication

		return responseMap;
	}

}

package de.rwth.i9.palm.interestmining.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.rwth.i9.palm.helper.comparator.CircleInterestByDateComparator;
import de.rwth.i9.palm.helper.comparator.CircleInterestProfileByProfileNameLengthComparator;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.CircleInterest;
import de.rwth.i9.palm.model.CircleInterestProfile;
import de.rwth.i9.palm.model.Interest;
import de.rwth.i9.palm.model.InterestProfileCircle;
import de.rwth.i9.palm.model.InterestProfileType;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;

@Service
public class CircleInterestMiningService
{

	private final static Logger logger = LoggerFactory.getLogger( CircleInterestMiningService.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private CValueCircleInterestProfile cValueCircleInterestProfile;

	@Autowired
	private CorePhraseCircleInterestProfile corePhraseCircleInterestProfile;

	@Autowired
	private WordFreqCircleInterestProfile wordFreqCircleInterestProfile;

	/**
	 * Get circle interests from active circle profiles
	 * 
	 * @param responseMap
	 * @param circle
	 * @param updateCircleInterest
	 * @return
	 * @throws ParseException
	 */
	public Map<String, Object> getInterestFromCircle( Map<String, Object> responseMap, Circle circle, boolean updateCircleInterest ) throws ParseException
	{
		logger.info( "start circle mining interest " );
		// get default interest profile
		List<InterestProfileCircle> interestProfilesDefault = persistenceStrategy.getInterestProfileCircleDAO().getAllActiveInterestProfile( InterestProfileType.DEFAULT );

		// get default interest profile
		List<InterestProfileCircle> interestProfilesDerived = persistenceStrategy.getInterestProfileCircleDAO().getAllActiveInterestProfile( InterestProfileType.DERIVED );

		if ( interestProfilesDefault.isEmpty() && interestProfilesDerived.isEmpty() )
		{
			logger.warn( "No active interest profile found" );
			return responseMap;
		}

		if ( circle.getPublications() == null || circle.getPublications().isEmpty() )
		{
			logger.warn( "No publication found" );
			return responseMap;
		}

		// update for all circle interest profile
		// updateCircleInterest = true;
		if ( !updateCircleInterest )
		{
			// get interest profile from circle
			Set<CircleInterestProfile> circleInterestProfiles = circle.getCircleInterestProfiles();
			if ( circleInterestProfiles != null && !circleInterestProfiles.isEmpty() )
			{
				// check for missing default interest profile in circle
				// only calculate missing one
				for ( Iterator<InterestProfileCircle> interestProfileIterator = interestProfilesDefault.iterator(); interestProfileIterator.hasNext(); )
				{
					InterestProfileCircle interestProfileDefault = interestProfileIterator.next();
					for ( CircleInterestProfile circleInterestProfile : circleInterestProfiles )
					{
						if ( circleInterestProfile.getInterestProfileCircle() != null && circleInterestProfile.getInterestProfileCircle().equals( interestProfileDefault ) )
						{
							interestProfileIterator.remove();
							break;
						}
					}
				}

				// check for missing derivative interest profile
				for ( Iterator<InterestProfileCircle> interestProfileIterator = interestProfilesDerived.iterator(); interestProfileIterator.hasNext(); )
				{
					InterestProfileCircle interestProfileDerived = interestProfileIterator.next();
					for ( CircleInterestProfile circleInterestProfile : circleInterestProfiles )
					{
						if ( circleInterestProfile.getInterestProfileCircle() != null && circleInterestProfile.getInterestProfileCircle().equals( interestProfileDerived ) )
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
			if ( circle.getCircleInterestProfiles() != null && !circle.getCircleInterestProfiles().isEmpty() )
			{
				circle.getCircleInterestProfiles().clear();
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
			constructPublicationClusterByLanguageAndYear( circle, publicationClustersMap );
			// may be you have to turn off the connection with team viewer and
			// share only screens in skype No no its ok
			// cluster is ready
			if ( !publicationClustersMap.isEmpty() )
			{
				// calculate default interest profile
				calculateInterestProfilesDefault( circle, publicationClustersMap, interestProfilesDefault );
			}
		}

		// check for derived interest profile
		if ( !interestProfilesDerived.isEmpty() )
		{
			// calculate derived interest profile
			calculateInterestProfilesDerived( circle, interestProfilesDerived );
		}

		// get and put circle interest profile into map or list
		getInterestFromDatabase( circle, responseMap );

		return responseMap;
	}

	/**
	 * Calculated derived interest profile (Intersection and/or Union between
	 * interest profile) in an circle
	 * 
	 * @param circle
	 * @param interestProfilesDerived
	 */
	private void calculateInterestProfilesDerived( Circle circle, List<InterestProfileCircle> interestProfilesDerived )
	{
		// get circleInterest set on profile
		for ( InterestProfileCircle interestProfileDerived : interestProfilesDerived )
		{

			String[] derivedInterestProfileName = interestProfileDerived.getName().split( "\\s+" );

			// at list profile name has three segment
			if ( derivedInterestProfileName.length < 3 )
				continue;

			// prepare variables
			CircleInterestProfile circleInterestProfile1 = null;
			CircleInterestProfile circleInterestProfile2 = null;
			CircleInterestProfile circleInterestProfileResult = null;
			String operationType = null;

			for ( String partOfProfileName : derivedInterestProfileName )
			{
				// ? sometimes problem on encoding
				if ( partOfProfileName.equals( "∩" ) || partOfProfileName.equals( "?" ) || partOfProfileName.equals( "+" ) || partOfProfileName.equals( "∪" ) )
				{
					if ( circleInterestProfileResult != null )
					{
						circleInterestProfile1 = circleInterestProfileResult;
						circleInterestProfileResult = null;
					}
					if ( partOfProfileName.equals( "∩" ) || partOfProfileName.equals( "?" ) || partOfProfileName.equals( "+" ) )
						operationType = "INTERSECTION";
					else
						operationType = "UNION";
				}
				else
				{
					if ( circleInterestProfile1 == null )
					{
						circleInterestProfile1 = circle.getSpecificCircleInterestProfile( partOfProfileName );

						if ( circleInterestProfile1 == null )
						{
							logger.error( "CircleInterestProfile " + partOfProfileName + " not found" );
							// continue to next derived circle profile, if exist
							break;
						}
					}
					else
					{
						circleInterestProfile2 = circle.getSpecificCircleInterestProfile( partOfProfileName );

						if ( circleInterestProfile2 == null )
						{
							logger.error( "CircleInterestProfile " + partOfProfileName + " not found" );
							// continue to next derived circle profile, if exist
							break;
						}
					}

					// calculate and persist
					if ( circleInterestProfile1 != null && circleInterestProfile2 != null && operationType != null )
					{
						if ( operationType.equals( "INTERSECTION" ) )
							circleInterestProfileResult = calculateIntersectionOfCircleInterestProfiles( circleInterestProfile1, circleInterestProfile2, interestProfileDerived );
						else
							circleInterestProfileResult = calculateUnionOfCircleInterestProfiles( circleInterestProfile1, circleInterestProfile2, interestProfileDerived );
					}
				}
			}
			// persist result
			if ( circleInterestProfileResult != null && ( circleInterestProfileResult.getCircleInterests() != null && !circleInterestProfileResult.getCircleInterests().isEmpty() ) )
			{
				circleInterestProfileResult.setCircle( circle );
				circle.addCircleInterestProfiles( circleInterestProfileResult );
				persistenceStrategy.getCircleDAO().persist( circle );

				persistenceStrategy.getCircleInterestProfileDAO().persist( circleInterestProfileResult );
			}

		}

	}

	/**
	 * Calculate Union interest between 2 Circle interest profiles
	 * 
	 * @param circleInterestProfile1
	 * @param circleInterestProfile2
	 * @param interestProfileDerived
	 * @return
	 */
	private CircleInterestProfile calculateUnionOfCircleInterestProfiles( CircleInterestProfile circleInterestProfile1, CircleInterestProfile circleInterestProfile2, InterestProfileCircle interestProfileDerived )
	{
		Calendar calendar = Calendar.getInstance();
		CircleInterestProfile circleInterestProfileResult = new CircleInterestProfile();
		// set derived profile name
		String circleInterestProfileName = circleInterestProfile1.getName() + " ∪ " + circleInterestProfile2.getName();

		circleInterestProfileResult.setCreated( calendar.getTime() );
		circleInterestProfileResult.setName( circleInterestProfileName );
		circleInterestProfileResult.setDescription( "Interest mining using " + circleInterestProfileName + " algorithm" );

		Set<CircleInterest> circleInterests1 = circleInterestProfile1.getCircleInterests();
		Set<CircleInterest> circleInterests2 = circleInterestProfile2.getCircleInterests();

		for ( CircleInterest eachCircleInterest1 : circleInterests1 )
		{
			CircleInterest circleInterestResult = null;
			for ( CircleInterest eachCircleInterest2 : circleInterests2 )
			{
				if ( eachCircleInterest1.getLanguage().equals( eachCircleInterest2.getLanguage() ) && eachCircleInterest1.getYear().equals( eachCircleInterest2.getYear() ) )
				{
					circleInterestResult = calculateUnionOfCircleInterest( eachCircleInterest1, eachCircleInterest2 );
				}
			}

			if ( circleInterestResult != null && circleInterestResult.getTermWeights() != null && !circleInterestResult.getTermWeights().isEmpty() )
			{
				circleInterestResult.setCircleInterestProfile( circleInterestProfileResult );
				circleInterestProfileResult.addCircleInterest( circleInterestResult );
				circleInterestProfileResult.setInterestProfileCircle( interestProfileDerived );
			}
		}

		return circleInterestProfileResult;
	}

	/**
	 * Calculate Union interest between 2 CircleInterest
	 * 
	 * @param eachCircleInterest1
	 * @param eachCircleInterest2
	 * @return
	 */
	private CircleInterest calculateUnionOfCircleInterest( CircleInterest eachCircleInterest1, CircleInterest eachCircleInterest2 )
	{
		CircleInterest circleInterestResult = new CircleInterest();
		circleInterestResult.setLanguage( eachCircleInterest1.getLanguage() );
		circleInterestResult.setYear( eachCircleInterest1.getYear() );

		Map<Interest, Double> termsWeight1 = eachCircleInterest1.getTermWeights();
		Map<Interest, Double> termsWeight2 = eachCircleInterest2.getTermWeights();

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
			circleInterestResult.setTermWeights( termsWeightResult );

		return circleInterestResult;
	}

	/**
	 * Calculate Intersection interest between 2 Circle interest profiles
	 * 
	 * @param circleInterestProfile1
	 * @param circleInterestProfile2
	 * @param interestProfileDerived
	 * @return
	 */
	private CircleInterestProfile calculateIntersectionOfCircleInterestProfiles( CircleInterestProfile circleInterestProfile1, CircleInterestProfile circleInterestProfile2, InterestProfileCircle interestProfileDerived )
	{
		Calendar calendar = Calendar.getInstance();
		CircleInterestProfile circleInterestProfileResult = new CircleInterestProfile();
		// set derived profile name
		String circleInterestProfileName = circleInterestProfile1.getName() + " ∩ " + circleInterestProfile2.getName();

		circleInterestProfileResult.setCreated( calendar.getTime() );
		circleInterestProfileResult.setName( circleInterestProfileName );
		circleInterestProfileResult.setDescription( "Interest mining using " + circleInterestProfileName + " algorithm" );

		Set<CircleInterest> circleInterests1 = circleInterestProfile1.getCircleInterests();
		Set<CircleInterest> circleInterests2 = circleInterestProfile2.getCircleInterests();

		for ( CircleInterest eachCircleInterest1 : circleInterests1 )
		{
			CircleInterest circleInterestResult = null;
			for ( CircleInterest eachCircleInterest2 : circleInterests2 )
			{
				if ( eachCircleInterest1.getLanguage().equals( eachCircleInterest2.getLanguage() ) && eachCircleInterest1.getYear().equals( eachCircleInterest2.getYear() ) )
				{
					circleInterestResult = calculateIntersectionOfCircleInterest( eachCircleInterest1, eachCircleInterest2 );
				}
			}

			if ( circleInterestResult != null && circleInterestResult.getTermWeights() != null && !circleInterestResult.getTermWeights().isEmpty() )
			{
				circleInterestResult.setCircleInterestProfile( circleInterestProfileResult );
				circleInterestProfileResult.addCircleInterest( circleInterestResult );
				circleInterestProfileResult.setInterestProfileCircle( interestProfileDerived );
			}
		}

		return circleInterestProfileResult;
	}

	/**
	 * Calculate Intersection interest between 2 CircleInterest
	 * 
	 * @param eachCircleInterest1
	 * @param eachCircleInterest2
	 * @return
	 */
	private CircleInterest calculateIntersectionOfCircleInterest( CircleInterest eachCircleInterest1, CircleInterest eachCircleInterest2 )
	{
		CircleInterest circleInterestResult = new CircleInterest();
		circleInterestResult.setLanguage( eachCircleInterest1.getLanguage() );
		circleInterestResult.setYear( eachCircleInterest1.getYear() );

		Map<Interest, Double> termsWeight1 = eachCircleInterest1.getTermWeights();
		Map<Interest, Double> termsWeight2 = eachCircleInterest2.getTermWeights();
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
			circleInterestResult.setTermWeights( termsWeightResult );

		return circleInterestResult;
	}

	/**
	 * Main method to calculate default InterestProfile such as : C-Value,
	 * Corephrase and WordFreq profile
	 * 
	 * @param circle
	 * @param publicationClustersMap
	 * @param interestProfilesDefault
	 */
	public void calculateInterestProfilesDefault( Circle circle, Map<String, PublicationClusterHelper> publicationClustersMap, List<InterestProfileCircle> interestProfilesDefault )
	{
		// calculate frequencies of term in cluster
		for ( Map.Entry<String, PublicationClusterHelper> publicationClusterEntry : publicationClustersMap.entrySet() )
			publicationClusterEntry.getValue().calculateTermProperties();

		// loop through all interest profiles default
		for ( InterestProfileCircle interestProfileDefault : interestProfilesDefault )
			calculateEachInterestProfileDefault( circle, interestProfileDefault, publicationClustersMap );
	}

	/**
	 * Calculate each default InterestProfile
	 * 
	 * @param circle
	 * @param interestProfileDefault
	 * @param publicationClustersMap
	 */
	public void calculateEachInterestProfileDefault( Circle circle, InterestProfileCircle interestProfileDefault, Map<String, PublicationClusterHelper> publicationClustersMap )
	{
		// get circle interest profile
		Calendar calendar = Calendar.getInstance();
		// default profile name [DEFAULT_PROFILENAME]
		String circleInterestProfileName = interestProfileDefault.getName();

		// create new circle interest profile for c-value
		CircleInterestProfile circleInterestProfile = new CircleInterestProfile();
		circleInterestProfile.setCreated( calendar.getTime() );
		circleInterestProfile.setDescription( "Interest mining using " + interestProfileDefault.getName() + " algorithm" );
		circleInterestProfile.setName( circleInterestProfileName );
		
		// CorePhrase and WordFreq specific, according to Svetoslav Evtimov thesis
		// yearFactor Map format Map< Language-Year , value >
		// totalYearsFactor Map< Language, value >
		
		Map<String, Double> yearFactorMap = new HashMap<String, Double>();
		Map<String, Double> totalYearsFactorMap = new HashMap<String, Double>();
		
		// calculate some weighting factors
		if ( interestProfileDefault.getName().toLowerCase().equals( "corephrase" ) ||
				interestProfileDefault.getName().toLowerCase().equals( "wordfreq" )	)
		{
			yearFactorMap = CorePhraseAndWordFreqHelper.calculateYearFactor( publicationClustersMap, 0.25 );
			totalYearsFactorMap = CorePhraseAndWordFreqHelper.calculateTotalYearsFactor( publicationClustersMap );
		}

		// get the number of active extraction services
		int numberOfExtractionService = applicationService.getExtractionServices().size();

		// loop to each cluster and calculate default profiles
		for ( Map.Entry<String, PublicationClusterHelper> publicationClusterEntry : publicationClustersMap.entrySet() )
		{
			PublicationClusterHelper publicationCluster = publicationClusterEntry.getValue();

			if ( publicationCluster.getTermMap() == null || publicationCluster.getTermMap().isEmpty() )
				continue;

			// prepare variables
			CircleInterest circleInterest = new CircleInterest();

			// assign circle interest method
			if ( interestProfileDefault.getName().toLowerCase().equals( "cvalue" ) )
			{
				cValueCircleInterestProfile.doCValueCalculation( circleInterest, publicationCluster, numberOfExtractionService );
			}
			else if ( interestProfileDefault.getName().toLowerCase().equals( "corephrase" ) )
			{
				Double yearFactor = yearFactorMap.get( publicationCluster.getLanguage() + publicationCluster.getYear() );
				Double totalYearFactor = totalYearsFactorMap.get( publicationCluster.getLanguage() );
				corePhraseCircleInterestProfile.doCorePhraseCalculation( circleInterest, publicationCluster, yearFactor, totalYearFactor, numberOfExtractionService );
			}
			else if ( interestProfileDefault.getName().toLowerCase().equals( "wordfreq" ) )
			{
				Double yearFactor = yearFactorMap.get( publicationCluster.getLanguage() + publicationCluster.getYear() );
				Double totalYearFactor = totalYearsFactorMap.get( publicationCluster.getLanguage() );
				wordFreqCircleInterestProfile.doWordFreqCalculation( circleInterest, publicationCluster, yearFactor, totalYearFactor, numberOfExtractionService );
			}
			// Put other default interest profiles
			else if ( interestProfileDefault.getName().toLowerCase().equals( "lda" ) )
			{

			}

			// check circle interest calculation result
			if ( circleInterest.getTermWeights() != null && !circleInterest.getTermWeights().isEmpty() )
			{
				circleInterest.setCircleInterestProfile( circleInterestProfile );
				circleInterestProfile.addCircleInterest( circleInterest );
				circleInterestProfile.setInterestProfileCircle( interestProfileDefault );
			}
		}

		// at the end persist
		if ( circleInterestProfile.getCircleInterests() != null && !circleInterestProfile.getCircleInterests().isEmpty() )
		{
			circleInterestProfile.setCircle( circle );
			circle.addCircleInterestProfiles( circleInterestProfile );
			persistenceStrategy.getCircleDAO().persist( circle );
		}
	}

	/**
	 * Create a cluster for publications, based on language and year
	 * 
	 * @param circle
	 * @param publicationClustersMap
	 */
	public void constructPublicationClusterByLanguageAndYear( Circle circle, Map<String, PublicationClusterHelper> publicationClustersMap )
	{
		// fill publication clusters
		// prepare calendar for publication year
		Calendar calendar = Calendar.getInstance();
		// get all publications from specific circle and put it into cluster
		for ( Publication publication : circle.getPublications() )
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
	 * Collect the circle interest result as JSON object
	 * 
	 * @param circle
	 * @param responseMap
	 * @return
	 */
	private Map<String, Object> getInterestFromDatabase( Circle circle, Map<String, Object> responseMap )
	{
		// get available year
		List<String> years = persistenceStrategy.getPublicationDAO().getDistinctPublicationYearByCircle( circle, "ASC" );

		List<CircleInterestProfile> circleInterestProfiles = new ArrayList<CircleInterestProfile>();
		circleInterestProfiles.addAll( circle.getCircleInterestProfiles() );
		// sort based on profile length ( currently there is no attribute to
		// store position)
		Collections.sort( circleInterestProfiles, new CircleInterestProfileByProfileNameLengthComparator() );

		// the whole result related to interest
		List<Object> circleInterestResult = new ArrayList<Object>();

		for ( CircleInterestProfile circleInterestProfile : circleInterestProfiles )
		{
			// put profile on map
			Map<String, Object> circleInterestResultProfilesMap = new HashMap<String, Object>();

			// get interest profile name and description
			String interestProfileName = circleInterestProfile.getName();
			String interestProfileDescription = circleInterestProfile.getDescription();

			// get circleInterest set on profile
			Set<CircleInterest> circleInterests = circleInterestProfile.getCircleInterests();

			// if profile contain no circleInterest just skip
			if ( circleInterests == null || circleInterests.isEmpty() )
				continue;

			// a map for storing circleInterst based on language
			Map<String, List<CircleInterest>> circleInterestLanguageMap = new HashMap<String, List<CircleInterest>>();

			// split circleinterest based on language and put it on the map
			for ( CircleInterest circleInterest : circleInterests )
			{
				if ( circleInterestLanguageMap.get( circleInterest.getLanguage() ) != null )
				{
					circleInterestLanguageMap.get( circleInterest.getLanguage() ).add( circleInterest );
				}
				else
				{
					List<CircleInterest> circleInterestList = new ArrayList<CircleInterest>();
					circleInterestList.add( circleInterest );
					circleInterestLanguageMap.put( circleInterest.getLanguage(), circleInterestList );
				}
			}

			// prepare calendar for extractind year from date
			Calendar calendar = Calendar.getInstance();

			// result circle interest based on language
			List<Object> circleInterestResultLanguageList = new ArrayList<Object>();

			// sort circleinterest based on year
			for ( Map.Entry<String, List<CircleInterest>> circleInterestLanguageIterator : circleInterestLanguageMap.entrySet() )
			{
				// result container
				Map<String, Object> circleInterestResultLanguageMap = new LinkedHashMap<String, Object>();
				// hashmap value
				String interestLanguage = circleInterestLanguageIterator.getKey();
				List<CircleInterest> interestList = circleInterestLanguageIterator.getValue();

				// sort based on year
				Collections.sort( interestList, new CircleInterestByDateComparator() );

				// term values based on year result container
				List<Object> circleInterestResultYearList = new ArrayList<Object>();

				// get interest year, term and value
				int indexYear = 0;
				boolean increaseIndex = true;
				for ( CircleInterest circleInterest : interestList )
				{
					if ( circleInterest.getTermWeights() == null || circleInterest.getTermWeights().isEmpty() )
						continue;

					// get year
					calendar.setTime( circleInterest.getYear() );
					String year = Integer.toString( calendar.get( Calendar.YEAR ) );

					while ( !years.get( indexYear ).equals( year ) )
					{

						// empty result
						Map<String, Object> circleInterestResultYearMap = new LinkedHashMap<String, Object>();

						circleInterestResultYearMap.put( "year", years.get( indexYear ) );
						circleInterestResultYearMap.put( "termvalue", Collections.emptyList() );
						indexYear++;
						increaseIndex = false;

						// remove duplicated year
						if ( !circleInterestResultYearList.isEmpty() )
						{
							@SuppressWarnings( "unchecked" )
							Map<String, Object> prevAuthorInterestResultYearMap = (Map<String, Object>) circleInterestResultYearList.get( circleInterestResultYearList.size() - 1 );
							if ( prevAuthorInterestResultYearMap.get( "year" ).equals( years.get( indexYear - 1 ) ) )
								continue;
						}
						circleInterestResultYearList.add( circleInterestResultYearMap );

					}

					List<Object> termValueResult = new ArrayList<Object>();

					// put term and value
					for ( Map.Entry<Interest, Double> termWeightMap : circleInterest.getTermWeights().entrySet() )
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
					Map<String, Object> circleInterestResultYearMap = new LinkedHashMap<String, Object>();

					circleInterestResultYearMap.put( "year", year );
					circleInterestResultYearMap.put( "termvalue", termValueResult );
					circleInterestResultYearList.add( circleInterestResultYearMap );
					if ( increaseIndex )
						indexYear++;
				}

				// continue interest year which is missing
				for ( int i = indexYear + 1; i < years.size(); i++ )
				{
					Map<String, Object> circleInterestResultYearMap = new LinkedHashMap<String, Object>();

					circleInterestResultYearMap.put( "year", years.get( i ) );
					circleInterestResultYearMap.put( "termvalue", Collections.emptyList() );
					circleInterestResultYearList.add( circleInterestResultYearMap );
				}

				circleInterestResultLanguageMap.put( "language", interestLanguage );
				circleInterestResultLanguageMap.put( "interestyears", circleInterestResultYearList );
				if ( interestLanguage.equals( "english" ) )
					circleInterestResultLanguageList.add( 0, circleInterestResultLanguageMap );
				else
					circleInterestResultLanguageList.add( circleInterestResultLanguageMap );
			}

			// put profile map
			circleInterestResultProfilesMap.put( "profile", interestProfileName );
			circleInterestResultProfilesMap.put( "description", interestProfileDescription );
			circleInterestResultProfilesMap.put( "interestlanguages", circleInterestResultLanguageList );
			circleInterestResult.add( circleInterestResultProfilesMap );
		}

		responseMap.put( "interest", circleInterestResult );

		// put also publication

		return responseMap;
	}

}

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

import de.rwth.i9.palm.helper.comparator.EventInterestByDateComparator;
import de.rwth.i9.palm.helper.comparator.EventInterestProfileByProfileNameLengthComparator;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.EventInterest;
import de.rwth.i9.palm.model.EventInterestProfile;
import de.rwth.i9.palm.model.Interest;
import de.rwth.i9.palm.model.InterestProfileEvent;
import de.rwth.i9.palm.model.InterestProfileType;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;

@Service
public class EventInterestMiningService
{

	private final static Logger logger = LoggerFactory.getLogger( EventInterestMiningService.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private CValueEventInterestProfile cValueEventInterestProfile;

	@Autowired
	private CorePhraseEventInterestProfile corePhraseEventInterestProfile;

	@Autowired
	private WordFreqEventInterestProfile wordFreqEventInterestProfile;

	/**
	 * Get event interests from active event profiles
	 * 
	 * @param responseMap
	 * @param event
	 * @param updateEventInterest
	 * @return
	 * @throws ParseException
	 */
	public Map<String, Object> getInterestFromEvent( Map<String, Object> responseMap, Event event, boolean updateEventInterest ) throws ParseException
	{
		logger.info( "start event mining interest " );
		// get default interest profile
		List<InterestProfileEvent> interestProfilesDefault = persistenceStrategy.getInterestProfileEventDAO().getAllActiveInterestProfile( InterestProfileType.DEFAULT );

		// get default interest profile
		List<InterestProfileEvent> interestProfilesDerived = persistenceStrategy.getInterestProfileEventDAO().getAllActiveInterestProfile( InterestProfileType.DERIVED );

		if ( interestProfilesDefault.isEmpty() && interestProfilesDerived.isEmpty() )
		{
			logger.warn( "No active interest profile found" );
			return responseMap;
		}

		if ( event.getPublications() == null || event.getPublications().isEmpty() )
		{
			logger.warn( "No publication found" );
			return responseMap;
		}

		// update for all event interest profile
		// updateEventInterest = true;
		if ( !updateEventInterest )
		{
			// get interest profile from event
			Set<EventInterestProfile> eventInterestProfiles = event.getEventInterestProfiles();
			if ( eventInterestProfiles != null && !eventInterestProfiles.isEmpty() )
			{
				// check for missing default interest profile in event
				// only calculate missing one
				for ( Iterator<InterestProfileEvent> interestProfileIterator = interestProfilesDefault.iterator(); interestProfileIterator.hasNext(); )
				{
					InterestProfileEvent interestProfileDefault = interestProfileIterator.next();
					for ( EventInterestProfile eventInterestProfile : eventInterestProfiles )
					{
						if ( eventInterestProfile.getInterestProfileEvent() != null && eventInterestProfile.getInterestProfileEvent().equals( interestProfileDefault ) )
						{
							interestProfileIterator.remove();
							break;
						}
					}
				}

				// check for missing derivative interest profile
				for ( Iterator<InterestProfileEvent> interestProfileIterator = interestProfilesDerived.iterator(); interestProfileIterator.hasNext(); )
				{
					InterestProfileEvent interestProfileDerived = interestProfileIterator.next();
					for ( EventInterestProfile eventInterestProfile : eventInterestProfiles )
					{
						if ( eventInterestProfile.getInterestProfileEvent() != null && eventInterestProfile.getInterestProfileEvent().equals( interestProfileDerived ) )
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
			if ( event.getEventInterestProfiles() != null && !event.getEventInterestProfiles().isEmpty() )
			{
				event.getEventInterestProfiles().clear();
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
			constructPublicationClusterByLanguageAndYear( event, publicationClustersMap );
			// may be you have to turn off the connection with team viewer and
			// share only screens in skype No no its ok
			// cluster is ready
			if ( !publicationClustersMap.isEmpty() )
			{
				// calculate default interest profile
				calculateInterestProfilesDefault( event, publicationClustersMap, interestProfilesDefault );
			}
		}

		// check for derived interest profile
		if ( !interestProfilesDerived.isEmpty() )
		{
			// calculate derived interest profile
			calculateInterestProfilesDerived( event, interestProfilesDerived );
		}

		// get and put event interest profile into map or list
		getInterestFromDatabase( event, responseMap );

		return responseMap;
	}

	/**
	 * Calculated derived interest profile (Intersection and/or Union between
	 * interest profile) in an event
	 * 
	 * @param event
	 * @param interestProfilesDerived
	 */
	private void calculateInterestProfilesDerived( Event event, List<InterestProfileEvent> interestProfilesDerived )
	{
		// get eventInterest set on profile
		for ( InterestProfileEvent interestProfileDerived : interestProfilesDerived )
		{

			String[] derivedInterestProfileName = interestProfileDerived.getName().split( "\\s+" );

			// at list profile name has three segment
			if ( derivedInterestProfileName.length < 3 )
				continue;

			// prepare variables
			EventInterestProfile eventInterestProfile1 = null;
			EventInterestProfile eventInterestProfile2 = null;
			EventInterestProfile eventInterestProfileResult = null;
			String operationType = null;

			for ( String partOfProfileName : derivedInterestProfileName )
			{
				// ? sometimes problem on encoding
				if ( partOfProfileName.equals( "∩" ) || partOfProfileName.equals( "?" ) || partOfProfileName.equals( "+" ) || partOfProfileName.equals( "∪" ) )
				{
					if ( eventInterestProfileResult != null )
					{
						eventInterestProfile1 = eventInterestProfileResult;
						eventInterestProfileResult = null;
					}
					if ( partOfProfileName.equals( "∩" ) || partOfProfileName.equals( "?" ) || partOfProfileName.equals( "+" ) )
						operationType = "INTERSECTION";
					else
						operationType = "UNION";
				}
				else
				{
					if ( eventInterestProfile1 == null )
					{
						eventInterestProfile1 = event.getSpecificEventInterestProfile( partOfProfileName );

						if ( eventInterestProfile1 == null )
						{
							logger.error( "EventInterestProfile " + partOfProfileName + " not found" );
							// continue to next derived event profile, if exist
							break;
						}
					}
					else
					{
						eventInterestProfile2 = event.getSpecificEventInterestProfile( partOfProfileName );

						if ( eventInterestProfile2 == null )
						{
							logger.error( "EventInterestProfile " + partOfProfileName + " not found" );
							// continue to next derived event profile, if exist
							break;
						}
					}

					// calculate and persist
					if ( eventInterestProfile1 != null && eventInterestProfile2 != null && operationType != null )
					{
						if ( operationType.equals( "INTERSECTION" ) )
							eventInterestProfileResult = calculateIntersectionOfEventInterestProfiles( eventInterestProfile1, eventInterestProfile2, interestProfileDerived );
						else
							eventInterestProfileResult = calculateUnionOfEventInterestProfiles( eventInterestProfile1, eventInterestProfile2, interestProfileDerived );
					}
				}
			}
			// persist result
			if ( eventInterestProfileResult != null && ( eventInterestProfileResult.getEventInterests() != null && !eventInterestProfileResult.getEventInterests().isEmpty() ) )
			{
				eventInterestProfileResult.setEvent( event );
				event.addEventInterestProfiles( eventInterestProfileResult );
				persistenceStrategy.getEventDAO().persist( event );

				persistenceStrategy.getEventInterestProfileDAO().persist( eventInterestProfileResult );
			}

		}

	}

	/**
	 * Calculate Union interest between 2 Event interest profiles
	 * 
	 * @param eventInterestProfile1
	 * @param eventInterestProfile2
	 * @param interestProfileDerived
	 * @return
	 */
	private EventInterestProfile calculateUnionOfEventInterestProfiles( EventInterestProfile eventInterestProfile1, EventInterestProfile eventInterestProfile2, InterestProfileEvent interestProfileDerived )
	{
		Calendar calendar = Calendar.getInstance();
		EventInterestProfile eventInterestProfileResult = new EventInterestProfile();
		// set derived profile name
		String eventInterestProfileName = eventInterestProfile1.getName() + " ∪ " + eventInterestProfile2.getName();

		eventInterestProfileResult.setCreated( calendar.getTime() );
		eventInterestProfileResult.setName( eventInterestProfileName );
		eventInterestProfileResult.setDescription( "Interest mining using " + eventInterestProfileName + " algorithm" );

		Set<EventInterest> eventInterests1 = eventInterestProfile1.getEventInterests();
		Set<EventInterest> eventInterests2 = eventInterestProfile2.getEventInterests();

		for ( EventInterest eachEventInterest1 : eventInterests1 )
		{
			EventInterest eventInterestResult = null;
			for ( EventInterest eachEventInterest2 : eventInterests2 )
			{
				if ( eachEventInterest1.getLanguage().equals( eachEventInterest2.getLanguage() ) && eachEventInterest1.getYear().equals( eachEventInterest2.getYear() ) )
				{
					eventInterestResult = calculateUnionOfEventInterest( eachEventInterest1, eachEventInterest2 );
				}
			}

			if ( eventInterestResult != null && eventInterestResult.getTermWeights() != null && !eventInterestResult.getTermWeights().isEmpty() )
			{
				eventInterestResult.setEventInterestProfile( eventInterestProfileResult );
				eventInterestProfileResult.addEventInterest( eventInterestResult );
				eventInterestProfileResult.setInterestProfileEvent( interestProfileDerived );
			}
		}

		return eventInterestProfileResult;
	}

	/**
	 * Calculate Union interest between 2 EventInterest
	 * 
	 * @param eachEventInterest1
	 * @param eachEventInterest2
	 * @return
	 */
	private EventInterest calculateUnionOfEventInterest( EventInterest eachEventInterest1, EventInterest eachEventInterest2 )
	{
		EventInterest eventInterestResult = new EventInterest();
		eventInterestResult.setLanguage( eachEventInterest1.getLanguage() );
		eventInterestResult.setYear( eachEventInterest1.getYear() );

		Map<Interest, Double> termsWeight1 = eachEventInterest1.getTermWeights();
		Map<Interest, Double> termsWeight2 = eachEventInterest2.getTermWeights();

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
			eventInterestResult.setTermWeights( termsWeightResult );

		return eventInterestResult;
	}

	/**
	 * Calculate Intersection interest between 2 Event interest profiles
	 * 
	 * @param eventInterestProfile1
	 * @param eventInterestProfile2
	 * @param interestProfileDerived
	 * @return
	 */
	private EventInterestProfile calculateIntersectionOfEventInterestProfiles( EventInterestProfile eventInterestProfile1, EventInterestProfile eventInterestProfile2, InterestProfileEvent interestProfileDerived )
	{
		Calendar calendar = Calendar.getInstance();
		EventInterestProfile eventInterestProfileResult = new EventInterestProfile();
		// set derived profile name
		String eventInterestProfileName = eventInterestProfile1.getName() + " ∩ " + eventInterestProfile2.getName();

		eventInterestProfileResult.setCreated( calendar.getTime() );
		eventInterestProfileResult.setName( eventInterestProfileName );
		eventInterestProfileResult.setDescription( "Interest mining using " + eventInterestProfileName + " algorithm" );

		Set<EventInterest> eventInterests1 = eventInterestProfile1.getEventInterests();
		Set<EventInterest> eventInterests2 = eventInterestProfile2.getEventInterests();

		if ( eventInterests1 != null && !eventInterests1.isEmpty() )
		{
			for ( EventInterest eachEventInterest1 : eventInterests1 )
			{
				EventInterest eventInterestResult = null;
				for ( EventInterest eachEventInterest2 : eventInterests2 )
				{
					if ( eachEventInterest1.getLanguage().equals( eachEventInterest2.getLanguage() ) && eachEventInterest1.getYear().equals( eachEventInterest2.getYear() ) )
					{
						eventInterestResult = calculateIntersectionOfEventInterest( eachEventInterest1, eachEventInterest2 );
					}
				}

				if ( eventInterestResult != null && eventInterestResult.getTermWeights() != null && !eventInterestResult.getTermWeights().isEmpty() )
				{
					eventInterestResult.setEventInterestProfile( eventInterestProfileResult );
					eventInterestProfileResult.addEventInterest( eventInterestResult );
					eventInterestProfileResult.setInterestProfileEvent( interestProfileDerived );
				}
			}
		}

		return eventInterestProfileResult;
	}

	/**
	 * Calculate Intersection interest between 2 EventInterest
	 * 
	 * @param eachEventInterest1
	 * @param eachEventInterest2
	 * @return
	 */
	private EventInterest calculateIntersectionOfEventInterest( EventInterest eachEventInterest1, EventInterest eachEventInterest2 )
	{
		EventInterest eventInterestResult = new EventInterest();
		eventInterestResult.setLanguage( eachEventInterest1.getLanguage() );
		eventInterestResult.setYear( eachEventInterest1.getYear() );

		Map<Interest, Double> termsWeight1 = eachEventInterest1.getTermWeights();
		Map<Interest, Double> termsWeight2 = eachEventInterest2.getTermWeights();
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
			eventInterestResult.setTermWeights( termsWeightResult );

		return eventInterestResult;
	}

	/**
	 * Main method to calculate default InterestProfile such as : C-Value,
	 * Corephrase and WordFreq profile
	 * 
	 * @param event
	 * @param publicationClustersMap
	 * @param interestProfilesDefault
	 */
	public void calculateInterestProfilesDefault( Event event, Map<String, PublicationClusterHelper> publicationClustersMap, List<InterestProfileEvent> interestProfilesDefault )
	{
		// calculate frequencies of term in cluster
		for ( Map.Entry<String, PublicationClusterHelper> publicationClusterEntry : publicationClustersMap.entrySet() )
			publicationClusterEntry.getValue().calculateTermProperties();

		// loop through all interest profiles default
		for ( InterestProfileEvent interestProfileDefault : interestProfilesDefault )
			calculateEachInterestProfileDefault( event, interestProfileDefault, publicationClustersMap );
	}

	/**
	 * Calculate each default InterestProfile
	 * 
	 * @param event
	 * @param interestProfileDefault
	 * @param publicationClustersMap
	 */
	public void calculateEachInterestProfileDefault( Event event, InterestProfileEvent interestProfileDefault, Map<String, PublicationClusterHelper> publicationClustersMap )
	{
		// get event interest profile
		Calendar calendar = Calendar.getInstance();
		// default profile name [DEFAULT_PROFILENAME]
		String eventInterestProfileName = interestProfileDefault.getName();

		// create new event interest profile for c-value
		EventInterestProfile eventInterestProfile = new EventInterestProfile();
		eventInterestProfile.setCreated( calendar.getTime() );
		eventInterestProfile.setDescription( "Interest mining using " + interestProfileDefault.getName() + " algorithm" );
		eventInterestProfile.setName( eventInterestProfileName );
		
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
			EventInterest eventInterest = new EventInterest();

			// assign event interest method
			if ( interestProfileDefault.getName().toLowerCase().equals( "cvalue" ) )
			{
				cValueEventInterestProfile.doCValueCalculation( eventInterest, publicationCluster, numberOfExtractionService );
			}
			else if ( interestProfileDefault.getName().toLowerCase().equals( "corephrase" ) )
			{
				Double yearFactor = yearFactorMap.get( publicationCluster.getLanguage() + publicationCluster.getYear() );
				Double totalYearFactor = totalYearsFactorMap.get( publicationCluster.getLanguage() );
				corePhraseEventInterestProfile.doCorePhraseCalculation( eventInterest, publicationCluster, yearFactor, totalYearFactor, numberOfExtractionService );
			}
			else if ( interestProfileDefault.getName().toLowerCase().equals( "wordfreq" ) )
			{
				Double yearFactor = yearFactorMap.get( publicationCluster.getLanguage() + publicationCluster.getYear() );
				Double totalYearFactor = totalYearsFactorMap.get( publicationCluster.getLanguage() );
				wordFreqEventInterestProfile.doWordFreqCalculation( eventInterest, publicationCluster, yearFactor, totalYearFactor, numberOfExtractionService );
			}
			// Put other default interest profiles
			else if ( interestProfileDefault.getName().toLowerCase().equals( "lda" ) )
			{

			}

			// check event interest calculation result
			if ( eventInterest.getTermWeights() != null && !eventInterest.getTermWeights().isEmpty() )
			{
				eventInterest.setEventInterestProfile( eventInterestProfile );
				eventInterestProfile.addEventInterest( eventInterest );
				eventInterestProfile.setInterestProfileEvent( interestProfileDefault );
			}
		}

		// at the end persist
		if ( eventInterestProfile.getEventInterests() != null && !eventInterestProfile.getEventInterests().isEmpty() )
		{
			eventInterestProfile.setEvent( event );
			event.addEventInterestProfiles( eventInterestProfile );
			persistenceStrategy.getEventDAO().persist( event );
		}
	}

	/**
	 * Create a cluster for publications, based on language and year
	 * 
	 * @param event
	 * @param publicationClustersMap
	 */
	public void constructPublicationClusterByLanguageAndYear( Event event, Map<String, PublicationClusterHelper> publicationClustersMap )
	{
		// fill publication clusters
		// prepare calendar for publication year
		Calendar calendar = Calendar.getInstance();
		// get all publications from specific event and put it into cluster
		for ( Publication publication : event.getPublications() )
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
	 * Collect the event interest result as JSON object
	 * 
	 * @param event
	 * @param responseMap
	 * @return
	 */
	private Map<String, Object> getInterestFromDatabase( Event event, Map<String, Object> responseMap )
	{
		List<EventInterestProfile> eventInterestProfiles = new ArrayList<EventInterestProfile>();
		eventInterestProfiles.addAll( event.getEventInterestProfiles() );
		// sort based on profile length ( currently there is no attribute to
		// store position)
		Collections.sort( eventInterestProfiles, new EventInterestProfileByProfileNameLengthComparator() );

		// the whole result related to interest
		List<Object> eventInterestResult = new ArrayList<Object>();

		for ( EventInterestProfile eventInterestProfile : eventInterestProfiles )
		{
			// put profile on map
			Map<String, Object> eventInterestResultProfilesMap = new HashMap<String, Object>();

			// get interest profile name and description
			String interestProfileName = eventInterestProfile.getName();
			String interestProfileDescription = eventInterestProfile.getDescription();

			// get eventInterest set on profile
			Set<EventInterest> eventInterests = eventInterestProfile.getEventInterests();

			// if profile contain no eventInterest just skip
			if ( eventInterests == null || eventInterests.isEmpty() )
				continue;

			// a map for storing eventInterst based on language
			Map<String, List<EventInterest>> eventInterestLanguageMap = new HashMap<String, List<EventInterest>>();

			// split eventinterest based on language and put it on the map
			for ( EventInterest eventInterest : eventInterests )
			{
				if ( eventInterestLanguageMap.get( eventInterest.getLanguage() ) != null )
				{
					eventInterestLanguageMap.get( eventInterest.getLanguage() ).add( eventInterest );
				}
				else
				{
					List<EventInterest> eventInterestList = new ArrayList<EventInterest>();
					eventInterestList.add( eventInterest );
					eventInterestLanguageMap.put( eventInterest.getLanguage(), eventInterestList );
				}
			}

			// prepare calendar for extractind year from date
			Calendar calendar = Calendar.getInstance();

			// result event interest based on language
			List<Object> eventInterestResultLanguageList = new ArrayList<Object>();

			// sort eventinterest based on year
			for ( Map.Entry<String, List<EventInterest>> eventInterestLanguageIterator : eventInterestLanguageMap.entrySet() )
			{
				// result container
				Map<String, Object> eventInterestResultLanguageMap = new LinkedHashMap<String, Object>();
				// hashmap value
				String interestLanguage = eventInterestLanguageIterator.getKey();
				List<EventInterest> interestList = eventInterestLanguageIterator.getValue();

				// sort based on year
				Collections.sort( interestList, new EventInterestByDateComparator() );

				// term values based on year result container
				List<Object> eventInterestResultYearList = new ArrayList<Object>();

				// get interest year, term and value
				for ( EventInterest eventInterest : interestList )
				{
					if ( eventInterest.getTermWeights() == null || eventInterest.getTermWeights().isEmpty() )
						continue;

					// result container
					Map<String, Object> eventInterestResultYearMap = new LinkedHashMap<String, Object>();

					// get year
					calendar.setTime( eventInterest.getYear() );
					String year = Integer.toString( calendar.get( Calendar.YEAR ) );

					List<Object> termValueResult = new ArrayList<Object>();

					// put term and value
					for ( Map.Entry<Interest, Double> termWeightMap : eventInterest.getTermWeights().entrySet() )
					{
						List<Object> termWeightObjects = new ArrayList<Object>();
						termWeightObjects.add( termWeightMap.getKey().getId() );
						termWeightObjects.add( termWeightMap.getKey().getTerm() );
						termWeightObjects.add( termWeightMap.getValue() );
						termValueResult.add( termWeightObjects );
					}
					eventInterestResultYearMap.put( "year", year );
					eventInterestResultYearMap.put( "termvalue", termValueResult );
					eventInterestResultYearList.add( eventInterestResultYearMap );
				}

				eventInterestResultLanguageMap.put( "language", interestLanguage );
				eventInterestResultLanguageMap.put( "interestyears", eventInterestResultYearList );
				if ( interestLanguage.equals( "english" ) )
					eventInterestResultLanguageList.add( 0, eventInterestResultLanguageMap );
				else
					eventInterestResultLanguageList.add( eventInterestResultLanguageMap );
			}

			// put profile map
			eventInterestResultProfilesMap.put( "profile", interestProfileName );
			eventInterestResultProfilesMap.put( "description", interestProfileDescription );
			eventInterestResultProfilesMap.put( "interestlanguages", eventInterestResultLanguageList );
			eventInterestResult.add( eventInterestResultProfilesMap );
		}

		responseMap.put( "interest", eventInterestResult );

		// put also publication

		return responseMap;
	}

}

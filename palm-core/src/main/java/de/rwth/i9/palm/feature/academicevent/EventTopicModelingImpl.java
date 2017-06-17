package de.rwth.i9.palm.feature.academicevent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.analytics.util.TopicMiningConstants;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class EventTopicModelingImpl implements EventTopicModeling
{

	@Autowired
	private PalmAnalytics palmAnalytics;

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	private String path = TopicMiningConstants.USER_DESKTOP_PATH;

	/**
	 * This is the first method used to show the widget Simple LDA
	 * implementation String eventId boolean isReplaceExistingResult
	 */
	@Override
	public Map<String, Object> getTopicModeling( String eventId, boolean isReplaceExistingResult )
	{
		return null;
	}

	@Override
	public Map<String, Object> getStaticTopicModelingNgrams( String eventId, boolean isReplaceExistingResult ) throws IOException
	{

		// Create JSON map with the responses
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// Handle the status entry on the result
		if ( !eventId.isEmpty() )
		{
			responseMap.put( "status", "ok" );
		}
		else
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "Event not found" );
			return responseMap;
		}

		// create the List with objects which will hold the profiles
		List<Object> topicModel = new ArrayList<Object>();

		// create HashMap to hold the result and profileName for each algorithm
		Map<String, Object> algorithmResultUniGrams = new LinkedHashMap<String, Object>();
		Map<String, Object> algorithmResultNGrams = new LinkedHashMap<String, Object>();

		// add the profile names on the respective map
		algorithmResultUniGrams.put( "profile", "Unigrams" );
		algorithmResultNGrams.put( "profile", "Ngrams" );

		// loop over all the results of algorithm and put the elements in List
		for ( Entry<String, List<String>> topics : palmAnalytics.getNGrams().runTopicComposition( eventId, path, "Event", 10, 10, 5, false, palmAnalytics.getNGrams().dateCheckCriteria(path, "Event", eventId.toString()), true ).entrySet() )
		{
			List<Object> termValueResult = new ArrayList<Object>();
			// Expected only one entry in this map with eventId
			for ( String topicDetails : topics.getValue() )
			{
				List<Object> termvalueUnigram = new ArrayList<Object>();
				termvalueUnigram.add( topicDetails.split( "_-_" )[0] );
				termvalueUnigram.add( Double.parseDouble( topicDetails.split( "_-_" )[1] ) );
				termValueResult.add( termvalueUnigram );
			}
			algorithmResultUniGrams.put( "termvalue", termValueResult );
		}

		// add the unigrams into the topicModel list
		topicModel.add( algorithmResultUniGrams );

		for ( Entry<String, List<String>> topics : palmAnalytics.getNGrams().runTopicComposition( eventId, path, "Event-Test", 10, 10, 5, false, true, false ).entrySet() )
		{
			List<Object> termValueResult = new ArrayList<Object>();
			// expacted only one entry in this map with eventId
			for ( String topicDetails : topics.getValue() )
			{
				List<Object> termvalueNgram = new ArrayList<Object>();
				termvalueNgram.add( topicDetails.split( "_-_" )[0] );
				termvalueNgram.add( Double.parseDouble( topicDetails.split( "_-_" )[1] ) );
				termValueResult.add( termvalueNgram );
			}
			algorithmResultNGrams.put( "termvalue", termValueResult );
		}

		// add the ngrams into the topicModel list
		topicModel.add( algorithmResultNGrams );

		// add the result of topic Modeling into the result Map
		responseMap.put( "topicModel", topicModel );

		return responseMap;
	}

	public Map<String, Object> getStaticTopicModelingNgramsEventGroup( String eventId, boolean isReplaceExistingResult ) throws IOException
	{

		// Create JSON map with the responses
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// Handle the status entry on the result
		if ( !eventId.isEmpty() )
		{
			responseMap.put( "status", "ok" );
		}
		else
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "Event not found" );
			return responseMap;
		}

		// create the List with objects which will hold the profiles
		List<Object> topicModel = new ArrayList<Object>();

		// create HashMap to hold the result and profileName for each algorithm
		Map<String, Object> algorithmResultUniGrams = new LinkedHashMap<String, Object>();
		Map<String, Object> algorithmResultNGrams = new LinkedHashMap<String, Object>();

		// add the profile names on the respective map
		algorithmResultUniGrams.put( "profile", "Unigrams" );
		algorithmResultNGrams.put( "profile", "Ngrams" );
	
		// loop over all the results of algorithm and put the elements in List
		for ( Entry<String, List<String>> topics : palmAnalytics.getNGrams().runTopicComposition( persistenceStrategy.getEventDAO().getById(eventId).getEventGroup().getId().toString() , path, "EventGroups", 10, 10, 5, false, palmAnalytics.getNGrams().dateCheckCriteria(path, "EventGroups", eventId.toString()), true ).entrySet() )
		{
			List<Object> termValueResult = new ArrayList<Object>();
			// Expected only one entry in this map with eventId
			for ( String topicDetails : topics.getValue() )
			{
				List<Object> termvalueUnigram = new ArrayList<Object>();
				termvalueUnigram.add( topicDetails.split( "_-_" )[0] );
				termvalueUnigram.add( Double.parseDouble( topicDetails.split( "_-_" )[1] ) );
				termValueResult.add( termvalueUnigram );
			}
			algorithmResultUniGrams.put( "termvalue", termValueResult );
		}

		// add the unigrams into the topicModel list
		topicModel.add( algorithmResultUniGrams );

		for ( Entry<String, List<String>> topics : palmAnalytics.getNGrams().runTopicComposition( persistenceStrategy.getEventDAO().getById(eventId).getEventGroup().getId().toString(), path, "EventGroups", 10, 10, 5, false, true, false ).entrySet() )
		{
			List<Object> termValueResult = new ArrayList<Object>();
			// expacted only one entry in this map with eventId
			for ( String topicDetails : topics.getValue() )
			{
				List<Object> termvalueNgram = new ArrayList<Object>();
				termvalueNgram.add( topicDetails.split( "_-_" )[0] );
				termvalueNgram.add( Double.parseDouble( topicDetails.split( "_-_" )[1] ) );
				termValueResult.add( termvalueNgram );
			}
			algorithmResultNGrams.put( "termvalue", termValueResult );
		}

		// add the ngrams into the topicModel list
		topicModel.add( algorithmResultNGrams );

		// add the result of topic Modeling into the result Map
		responseMap.put( "topicModel", topicModel );

		return responseMap;
	}

	@Override
	public Map<String, Object> getTopicModelUniCloud( Event event, boolean isReplaceExistingResult ) throws IOException
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// event details
		Map<String, String> eventdetail = new HashMap<String, String>();
		eventdetail.put( "id", event.getId().toString() );

		// algorithm result
		HashMap<String, Double> topiccomposition = new LinkedHashMap<String, Double>();
		topiccomposition = palmAnalytics.getNGrams().runweightedTopicComposition( path, "Event", event.getId().toString(), 10, 10, 10, palmAnalytics.getNGrams().dateCheckCriteria(path, "Event", event.getId().toString()), true );

		if ( topiccomposition.isEmpty() != true )
		{
			responseMap.put( "status", "Ok" );
		}
		else
		{
			responseMap.put( "status", "No Topics discovered" );
		}

		responseMap.put( "event", eventdetail );

		// add the event id to the map
		responseMap.put( "event", event.getId().toString() );

		// hold the temporal results from the algorithm

		List<LinkedHashMap<String, Object>> topicList = new ArrayList<LinkedHashMap<String, Object>>();

		for ( Entry<String, Double> topic : topiccomposition.entrySet() )
		{
			Map<String, Object> topicdistribution = new LinkedHashMap<String, Object>();
			topicdistribution.put( topic.getKey().toString(), topic.getValue() );
			// topicdistribution.put( "size", topic.getValue() );
			topicList.add( (LinkedHashMap<String, Object>) topicdistribution );

			// TO DO Unigrams/Ngrams preferences
		}
		responseMap.put( "termvalue", topicList );

		return responseMap;
	}

	@Override
	public Map<String, Object> getTopicModelNCloud( Event event, boolean isReplaceExistingResult ) throws IOException
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// event details
		Map<String, String> eventdetail = new HashMap<String, String>();
		eventdetail.put( "id", event.getId().toString() );


		// algorithm result
		HashMap<String, Double> topiccomposition = new LinkedHashMap<String, Double>();
		topiccomposition = palmAnalytics.getNGrams().runweightedTopicComposition( path, "Event", event.getId().toString(), 10, 10, 10, palmAnalytics.getNGrams().dateCheckCriteria(path, "Event", event.getId().toString()), false );

		if ( topiccomposition.isEmpty() != true )
		{
			responseMap.put( "status", "Ok" );
		}
		else
		{
			responseMap.put( "status", "No Topics discovered" );
		}

		responseMap.put( "event", eventdetail );

		// add the event id to the map
		// responseMap.put( "event", event.getId().toString() );

		// hold the temporal results from the algorithm

		List<LinkedHashMap<String, Object>> topicList = new ArrayList<LinkedHashMap<String, Object>>();

		for ( Entry<String, Double> topic : topiccomposition.entrySet() )
		{
			Map<String, Object> topicdistribution = new LinkedHashMap<String, Object>();
			topicdistribution.put( topic.getKey().toString(), topic.getValue() );
			// topicdistribution.put( "size", topic.getValue() );
			topicList.add( (LinkedHashMap<String, Object>) topicdistribution );
		}
		responseMap.put( "termvalue", topicList );

		return responseMap;
	}

	@Override
	public Map<String, Object> getSimilarEventsMap( Event event, int startPage, int maxresult )
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		List<String> similarEvents = new ArrayList<String>();
		similarEvents = palmAnalytics.getNGrams().runSimilarEntities( event.getId().toString(), "C:/Users/Administrator/Desktop/", "Conferences", 20, 10, 3, true );
		// similarEntities( event.getId(), maxresult, 3 );

		// Prepare set of similarEvent HashSet;
		Set<String> similareventSet = new HashSet<String>();

		for ( String similar : similarEvents )
		{
			// if ( persistenceStrategy.getEventDAO().getById( similar.split(
			// "->" )[0] ).equals( event ) )
			// continue;

			similareventSet.add( similar );
		}

		// prepare list of object map containing similarEvent details
		List<Map<String, Object>> similarEventList = new ArrayList<Map<String, Object>>();

		for ( String similarEvent : similareventSet )
		{
			// only copy necessary attributes
			// persistenceStrategy.( similar.split( "->"
			// )[0] )
			Map<String, Object> similarEventMap = new LinkedHashMap<String, Object>();
			similarEventMap.put( "id", similarEvent.split( "->" )[0] );
			if ( !persistenceStrategy.getEventDAO().getById( similarEvent.split( "->" )[0] ).getName().isEmpty() )
				similarEventMap.put( "name", persistenceStrategy.getEventDAO().getById( similarEvent.split( "->" )[0] ).getName() );
			similarEventMap.put( "similarity", similarEvent.split( "->" )[1] );
			// add into list
			similarEventList.add( similarEventMap );
		}

		// prepare list of object map containing similarEvent details
		List<Map<String, Object>> similarEventListPaging = new ArrayList<Map<String, Object>>();

		int position = 0;
		for ( Map<String, Object> similarEvent : similarEventList )
		{
			if ( position >= startPage && similarEventListPaging.size() < maxresult )
			{
				similarEventListPaging.add( similarEvent );
			}
		}

		// remove unnecessary result

		// put similarEvent to responseMap
		responseMap.put( "countTotal", similarEventList.size() );
		responseMap.put( "count", similarEventListPaging.size() );
		responseMap.put( "similarEvents", similarEventListPaging );

		return responseMap;
	}
	

	@Override
	public Map<String, Object> getEventGroupTopicEvolutionTest( Event event ) throws IOException
	{

		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		HashMap<String, List<String>> topicevolution = new LinkedHashMap<String, List<String>>();

		// getEvolutionofTopicOverTime( 0, 5, false );
		topicevolution = (HashMap<String, List<String>>) palmAnalytics.getNGrams().runDiscreteTopicEvolution( path, "EventGroupsClustered", event.getEventGroup().getId().toString(), 5, 10, 10, palmAnalytics.getNGrams().dateCheckCriteria(path, "EventGroupsClustered", event.getId().toString()), false, false );
		// Prepare set of similarAuthor HashSet;
		List<LinkedHashMap<String, Object>> topicList = new ArrayList<LinkedHashMap<String, Object>>();
		String[] colors = { "0efff8", "ff7f0e", "0eff7f", "ffa70e", "ff7f5a", "d4991c", "ad937c", "ff430e", "ff0e8e", "0e8eff" };
		int col = 0;
		for ( Entry<String, List<String>> topic : topicevolution.entrySet() )
		{
			LinkedHashMap<String, Object> evolutionMap = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, String> yearproportion = new LinkedHashMap<String, String>();
			for ( String proportions : topic.getValue() )
			{
				if ( !Double.isNaN( Double.parseDouble( proportions.split( "_-_" )[1] ) ) )
					yearproportion.put( proportions.split( "_-_" )[0], proportions.split( "_-_" )[1] );
				else
					yearproportion.put( proportions.split( "_-_" )[0], "0.0" );
			}
			evolutionMap.put( "values", yearproportion );
			evolutionMap.put( "key", topic.getKey() );
			evolutionMap.put( "color", colors[col] );
			if ( col < colors.length )
			{
				col++;
			}
			else
			{
				col = 0;
			}

			topicList.add( evolutionMap );
		}

		// prepare list of object map containing similarAuthor details
		List<Map<String, Object>> idk = new ArrayList<Map<String, Object>>();

		for ( Map<String, Object> individaltopics : topicList )
		{
			idk.add( individaltopics );
		}

		// put similarAuthor to responseMap
		responseMap.put( "termvalues", idk );

		return responseMap;
	}

	@Override
	public Map<String, Object> getTopicModelEventGroupUniCloud( Event eventgroup, boolean isReplaceExistingResult ) throws IOException
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// eventgroup details
		Map<String, String> eventgroupdetail = new HashMap<String, String>();
		eventgroupdetail.put( "id", eventgroup.getId().toString() );
		eventgroupdetail.put( "name", eventgroup.getName().toString() );

		// algorithm result
		HashMap<String, Double> topiccomposition = new LinkedHashMap<String, Double>();
		topiccomposition = palmAnalytics.getNGrams().runweightedTopicComposition( path, "EventGroupsClustered", eventgroup.getEventGroup().getId().toString(), 5, 5, 5, palmAnalytics.getNGrams().dateCheckCriteria(path, "EventGroupsClustered", eventgroup.getId().toString()), true );

		if ( topiccomposition.isEmpty() != true )
		{
			responseMap.put( "status", "Ok" );
		}
		else
		{
			responseMap.put( "status", "No Topics discovered" );
		}

		responseMap.put( "eventgroup", eventgroupdetail );

		// add the eventgroup id to the map
		responseMap.put( "eventgroup", eventgroup.getId().toString() );

		// hold the temporal results from the algorithm

		List<LinkedHashMap<String, Object>> topicList = new ArrayList<LinkedHashMap<String, Object>>();

		for ( Entry<String, Double> topic : topiccomposition.entrySet() )
		{
			Map<String, Object> topicdistribution = new LinkedHashMap<String, Object>();
			topicdistribution.put( topic.getKey().toString(), topic.getValue() );
			// topicdistribution.put( "size", topic.getValue() );
			topicList.add( (LinkedHashMap<String, Object>) topicdistribution );

			// TO DO Unigrams/Ngrams preferences
		}
		responseMap.put( "termvalue", topicList );

		return responseMap;
	}

	@Override
	public Map<String, Object> getTopicModelEventGroupNCloud( Event eventgroup, boolean isReplaceExistingResult ) throws IOException
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// eventgroupgroup details
		Map<String, String> eventgroupdetail = new HashMap<String, String>();
		eventgroupdetail.put( "id", eventgroup.getId().toString() );
		eventgroupdetail.put( "name", eventgroup.getName().toString() );

		// algorithm result
		HashMap<String, Double> topiccomposition = new LinkedHashMap<String, Double>();
		topiccomposition = palmAnalytics.getNGrams().runweightedTopicComposition( path, "EventGroupsClustered", eventgroup.getEventGroup().getId().toString(), 5, 5, 5, palmAnalytics.getNGrams().dateCheckCriteria(path, "EventGroupsClustered", eventgroup.getId().toString()), false );

		if ( topiccomposition.isEmpty() != true )
		{
			responseMap.put( "status", "Ok" );
		}

		responseMap.put( "eventgroup", eventgroupdetail );

		// add the eventgroup id to the map
		// responseMap.put( "eventgroup", eventgroup.getId().toString() );

		// hold the temporal results from the algorithm

		List<LinkedHashMap<String, Object>> topicList = new ArrayList<LinkedHashMap<String, Object>>();

		for ( Entry<String, Double> topic : topiccomposition.entrySet() )
		{
			Map<String, Object> topicdistribution = new LinkedHashMap<String, Object>();
			topicdistribution.put( topic.getKey().toString(), topic.getValue() );
			// topicdistribution.put( "size", topic.getValue() );
			topicList.add( (LinkedHashMap<String, Object>) topicdistribution );

			// TO DO Unigrams/Ngrams preferences
		}
		responseMap.put( "termvalue", topicList );

		return responseMap;
	}

	@Override
	public Map<String, Object> getSimilarEvents(Event event, int startPage, int maxresult) throws IOException {
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		
		// find the list of similar events
		List<String> similarEntities = new ArrayList<String>();
		similarEntities = palmAnalytics.getNGrams().runSimilarEntities( event.getEventGroup().getId().toString(), "C:/Users/Administrator/Desktop/", "Conferences", 50, 10, 3, palmAnalytics.getNGrams().dateCheckCriteria( path, "Conferences", event.getId().toString() ) );
		
		List<Map<String, Object>> similarEventList = new ArrayList<Map<String, Object>>();
		
		// get the list of words for the event 
		List<String> eventtopicWords = new ArrayList<String>();
		for (String entity : similarEntities){
			String piro = event.getEventGroup().getId().toString();
			if(entity.split("->")[0].equals(piro))
				eventtopicWords = new ArrayList<String>(palmAnalytics.getNGrams().runweightedTopicComposition(path,"EventGroups", entity.split("->")[0], 10, 10, 10, palmAnalytics.getNGrams().dateCheckCriteria(path, "EventGroups", event.getId().toString()), false ).keySet());
		}
		
		// run for each of the entities of the list the weightedTopic Composition
		for (String entity : similarEntities){
			if(!entity.split("->")[0].equals(event.getId()))
			{		
				List<String> similartopicWords = new ArrayList<String>(palmAnalytics.getNGrams().runweightedTopicComposition(path,"EventGroups", entity.split("->")[0], 10, 10, 10, palmAnalytics.getNGrams().dateCheckCriteria(path, "EventGroups", event.getId().toString()), false ).keySet());
				
				Map<String, Object> similarEventMap = new LinkedHashMap<String, Object>();

				// insert the initial basic information
				similarEventMap.put( "id", entity.split("->")[0] );
				similarEventMap.put( "name", persistenceStrategy.getEventGroupDAO().getById( entity.split("->")[0] ).getName() );
				
				
				// return a HashMap with the similar words and similarity degree 
				HashMap<String, Double> similarDetail = comparePhraseTopicLevel(eventtopicWords,similartopicWords );
				similarEventMap.put( "similarity", similarDetail.entrySet().iterator().next().getValue());
				
				// construct the map for the list of topics
				List<Object> topicleveldetail = new ArrayList<Object>();
				Map<String, Object> topicproportions = new LinkedHashMap<String, Object>();
				topicproportions.put( "name", similarDetail.keySet().toArray()[0] );
				topicproportions.put( "value", "" );
				topicleveldetail.add( topicproportions );
				

				similarEventMap.put( "topicdetail", topicleveldetail );
				
				// check if the similarity is significant 
				// The threshhold is decided heuristically 
				double a = similarDetail.entrySet().iterator().next().getValue();
				if ( a > 0.035){
					// add into list if the similarity 
					similarEventList.add( similarEventMap );
				}
				else
				{
					continue;
				}
			}
		}
		// prepare list of object map containing similarEvent details
		List<Map<String, Object>> similarEventListPaging = new ArrayList<Map<String, Object>>();

		int position = 0;
		for ( Map<String, Object> similarEvent : similarEventList )
		{
			if ( position >= startPage && similarEventListPaging.size() < maxresult )
			{
				similarEventListPaging.add( similarEvent );
			}
		}

		// put similarEvent to responseMap
		responseMap.put( "countTotal", similarEventList.size() );
		responseMap.put( "count", similarEventListPaging.size() );
		responseMap.put( "similarEvents", similarEventListPaging );
		
		return responseMap;
	}
	
	// compare the two events in word level
	private HashMap<String, Double> comparePhraseTopicLevel(List<String> eventtopicWords, List<String> similartopicWords) {
		HashMap<String, Double> result = new HashMap<String,Double>();
		String topic = "";
		int count = 0;
		
		for (String eventphrase : eventtopicWords){
			for (String similarphrase : similartopicWords){
				if (eventphrase.contains(similarphrase)){
					topic +=  eventphrase + ",";
					count++;
				}
				else if(similarphrase.contains(eventphrase)){
						topic +=  eventphrase + ",";
						count++;
					}
				else
					continue;
			}
		}
		
		String [] topicArray = new HashSet<String>(Arrays.asList(topic.split(","))).toArray(new String[0]);
		String phrase = " ";
		
		for (String str : topicArray){
			phrase +=  str + ",";
			}
		
		if (phrase != null && phrase.length() > 0 && phrase.charAt(phrase.length()-1)==',') {
			phrase = phrase.substring(0, phrase.length()-1);
		    }
		
		result.put(phrase, (double)topicArray.length/eventtopicWords.size());
		
		return result;
	}

}

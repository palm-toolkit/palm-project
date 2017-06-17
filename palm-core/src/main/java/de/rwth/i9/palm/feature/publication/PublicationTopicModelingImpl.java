
package de.rwth.i9.palm.feature.publication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.analytics.util.TopicMiningConstants;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

// latest updates on 05.06 Uhr. 15:00
@Component
public class PublicationTopicModelingImpl implements PublicationTopicModeling
{
	@Autowired
	private PalmAnalytics palmAnalytics;

	@Autowired
	private PersistenceStrategy persistenceStrategy;
	private String path = TopicMiningConstants.USER_DESKTOP_PATH;
	
	@Override
	public Map<String, Object> getTopicModeling( String publicationId, boolean isReplaceExistingResult )
	{
		// get the publication based on the id
		// Publication publication =
		// persistenceStrategy.getPublicationDAO().getById( publicationId );
		
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		
		responseMap.put( "status", "ok" );
		
		// create a List of topic maps for two profiles 
		List<LinkedHashMap<String, Object>> topics = new ArrayList<LinkedHashMap<String, Object>>();
		
		// use this loop to get the add the two profiles
		// int i=0;
		// while (i<2){
			// crate the hashmap with each profile elements
		LinkedHashMap<String, Object> components = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, Object> components2 = new LinkedHashMap<String, Object>();
		components.put( "id", publicationId );
		components2.put( "id", publicationId );
		// if (i==0){
		components.put( "extractor", "Unigrams" );
		components2.put( "extractor", "Ngrams" );
				
		// create the list for the termvalues
		List<LinkedHashMap<String, Object>> termvalues = new ArrayList<LinkedHashMap<String, Object>>();
		List<LinkedHashMap<String, Object>> termvalues2 = new ArrayList<LinkedHashMap<String, Object>>();
		List<String> unigrams;
		List<String> ngrams;
		List<String> resultsunigrams;
		List<String> resultsngrams;
		try
		{
			unigrams = palmAnalytics.getNGrams().runTopicsFromListofEntities( path, "Authors", extractCoauthros( publicationId ), publicationId, 10, 10, 5, true, true, false ).get( publicationId );
			ngrams = palmAnalytics.getNGrams().runTopicsFromListofEntities( path, "Authors", extractCoauthros( publicationId ), publicationId, 10, 10, 5, true, false, false ).get( publicationId );

			// get the top 10 topics from the merged topics
			resultsunigrams = extractTopTopics( unigrams, 10 );
			resultsngrams = extractTopTopics( ngrams, 10 );

			for ( String terms : resultsunigrams )
			{
				LinkedHashMap<String, Object> algorithmResult = new LinkedHashMap<String, Object>();

				algorithmResult.put( "term", terms.split( "_-_" )[0] );
				algorithmResult.put( "value", Double.parseDouble( terms.split( "_-_" )[1] ) );
				termvalues.add( algorithmResult );
			}
			for ( String terms : resultsngrams )
			{
				LinkedHashMap<String, Object> algorithmResult = new LinkedHashMap<String, Object>();

				algorithmResult.put( "term", terms.split( "_-_" )[0] );
				algorithmResult.put( "value", Double.parseDouble( terms.split( "_-_" )[1] ) );
				termvalues2.add( algorithmResult );
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		// add the termvalues on the components hashmap
		components.put( "termvalues", termvalues );
		components2.put( "termvalues", termvalues2 );
		
		// add the components to the topics profile list
		topics.add( components );
		topics.add( components2 );

		// add the list into the responseMap
		responseMap.put( "topics", topics );

		return responseMap;
}
	
	
	public Map<String, Object> getTopicComposition( String publicationId, boolean isReplaceExistingResult )
	{
		// Create JSON map with the responses
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// Handle the status entry on the result
		if ( !publicationId.isEmpty() )
		{
			responseMap.put( "status", "ok" );
		}
		else
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "Publication not found" );
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
		
		List<Object> termValueResultunigrams = new ArrayList<Object>();
		// loop over all the results of algorithm and put the elements in List
		List<String> unigrams = palmAnalytics.getNGrams().runTopicsFromListofEntities( path, "Authors", extractCoauthros( publicationId ), publicationId, 15, 15, 5, true, true, false ).get( publicationId );
		
		// method used to get the top topics (in this case 5)
		unigrams = extractTopTopics(unigrams,5);
		
		for ( String topics : unigrams )
		{
			// List<Object> termvalueUnigram = new ArrayList<Object>();
			Map<String, Object> termvalueUnigram = new LinkedHashMap<String, Object>();
			termvalueUnigram.put( "term", topics.split( "_-_" )[0] );
			termvalueUnigram.put( "value", Double.parseDouble( topics.split( "_-_" )[1] ) );
			termValueResultunigrams.add( termvalueUnigram );
		}
		
		algorithmResultUniGrams.put( "termvalues", termValueResultunigrams );

		// add the unigrams into the topicModel list
		topicModel.add( algorithmResultUniGrams );
		
		List<Object> termValueResultngrams = new ArrayList<Object>();
		List<String> ngrams = palmAnalytics.getNGrams().runTopicsFromListofEntities( path, "Authors", extractCoauthros( publicationId ), publicationId, 15, 15, 5, true, false, false ).get( publicationId );
		
		// method used to get the top topics (in this case 5)
		ngrams = extractTopTopics(ngrams,5);
		
		for ( String topics : ngrams){
			// List<Object> termvalueNgram = new ArrayList<Object>();
			Map<String, Object> termvalueNgram = new LinkedHashMap<String, Object>();
			termvalueNgram.put( "term", topics.split( "_-_" )[0] );
			termvalueNgram.put( "value", Double.parseDouble( topics.split( "_-_" )[1] ) );
			termValueResultngrams.add( termvalueNgram );
		}
		algorithmResultNGrams.put( "termvalues", termValueResultngrams );
		// add the ngrams into the topicModel list
		topicModel.add( algorithmResultNGrams );

		// add the result of topic Modeling into the result Map
		responseMap.put( "topicModel", topicModel );

		return responseMap;
	}


	// method used to get the top elements from an unsorted list created by
	// composed elements
	private List<String> extractTopTopics( List<String> topics, int maxResult )
	{
		List<String> result = new ArrayList<String>( topics.size() );
		int N = maxResult;
		// loop to get the top elements
		while ( N > 0 )
		{
			double max = -1;
			String temporalMax;
			String topic = "";
			int index = -1;
			for ( int i = 0; i < topics.size(); i++ )
			{

				if ( Double.parseDouble( topics.get( i ).split( "_-_" )[1] ) >= max )
				{
					max = Double.parseDouble( topics.get( i ).split( "_-_" )[1] );
					topic = topics.get( i ).split( "_-_" )[0];
					index = i;
				}
			}
			if ( index != -1 )
			{
				temporalMax = topic + "_-_" + max;
				result.add( temporalMax );
				topics.remove( index );
			}

			N--;
		}
		return result;
	}

	@Override
	public Map<String, Object> getStaticTopicModelingNgrams( String publicationId, boolean isReplaceExistingResult )
	{
		return null;
	}

	/**
	 * 
	 * @param publicationId - get the publicationId 
	 * @return List of co-authors
	 */
	public List<String> extractCoauthros( String publicationId )
	{
		LinkedList<String> coauthors = new LinkedList<String>();
		
		for ( Author author : persistenceStrategy.getPublicationDAO().getById( publicationId ).getAuthors() )
		{
			coauthors.add( author.getId().toString() );
		}
		//get the list all the authors
		return coauthors;
	}

	@Override
	public Map<String, Object> getTopicModelUniCloud( String publicationId, boolean isReplaceExistingResult )
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// author details
		Map<String, String> publicationdetail = new HashMap<String, String>();
		publicationdetail.put( "id", publicationId.toString() );
		// publicationdetail.put( "name", publicationId.getT().toString() );

		// algorithm result
		HashMap<String, Double> topiccomposition = new LinkedHashMap<String, Double>();
		topiccomposition = palmAnalytics.getNGrams().runweightedTopicCompositionforPublications( path, "Authors", publicationId, extractCoauthros( publicationId ), 5, 5, 5, true, true );

		if ( topiccomposition.isEmpty() != true )
		{
			responseMap.put( "status", "Ok" );
		}

		responseMap.put( "publication", publicationdetail );

		// add the author id to the map
		// responseMap.put( "author", author.getId().toString() );

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
	public Map<String, Object> getTopicModelNCloud( String publicationId, boolean isReplaceExistingResult )
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// author details
		Map<String, String> publicationdetail = new HashMap<String, String>();
		publicationdetail.put( "id", publicationId.toString() );
		// publicationdetail.put( "name", publicationId.getT().toString() );

		// algorithm result
		HashMap<String, Double> topiccomposition = new LinkedHashMap<String, Double>();
		topiccomposition = palmAnalytics.getNGrams().runweightedTopicCompositionforPublications( path, "Authors", publicationId, extractCoauthros( publicationId ), 5, 5, 5, true, false );

		if ( topiccomposition.isEmpty() != true )
		{
			responseMap.put( "status", "Ok" );
		}

		responseMap.put( "publication", publicationdetail );

		// add the author id to the map
		// responseMap.put( "author", author.getId().toString() );

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
	public Map<String, Object> getResearcherSimilarPublicationMap( Publication publication, int startPage, int maxresult )
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		List<String> similarPublications = new ArrayList<String>();
		similarPublications = palmAnalytics.getNGrams().runSimilarEntities( publication.getId().toString(), "C:/Users/Administrator/Desktop/", "Publications", 20, 10, 3, true );
		// similarEntities( author.getId(), maxresult, 3 );

		// Prepare set of similarPublication HashSet;
		Set<String> similarpublicationSet = new HashSet<String>();

		for ( String similar : similarPublications )
		{
			if ( persistenceStrategy.getPublicationDAO().getById( similar.split( "->" )[0] ).equals( publication ) )
				continue;

			similarpublicationSet.add( similar );
		}

		// prepare list of object map containing similarPublication details
		List<Map<String, Object>> similarPublicationList = new ArrayList<Map<String, Object>>();

		for ( String similarPublication : similarpublicationSet )
		{
			// only copy necessary attributes
			// persistenceStrategy.( similar.split( "->"
			// )[0] )
			Map<String, Object> similarPublicationMap = new LinkedHashMap<String, Object>();
			similarPublicationMap.put( "id", similarPublication.split( "->" )[0] );
			similarPublicationMap.put( "name", persistenceStrategy.getPublicationDAO().getById( similarPublication.split( "->" )[0] ).getTitle() );
			similarPublicationMap.put( "similarity", similarPublication.split( "->" )[1] );
			similarPublicationMap.put( "author", persistenceStrategy.getPublicationDAO().getById( similarPublication ).getAuthors() );
			// add into list
			similarPublicationList.add( similarPublicationMap );
		}

		// prepare list of object map containing similarPublication details
		List<Map<String, Object>> similarPublicationListPaging = new ArrayList<Map<String, Object>>();

		int position = 0;
		for ( Map<String, Object> similarPublication : similarPublicationList )
		{
			if ( position >= startPage && similarPublicationListPaging.size() < maxresult )
			{
				similarPublicationListPaging.add( similarPublication );
			}
		}

		// remove unnecessary result

		// put similarPublication to responseMap
		responseMap.put( "countTotal", similarPublicationList.size() );
		responseMap.put( "count", similarPublicationListPaging.size() );
		responseMap.put( "similarPublications", similarPublicationListPaging );

		return responseMap;
	}

}

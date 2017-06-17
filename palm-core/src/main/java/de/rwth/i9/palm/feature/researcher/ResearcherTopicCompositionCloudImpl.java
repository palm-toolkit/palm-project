package de.rwth.i9.palm.feature.researcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.analytics.util.TopicMiningConstants;
import de.rwth.i9.palm.model.Author;

@Component
public class ResearcherTopicCompositionCloudImpl implements ResearcherTopicCompositionCloud
{
	@Autowired
	private PalmAnalytics palmAnalytics;
	private String path = TopicMiningConstants.USER_DESKTOP_PATH;
	
	@Override
	public Map<String, Object> getTopicModelUniCloud( Author author, boolean isReplaceExistingResult ) throws IOException
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// author details
		Map<String, String> authordetail = new HashMap<String, String>();
		authordetail.put( "id", author.getId().toString() );
		authordetail.put( "name", author.getName().toString() );

		// algorithm result
		HashMap<String, Double> topiccomposition = new LinkedHashMap<String, Double>();
		topiccomposition = palmAnalytics.getNGrams().runweightedTopicComposition( path, "Authors", author.getId().toString(), 5, 5, 5, palmAnalytics.getNGrams().dateCheckCriteria( path, "Authors", author.getId().toString() ), true );

		if ( topiccomposition.isEmpty() != true )
		{
			responseMap.put( "status", "Ok" );
		}

		responseMap.put( "author", authordetail );

		// add the author id to the map
		responseMap.put( "author", author.getId().toString() );

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
	public Map<String, Object> getTopicModelNCloud( Author author, boolean isReplaceExistingResult ) throws IOException
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// author details
		Map<String, String> authordetail = new HashMap<String, String>();
		authordetail.put( "id", author.getId().toString() );
		authordetail.put( "name", author.getName().toString() );

		// algorithm result
		HashMap<String, Double> topiccomposition = new LinkedHashMap<String, Double>();
		topiccomposition = palmAnalytics.getNGrams().runweightedTopicComposition( path, "Authors", author.getId().toString(), 10, 10, 10, palmAnalytics.getNGrams().dateCheckCriteria( path, "Authors", author.getId().toString() ), false );

		if ( topiccomposition.isEmpty() != true )
		{
			responseMap.put( "status", "Ok" );
		}

		responseMap.put( "author", authordetail );

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
	public Map<String, Object> getTopicModelTagCloud( Author author, boolean isReplaceExistingResult )
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// author details
		Map<String, String> authordetail = new HashMap<String, String>();
		authordetail.put( "id", author.getId().toString() );
		authordetail.put( "name", author.getName().toString() );

		// algorithm result
		HashMap<String, Double> topiccomposition = new LinkedHashMap<String, Double>();
		topiccomposition = palmAnalytics.getNGrams().runweightedTopicComposition( path, "Author-Test", author.getId().toString(), 5, 5, 5, true, false );

		if ( topiccomposition.isEmpty() != true )
		{
			responseMap.put( "status", "Ok" );
		}

		responseMap.put( "author", authordetail );

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

}

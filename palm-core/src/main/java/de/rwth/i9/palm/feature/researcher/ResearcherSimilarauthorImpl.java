package de.rwth.i9.palm.feature.researcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import de.rwth.i9.palm.helper.comparator.SimilarityComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class ResearcherSimilarauthorImpl implements ResearcherSimilarauthor
{
	@Autowired
	private PalmAnalytics palmAnalytics;

	@Autowired
	private PersistenceStrategy persistenceStrategy;
	private String path = TopicMiningConstants.USER_DESKTOP_PATH;

	@Override
	public Map<String, Object> getResearcherSimilarAuthorMap( Author author, int startPage, int maxresult )
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		List<String> similarAuthors = new ArrayList<String>();
		
		// similarAuthors = palmAnalytics.getNGrams().runSimilarEntities(
		// author.getId().toString(), path, "Authors", 50, maxresult, 3, false
		// );

		HashMap<String, List<String>> similarAuthorsMap = new HashMap<String, List<String>>();
		similarAuthorsMap = palmAnalytics.getNGrams().runSimilarEntitiesContributorsTopicLevel( author.getId().toString(), path, "Authors", 50, maxresult, 3, false );

		// Prepare set of similarAuthor HashSet;
		Set<String> similarauthorSet = new HashSet<String>();

		for ( String similar : similarAuthors )
		{
			if ( persistenceStrategy.getAuthorDAO().getById( similar.split( "->" )[0] ).equals( author ) )
				continue;

			similarauthorSet.add( similar );
		}

		// prepare list of object map containing similarAuthor details
		List<Map<String, Object>> similarAuthorList = new ArrayList<Map<String, Object>>();

		for ( String similarAuthor : similarauthorSet )
		{
			// only copy necessary attributes
			// persistenceStrategy.( similar.split( "->"
			// )[0] )
			Author auth = persistenceStrategy.getAuthorDAO().getById( similarAuthor.split( "->" )[0] );

			Map<String, Object> similarAuthorMap = new LinkedHashMap<String, Object>();
			similarAuthorMap.put( "id", similarAuthor.split( "->" )[0] );
			similarAuthorMap.put( "name", persistenceStrategy.getAuthorDAO().getById( similarAuthor.split( "->" )[0] ).getName() );
			if ( persistenceStrategy.getAuthorDAO().getById( similarAuthor.split( "->" )[0] ).getInstitution() != null )
				similarAuthorMap.put( "affiliation", persistenceStrategy.getAuthorDAO().getById( similarAuthor.split( "->" )[0] ).getInstitution().getName() );
			if ( persistenceStrategy.getAuthorDAO().getById( similarAuthor.split( "->" )[0] ).getPhotoUrl() != null )
				similarAuthorMap.put( "photo", persistenceStrategy.getAuthorDAO().getById( similarAuthor.split( "->" )[0] ).getPhotoUrl() );
			if ( persistenceStrategy.getAuthorDAO().getById( similarAuthor.split( "->" )[0] ).getAcademicStatus() != null )
				similarAuthorMap.put( "status", persistenceStrategy.getAuthorDAO().getById( similarAuthor.split( "->" )[0] ).getAcademicStatus() );

			similarAuthorMap.put( "citedBy", persistenceStrategy.getAuthorDAO().getById( similarAuthor.split( "->" )[0] ).getCitedBy() );
			similarAuthorMap.put( "publicationsNumber", persistenceStrategy.getAuthorDAO().getById( similarAuthor.split( "->" )[0] ).getNoPublication() );
			similarAuthorMap.put( "hindex", persistenceStrategy.getAuthorDAO().getById( similarAuthor.split( "->" )[0] ).getHindex() );
			similarAuthorMap.put( "isAdded", persistenceStrategy.getAuthorDAO().getById( similarAuthor.split( "->" )[0] ).isAdded() );
			similarAuthorMap.put( "similarity", similarAuthor.split( "->" )[1] );

			// add into list
			similarAuthorList.add( similarAuthorMap );
		}

		// prepare list of object map containing similarAuthor details
		List<Map<String, Object>> similarAuthorListPaging = new ArrayList<Map<String, Object>>();

		int position = 0;
		for ( Map<String, Object> similarAuthor : similarAuthorList )
		{
			if ( position >= startPage && similarAuthorListPaging.size() < maxresult )
			{
				similarAuthorListPaging.add( similarAuthor );
			}
		}

		// remove unnecessary result

		// put author in response
		responseMap.put( "author", createAuthorMap( author ) );

		// put similarAuthor to responseMap
		responseMap.put( "countTotal", similarAuthorList.size() );
		responseMap.put( "count", similarAuthorListPaging.size() );
		responseMap.put( "similarAuthors", similarAuthorListPaging );

		return responseMap;
	}

	@Override
	public Map<String, Object> getResearcherSimilarAuthorTopicLevelMap( Author author, int startPage, int maxresult )
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		Map<String, List<String>> similarAuthors = new LinkedHashMap<String, List<String>>();
		similarAuthors = palmAnalytics.getNGrams().runSimilarEntitiesTopicLevel( author.getId().toString(), path, "Authors", 50, maxresult, 3, false );

		// get the id, degree of similarity, topics proportions
		// put them into the map
		// prepare list of object map containing similarAuthor details
		List<Map<String, Object>> similarAuthorList = new ArrayList<Map<String, Object>>();

		for ( Entry<String, List<String>> similar : similarAuthors.entrySet() )
		{
			if ( !persistenceStrategy.getAuthorDAO().getById( similar.getKey().split( "->" )[0] ).equals( author ) )
			{
			Map<String, Object> similarAuthorMap = new LinkedHashMap<String, Object>();

			// insert the initial basic information
			similarAuthorMap.put( "id", similar.getKey().split( "->" )[0] );
			similarAuthorMap.put( "name", persistenceStrategy.getAuthorDAO().getById( similar.getKey().split( "->" )[0] ).getName() );
			if ( persistenceStrategy.getAuthorDAO().getById( similar.getKey().split( "->" )[0] ).getInstitution() != null )
				similarAuthorMap.put( "affiliation", persistenceStrategy.getAuthorDAO().getById( similar.getKey().split( "->" )[0] ).getInstitution().getName() );
			if ( persistenceStrategy.getAuthorDAO().getById( similar.getKey().split( "->" )[0] ).getPhotoUrl() != null )
				similarAuthorMap.put( "photo", persistenceStrategy.getAuthorDAO().getById( similar.getKey().split( "->" )[0] ).getPhotoUrl() );
				if ( persistenceStrategy.getAuthorDAO().getById( similar.getKey().split( "->" )[0] ).getAcademicStatus() != null )
					similarAuthorMap.put( "status", persistenceStrategy.getAuthorDAO().getById( similar.getKey().split( "->" )[0] ).getAcademicStatus() );

				similarAuthorMap.put( "citedBy", persistenceStrategy.getAuthorDAO().getById( similar.getKey().split( "->" )[0] ).getCitedBy() );
				similarAuthorMap.put( "publicationsNumber", persistenceStrategy.getAuthorDAO().getById( similar.getKey().split( "->" )[0] ).getNoPublication() );
				similarAuthorMap.put( "hindex", persistenceStrategy.getAuthorDAO().getById( similar.getKey().split( "->" )[0] ).getHindex() );
				similarAuthorMap.put( "isAdded", persistenceStrategy.getAuthorDAO().getById( similar.getKey().split( "->" )[0] ).isAdded() );
				similarAuthorMap.put( "similarity", similar.getKey().split( "->" )[1] );

			// construct the map for the list of topics
			List<Object> topicleveldetail = new ArrayList<Object>();
			for ( String topic : similar.getValue() )
			{
				Map<String, Object> topicproportions = new LinkedHashMap<String, Object>();
				topicproportions.put( "name", topic.split( "_-_" )[0] );
					topicproportions.put( "value", Math.round( ( Double.parseDouble( topic.split( "_-_" )[1] ) * 100 ) / 100 ) );

				topicleveldetail.add( topicproportions );
			}

			similarAuthorMap.put( "topicdetail", topicleveldetail );
			// add into list
			similarAuthorList.add( similarAuthorMap );
			}
		}

		// prepare list of object map containing similarAuthor details
		List<Map<String, Object>> similarAuthorListPaging = new ArrayList<Map<String, Object>>();

		int position = 0;
		System.out.println( "similarAuthorList.size() = " + similarAuthorList.size() );
		for ( Map<String, Object> similarAuthor : similarAuthorList )
		{
			if ( position >= startPage && similarAuthorListPaging.size() < maxresult )
			{
				similarAuthorListPaging.add( similarAuthor );
			}
		}

		// put author in response
		responseMap.put( "author", createAuthorMap( author ) );

		// put similarAuthor to responseMap
		responseMap.put( "countTotal", similarAuthorList.size() );
		responseMap.put( "count", similarAuthorListPaging.size() );
		responseMap.put( "similarAuthors", similarAuthorListPaging );

		return responseMap;
	}

@Override
public Map<String, Object> getResearcherSimilarAuthorTopicLevelRevised( Author author, int startPage, int maxresult ) throws NullPointerException, IOException
{
	// researchers list container
	Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
	
	// find the list of similar authors
		 List<String> similarEntities = new ArrayList<String>();
		 similarEntities = palmAnalytics.getNGrams().runSimilarEntities(
		 author.getId().toString(), path, "Authors", 50, maxresult, 3, false
		 );
		
	HashMap<String, List<String>> similarEntities1 = new HashMap<String, List<String>>();
//		similarEntities1 = palmAnalytics.getNGrams().runSimilarEntitiesContributorsTopicLevel( author.getId().toString(), path, "Authors", 50, maxresult, 3, false );
//
//	
//		List<Map<String, Object>> similarEntitiesListMap = new ArrayList<Map<String, Object>>();
//
//		Double trashold = 0.5;
//	for( Map.Entry<String, List<String>> similarAuthor : similarEntities1.entrySet() ){
//		List<String> similarAuthorValues = similarAuthor.getValue();
//			Double similarityDegree = Double.parseDouble( similarAuthorValues.get( 0 ) );
//
//			Map<String, Object> entity = new HashMap<String, Object>();
//			entity.put( "author_id", similarAuthor.getKey() );
//			entity.put( "similarityDegree", similarityDegree );
//
//			if ( similarityDegree > trashold )
//			{
//				List<Map<String, Object>> topicList = new ArrayList<Map<String, Object>>();
//
//				for ( String topics : similarAuthorValues )
//				{
//					String[] topicvalue = topics.split( "_-_" );
//
//					if ( topicvalue.length > 1 )
//					{
//						Map<String, Object> topicMap = new HashMap<String, Object>();
//
//						if ( Double.parseDouble( topicvalue[1] ) > 0 )
//						{
//							topicMap.put( "name", topicvalue[0] );
//							topicMap.put( "value", topicvalue[1] );
//							topicList.add( topicMap );
//						}
//					}
//				}
//				entity.put( "topics", topicList );
//			}
//
//			if ( similarityDegree > trashold )
//				similarEntitiesListMap.add( entity );
//	}
//	
//		List<Map<String, Object>> similarAuthorList1 = new ArrayList<Map<String, Object>>();
//	
//	// run for each of the entities of the list the weightedTopic Composition
//		for ( Map<String, Object> entity : similarEntitiesListMap )
//		{
//			String entityId = (String) entity.get( "author_id" );
//			Author similarAuthor = (Author) persistenceStrategy.getAuthorDAO().getById( entityId );
//
//			Map<String, Object> similarAuthorMap = new LinkedHashMap<String, Object>();
//
//			// insert the initial basic information
//			similarAuthorMap.put( "id", entityId );
//			similarAuthorMap.put( "name", similarAuthor.getName() );
//
//			if ( similarAuthor.getInstitution() != null )
//				similarAuthorMap.put( "affiliation", similarAuthor.getInstitution().getName() );
//
//			if ( similarAuthor.getPhotoUrl() != null )
//				similarAuthorMap.put( "photo", similarAuthor.getPhotoUrl() );
//
//			if ( similarAuthor.getAcademicStatus() != null )
//				similarAuthorMap.put( "status", similarAuthor.getAcademicStatus() );
//
//			similarAuthorMap.put( "citedBy", similarAuthor.getCitedBy() );
//			similarAuthorMap.put( "publicationsNumber", similarAuthor.getNoPublication() );
//			similarAuthorMap.put( "hindex", similarAuthor.getHindex() );
//			similarAuthorMap.put( "isAdded", similarAuthor.isAdded() );
//
//			similarAuthorMap.put( "similarity", entity.get( "similarityDegree" ) );
//			
//			similarAuthorMap.put( "topics", (List<Map<String, Object>>) entity.get( "topics" ) );
//			
//			similarAuthorList1.add( similarAuthorMap );
//		}
			
		 List<Map<String, Object>> similarAuthorList = new
		 ArrayList<Map<String, Object>>();
		
		 // get the list of words for the author
		 List<String> authortopicWords = new ArrayList<String>();
		 for (String entity : similarEntities){
		 if(entity.split("->")[0].equals(author.getId()))
		 authortopicWords = new ArrayList<String>(
						palmAnalytics.getNGrams().runweightedTopicComposition( path, "Authors", entity.split( "->" )[0], 10, 10, 10, palmAnalytics.getNGrams().dateCheckCriteria( path, "Authors", author.getId().toString() ), false ).keySet() );
		}
		
		 // run for each of the entities of the list the weightedTopic
		// Composition
		 for (String entity : similarEntities){
		 if(!entity.split("->")[0].equals(author.getId())){
				List<String> similartopicWords = new ArrayList<String>( palmAnalytics.getNGrams().runweightedTopicComposition( path, "Authors", entity.split( "->" )[0], 10, 10, 10, palmAnalytics.getNGrams().dateCheckCriteria( path, "Authors", author.getId().toString() ), false ).keySet() );

				Map<String, Object> similarAuthorMap = new LinkedHashMap<String, Object>();

				// insert the initial basic information
				similarAuthorMap.put( "id", entity.split( "->" )[0] );
				similarAuthorMap.put( "name", persistenceStrategy.getAuthorDAO().getById( entity.split( "->" )[0] ).getName() );
				if ( persistenceStrategy.getAuthorDAO().getById( entity.split( "->" )[0] ).getInstitution() != null )
					similarAuthorMap.put( "affiliation", persistenceStrategy.getAuthorDAO().getById( entity.split( "->" )[0] ).getInstitution().getName() );
				if ( persistenceStrategy.getAuthorDAO().getById( entity.split( "->" )[0] ).getPhotoUrl() != null )
					similarAuthorMap.put( "photo", persistenceStrategy.getAuthorDAO().getById( entity.split( "->" )[0] ).getPhotoUrl() );
				if ( persistenceStrategy.getAuthorDAO().getById( entity.split( "->" )[0] ).getAcademicStatus() != null )
					similarAuthorMap.put( "status", persistenceStrategy.getAuthorDAO().getById( entity.split( "->" )[0] ).getAcademicStatus() );

				similarAuthorMap.put( "citedBy", persistenceStrategy.getAuthorDAO().getById( entity.split( "->" )[0] ).getCitedBy() );
				similarAuthorMap.put( "publicationsNumber", persistenceStrategy.getAuthorDAO().getById( entity.split( "->" )[0] ).getNoPublication() );
				similarAuthorMap.put( "hindex", persistenceStrategy.getAuthorDAO().getById( entity.split( "->" )[0] ).getHindex() );
				similarAuthorMap.put( "isAdded", persistenceStrategy.getAuthorDAO().getById( entity.split( "->" )[0] ).isAdded() );

				// return a HashMap with the similar words and similarity degree
				HashMap<String, Double> similarDetail = comparePhraseTopicLevel( authortopicWords, similartopicWords );
				similarAuthorMap.put( "similarity", similarDetail.entrySet().iterator().next().getValue() );

				// construct the map for the list of topics
				List<Object> topicleveldetail = new ArrayList<Object>();
				Map<String, Object> topicproportions = new LinkedHashMap<String, Object>();
				topicproportions.put( "name", similarDetail.keySet().toArray()[0] );
				topicproportions.put( "value", "" );
				topicleveldetail.add( topicproportions );

				similarAuthorMap.put( "topicdetail", topicleveldetail );
				// add into list
				// check if the similarity is significant
				// The threshhold is decided heuristically
				double a = similarDetail.entrySet().iterator().next().getValue();
				if ( a > 0.029 )
				{
					// add into list if the similarity
					similarAuthorList.add( similarAuthorMap );
				}
				else
				{
					continue;
				}

			}
	}
	
	Collections.sort( similarAuthorList, new SimilarityComparator() );
	// prepare list of object map containing similarAuthor details
	List<Map<String, Object>> similarAuthorListPaging = new ArrayList<Map<String, Object>>();

	int position = 0;
		for ( Map<String, Object> similarAuthor : similarAuthorList )
		{
			if ( position >= startPage && similarAuthorListPaging.size() < maxresult )
			{
			similarAuthorListPaging.add( similarAuthor );
		}
	}

		// put author in response
		responseMap.put( "author", createAuthorMap( author ) );

	// put similarAuthor to responseMap
		responseMap.put( "countTotal", similarAuthorList.size() );
		responseMap.put( "count", similarAuthorListPaging.size() );
		responseMap.put( "similarAuthors", similarAuthorListPaging );
	
		return responseMap;
}

// compare the two authors in word level
private HashMap<String, Double> comparePhraseTopicLevel(List<String> authortopicWords, List<String> similartopicWords) {
	HashMap<String, Double> result = new HashMap<String,Double>();
	String topic = "";
	int count = 0;
	
	for (String authorphrase : authortopicWords){
		for (String similarphrase : similartopicWords){
			if (authorphrase.contains(similarphrase)){
				topic +=  authorphrase + ",";
				count++;
			}
			else if(similarphrase.contains(authorphrase)){
					topic +=  authorphrase + ",";
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
		
	
	result.put(topic, (double)topicArray.length/authortopicWords.size());
	
	return result;
}

	private Map<String, Object> createAuthorMap( Author author )
	{
		Map<String, Object> authorMap = new HashMap<String, Object>();

		authorMap.put( "id", author.getId() );
		authorMap.put( "name", author.getName() );
		authorMap.put( "isAdded", author.isAdded() );

		if ( author.getPhotoUrl() != null )
			authorMap.put( "photo", author.getPhotoUrl() );

		if ( author.getInstitution() != null )
		{
			Map<String, String> affiliationData = new HashMap<String, String>();

			affiliationData.put( "institution", author.getInstitution().getName() );

			if ( author.getInstitution().getLocation() != null )
			{
				affiliationData.put( "country", author.getInstitution().getLocation().getCountry().getName() );
			}
			authorMap.put( "aff", affiliationData );
		}
		authorMap.put( "hindex", author.getHindex() );

		return authorMap;
	}
}

package de.rwth.i9.palm.feature.academicevent;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.helper.MapUtil;
import de.rwth.i9.palm.helper.comparator.PublicationByPageComparator;
import de.rwth.i9.palm.helper.comparator.PublicationTopicByExtractionServiceTypeComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationTopic;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;
import de.rwth.i9.palm.topicextraction.service.TopicExtractionService;

@Component
public class EventPublicationImpl implements EventPublication
{

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private TopicExtractionService topicExtractionService;

	@Autowired
	private ApplicationService applicationService;

	@SuppressWarnings( "unchecked" )
	@Override
	public Map<String, Object> getPublicationListByEventId( String eventId, String query, String publicationId )
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// get author
		Event event = persistenceStrategy.getEventDAO().getById( eventId );

		if ( event == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - venue not found" );
			return responseMap;
		}

		if ( event.getPublications() == null || event.getPublications().isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - venue contain no publication" );
			return responseMap;
		}

		responseMap.put( "status", "ok" );

		if ( publicationId != null )
			responseMap.put( "publicationId", publicationId );

		List<Map<String, Object>> publicationList = new ArrayList<Map<String, Object>>();

		// get data name
		String title = event.getEventGroup().getName();
		if ( event.getEventGroup().getNotation() != null && !title.equals( event.getEventGroup().getNotation() ) )
			title = event.getEventGroup().getNotation();



		Map<String, Object> eventMapQuery = new LinkedHashMap<String, Object>();
		eventMapQuery.put( "id", event.getId() );
		eventMapQuery.put( "title", title );

		responseMap.put( "event", eventMapQuery );

		List<Publication> publications = null;
		// get publication list
		if ( query != null && !query.equals( "" ) )
		{
			responseMap.put( "query", query );
			Map<String, Object> publicationsMap = persistenceStrategy.getPublicationDAO().getPublicationWithPaging( query, "all", null, event, null, null, "all", null );
			publications = (List<Publication>) publicationsMap.get( "publications" );
			if ( publications != null )
				responseMap.put( "count", publications.size() );
			else
				responseMap.put( "count", 0 );
		}
		else
		{
			publications = new ArrayList<Publication>( event.getPublications() );
		}

		// sort based on period
		if ( publications == null )
			return responseMap;

		Collections.sort( publications, new PublicationByPageComparator() );

		for ( Publication publication : publications )
		{

			// put publication detail
			Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();
			publicationMap.put( "id", publication.getId() );
			publicationMap.put( "title", publication.getTitle() );
			if ( publication.getAbstractText() != null )
				publicationMap.put( "abstract", publication.getAbstractText() );
			// coauthor
			List<Map<String, Object>> coathorList = new ArrayList<Map<String, Object>>();
			for ( Author author : publication.getCoAuthors() )
			{
				Map<String, Object> authorMap = new LinkedHashMap<String, Object>();
				authorMap.put( "id", author.getId() );
				authorMap.put( "name", WordUtils.capitalize( author.getName() ) );
				if ( author.getInstitution() != null )
					authorMap.put( "aff", author.getInstitution().getName() );
				if ( author.getPhotoUrl() != null )
					authorMap.put( "photo", author.getPhotoUrl() );

				authorMap.put( "isAdded", author.isAdded() );

				coathorList.add( authorMap );
			}
			publicationMap.put( "coauthor", coathorList );

			if ( publication.getKeywordText() != null )
				publicationMap.put( "keyword", publication.getKeywordText() );

			if ( publication.getPublicationDate() != null )
			{
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
				publicationMap.put( "date", sdf.format( publication.getPublicationDate() ) );
			}

			if ( publication.getLanguage() != null )
				publicationMap.put( "language", publication.getLanguage() );

			if ( publication.getCitedBy() != 0 )
				publicationMap.put( "cited", publication.getCitedBy() );

			if ( publication.getPublicationType() != null )
			{
				String publicationType = publication.getPublicationType().toString();
				publicationType = publicationType.substring( 0, 1 ).toUpperCase() + publicationType.substring( 1 );
				publicationMap.put( "type", publicationType );
			}

			if ( publication.getEvent() != null )
			{
				Map<String, Object> eventMap = new LinkedHashMap<String, Object>();
				eventMap.put( "id", publication.getEvent().getId() );
				eventMap.put( "name", publication.getEvent().getEventGroup().getName() );
				publicationMap.put( "event", eventMap );
			}

			if ( publication.getAdditionalInformation() != null )
				publicationMap.putAll( publication.getAdditionalInformationAsMap() );

			if ( publication.getStartPage() > 0 )
				publicationMap.put( "pages", publication.getStartPage() + " - " + publication.getEndPage() );

			if ( publication.getAbstractText() != null || publication.getKeywordText() != null )
				publicationMap.put( "contentExist", true );
			else
				publicationMap.put( "contentExist", false );

			publicationList.add( publicationMap );
		}
		responseMap.put( "publications", publicationList );

		return responseMap;
	}

	@Override
	public Map<String, Object> getPublicationTopListByEventId( String eventId, String pid, Integer maxresult, String orderBy ) throws UnsupportedEncodingException, InterruptedException, URISyntaxException, ExecutionException
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// get author
		Event event = persistenceStrategy.getEventDAO().getById( eventId );

		if ( event == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - venue not found" );
			return responseMap;
		}

		if ( event.getPublications() == null || event.getPublications().isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - venue contain no publication" );
			return responseMap;
		}

		responseMap.put( "status", "ok" );

		List<Map<String, Object>> publicationList = new ArrayList<Map<String, Object>>();

		// get data name
		String title = event.getEventGroup().getName();
		if ( event.getEventGroup().getNotation() != null && !title.equals( event.getEventGroup().getNotation() ) )
			title = event.getEventGroup().getNotation();

		Map<String, Object> eventMapQuery = new LinkedHashMap<String, Object>();
		eventMapQuery.put( "id", event.getId() );
		eventMapQuery.put( "title", title );

		responseMap.put( "event", eventMapQuery );

		List<Publication> publications = null;
		// get publication list
		
		Map<String, Object> publicationsMap = persistenceStrategy.getPublicationDAO().getPublicationWithPaging( "", "all", null, event, null, maxresult, "all", orderBy );
		publications = (List<Publication>) publicationsMap.get( "publications" );
		
		if ( publications != null )
			responseMap.put( "count", publications.size() );
		else
			responseMap.put( "count", 0 );
		// sort based on period
		if ( publications == null )
			return responseMap;

		Collections.sort( publications, new PublicationByPageComparator() );

		for ( Publication publication : publications )
		{

			// put publication detail
			Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();
			publicationMap.put( "id", publication.getId() );
			publicationMap.put( "title", publication.getTitle() );

			if ( publication.getAbstractText() != null )
				publicationMap.put( "abstract", publication.getAbstractText() );

			publicationMap.put( "topics", getPublicationTopics( publication, pid, 50 ) );

			// coauthor
			List<Map<String, Object>> coathorList = new ArrayList<Map<String, Object>>();
			for ( Author author : publication.getCoAuthors() )
			{
				Map<String, Object> authorMap = new LinkedHashMap<String, Object>();
				authorMap.put( "id", author.getId() );
				authorMap.put( "name", WordUtils.capitalize( author.getName() ) );
				if ( author.getInstitution() != null )
					authorMap.put( "aff", author.getInstitution().getName() );
				if ( author.getPhotoUrl() != null )
					authorMap.put( "photo", author.getPhotoUrl() );

				authorMap.put( "isAdded", author.isAdded() );

				coathorList.add( authorMap );
			}
			publicationMap.put( "coauthor", coathorList );

			if ( publication.getKeywordText() != null )
				publicationMap.put( "keyword", publication.getKeywordText() );

			if ( publication.getPublicationDate() != null )
			{
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
				publicationMap.put( "date", sdf.format( publication.getPublicationDate() ) );
			}

			if ( publication.getLanguage() != null )
				publicationMap.put( "language", publication.getLanguage() );

			if ( publication.getCitedBy() != 0 )
				publicationMap.put( "cited", publication.getCitedBy() );

			if ( publication.getPublicationType() != null )
			{
				String publicationType = publication.getPublicationType().toString();
				publicationType = publicationType.substring( 0, 1 ).toUpperCase() + publicationType.substring( 1 );
				publicationMap.put( "type", publicationType );
			}

			if ( publication.getEvent() != null )
			{
				Map<String, Object> eventMap = new LinkedHashMap<String, Object>();
				eventMap.put( "id", publication.getEvent().getId() );
				eventMap.put( "name", publication.getEvent().getEventGroup().getName() );
				publicationMap.put( "event", eventMap );
			}

			if ( publication.getAdditionalInformation() != null )
				publicationMap.putAll( publication.getAdditionalInformationAsMap() );

			if ( publication.getStartPage() > 0 )
				publicationMap.put( "pages", publication.getStartPage() + " - " + publication.getEndPage() );

			if ( publication.getAbstractText() != null || publication.getKeywordText() != null )
				publicationMap.put( "contentExist", true );
			else
				publicationMap.put( "contentExist", false );

			publicationList.add( publicationMap );
		}
		responseMap.put( "publications", publicationList );

		return responseMap;
}

	private List<Object> getPublicationTopics( Publication publication, String pid, Integer maxTermRetrieve ) throws UnsupportedEncodingException, InterruptedException, URISyntaxException, ExecutionException
	{
	// check if publication is updated
	if ( publication.getAbstractText() != null ){
		applicationService.putProcessLog( pid, "Check if topics are up to date <br>", "append" );
		if ( publication.isContentUpdated() )
			applicationService.putProcessLog( pid, "Updating topics <br>", "append" );
		topicExtractionService.extractTopicFromSpecificPublication( publication );
	}

	// put publication topic
	List<PublicationTopic> publicationTopics = new ArrayList<PublicationTopic>( publication.getPublicationTopics() );

	if ( publicationTopics.isEmpty() )
		return new ArrayList<Object>();
			
	// sort publicationTopic in natural order based on ExtractionServiceType
	Collections.sort( publicationTopics, new PublicationTopicByExtractionServiceTypeComparator() );

	// publicationTopic list for json response
	List<Object> publicationTopicList = new ArrayList<Object>();

	// loop to each PublicationTopic
	for ( PublicationTopic publicationTopic : publicationTopics ){
		Map<String, Object> publicationTopicMap = new LinkedHashMap<String, Object>();
		publicationTopicMap.put( "id", publicationTopic.getId() );
		publicationTopicMap.put( "extractionDate", publicationTopic.getExtractionDate() );
		publicationTopicMap.put( "extractor", publicationTopic.getExtractionServiceType().toString() );
		// get the term value
		// sort term value and cut until maxRetrieve
		if ( publicationTopic.getTermValues() == null )
			continue;

		Map<String, Double> sortedMap = MapUtil.sortByValue( publicationTopic.getTermValues() );
		sortedMap.putAll( publicationTopic.getTermValues() );

		List<Object> termValueList = new ArrayList<Object>();
		int mapIndex = 0;
		for ( Map.Entry<String, Double> entry : sortedMap.entrySet() ){
			Map<String, Object> termValue = new LinkedHashMap<String, Object>();
			termValue.put( "term", entry.getKey() );
			termValue.put( "value", entry.getValue() );
			termValueList.add( termValue );

			mapIndex++;
			if ( mapIndex >= maxTermRetrieve )
				break;
		}
		publicationTopicMap.put( "termvalues", termValueList );

		// add into list
		publicationTopicList.add( publicationTopicMap );
	}
	return publicationTopicList;
}
}
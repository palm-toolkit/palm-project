package de.rwth.i9.palm.feature.academicevent;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationTopic;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class EventTopResearcherImpl implements EventTopResearcher
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@SuppressWarnings( "unchecked" )
	@Override
	public Map<String, Object> getResearcherListByEventId( String query, String eventId, String pid, Integer maxresult, String orderBy ) throws UnsupportedEncodingException, InterruptedException, URISyntaxException, ExecutionException
	{
		Map<String, Object> responseMap = new HashMap<String, Object>();

		Event event = persistenceStrategy.getEventDAO().getById( eventId );

		if ( event == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - venue not found" );
			return responseMap;
		}

		List<Publication> eventPublications = event.getPublications();
		if ( eventPublications == null || eventPublications.isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - venue contain no publication" );
			return responseMap;
		}

		responseMap.put( "status", "ok" );

		// get data name
		String title = event.getEventGroup().getName();

		if ( event.getEventGroup().getNotation() != null && !title.equals( event.getEventGroup().getNotation() ) )
			title = event.getEventGroup().getNotation();

		Map<String, Object> eventMapQuery = new LinkedHashMap<String, Object>();
		eventMapQuery.put( "id", event.getId() );
		eventMapQuery.put( "title", title );

		responseMap.put( "event", eventMapQuery );

		// get event participants
		List<Map<String, Object>> eventParticipantsMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> participantsMap = persistenceStrategy.getEventDAO().getParticipantsEvent( query, event, null, maxresult, orderBy );
		List<Author> participantsList = (List<Author>) participantsMap.get( "participants" );

		for ( Author participant : participantsList )
		{
			Map<String, Object> authorMap = new HashMap<String, Object>();
			authorMap.put( "id", participant.getId() );
			authorMap.put( "name", participant.getName() );
			authorMap.put( "hindex", participant.getHindex() );
			if ( participant.getInstitution() != null )
			{
				Map<String, String> affiliationData = new HashMap<String, String>();

				affiliationData.put( "institution", participant.getInstitution().getName() );

				if ( participant.getInstitution().getLocation() != null )
				{
					affiliationData.put( "country", participant.getInstitution().getLocation().getCountry().getName() );
				}

				authorMap.put( "aff", affiliationData );
			}

			if ( participant.getPhotoUrl() != null )
				authorMap.put( "photo", participant.getPhotoUrl() );

			authorMap.put( "isAdded", participant.isAdded() );
			authorMap.put( "status", participant.getAcademicStatus() );

			Map<String, Object> authorPublicationEventMap = persistenceStrategy.getPublicationDAO().getPublicationWithPaging( "", "all", participant, event, null, null, "all", "citation" );
			List<Publication> authorPublicationEventList = (List<Publication>) authorPublicationEventMap.get( "publications" );

			int eventPublCitations = 0;
			List<Map<String, Object>> publicationsMapList = new ArrayList<Map<String, Object>>();

			for ( Publication publication : authorPublicationEventList )
			{
				publicationsMapList.add( this.getPublicationDetails( publication ) );
				eventPublCitations += publication.getCitedBy();
			}
			authorMap.put( "publications", publicationsMapList );
			authorMap.put( "publicationsCitations", eventPublCitations );
			authorMap.put( "publicationsNumber", authorPublicationEventList.size() );

			authorMap.put( "citedBy", participant.getCitedBy() );

			eventParticipantsMap.add( authorMap );
		}

		responseMap.put( "participants", eventParticipantsMap );

		return responseMap;
	}

	public Map<String, Object> getPublicationDetails( Publication publication )
	{
		Map<String, Object> publicationDetails = new HashMap<String, Object>();
		if ( publication.getTitle() != null )
			publicationDetails.put( "title", publication.getTitle() );

		if ( publication.getPublicationDate() != null )
		{
			SimpleDateFormat sdf = new SimpleDateFormat( publication.getPublicationDateFormat() );
			publicationDetails.put( "date", sdf.format( publication.getPublicationDate() ) );
			publicationDetails.put( "dateFormat", publication.getPublicationDateFormat() );
		}

		publicationDetails.put( "id", publication.getId() );
		publicationDetails.put( "type", publication.getPublicationType() );
		publicationDetails.put( "abstract", publication.getAbstractText() );
		publicationDetails.put( "cited", publication.getCitedBy() );

		if ( publication.getAbstractText() != null || publication.getKeywordText() != null )
			publicationDetails.put( "contentExist", true );
		else
			publicationDetails.put( "contentExist", false );

		// publication coauthors
		List<Map<String, Object>> coauthors = new ArrayList<Map<String, Object>>();
		if ( publication.getCoAuthors() != null )
			for ( Author coauthor : publication.getCoAuthors() )
			{
				Map<String, Object> coauthorMap = new HashMap<String, Object>();
				coauthorMap.put( "name", coauthor.getName() );
				coauthors.add( coauthorMap );
			}
		publicationDetails.put( "coauthor", coauthors );

		// publication topics
		List<Map<String, Object>> topics = new ArrayList<Map<String, Object>>();
		if ( publication.getPublicationTopics() != null )
			for ( PublicationTopic publicationTopic : publication.getPublicationTopics() )
			{
				Map<String, Object> publicationTopicMap = new HashMap<String, Object>();
				publicationTopicMap.put( "termstring", publicationTopic.getTermString() );
				publicationTopicMap.put( "termvalues", publicationTopic.getTermValues() );
				topics.add( publicationTopicMap );
			}
		publicationDetails.put( "topics", topics );

		return publicationDetails;
	}
}

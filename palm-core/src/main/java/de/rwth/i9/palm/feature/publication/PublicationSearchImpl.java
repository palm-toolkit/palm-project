package de.rwth.i9.palm.feature.publication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class PublicationSearchImpl implements PublicationSearch
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Override
	public Map<String, Object> getPublicationListByQuery( String query, String publicationType, String authorId, String eventId, Integer page, Integer maxresult, String source, String fulltextSearch, String year, String orderBy )
	{
		Map<String, Object> publicationMap;

		Author author = null;
		if ( authorId != null )
			author = persistenceStrategy.getAuthorDAO().getById( authorId );

		Event event = null;
		if ( eventId != null )
			event = persistenceStrategy.getEventDAO().getById( eventId );

		// get the publication
		if( fulltextSearch.equals( "yes" )){
			publicationMap = persistenceStrategy.getPublicationDAO().getPublicationByFullTextSearchWithPaging( query, publicationType, author, event, page, maxresult, year, orderBy );
		} else {
			publicationMap = persistenceStrategy.getPublicationDAO().getPublicationWithPaging( query, publicationType, author, event, page, maxresult, year, orderBy );
		}

		return publicationMap;
	}

	@Override
	public Map<String, Object> printJsonOutput( Map<String, Object> responseMap, List<Publication> publications )
	{
		if ( publications == null || publications.isEmpty() )
		{
			responseMap.put( "count", 0 );
			return responseMap;
		}

		List<Map<String, Object>> publicationList = new ArrayList<Map<String, Object>>();

		// preparing data format
		DateFormat dateFormat = new SimpleDateFormat( "yyyy", Locale.ENGLISH );

		for ( Publication publication : publications )
		{
			Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();
			publicationMap.put( "id", publication.getId() );

			if ( publication.getPublicationType() != null )
			{
				String publicationType = publication.getPublicationType().toString();
				publicationType = publicationType.substring( 0, 1 ).toUpperCase() + publicationType.toLowerCase().substring( 1 );
				publicationMap.put( "type", publicationType );
			}

			publicationMap.put( "title", publication.getTitle() );
			if ( publication.getCitedBy() > 0 )
				publicationMap.put( "cited", Integer.toString( publication.getCitedBy() ) );

			if ( publication.getPublicationDate() != null )
				publicationMap.put( "date published", dateFormat.format( publication.getPublicationDate() ) );

			List<Object> authorObject = new ArrayList<Object>();

			for ( Author author : publication.getCoAuthors() )
			{
				Map<String, Object> authorMap = new LinkedHashMap<String, Object>();
				authorMap.put( "id", author.getId() );
				authorMap.put( "name", WordUtils.capitalize( author.getName() ) );
				if ( author.getInstitution() != null )
					authorMap.put( "aff", author.getInstitution().getName() );
				// if ( author.getPhotoUrl() != null )
				// authorMap.put( "photo", author.getPhotoUrl() );

				authorMap.put( "isAdded", author.isAdded() );

				authorObject.add( authorMap );
			}
			publicationMap.put( "authors", authorObject );

			if ( publication.getPublicationDate() != null )
			{
				SimpleDateFormat sdf = new SimpleDateFormat( publication.getPublicationDateFormat() );
				publicationMap.put( "date", sdf.format( publication.getPublicationDate() ) );
			}

			if ( publication.getLanguage() != null )
				publicationMap.put( "language", publication.getLanguage() );

			if ( publication.getCitedBy() != 0 )
				publicationMap.put( "cited", publication.getCitedBy() );

			if ( publication.getEvent() != null )
			{
				Map<String, Object> eventMap = new LinkedHashMap<String, Object>();
				eventMap.put( "id", publication.getEvent().getId() );
				String eventName = publication.getEvent().getEventGroup().getName();
				if ( publication.getEvent().getEventGroup().getNotation() != null && 
						!publication.getEvent().getEventGroup().getNotation().isEmpty() && 
						!publication.getEvent().getEventGroup().getNotation().equals( eventName ) )
					eventName += " - " + publication.getEvent().getEventGroup().getNotation() + ",";
				eventMap.put( "name", eventName );
				eventMap.put( "isAdded", publication.getEvent().isAdded() );
				publicationMap.put( "event", eventMap );
			}

			if ( publication.getAdditionalInformation() != null )
				publicationMap.putAll( publication.getAdditionalInformationAsMap() );

			if ( publication.getStartPage() > 0 )
				publicationMap.put( "pages", publication.getStartPage() + " - " + publication.getEndPage() );

			publicationList.add( publicationMap );
		}

		responseMap.put( "count", publicationList.size() );
		responseMap.put( "publications", publicationList );

		return responseMap;
	}

	@Override
	public Map<String, Object> printElementAsJsonOutput( Publication publication )
	{
		// preparing data format
		DateFormat dateFormat = new SimpleDateFormat( "yyyy", Locale.ENGLISH );

		Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();
		publicationMap.put( "id", publication.getId() );

		if ( publication.getPublicationType() != null )
		{
			String publicationType = publication.getPublicationType().toString();
			publicationType = publicationType.substring( 0, 1 ).toUpperCase() + publicationType.toLowerCase().substring( 1 );
			publicationMap.put( "type", publicationType );
		}

		publicationMap.put( "title", publication.getTitle() );
		if ( publication.getCitedBy() > 0 )
			publicationMap.put( "cited", Integer.toString( publication.getCitedBy() ) );

		if ( publication.getPublicationDate() != null )
			publicationMap.put( "date published", dateFormat.format( publication.getPublicationDate() ) );

		List<Object> authorObject = new ArrayList<Object>();

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

			authorObject.add( authorMap );
		}
		publicationMap.put( "authors", authorObject );

		if ( publication.getPublicationDate() != null )
		{
			SimpleDateFormat sdf = new SimpleDateFormat( publication.getPublicationDateFormat() );
			publicationMap.put( "date", sdf.format( publication.getPublicationDate() ) );
		}

		if ( publication.getLanguage() != null )
			publicationMap.put( "language", publication.getLanguage() );

		if ( publication.getCitedBy() != 0 )
			publicationMap.put( "cited", publication.getCitedBy() );

		if ( publication.getEvent() != null )
		{
			Map<String, Object> eventMap = new LinkedHashMap<String, Object>();
			eventMap.put( "id", publication.getEvent().getId() );
			String eventName = publication.getEvent().getEventGroup().getName();
			if ( publication.getEvent().getEventGroup().getNotation() != null && !publication.getEvent().getEventGroup().getNotation().isEmpty() && !publication.getEvent().getEventGroup().getNotation().equals( eventName ) )
				eventName += " - " + publication.getEvent().getEventGroup().getNotation() + ",";
			eventMap.put( "name", eventName );
			eventMap.put( "isAdded", publication.getEvent().isAdded() );
			publicationMap.put( "event", eventMap );
		}

		if ( publication.getAdditionalInformation() != null )
			publicationMap.putAll( publication.getAdditionalInformationAsMap() );

		if ( publication.getStartPage() > 0 )
			publicationMap.put( "pages", publication.getStartPage() + " - " + publication.getEndPage() );

		return publicationMap;
	}

}

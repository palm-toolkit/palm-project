package de.rwth.i9.palm.feature.circle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.helper.comparator.PublicationByNoCitationComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class CircleTopPublicationImpl implements CircleTopPublication
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@SuppressWarnings( "unchecked" )
	@Override
	public Map<String, Object> getTopPublicationListByCircleId( String circleId, Integer startPage, Integer maxresult )
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// get author
		Circle targetCircle = persistenceStrategy.getCircleDAO().getById( circleId );

		if ( targetCircle == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - author not found" );
			return responseMap;
		}

		Map<String, Object> targetCircleMap = new LinkedHashMap<String, Object>();
		targetCircleMap.put( "id", targetCircle.getId() );
		targetCircleMap.put( "name", targetCircle.getName() );
		responseMap.put( "author", targetCircleMap );
		responseMap.put( "totalPublication", targetCircle.getPublications().size() );

		if ( targetCircle.getPublications() == null || targetCircle.getPublications().isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - circle contain no publications" );
			return responseMap;
		}

		// for now get publications directly from circle instead of database
		List<Publication> publications = new ArrayList<Publication>( targetCircle.getPublications() );

		// sort based on citation
		Collections.sort( publications, new PublicationByNoCitationComparator() );

		if ( publications == null || publications.isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - empty publication" );
			return responseMap;
		}

		responseMap.put( "status", "ok" );

		if ( maxresult != null )
			responseMap.put( "maxresult", maxresult );

		List<Map<String, Object>> publicationList = new ArrayList<Map<String, Object>>();

		// for now, paging is not supported yet, only consider maxresult
		int indexCounter = 0;
		for ( Publication publication : publications )
		{

			// put publication detail
			Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();
			publicationMap.put( "id", publication.getId() );
			publicationMap.put( "title", publication.getTitle() );
			// if ( publication.getAbstractText() != null )
			// publicationMap.put( "abstract", publication.getAbstractText() );
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

			// if ( publication.getKeywordText() != null )
			// publicationMap.put( "keyword", publication.getKeywordText() );

			if ( publication.getPublicationDate() != null )
			{
				SimpleDateFormat sdf = new SimpleDateFormat( publication.getPublicationDateFormat() );
				publicationMap.put( "date", sdf.format( publication.getPublicationDate() ) );
			}

			if ( publication.getLanguage() != null )
				publicationMap.put( "language", publication.getLanguage() );

			if ( publication.getCitedBy() > 0 )
			{
				publicationMap.put( "cited", publication.getCitedBy() );
				if ( publication.getCitedByUrl() != null )
					publicationMap.put( "citedUrl", publication.getCitedByUrl() );
			}

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
				if ( publication.getEvent().getEventGroup().getNotation() != null && 
						!publication.getEvent().getEventGroup().getNotation().isEmpty() && 
						!publication.getEvent().getEventGroup().getNotation().equals( publication.getEvent().getEventGroup().getName() ) )
					eventMap.put( "abbr", publication.getEvent().getEventGroup().getNotation() );
				eventMap.put( "isAdded", publication.getEvent().isAdded() );
				if ( publication.getEvent().getEventGroup() != null )
					eventMap.put( "isGroupAdded", publication.getEvent().getEventGroup().isAdded() );
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

			indexCounter++;
			if ( indexCounter > maxresult )
				break;
		}
		responseMap.put( "count", publicationList.size() );
		responseMap.put( "publications", publicationList );

		return responseMap;
	}

}

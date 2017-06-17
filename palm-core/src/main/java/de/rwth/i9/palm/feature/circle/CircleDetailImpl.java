package de.rwth.i9.palm.feature.circle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.helper.comparator.AuthorByNaturalOrderComparator;
import de.rwth.i9.palm.helper.comparator.PublicationByDateComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class CircleDetailImpl implements CircleDetail
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Override
	public Map<String, Object> getCircleDetailById( String circleId, boolean isRetrieveAuthorDetail, boolean isRetrievePublicationDetail )
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// get circle
		Circle circle = persistenceStrategy.getCircleDAO().getById( circleId );
		if ( circle == null )
		{
			responseMap.put( "status", "Error - circle not found" );
			return responseMap;
		}

		responseMap.put( "status", "ok" );

		// preparing data format
		DateFormat dateFormat = new SimpleDateFormat( "dd/mm/yyyy", Locale.ENGLISH );

		// get all of Querse properties
		Map<String, Object> queryMap = new LinkedHashMap<String, Object>();
		queryMap.put( "circleId", circle.getId() );
		queryMap.put( "isRetrieveAuthorDetail", isRetrieveAuthorDetail );
		queryMap.put( "isRetrievePublicationDetail", isRetrievePublicationDetail );
		responseMap.put( "query", queryMap );

		// get all of Circle properties
		Map<String, Object> circleMap = new LinkedHashMap<String, Object>();
		circleMap.put( "id", circle.getId() );
		circleMap.put( "name", circle.getName() );
		circleMap.put( "dateCreated", dateFormat.format( circle.getCreationDate() ) );
		circleMap.put( "description", circle.getDescription() );

		if ( circle.getCreator() != null )
		{
			Map<String, Object> creatorMap = new LinkedHashMap<String, Object>();
			creatorMap.put( "id", circle.getCreator().getId() );
			creatorMap.put( "name", WordUtils.capitalize( circle.getCreator().getName() ) );
			if ( circle.getCreator().getAuthor() != null )
				creatorMap.put( "authorId", circle.getCreator().getAuthor().getId() );

			circleMap.put( "creator", creatorMap );
		}

		if ( isRetrieveAuthorDetail && circle.getAuthors() != null )
		{
			List<Author> authors = new ArrayList<Author>();
			authors.addAll( circle.getAuthors() );

			Collections.sort( authors, new AuthorByNaturalOrderComparator() );

			circleMap.put( "numberAuthors", authors.size() );
			if ( circle.getAuthors().size() > 0 )
			{
				circleMap.put( "researchers", printCircleAuthors( authors ) );
			}
		}

		if ( isRetrievePublicationDetail && circle.getPublications() != null )
		{
			List<Publication> publications = new ArrayList<Publication>();
			publications.addAll( circle.getPublications() );

			Collections.sort( publications, new PublicationByDateComparator() );

			circleMap.put( "numberPublications", publications.size() );
			if ( circle.getPublications().size() > 0 )
			{
				circleMap.put( "publications", printCirclePublications( publications ) );
			}
		}

		// check autowired with security service here
		circleMap.put( "isLock", circle.isLock() );
		circleMap.put( "isValid", circle.isValid() );

		responseMap.put( "circle", circleMap );

		return responseMap;
	}

	private Object printCirclePublications( List<Publication> publications )
	{
		List<Map<String, Object>> publicationList = new ArrayList<Map<String, Object>>();

		for ( Publication publication : publications )
		{

			// put publication detail
			Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();
			publicationMap.put( "id", publication.getId() );
			publicationMap.put( "title", publication.getTitle() );
			//if ( publication.getAbstractText() != null )
				//publicationMap.put( "abstract", publication.getAbstractText() );
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
			publicationMap.put( "authors", coathorList );

			if ( publication.getKeywordText() != null )
				publicationMap.put( "keyword", publication.getKeywordText() );

			if ( publication.getPublicationDate() != null )
			{
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
				publicationMap.put( "date", sdf.format( publication.getPublicationDate() ) );
			}

//			if ( publication.getLanguage() != null )
//				publicationMap.put( "language", publication.getLanguage() );
//
//			if ( publication.getCitedBy() != 0 )
//				publicationMap.put( "cited", publication.getCitedBy() );

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
				eventMap.put( "isAdded", publication.getEvent().isAdded() );
				publicationMap.put( "event", eventMap );
			}

			if ( publication.getAdditionalInformation() != null )
				publicationMap.putAll( publication.getAdditionalInformationAsMap() );

			if ( publication.getStartPage() > 0 )
				publicationMap.put( "pages", publication.getStartPage() + " - " + publication.getEndPage() );

			publicationList.add( publicationMap );
		}
		return publicationList;
	}

	private Object printCircleAuthors( List<Author> authors )
	{
		List<Map<String, Object>> researcherList = new ArrayList<Map<String, Object>>();

		for ( Author researcher : authors )
		{
			Map<String, Object> researcherMap = new LinkedHashMap<String, Object>();
			researcherMap.put( "id", researcher.getId() );
			researcherMap.put( "name", WordUtils.capitalize( researcher.getName() ) );
			if ( researcher.getPhotoUrl() != null )
				researcherMap.put( "photo", researcher.getPhotoUrl() );
			if ( researcher.getAcademicStatus() != null )
				researcherMap.put( "status", researcher.getAcademicStatus() );
			if ( researcher.getInstitution() != null )
				researcherMap.put( "aff", researcher.getInstitution().getName() );

			// unnecessary information
			/*
			if ( researcher.getCitedBy() > 0 )
				researcherMap.put( "citedBy", Integer.toString( researcher.getCitedBy() ) );

			if ( researcher.getPublicationAuthors() != null )
				researcherMap.put( "publicationsNumber", researcher.getNoPublication() );
			else
				researcherMap.put( "publicationsNumber", 0 );
			String otherDetail = "";
			if ( researcher.getOtherDetail() != null )
				otherDetail += researcher.getOtherDetail();
			if ( researcher.getDepartment() != null )
				otherDetail += ", " + researcher.getDepartment();
			if ( !otherDetail.equals( "" ) )
				researcherMap.put( "detail", otherDetail );
			*/
			researcherMap.put( "isAdded", researcher.isAdded() );
			
			researcherList.add( researcherMap );
		}
		
		return researcherList;
	}

}

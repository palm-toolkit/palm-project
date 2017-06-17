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

import de.rwth.i9.palm.helper.comparator.PublicationByDateComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class CirclePublicationImpl implements CirclePublication
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Override
	public Map<String, Object> getCirclePublicationMap( Circle circle )
	{
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		List<Map<String, Object>> publicationList = new ArrayList<Map<String, Object>>();

		if ( circle.getPublications() == null || circle.getPublications().isEmpty() )
		{
			responseMap.put( "count", 0 );
			return responseMap;
		}

		// preparing data format
		DateFormat dateFormat = new SimpleDateFormat( "yyyy", Locale.ENGLISH );

		for ( Publication publication : circle.getPublications() )
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
				if ( publication.getEvent().getEventGroup().getNotation() != null )
					if ( !publication.getEvent().getEventGroup().getNotation().equals( eventName ) )
						eventName += " - " + publication.getEvent().getEventGroup().getNotation() + ",";
				eventMap.put( "name", eventName );
				eventMap.put( "isAdded", publication.getEvent().isAdded() );
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
		responseMap.put( "count", publicationList.size() );
		responseMap.put( "publications", publicationList );

		return responseMap;
	}

	@Override
	public Map<String, Object> getCirclePublicationByCircleId( String circleId, String query, String year, Integer startPage, Integer maxresult, String orderBy )
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// get author
		Circle targetCircle = persistenceStrategy.getCircleDAO().getById( circleId );

		if ( targetCircle == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - circle not found" );
			if ( query != null && !query.equals( "" ) )
				responseMap.put( "query", query );
			return responseMap;
		}

		Map<String, Object> targetCircleMap = new LinkedHashMap<String, Object>();
		targetCircleMap.put( "id", targetCircle.getId() );
		targetCircleMap.put( "name", targetCircle.getName() );
		responseMap.put( "circle", targetCircleMap );
		responseMap.put( "totalPublication", targetCircle.getPublications().size() );
		if ( query != null && !query.equals( "" ) )
			responseMap.put( "query", query );

		if ( targetCircle.getPublications() == null || targetCircle.getPublications().isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - author contain no publications" );
			return responseMap;
		}

		List<Publication> publications = null;
		// get publication list
		if ( !query.equals( "" ) || !year.equals( "all" ) || startPage != null || maxresult != null )
		{
			Map<String, Object> publicationsMap = persistenceStrategy.getPublicationDAO().getPublicationWithPaging( query, "all", targetCircle, startPage, maxresult, year, orderBy );
			publications = (List<Publication>) publicationsMap.get( "publications" );
		}
		else
		{
			publications = new ArrayList<Publication>( targetCircle.getPublications() );
			// sort by date
			Collections.sort( publications, new PublicationByDateComparator() );
		}

		// get available year
		responseMap.put( "years", persistenceStrategy.getPublicationDAO().getDistinctPublicationYearByCircle( targetCircle, "DESC" ) );

		if ( publications == null || publications.isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - empty publication" );
			return responseMap;
		}

		responseMap.put( "status", "ok" );

		responseMap.put( "year", year );

		if ( maxresult != null )
			responseMap.put( "maxresult", maxresult );

		List<Map<String, Object>> publicationList = new ArrayList<Map<String, Object>>();
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
				if ( publication.getEvent().getEventGroup().getNotation() != null )
					if ( !publication.getEvent().getEventGroup().getNotation().equals( publication.getEvent().getEventGroup().getName() ) )
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
		}
		responseMap.put( "count", publicationList.size() );
		responseMap.put( "publications", publicationList );

		return responseMap;
	}

	@Override
	public Map<String, Object> getCirclePublicationByCircleIdAndTimePeriod( String circleId, String query, Integer yearMin, Integer yearMax, Integer startPage, Integer maxresult, String orderBy )
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// get author
		Circle targetCircle = persistenceStrategy.getCircleDAO().getById( circleId );

		if ( targetCircle == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - circle not found" );
			if ( query != null && !query.equals( "" ) )
				responseMap.put( "query", query );
			return responseMap;
		}

		Map<String, Object> targetCircleMap = new LinkedHashMap<String, Object>();
		targetCircleMap.put( "id", targetCircle.getId() );
		targetCircleMap.put( "name", targetCircle.getName() );
		responseMap.put( "circle", targetCircleMap );
		responseMap.put( "totalPublication", targetCircle.getPublications().size() );
		if ( query != null && !query.equals( "" ) )
			responseMap.put( "query", query );

		if ( targetCircle.getPublications() == null || targetCircle.getPublications().isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - author contain no publications" );
			return responseMap;
		}

		List<Publication> publications = null;
		// get publication list
		if ( !query.equals( "" ) || yearMin != 0 || yearMax != 0 || startPage != null || maxresult != null )
		{
			Map<String, Object> publicationsMap = persistenceStrategy.getPublicationDAO().getPublicationWithPaging( query, "all", targetCircle, startPage, maxresult, yearMin, yearMax, orderBy );
			publications = (List<Publication>) publicationsMap.get( "publications" );
		}
		else
		{
			publications = new ArrayList<Publication>( targetCircle.getPublications() );
			// sort by date
			Collections.sort( publications, new PublicationByDateComparator() );
		}

		// get available year
		responseMap.put( "years", persistenceStrategy.getPublicationDAO().getDistinctPublicationYearByCircle( targetCircle, "DESC" ) );

		if ( publications == null || publications.isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - empty publication" );
			return responseMap;
		}

		responseMap.put( "status", "ok" );

		responseMap.put( "yearMin", yearMin );
		responseMap.put( "yearMax", yearMax );

		if ( maxresult != null )
			responseMap.put( "maxresult", maxresult );

		List<Map<String, Object>> publicationList = new ArrayList<Map<String, Object>>();
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
				if ( publication.getEvent().getEventGroup().getNotation() != null )
					if ( !publication.getEvent().getEventGroup().getNotation().equals( publication.getEvent().getEventGroup().getName() ) )
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
		}
		responseMap.put( "count", publicationList.size() );
		responseMap.put( "publications", publicationList );

		return responseMap;
	}

	@Override
	public Map<String, Object> getCircleMemberPublication( String circleId, String author_id, String query, Integer yearMin, Integer yearMax, Integer startPage, Integer maxresult, String orderBy )
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// get circle
		Circle targetCircle = persistenceStrategy.getCircleDAO().getById( circleId );
		
		// get author
		Author memberCircle = null;
		if ( author_id != null && !author_id.equals( "" ) )
			memberCircle = persistenceStrategy.getAuthorDAO().getById( author_id );

		if ( targetCircle == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - circle not found" );
			return responseMap;
		}

		Map<String, Object> targetCircleMap = new LinkedHashMap<String, Object>();

		targetCircleMap.put( "name", targetCircle.getName() );
		responseMap.put( "circle", targetCircleMap );
		responseMap.put( "totalPublication", targetCircle.getPublications().size() );

		if ( memberCircle.getPublications() == null || memberCircle.getPublications().isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - author contain no publications" );
			return responseMap;
		}

		List<Publication> publications = null;
		List<Map<String, Object>> citationRatePerYearMap = new ArrayList<Map<String, Object>>();

		// get publication list
		if ( !query.equals( "" ) || yearMin != 0 || yearMax != 0 || startPage != null || maxresult != null || memberCircle != null )
		{
			Map<String, Object> publicationsMap = persistenceStrategy.getPublicationDAO().getPublicationWithPaging( query, "all", targetCircle, memberCircle, startPage, maxresult, yearMin, yearMax, orderBy );
			publications = (List<Publication>) publicationsMap.get( "publications" );

			List<Object[]> citationRatePerYearList = (List<Object[]>) publicationsMap.get( "citationRate" );

			if ( citationRatePerYearList != null && citationRatePerYearList.size() != 0 )
				for ( Object[] objList : citationRatePerYearList )
				{
					Map<String, Object> paperCitationMap = new LinkedHashMap<String, Object>();
					paperCitationMap.put( "year", objList[0] );
					paperCitationMap.put( "citationCount", objList[1] );
					paperCitationMap.put( "paperCount", objList[2] );

					citationRatePerYearMap.add( paperCitationMap );
				}
		}
		else
		{
			publications = new ArrayList<Publication>( targetCircle.getPublications() );
			// sort by date
			Collections.sort( publications, new PublicationByDateComparator() );
		}

		// citation rate per year
		responseMap.put( "citationRate", citationRatePerYearMap );

		// get available year
		responseMap.put( "years", persistenceStrategy.getPublicationDAO().getDistinctPublicationYearByCircle( targetCircle, "DESC" ) );

		if ( publications == null || publications.isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - empty publication" );
			return responseMap;
		}

		responseMap.put( "status", "ok" );

		if ( query != null && !query.equals( "" ) )
			responseMap.put( "query", query );
		responseMap.put( "yearMin", yearMin );
		responseMap.put( "yearMax", yearMax );

		if ( maxresult != null )
			responseMap.put( "maxresult", maxresult );

		List<Map<String, Object>> publicationList = new ArrayList<Map<String, Object>>();
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
				if ( publication.getEvent().getEventGroup().getNotation() != null )
					if ( !publication.getEvent().getEventGroup().getNotation().equals( publication.getEvent().getEventGroup().getName() ) )
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
		}

		responseMap.put( "count", publicationList.size() );
		responseMap.put( "publications", publicationList );

		return responseMap;
	}

}

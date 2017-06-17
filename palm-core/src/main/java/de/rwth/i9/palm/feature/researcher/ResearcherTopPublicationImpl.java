package de.rwth.i9.palm.feature.researcher;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class ResearcherTopPublicationImpl implements ResearcherTopPublication
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@SuppressWarnings( "unchecked" )
	@Override
	public Map<String, Object> getTopPublicationListByAuthorId( String authorId, Integer startPage, Integer maxresult )
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// get author
		Author targetAuthor = persistenceStrategy.getAuthorDAO().getById( authorId );

		if ( targetAuthor == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - author not found" );
			return responseMap;
		}

		Map<String, Object> targetAuthorMap = new LinkedHashMap<String, Object>();
		targetAuthorMap.put( "id", targetAuthor.getId() );
		targetAuthorMap.put( "name", targetAuthor.getName() );
		responseMap.put( "author", targetAuthorMap );
		responseMap.put( "totalPublication", targetAuthor.getPublications().size() );

		if ( targetAuthor.getPublications() == null || targetAuthor.getPublications().isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "message", "Error - author contain no publications" );
			return responseMap;
		}

		List<Publication> publications = null;
		//
		// Map<String, Object> publicationsMap =
		// persistenceStrategy.getPublicationDAO().getPublicationWithPaging( "",
		// "all", targetAuthor, null, startPage, maxresult, "all", "citation" );
		// publications = (List<Publication>) publicationsMap.get(
		// "publications" );
		//
		// if ( publications == null || publications.isEmpty() )
		// {
		// responseMap.put( "status", "error" );
		// responseMap.put( "message", "Error - empty publication" );
		// return responseMap;
		// }
		//
		// responseMap.put( "status", "ok" );
		//
		// if ( maxresult != null )
		// responseMap.put( "maxresult", maxresult );
		//
		// List<Map<String, Object>> publicationList = new ArrayList<Map<String,
		// Object>>();
		// for ( Publication publication : publications )
		// {
		//
		// // put publication detail
		// Map<String, Object> publicationMap = new LinkedHashMap<String,
		// Object>();
		// publicationMap.put( "id", publication.getId() );
		// publicationMap.put( "title", publication.getTitle() );
		// // if ( publication.getAbstractText() != null )
		// // publicationMap.put( "abstract", publication.getAbstractText() );
		// // coauthor
		//
		// Map<String, Object> topicsMap = new HashMap<String, Object>();
		//
		// if ( publication.getPublicationTopics() != null )
		// for ( PublicationTopic publTopic : publication.getPublicationTopics()
		// )
		// {
		// topicsMap.put( "termstring", publTopic.getTermString() );
		// topicsMap.put( "termvalue", publTopic.getTermValues() );
		// }
		//
		// publicationMap.put( "topics", topicsMap );
		//
		// List<Map<String, Object>> coathorList = new ArrayList<Map<String,
		// Object>>();
		// for ( Author author : publication.getCoAuthors() )
		// {
		// Map<String, Object> authorMap = new LinkedHashMap<String, Object>();
		// authorMap.put( "id", author.getId() );
		// authorMap.put( "name", WordUtils.capitalize( author.getName() ) );
		// authorMap.put( "hindex", author.getHindex() );
		// if ( author.getInstitution() != null )
		// authorMap.put( "aff", author.getInstitution().getName() );
		//
		// if ( author.getPhotoUrl() != null )
		// authorMap.put( "photo", author.getPhotoUrl() );
		//
		// authorMap.put( "isAdded", author.isAdded() );
		//
		// coathorList.add( authorMap );
		// }
		// publicationMap.put( "coauthor", coathorList );
		//
		// // if ( publication.getKeywordText() != null )
		// // publicationMap.put( "keyword", publication.getKeywordText() );
		//
		// if ( publication.getPublicationDate() != null )
		// {
		// SimpleDateFormat sdf = new SimpleDateFormat(
		// publication.getPublicationDateFormat() );
		// publicationMap.put( "date", sdf.format(
		// publication.getPublicationDate() ) );
		// }
		//
		// if ( publication.getLanguage() != null )
		// publicationMap.put( "language", publication.getLanguage() );
		//
		// if ( publication.getCitedBy() > 0 )
		// {
		// publicationMap.put( "cited", publication.getCitedBy() );
		// if ( publication.getCitedByUrl() != null )
		// publicationMap.put( "citedUrl", publication.getCitedByUrl() );
		// }
		//
		// if ( publication.getPublicationType() != null )
		// {
		// String publicationType = publication.getPublicationType().toString();
		// publicationType = publicationType.substring( 0, 1 ).toUpperCase() +
		// publicationType.substring( 1 );
		// publicationMap.put( "type", publicationType );
		// }
		//
		// if ( publication.getEvent() != null )
		// {
		// Map<String, Object> eventMap = new LinkedHashMap<String, Object>();
		// eventMap.put( "id", publication.getEvent().getId() );
		// eventMap.put( "name",
		// publication.getEvent().getEventGroup().getName() );
		// if ( publication.getEvent().getEventGroup().getNotation() != null &&
		// !publication.getEvent().getEventGroup().getNotation().isEmpty() &&
		// !publication.getEvent().getEventGroup().getNotation().equals(
		// publication.getEvent().getEventGroup().getName() ) )
		// eventMap.put( "abbr",
		// publication.getEvent().getEventGroup().getNotation() );
		// eventMap.put( "isAdded", publication.getEvent().isAdded() );
		// if ( publication.getEvent().getEventGroup() != null )
		// eventMap.put( "isGroupAdded",
		// publication.getEvent().getEventGroup().isAdded() );
		// publicationMap.put( "event", eventMap );
		// }
		//
		// if ( publication.getAdditionalInformation() != null )
		// publicationMap.putAll( publication.getAdditionalInformationAsMap() );
		//
		// if ( publication.getStartPage() > 0 )
		// publicationMap.put( "pages", publication.getStartPage() + " - " +
		// publication.getEndPage() );
		//
		// if ( publication.getAbstractText() != null ||
		// publication.getKeywordText() != null )
		// publicationMap.put( "contentExist", true );
		// else
		// publicationMap.put( "contentExist", false );
		//
		// publicationList.add( publicationMap );
		// }
		// responseMap.put( "count", publicationList.size() );
		// responseMap.put( "publications", publicationList );

		return responseMap;
	}

}

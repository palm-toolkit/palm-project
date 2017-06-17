package de.rwth.i9.palm.persistence;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.Publication;

public interface PublicationDAO extends GenericDAO<Publication>, InstantiableDAO
{
	/**
	 * Trigger batch indexing using Hibernate search powered by Lucene
	 * 
	 * @throws InterruptedException
	 */
	public void doReindexing() throws InterruptedException;

	/**
	 * Get all publication in pagination
	 * 
	 * @param pageNo
	 * @param maxResult
	 * @param orderBy
	 * @return
	 */
	public Map<String, Object> getPublicationWithPaging( String query, String publicationType, Author author, Event event, Integer pageNo, Integer maxResult, String year, String orderBy );

	/**
	 * Apply fulltext search with Hibernate search with paging
	 * 
	 * @param orderBy
	 * 
	 */
	public Map<String, Object> getPublicationByFullTextSearchWithPaging( String query, String publicationType, Author author, Event event, Integer page, Integer maxResult, String year, String orderBy );

	/**
	 * Get all publication on Circle in pagination
	 * 
	 * @param pageNo
	 * @param maxResult
	 * @param orderBy
	 * @return
	 */
	public Map<String, Object> getPublicationWithPaging( String query, String publicationType, Circle circle, Integer pageNo, Integer maxResult, String year, String orderBy );

	/**
	 * Apply fulltext search with Hibernate search
	 * 
	 * @param queryString
	 * @return list of all related publication
	 */
	public List<Publication> getPublicationByFullTextSearch( String queryString );

	/**
	 * Get all publication in pagination based on event
	 * 
	 * @param pageNo
	 * @param maxResult
	 * @return
	 */
	public List<Publication> getPublicationByEventWithPaging( Event event, Integer pageNo, Integer maxResult );
	
	/**
	 * Get publications based on how many words can be between the various words
	 * in the query phrase.
	 * 
	 * @param publicationTitle
	 * @param slope
	 * @return
	 */
	public List<Publication> getPublicationViaPhraseSlopQuery( String publicationTitle, int slope );

	/**
	 * Get publications given any number of authors (coauthors)
	 * 
	 * @param coauthors
	 * @return
	 */
	public List<Publication> getPublicationByCoAuthors( Author... coauthors );

	/**
	 * Get list of years where publications exist on researcher
	 * 
	 * @param author
	 * @return
	 */
	public List<String> getDistinctPublicationYearByAuthor( Author author, String orderBy );

	/**
	 * Get list of years where publications exist on circle
	 * 
	 * @param string
	 * 
	 * @param author
	 * @return
	 */
	public List<String> getDistinctPublicationYearByCircle( Circle circle, String string );

	public Map<String, Object> getPublicationWithPaging( String query, String publicationType, Circle circle, Author memberCircle, Integer startPage, Integer maxresult, String year, String orderBy );

	public Map<String, Object> getPublicationWithPaging( String query, String publicationType, Circle circle, Author memberCircle, Integer startPage, Integer maxresult, Integer yearMin, Integer yearMax, String orderBy );

	public Map<String, Object> getPublicationWithPaging( String query, String publicationType, Circle circle, Integer startPage, Integer maxresult, Integer yearMin, Integer yearMax, String orderBy );

}

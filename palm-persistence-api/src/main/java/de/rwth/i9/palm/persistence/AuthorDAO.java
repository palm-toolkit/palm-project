package de.rwth.i9.palm.persistence;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.Author;

public interface AuthorDAO extends GenericDAO<Author>, InstantiableDAO
{

	/**
	 * Get list of authors with similar last name
	 * 
	 * @param lastName
	 * @return
	 */
	public List<Author> getByLastName( String lastName );

	/**
	 * Get list of authors with similar last name
	 * 
	 * @param lastName
	 * @return
	 */
	public List<Author> getByName( String name );

	/**
	 * Get author by its unique uri
	 * 
	 * @param uri
	 * @return
	 */
	public Author getByUri( String uri );

	/**
	 * Trigger batch indexing using Hibernate search powered by Lucene
	 * 
	 * @throws InterruptedException
	 */
	public void doReindexing() throws InterruptedException;

	/**
	 * Get all author in pagination
	 * 
	 * @param pageNo
	 * @param maxResult
	 * @return
	 */
	public Map<String, Object> getAuthorWithPaging( String queryString, String addedAuthor, int pageNo, int maxResult );

	/**
	 * Apply fulltext search with hibernate search
	 * 
	 * @param queryString
	 * @return list of all related author
	 */
	public List<Author> getAuthorByFullTextSearch( String queryString );

	/**
	 * Apply fulltext search with hibernate search with paging
	 * 
	 * @param queryString
	 * @return Map<String, Object>
	 *         Map< "count", Int numberOfMatchingAuthor > - the total number of
	 *         matching authors 
	 *         Map< "result", List<Author> > - the author list
	 */
	public Map<String, Object> getAuthorByFullTextSearchWithPaging( String query, String addedAuthor, int page, int maxResult );

	public List<Author> getAuthorByNameAndInstitution( String name, String institution );

	public List<Author> getAuthorViaFuzzyQuery( String name, float threshold, int prefixLength );

	public List<Author> getAuthorWithLikeQuery( String name );

	public List<Author> getAuthorListWithPaging( String queryString, String addedAuthor, int pageNo, int maxResult );

	public List<Author> getAuthorListByFullTextSearchWithPaging( String queryString, String addedAuthor, int page, int maxResult );
}

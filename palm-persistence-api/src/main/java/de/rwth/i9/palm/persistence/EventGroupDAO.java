package de.rwth.i9.palm.persistence;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.EventGroup;

public interface EventGroupDAO extends GenericDAO<EventGroup>, InstantiableDAO
{
	/**
	 * Trigger batch indexing using Hibernate search powered by Lucene
	 * 
	 * @throws InterruptedException
	 */
	public void doReindexing() throws InterruptedException;

	/**
	 * Get Event Group by name or notation
	 * 
	 * @param eventNameOrNotation
	 * @return
	 */
	public EventGroup getEventGroupByEventNameOrNotation( String eventNameOrNotation );

	/**
	 * Get event group as list based on given parameters
	 * 
	 * @param queryString
	 * @param type
	 * @param pageNo
	 * @param maxResult
	 * @param addedVenue
	 * @return
	 */
	public List<EventGroup> getEventGroupListWithPaging( String queryString, String type, int pageNo, int maxResult, String addedVenue );

	/**
	 * Get event group as map based on given parameters
	 * 
	 * @param queryString
	 * @param type
	 * @param pageNo
	 * @param maxResult
	 * @return
	 */
	public Map<String, Object> getEventGroupMapWithPaging( String queryString, String type, int pageNo, int maxResult, String addedVenue );

	/**
	 * Get event group as list based on given parameters, with full text search
	 * 
	 * @param queryString
	 * @param type
	 * @param pageNo
	 * @param maxResult
	 * @return
	 */
	public List<EventGroup> getEventGroupListFullTextSearchWithPaging( String queryString, String type, int pageNo, int maxResult, String addedVenue );

	/**
	 * Get event group as map based on given parameters, with full text search
	 * 
	 * @param queryString
	 * @param notation
	 * @param type
	 * @param pageNo
	 * @param maxResult
	 * @param addedVenue
	 * @return
	 */
	public Map<String, Object> getEventGroupMapFullTextSearchWithPaging( String queryString, String notation, String type, int pageNo, int maxResult, String addedVenue );

	/**
	 * Get similar eventGroup, given EventGroup to be compared
	 * 
	 * @param evemntGroupCompareTo
	 * @return
	 */
	public EventGroup getSimilarEventGroup( EventGroup eventGroupCompareTo );
}

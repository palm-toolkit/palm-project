package de.rwth.i9.palm.persistence;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.EventGroup;

public interface EventDAO extends GenericDAO<Event>, InstantiableDAO
{
	/**
	 * Trigger batch indexing using Hibernate search powered by Lucene
	 * 
	 * @throws InterruptedException
	 */
	public void doReindexing() throws InterruptedException;

	/**
	 * Get the map of events
	 * 
	 * @return Hashmap with event notation as key and event as its value
	 */
	Map<String, Event> getNotationEventMaps();

	/**
	 * Get all event in pagination
	 * 
	 * @param pageNo
	 * @param maxResult
	 * @return
	 */
	public Map<String, Object> getEventWithPaging( int pageNo, int maxResult );

	/**
	 * Apply fulltext search with Hibernate search
	 * 
	 * @param queryString
	 * @return list of all related event
	 */
	public List<Event> getEventByFullTextSearch( String queryString );

	/**
	 * Apply fulltext search with Hibernate search with paging
	 * 
	 * @param queryString
	 * @return Map<String, Object> Map< "count", Int numberOfMatchingEvent > -
	 *         the total number of matching events Map< "result", List <Event> >
	 *         - the event list
	 */
	public Map<String, Object> getEventByFullTextSearchWithPaging( String query, int page, int maxResult );

	/**
	 * Get List of event using Lucene Fuzzy Query given threshold and
	 * prefixlength
	 * 
	 * @param name
	 * @param threshold
	 * @param prefixLength
	 * @return
	 */
	public List<EventGroup> getEventViaFuzzyQuery( String name, float threshold, int prefixLength );

	/**
	 * Get event given event notation/name and year
	 * 
	 * @param eventNameOrNotation
	 * @param year
	 * @return
	 */
	public Event getEventByEventNameOrNotationAndYear( String eventNameOrNotation, String year );

	public Map<String, Object> getParticipantsEvent( String query, Event event, Integer pageNo, Integer maxResult, String orderBy );
}

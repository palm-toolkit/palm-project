package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.persistence.EventDAO;

public class EventDAOHibernate extends GenericDAOHibernate<Event> implements EventDAO
{
	/**
	 * {@inheritDoc}
	 */
	public EventDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Event> getNotationEventMaps()
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT cg, e " );
		queryString.append( "FROM EventGroup cg " );
		queryString.append( "JOIN cg.events e " );
		queryString.append( "ORDER BY cg.notation ASC, e.year ASC " );

		Query query = getCurrentSession().createQuery( queryString.toString() );

		@SuppressWarnings( "unchecked" )
		List<Object[]> eventObjects = query.list();

		if ( eventObjects == null || eventObjects.isEmpty() )
			return Collections.emptyMap();

		// prepare the map object
		Map<String, Event> eventsMap = new LinkedHashMap<String, Event>();

		// loop through resultList object
		for ( Object[] item : eventObjects )
		{
			EventGroup eventGroup = (EventGroup) item[0];
			Event event = (Event) item[1];

			eventsMap.put( eventGroup.getNotation() + event.getYear(), event );
		}

		return eventsMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doReindexing() throws InterruptedException
	{
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		fullTextSession.createIndexer().startAndWait();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getEventWithPaging( int pageNo, int maxResult )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT c " );
		queryString.append( "FROM EventGroup cg " );
		queryString.append( "JOIN cg.events c " );
		queryString.append( "ORDER BY cg.notation ASC, c.year ASC " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setFirstResult( pageNo * maxResult );
		query.setMaxResults( maxResult );

		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put( "count", this.countTotal() );
		resultMap.put( "result", query.list() );

		return resultMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEventByFullTextSearch( String queryString )
	{
FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Event.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
.onFields( "eventGroup.name", "year", "thema" )
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Event.class);

		@SuppressWarnings( "unchecked" )
		List<Event> events = hibQuery.list();
		
		if( events ==  null || events.isEmpty() )
			return Collections.emptyList();
		
		return events;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getEventByFullTextSearchWithPaging( String queryString, int page, int maxResult )
	{

		if ( queryString.equals( "" ) )
			return this.getEventWithPaging( page, maxResult );

		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Event.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
.onFields( "eventGroup.name", "year", "eventGroup.notation" )
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Event.class);
		
		// get the total number of matching elements
		int totalRows = hibQuery.getResultSize();
		
		// apply limit
		hibQuery.setFirstResult( page * maxResult );
		hibQuery.setMaxResults( maxResult );
		
		if( totalRows == 0 )
			return null;
		
		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put( "count", totalRows );
		resultMap.put( "result", hibQuery.list() );

		return resultMap;
	}

	@Override
	public List<EventGroup> getEventViaFuzzyQuery( String name, float threshold, int prefixLength )
	{
FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( EventGroup.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .fuzzy()
			        .withThreshold( threshold )
			        .withPrefixLength( prefixLength )
				  .onFields("name")
				  .matching( name )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, EventGroup.class);
		
		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );

		@SuppressWarnings( "unchecked" )
		List<EventGroup> publicationGroups = hibQuery.list();
		
		if( publicationGroups ==  null || publicationGroups.isEmpty() )
			return Collections.emptyList();
		
		return publicationGroups;
	}


	@Override
	public Event getEventByEventNameOrNotationAndYear( String eventNameOrNotation, String year )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT c " );
		queryString.append( "FROM EventGroup cg " );
		queryString.append( "JOIN cg.events c " );
		queryString.append( "WHERE cg.name = :eventNameOrNotation " );
		queryString.append( "OR cg.notation = :eventNameOrNotation " );
		queryString.append( "AND c.year = :year " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "eventNameOrNotation", eventNameOrNotation );
		query.setParameter( "year", year );

		@SuppressWarnings( "unchecked" )
		List<Event> events = query.list();

		if ( events == null || events.isEmpty() )
			return null;

		return events.get( 0 );
	}

	@Override
	public Map<String, Object> getParticipantsEvent( String query, Event event, Integer pageNo, Integer maxResult, String orderBy )
	{

		// container
		Map<String, Object> researcherMap = new LinkedHashMap<String, Object>();

		boolean isWhereClauseEvoked = false;

		StringBuilder mainQuery = new StringBuilder();
		StringBuilder stringBuilder = new StringBuilder();

		mainQuery.append( "SELECT pa.author " );

		if ( event != null )
		{
			isWhereClauseEvoked = true;

			stringBuilder.append( "FROM Publication p " );
			stringBuilder.append( "LEFT JOIN  p.publicationAuthors pa " );
			stringBuilder.append( "WHERE p.event = :event " );
		}

		if ( !query.equals( "" ) )
		{
			query = query.replace( "-", " " );

			if ( !isWhereClauseEvoked )
			{
				stringBuilder.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				stringBuilder.append( "AND " );
			stringBuilder.append( " pa.author.name LIKE :query " );
		}

		stringBuilder.append( "GROUP BY pa.author " );

		if ( orderBy != null )
		{
			if ( orderBy.equals( "nrPublications" ) )
				stringBuilder.append( "ORDER BY COUNT(pa.author) DESC" );
			else if ( orderBy.equals( "hindex" ) )
				stringBuilder.append( "ORDER BY pa.author.hindex DESC" );
			else if ( orderBy.equals( "nrCitations" ) )
				stringBuilder.append( "ORDER BY SUM(p.citedBy) DESC" );

			stringBuilder.append( ", pa.author.name" );
		}
		else
			stringBuilder.append( "ORDER BY pa.author.name" );

		Query hibQueryMain = getCurrentSession().createQuery( mainQuery.toString() + stringBuilder.toString() );

		if ( event != null )
			hibQueryMain.setParameter( "event", event );

		if ( !query.equals( "" ) )
			hibQueryMain.setParameter( "query", query );

		if ( maxResult != null )
			hibQueryMain.setMaxResults( maxResult );

		List<Author> participantsList = hibQueryMain.list();

		researcherMap.put( "participants", participantsList );

		return researcherMap;
	}

}

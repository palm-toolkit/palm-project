package de.rwth.i9.palm.persistence.relational;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.model.PublicationType;
import de.rwth.i9.palm.persistence.EventGroupDAO;

public class EventGroupDAOHibernate extends GenericDAOHibernate<EventGroup>implements EventGroupDAO
{

	public EventGroupDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public void doReindexing() throws InterruptedException
	{
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		fullTextSession.createIndexer().startAndWait();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public List<EventGroup> getEventGroupListWithPaging( String queryString, String type, int pageNo, int maxResult, String addedVenue )
	{
		boolean isWhereClauseEvoked = false;

		StringBuilder mainQuery = new StringBuilder();
		mainQuery.append( "SELECT cg " );

		StringBuilder countQuery = new StringBuilder();
		countQuery.append( "SELECT COUNT(DISTINCT cg) " );

		StringBuilder restQuery = new StringBuilder();
		restQuery.append( "FROM EventGroup cg " );

		if ( !queryString.equals( "" ) )
		{
			isWhereClauseEvoked = true;
			restQuery.append( "WHERE cg.name LIKE :queryString OR cg.notation LIKE :queryString AND cg.added IS TRUE " );
		}

		if ( type.equals( "conference" ) || type.equals( "workshop" ) )
		{
			if ( !isWhereClauseEvoked )
			{
				restQuery.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				restQuery.append( "AND " );

			restQuery.append( "( cg.publicationType = :pubTypeConference OR cg.publicationType = :pubTypeWorkshop ) " );
		}
		if ( type.equals( "journal" ) )
		{
			if ( !isWhereClauseEvoked )
			{
				restQuery.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				restQuery.append( "AND " );

			restQuery.append( "cg.publicationType = :pubTypeJournal " );
		}
		if ( addedVenue.equals( "yes" ) )
		{
			if ( !isWhereClauseEvoked )
			{
				restQuery.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				restQuery.append( "AND " );
			restQuery.append( "cg.added IS TRUE " );
		}

		restQuery.append( "ORDER BY cg.name" );

		Query query = getCurrentSession().createQuery( mainQuery.toString() + restQuery.toString() );
		if ( !queryString.equals( "" ) )
			query.setParameter( "queryString", "%" + queryString + "%" );
		if ( type.equals( "conference" ) || type.equals( "workshop" ) )
		{
			query.setParameter( "pubTypeConference", PublicationType.CONFERENCE );
			query.setParameter( "pubTypeWorkshop", PublicationType.WORKSHOP );
		}
		if ( type.equals( "journal" ) )
			query.setParameter( "pubTypeJournal", PublicationType.JOURNAL );

		query.setFirstResult( pageNo * maxResult );
		query.setMaxResults( maxResult );

		// prepare the container for result
		List<EventGroup> eventGroup = new ArrayList<EventGroup>();

		eventGroup = query.list();

		return eventGroup;
	}

	@Override
	public Map<String, Object> getEventGroupMapWithPaging( String queryString, String type, int pageNo, int maxResult, String addedVenue )
	{
		boolean isWhereClauseEvoked = false;

		StringBuilder mainQuery = new StringBuilder();
		mainQuery.append( "SELECT cg " );

		StringBuilder countQuery = new StringBuilder();
		countQuery.append( "SELECT COUNT(DISTINCT cg) " );

		StringBuilder restQuery = new StringBuilder();
		restQuery.append( "FROM EventGroup cg " );
		if ( !queryString.equals( "" ) )
		{
			isWhereClauseEvoked = true;
			restQuery.append( "WHERE cg.name LIKE :queryString OR cg.notation LIKE :queryString AND cg.added IS TRUE " );
		}

		if ( type.equals( "conference" ) || type.equals( "workshop" ) )
		{
			if ( !isWhereClauseEvoked )
			{
				restQuery.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				restQuery.append( "AND " );

			restQuery.append( "( cg.publicationType = :pubTypeConference OR cg.publicationType = :pubTypeWorkshop ) " );
		}
		if ( type.equals( "journal" ) )
		{
			if ( !isWhereClauseEvoked )
			{
				restQuery.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				restQuery.append( "AND " );

			restQuery.append( "cg.publicationType = :pubTypeJournal " );
		}
		if ( addedVenue.equals( "yes" ) )
		{
			if ( !isWhereClauseEvoked )
			{
				restQuery.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				restQuery.append( "AND " );
			restQuery.append( "cg.added IS TRUE " );
		}

		restQuery.append( "ORDER BY cg.name" );

		Query query = getCurrentSession().createQuery( mainQuery.toString() + restQuery.toString() );
		if ( !queryString.equals( "" ) )
			query.setParameter( "queryString", "%" + queryString + "%" );
		if ( type.equals( "conference" ) || type.equals( "workshop" ) )
		{
			query.setParameter( "pubTypeConference", PublicationType.CONFERENCE );
			query.setParameter( "pubTypeWorkshop", PublicationType.WORKSHOP );
		}
		if ( type.equals( "journal" ) )
			query.setParameter( "pubTypeJournal", PublicationType.JOURNAL );

		query.setFirstResult( pageNo * maxResult );
		query.setMaxResults( maxResult );

		/* Executes count query */
		Query hibQueryCount = getCurrentSession().createQuery( countQuery.toString() + restQuery.toString() );
		if ( !queryString.equals( "" ) )
			hibQueryCount.setParameter( "queryString", "%" + queryString + "%" );
		if ( type.equals( "conference" ) || type.equals( "workshop" ) )
		{
			hibQueryCount.setParameter( "pubTypeConference", PublicationType.CONFERENCE );
			hibQueryCount.setParameter( "pubTypeWorkshop", PublicationType.WORKSHOP );
		}
		if ( type.equals( "journal" ) )
			hibQueryCount.setParameter( "pubTypeJournal", PublicationType.JOURNAL );

		int count = ( (Long) hibQueryCount.uniqueResult() ).intValue();

		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put( "totalCount", count );
		resultMap.put( "eventGroups", query.list() );

		return resultMap;
	}

	@Override
	public EventGroup getEventGroupByEventNameOrNotation( String eventNameOrNotation )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT cg " );
		queryString.append( "FROM EventGroup cg " );
		queryString.append( "WHERE cg.name = :eventNameOrNotation " );
		queryString.append( "OR cg.notation = :eventNameOrNotation " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "eventNameOrNotation", eventNameOrNotation );

		@SuppressWarnings( "unchecked" )
		List<EventGroup> eventGroups = query.list();

		if ( eventGroups == null || eventGroups.isEmpty() )
			return null;

		return eventGroups.get( 0 );
	}

	@Override
	public List<EventGroup> getEventGroupListFullTextSearchWithPaging( String queryString, String type, int pageNo, int maxResult, String addedVenue )
	{
		if ( queryString.equals( "" ) )
			return this.getEventGroupListWithPaging( queryString, type, pageNo, maxResult, addedVenue );

		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( EventGroup.class ).get();
		
		// Query using lucene boolean query
		@SuppressWarnings( "rawtypes" )
		BooleanJunction combinedBooleanJunction = qb.bool();
		
		combinedBooleanJunction
		.must( qb
			  .keyword()
			  .onFields("name", "notation")
			  .matching( queryString )
			  .createQuery());
	
		if( addedVenue.equals( "yes" )){
			combinedBooleanJunction
					.must( qb
							.keyword()
							.onFields( "added" )
							.matching( true )
							.createQuery()
							
					);
		}
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
	    fullTextSession.createFullTextQuery(combinedBooleanJunction.createQuery(), EventGroup.class);
	
		// apply limit
		hibQuery.setFirstResult( pageNo * maxResult );
		hibQuery.setMaxResults( maxResult );
		
		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );

		@SuppressWarnings( "unchecked" )
		List<EventGroup> eventGroups = hibQuery.list();
		
		if( eventGroups ==  null || eventGroups.isEmpty() )
			return Collections.emptyList();
		
		return eventGroups;
	}

	@Override
	public Map<String, Object> getEventGroupMapFullTextSearchWithPaging( String queryString, String notation, String type, int pageNo, int maxResult, String addedVenue )
	{
		if ( queryString.equals( "" ) )
			return this.getEventGroupMapWithPaging( queryString, type, pageNo, maxResult, addedVenue );

		// remove any common words
		queryString = queryString.toLowerCase().replace( "conference", "" );
		queryString = queryString.toLowerCase().replace( "journal", "" );
		queryString = queryString.toLowerCase().replace( "proceedings", "" );
		queryString = queryString.toLowerCase().replace( "international", "" );

		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( EventGroup.class ).get();
		
		// Query using lucene boolean query
		@SuppressWarnings( "rawtypes" )
		BooleanJunction combinedBooleanJunction = qb.bool();
		
		combinedBooleanJunction
			.must( qb
				  .keyword()
				  .onFields("name", "notation")
				  .matching( queryString )
				  .createQuery());
		
		if( addedVenue.equals( "yes" )){
			combinedBooleanJunction
					.must( qb
							.keyword()
							.onFields( "added" )
							.matching( true )
							.createQuery()
							
					);
		}
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(combinedBooleanJunction.createQuery(), EventGroup.class);
		
		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );
		
		// get the total number of matching elements
		int totalRows = hibQuery.getResultSize();
		
		// apply limit
		hibQuery.setFirstResult( pageNo * maxResult );
		hibQuery.setMaxResults( maxResult );
		
		@SuppressWarnings( "unchecked" )
		List<EventGroup> eventGroups = hibQuery.list();
		
		if( eventGroups.size() < maxResult )
			totalRows = eventGroups.size();
		
		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
				
		resultMap.put( "totalCount", totalRows );
		resultMap.put( "eventGroups", eventGroups );

		return resultMap;
	}

	@Override
	public EventGroup getSimilarEventGroup( EventGroup eventGroupCompareTo )
	{
		if ( eventGroupCompareTo.getDblpUrl() != null )
		{
			StringBuilder queryString = new StringBuilder();
			queryString.append( "SELECT cg " );
			queryString.append( "FROM EventGroup cg " );
			queryString.append( "WHERE cg.dblpUrl = :dblpUrl " );

			Query query = getCurrentSession().createQuery( queryString.toString() );
			query.setParameter( "dblpUrl", eventGroupCompareTo.getDblpUrl() );

			@SuppressWarnings( "unchecked" )
			List<EventGroup> eventGroups = query.list();

			if ( eventGroups != null && !eventGroups.isEmpty() )
				return eventGroups.get( 0 );

			else
			{
				if ( eventGroupCompareTo.getNotation() != null && !eventGroupCompareTo.getNotation().isEmpty() )
				{
					queryString = new StringBuilder();
					queryString.append( "SELECT cg " );
					queryString.append( "FROM EventGroup cg " );
					queryString.append( "WHERE REPLACE(cg.notation,'-','') = :notation " );
					queryString.append( "OR REPLACE(cg.name,'-','') = :notation2 " );
					queryString.append( "OR REPLACE(cg.name,'-','') = :name " );

					Query query2 = getCurrentSession().createQuery( queryString.toString() );
					query2.setParameter( "notation", eventGroupCompareTo.getNotation() );
					query2.setParameter( "notation2", eventGroupCompareTo.getNotation() );
					query2.setParameter( "name", eventGroupCompareTo.getName() );

					@SuppressWarnings( "unchecked" )
					List<EventGroup> eventGroups2 = query2.list();

					if ( eventGroups2 != null && !eventGroups2.isEmpty() )
						return eventGroups2.get( 0 );
				}
			}
		}
		return null;
	}

}

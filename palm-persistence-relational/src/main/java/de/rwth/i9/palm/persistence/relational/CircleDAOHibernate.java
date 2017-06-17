package de.rwth.i9.palm.persistence.relational;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.persistence.CircleDAO;

public class CircleDAOHibernate extends GenericDAOHibernate<Circle> implements CircleDAO
{

	public CircleDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
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

	@Override
	public Map<String, Object> getCircleWithPaging( String query, User creator, int pageNo, int maxResult, String orderBy )
	{
		boolean isWhereClauseEvoked = false;

		// container
		Map<String, Object> circleMap = new LinkedHashMap<String, Object>();

		StringBuilder mainQuery = new StringBuilder();
		mainQuery.append( "SELECT DISTINCT c " );

		StringBuilder countQuery = new StringBuilder();
		countQuery.append( "SELECT COUNT(DISTINCT c) " );

		StringBuilder restQuery = new StringBuilder();
		restQuery.append( "FROM Circle c " );

		if ( !query.equals( "" ) )
		{
			isWhereClauseEvoked = true;
			restQuery.append( "WHERE c.name LIKE :query " );
			restQuery.append( "OR c.description = :description " );
		}

		if ( creator != null )
		{
			if ( !isWhereClauseEvoked )
			{
				restQuery.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				restQuery.append( "AND " );
			restQuery.append( "c.creator = :creator " );
		}

		if ( orderBy.equals( "date" ) )
			restQuery.append( "ORDER BY c.creationDate DESC" );
		else if ( orderBy.equals( "name" ) )
			restQuery.append( "ORDER BY c.name DESC" );

		/* Executes main query */
		Query hibQueryMain = getCurrentSession().createQuery( mainQuery.toString() + restQuery.toString() );

		if ( !query.equals( "" ) )
		{
			hibQueryMain.setParameter( "query", "%" + query + "%" );
			hibQueryMain.setParameter( "description", "%" + query + "%" );
		}

		if ( creator != null )
			hibQueryMain.setParameter( "creator", creator );

		hibQueryMain.setFirstResult( pageNo * maxResult );
		hibQueryMain.setMaxResults( maxResult );

		@SuppressWarnings( "unchecked" )
		List<Circle> circles = hibQueryMain.list();

		if ( circles == null || circles.isEmpty() )
		{
			circleMap.put( "totalCount", 0 );
			return circleMap;
		}

		circleMap.put( "circles", circles );

		/* Executes count query */
		Query hibQueryCount = getCurrentSession().createQuery( countQuery.toString() + restQuery.toString() );

		if ( !query.equals( "" ) )
		{
			hibQueryCount.setParameter( "query", "%" + query + "%" );
			hibQueryCount.setParameter( "description", "%" + query + "%" );
		}

		if ( creator != null )
			hibQueryCount.setParameter( "creator", creator );

		int count = ( (Long) hibQueryCount.uniqueResult() ).intValue();
		circleMap.put( "totalCount", count );

		return circleMap;
	}

	@Override
	public Map<String, Object> getCircleFullTextSearchWithPaging( String queryString, User creator, int pageNo, int maxResult, String orderBy )
	{
		if ( queryString.equals( "" ) || creator != null )
			return this.getCircleWithPaging( "", creator, pageNo, maxResult, orderBy );
		
		// dead code ahead
		// due to strange behavior on circle indexing
		if ( true )
			return this.getCircleWithPaging( queryString, creator, pageNo, maxResult, orderBy );

		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Circle.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields( "name", "description" )
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Author.class);
		
		// get the total number of matching elements
		int totalRows = hibQuery.getResultSize();
		
		// apply limit
		hibQuery.setFirstResult( pageNo * maxResult );
		hibQuery.setMaxResults( maxResult );
		
		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		
		@SuppressWarnings( "unchecked" )
		List<Circle> circles = hibQuery.list();

		resultMap.put( "totalCount", totalRows );

		if ( circles != null && !circles.isEmpty() )
			resultMap.put( "circles", circles );

		return resultMap;
	}

	@Override
	public Map<String, Object> getCircleMembersByPublishingPeriod( Circle circle, Integer yearMin, Integer yearMax, int maxResult )
	{
		boolean isWhereClauseEvoked = false;

		// container
		Map<String, Object> circleMap = new LinkedHashMap<String, Object>();

		StringBuilder mainQuery = new StringBuilder();
		mainQuery.append( "SELECT a " );

		StringBuilder countQuery = new StringBuilder();
		countQuery.append( "SELECT a.id, COUNT( DISTINCT p), SUM( p.citedBy )  " );

		StringBuilder stringBuilder = new StringBuilder();


		stringBuilder.append( "FROM Circle c, Publication pub, Author a " );
		stringBuilder.append( "LEFT JOIN c.publications p " );
		stringBuilder.append( "LEFT JOIN pub.publicationAuthors pa " );
		stringBuilder.append( "LEFT JOIN c.authors ca " );
		stringBuilder.append( "WHERE c = :c AND pub = p AND a =ca AND pa.author.id = ca.id " );

		isWhereClauseEvoked = true;

		if ( yearMin != 0 )
		{
			if ( isWhereClauseEvoked )
			{
				stringBuilder.append( " AND pub.year >= :yMin " );
			}
		}
		if ( yearMax != 0 )
		{
			if ( isWhereClauseEvoked )
			{
				stringBuilder.append( " AND pub.year <= :yMax " );
			}
		}
		stringBuilder.append( "GROUP BY  a " );

		/* Executes count query */
		Query hibQuery = getCurrentSession().createQuery( mainQuery.toString() + stringBuilder.toString() );
		hibQuery.setParameter( "c", circle );
		if ( yearMin != 0 )
			hibQuery.setParameter( "yMin", yearMin.toString() );
		if ( yearMax != 0 )
			hibQuery.setParameter( "yMax", yearMax.toString() );
		hibQuery.setMaxResults( maxResult );

		@SuppressWarnings( "unchecked" )
		List<Author> authors = hibQuery.list();

		if ( authors == null || authors.isEmpty() )
		{
			circleMap.put( "totalCount", 0 );
			return circleMap;
		}

		/* Executes count query */
		Query hibCountQuery = getCurrentSession().createQuery( countQuery.toString() + stringBuilder.toString() );
		hibCountQuery.setParameter( "c", circle );
		if ( yearMin != 0 )
			hibCountQuery.setParameter( "yMin", yearMin.toString() );
		if ( yearMax != 0 )
			hibCountQuery.setParameter( "yMax", yearMax.toString() );
		hibCountQuery.setMaxResults( maxResult );

		@SuppressWarnings( "unchecked" )
		List<Object[]> counts = hibCountQuery.list();

		if ( counts == null || counts.isEmpty() )
		{
			circleMap.put( "totalCount", 0 );
			return circleMap;
		}

		for ( int i = 0; i < counts.size(); i++ )
		{
			authors.get( i ).setCitedBy( Math.toIntExact( (long) counts.get( i )[2] ) );
			authors.get( i ).setNoPublication( Math.toIntExact( (long) counts.get( i )[1] ) );
		}

		circleMap.put( "authors", authors );

		return circleMap;
	}

}
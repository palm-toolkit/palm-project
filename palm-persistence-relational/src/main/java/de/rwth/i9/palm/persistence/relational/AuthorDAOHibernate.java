package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.rwth.i9.palm.helper.comparator.AuthorByNoCitationComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.persistence.AuthorDAO;

public class AuthorDAOHibernate extends GenericDAOHibernate<Author> implements AuthorDAO
{

	public AuthorDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Author getByUri( String uri )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Author " );
		queryString.append( "WHERE uri = :uri " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "uri", uri );

		@SuppressWarnings( "unchecked" )
		List<Author> authors = query.list();

		if ( authors == null || authors.isEmpty() )
			return null;

		return authors.get( 0 );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Author> getByLastName( String lastName )
	{
		if( lastName.equals( "" ))
			return Collections.emptyList();
		
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Author " );
		queryString.append( "WHERE lastName = :lastName " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "lastName", lastName );

		@SuppressWarnings( "unchecked" )
		List<Author> authors = query.list();

		if ( authors == null || authors.isEmpty() )
			return Collections.emptyList();

		return authors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Author> getByName( String name )
	{
		if ( name.equals( "" ) )
			return Collections.emptyList();

		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT DISTINCT a " );
		queryString.append( "FROM Author a " );
		queryString.append( "LEFT JOIN a.aliases aa " );
		queryString.append( "WHERE a.name = :name " );
		queryString.append( "OR aa.name = :aname " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "name", name );
		query.setParameter( "aname", name );

		@SuppressWarnings( "unchecked" )
		List<Author> authors = query.list();

		if ( authors == null || authors.isEmpty() )
			return Collections.emptyList();

		return authors;
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
	public Map<String, Object> getAuthorWithPaging( String queryString, String addedAuthor, int pageNo, int maxResult )
	{
		boolean isWhereClauseEvoked = false;

		StringBuilder mainQuery = new StringBuilder();
		mainQuery.append( "SELECT a " );

		StringBuilder countQuery = new StringBuilder();
		countQuery.append( "SELECT COUNT(DISTINCT a) " );

		StringBuilder restQuery = new StringBuilder();
		restQuery.append( "FROM Author a " );

		if ( !queryString.equals( "" ) )
		{
			isWhereClauseEvoked = true;
			restQuery.append( "WHERE a.name LIKE :queryString " );
		}
		if ( addedAuthor.equals( "yes" ) )
		{
			if ( !isWhereClauseEvoked )
			{
				restQuery.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				restQuery.append( "AND " );
			restQuery.append( "a.added IS TRUE " );
		}

		restQuery.append( "ORDER BY a.citedBy desc, a.name asc" );
		
		Query query = getCurrentSession().createQuery( mainQuery.toString() + restQuery.toString() );
		if ( !queryString.equals( "" ) )
			query.setParameter( "queryString", "%" + queryString + "%" );
		query.setFirstResult( pageNo * maxResult );
		query.setMaxResults( maxResult );

		/* Executes count query */
		Query hibQueryCount = getCurrentSession().createQuery( countQuery.toString() + restQuery.toString() );
		if ( !queryString.equals( "" ) )
			hibQueryCount.setParameter( "queryString", "%" + queryString + "%" );

		int count = ( (Long) hibQueryCount.uniqueResult() ).intValue();

		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put( "totalCount", count );
		resultMap.put( "authors", query.list() );

		return resultMap;
	}

	@Override
	public List<Author> getAuthorListWithPaging( String queryString, String addedAuthor, int pageNo, int maxResult )
	{
		boolean isWhereClauseEvoked = false;

		StringBuilder mainQuery = new StringBuilder();
		mainQuery.append( "SELECT a FROM Author a " );

		if ( !queryString.equals( "" ) )
		{
			isWhereClauseEvoked = true;
			mainQuery.append( "WHERE name LIKE :queryString " );
		}
		if ( !addedAuthor.equals( "yes" ) )
		{
			if ( !isWhereClauseEvoked )
			{
				mainQuery.append( "WHERE " );
				isWhereClauseEvoked = true;
			}
			else
				mainQuery.append( "AND " );
			mainQuery.append( "added IS TRUE " );
		}

		mainQuery.append( "ORDER BY citedBy desc, name asc" );

		Query query = getCurrentSession().createQuery( mainQuery.toString() );

		if ( !queryString.equals( "" ) )
			query.setParameter( "queryString", "%" + queryString + "%" );
		query.setFirstResult( pageNo * maxResult );
		query.setMaxResults( maxResult );

		// prepare the container for result
		@SuppressWarnings( "unchecked" )
		List<Author> authors = query.list();

		if ( authors == null || authors.isEmpty() )
			return Collections.emptyList();

		return authors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Author> getAuthorByFullTextSearch( String queryString )
	{
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Author.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields("lastName", "name", "institutions.name")
				  .matching( queryString )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Author.class);
		
		org.apache.lucene.search.Sort sort = new Sort( new SortField("name",  SortField.Type.STRING ));
		hibQuery.setSort( sort );

		@SuppressWarnings( "unchecked" )
		List<Author> authors = hibQuery.list();
		
		if( authors ==  null || authors.isEmpty() )
			return Collections.emptyList();
		
		return authors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getAuthorByFullTextSearchWithPaging( String queryString, String addedAuthor, int page, int maxResult )
	{
		if ( queryString.equals( "" ) )
			return this.getAuthorWithPaging( "", addedAuthor, page, maxResult );
		
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Author.class ).get();
		
		org.apache.lucene.search.Query query = null;
		if( !queryString.equals( "" ) ){
			query = qb
					  .keyword()
					  .onFields( "lastName", "name" )
					  .matching( queryString )
					  .createQuery();
		} else{
			query = qb.all().createQuery();
		}
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Author.class);
		
		// get the total number of matching elements
		int totalRows = hibQuery.getResultSize();
		
		// apply limit
		hibQuery.setFirstResult( page * maxResult );
		hibQuery.setMaxResults( maxResult );

		if( totalRows == 0 )
			return null;
		
		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		
		@SuppressWarnings( "unchecked" )
		List<Author> authors =  hibQuery.list();
		
		Collections.sort( authors, new AuthorByNoCitationComparator() );
		
		resultMap.put( "totalCount", totalRows );
		resultMap.put( "authors", authors );

		return resultMap;
	}

	@Override
	public List<Author> getAuthorListByFullTextSearchWithPaging( String queryString, String addedAuthor, int page, int maxResult )
	{
		if ( queryString.equals( "" ) )
			return this.getAuthorListWithPaging( "", addedAuthor, page, maxResult );

		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );

		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query
		// parser
		// or the Lucene programmatic API. The Hibernate Search DSL is
		// recommended though
		
		QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( Author.class ).get();

		// Query using lucene boolean query
		@SuppressWarnings( "rawtypes" )
		BooleanJunction combinedBooleanJunction = qb.bool();
		
		combinedBooleanJunction
					.must( qb
							.keyword()
							.onFields( "lastName", "name" )
							.matching( queryString )
							.createQuery());
		
		if( addedAuthor.equals( "yes" )){
			combinedBooleanJunction
					.must( qb
							.keyword()
							.onFields( "added" )
							.matching( true )
							.createQuery()
							
					);
		}
			
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery = fullTextSession.createFullTextQuery( combinedBooleanJunction.createQuery(), Author.class );

		// apply limit
		hibQuery.setFirstResult( page * maxResult );
		hibQuery.setMaxResults( maxResult );

		// prepare the container for result
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

		@SuppressWarnings( "unchecked" )
		List<Author> authors = hibQuery.list();

		if ( authors == null || authors.isEmpty() )
			return Collections.emptyList();

		Collections.sort( authors, new AuthorByNoCitationComparator() );

		return authors;
	}

	@Override
	public List<Author> getAuthorByNameAndInstitution( String name, String institutionName )
	{
		StringBuilder queryString = new StringBuilder();

		queryString.append( "SELECT DISTINCT a " );
		queryString.append( "FROM Author a " );
		queryString.append( "LEFT JOIN a.aliases aa " );
		queryString.append( "WHERE a.name = :name " );
		queryString.append( "OR aa.name = :aname " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "name", name );
		query.setParameter( "aname", name );

		@SuppressWarnings( "unchecked" )
		List<Author> authors = query.list();

		if ( authors == null || authors.isEmpty() )
			return Collections.emptyList();

		// if only one result
		if ( authors.size() == 1 )
		{
			if ( authors.get( 0 ).getInstitution() == null )
				return authors;
			else
			{
				if ( authors.get( 0 ).getInstitution().getName().contains( institutionName.toLowerCase() ) )
					return authors;
				else
					return Collections.emptyList();
			}
		}
		else
		{
			Iterator<Author> i = authors.iterator();
			while ( i.hasNext() )
			{
				Author author = i.next();
				boolean removeAuthor = true;

				if ( author.getInstitution().getName().contains( institutionName.toLowerCase() ) )
					i.remove();
			}
		}
		return authors;
	}

	@Override
	public List<Author> getAuthorViaFuzzyQuery( String name, float threshold, int prefixLength )
	{
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Author.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .fuzzy()
			        .withThreshold( threshold )
			        .withPrefixLength( prefixLength )
				  .onFields("name")
				  .matching( name )
				  .createQuery();
		
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Author.class);

		@SuppressWarnings( "unchecked" )
		List<Author> authors = hibQuery.list();
		
		if( authors ==  null || authors.isEmpty() )
			return Collections.emptyList();
		
		return authors;
	}

	@Override
	public List<Author> getAuthorWithLikeQuery( String name )
	{
		if ( name.equals( "" ) )
			return Collections.emptyList();

		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT DISTINCT a " );
		queryString.append( "FROM Author a " );
		queryString.append( "LEFT JOIN a.aliases aa " );
		queryString.append( "WHERE a.name LIKE :name " );
		queryString.append( "OR aa.name LIKE :aname " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "name", "%" + name + "%" );
		query.setParameter( "aname", "%" + name + "%" );

		@SuppressWarnings( "unchecked" )
		List<Author> authors = query.list();

		if ( authors == null || authors.isEmpty() )
			return Collections.emptyList();

		return authors;
	}

}

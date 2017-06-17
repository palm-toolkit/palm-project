package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.rwth.i9.palm.model.Institution;
import de.rwth.i9.palm.persistence.InstitutionDAO;

public class InstitutionDAOHibernate extends GenericDAOHibernate<Institution> implements InstitutionDAO
{

	public InstitutionDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public Institution getByUri( String institutionUrl )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Institution " );
		queryString.append( "WHERE uri = :uri " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "uri", institutionUrl );

		@SuppressWarnings( "unchecked" )
		List<Institution> institutions = query.list();

		if ( institutions == null || institutions.isEmpty() )
			return null;

		return institutions.get( 0 );
	}

	@Override
	public List<Institution> getByName( String name )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Institution " );
		queryString.append( "WHERE name LIKE :name " );
		queryString.append( "OR abbr LIKE :name " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "name", "%" + name + "%" );

		@SuppressWarnings( "unchecked" )
		List<Institution> institutions = query.list();

		if ( institutions == null || institutions.isEmpty() )
			return Collections.emptyList();

		return institutions;
	}
	
	@Override
	public List<Institution> getWithFullTextSearch( String label )
	{
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		
		// create native Lucene query using the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Institution.class ).get();
		
		org.apache.lucene.search.Query query = qb
				  .keyword()
				  .onFields( "name", "abbr" )
				  .matching( label )
				  .createQuery();
		
		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.search.FullTextQuery hibQuery =
		    fullTextSession.createFullTextQuery(query, Institution.class);
		
		// org.apache.lucene.search.Sort sort = new Sort( new SortField(
		// "title", (Type) SortField.STRING_FIRST ) );
		// hibQuery.setSort( sort );

		@SuppressWarnings( "unchecked" )
		List<Institution> institutions = hibQuery.list();
		
		if( institutions ==  null || institutions.isEmpty() )
			return Collections.emptyList();
		
		return institutions;
	}

	@Override
	public void doReindexing() throws InterruptedException
	{
		FullTextSession fullTextSession = Search.getFullTextSession( getCurrentSession() );
		fullTextSession.createIndexer().startAndWait();
	}

}

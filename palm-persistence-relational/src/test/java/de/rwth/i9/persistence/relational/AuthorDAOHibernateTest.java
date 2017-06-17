package de.rwth.i9.persistence.relational;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.i9.palm.config.DatabaseConfigTest;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.persistence.AuthorDAO;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = DatabaseConfigTest.class, loader = AnnotationConfigContextLoader.class )
@Transactional
public class AuthorDAOHibernateTest
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	private AuthorDAO authorDAO;

	@Before
	public void init()
	{
		authorDAO = persistenceStrategy.getAuthorDAO();
		assertNotNull( authorDAO );
	}

	@Test
	@Ignore
	public void reIndexing() throws InterruptedException
	{
		// do reindexing first
		persistenceStrategy.getAuthorDAO().doReindexing();
	}

	@Test
	@Ignore
	public void testFullTextSearch()
	{

		// try to search something
		List<Author> authors = persistenceStrategy.getAuthorDAO().getAuthorByFullTextSearch( "chatti" );

		for ( Author author : authors )
		{
			System.out.println( "name : " + author.getName());
		}

		int totalAuthors = persistenceStrategy.getAuthorDAO().countTotal();
		System.out.println( "total record : " + totalAuthors );
	}

	@Test
	@Ignore
	public void fullTextSearchPaging()
	{
		Map<String, Object> results = persistenceStrategy.getAuthorDAO().getAuthorByFullTextSearchWithPaging( "rwth aachen", "no", 0, 20 );

		System.out.println( "total record " + results.get( "count" ) );
		@SuppressWarnings( "unchecked" )
		List<Author> authors = (List<Author>) results.get( "result" );

		for ( Author author : authors )
		{
			System.out.println( "name : " + author.getName() );
		}

//		Map<String, Object> results2 = persistenceStrategy.getAuthorDAO().getAuthorByFUllTextSearchWithPaging( "", 1, 20 );
//		@SuppressWarnings( "unchecked" )
//		List<Author> authors2 = (List<Author>) results2.get( "result" );
//
//		for ( Author author : authors2 )
//		{
//			System.out.println( "name : " + author.getTitle() );
//		}
//
//		System.out.println( "total record2 " + results2.get( "count" ) );
	}
}

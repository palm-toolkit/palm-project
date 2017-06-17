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
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.persistence.PublicationDAO;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = DatabaseConfigTest.class, loader = AnnotationConfigContextLoader.class )
@Transactional
public class PublicationDAOHibernateTest
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	private PublicationDAO publicationDAO;

	@Before
	public void init()
	{
		publicationDAO = persistenceStrategy.getPublicationDAO();
		assertNotNull( publicationDAO );
	}

	@Test
	@Ignore
	public void test() throws InterruptedException
	{
		// do reindexing first
		// persistenceStrategy.getPublicationDAO().doReindexing();

		// try to search something
		List<Publication> publications = persistenceStrategy.getPublicationDAO().getPublicationByFullTextSearch( "learning analytics" );

		for ( Publication publication : publications )
		{
			System.out.println( "title : " + publication.getTitle() );
		}

		int totalPublications = persistenceStrategy.getPublicationDAO().countTotal();
		System.out.println( "total record : " + totalPublications );
	}

	@SuppressWarnings( "unchecked" )
	@Test
	@Ignore
	public void fullTextSearchPagging()
	{
		Map<String, Object> publicationMap = persistenceStrategy.getPublicationDAO().getPublicationByFullTextSearchWithPaging( "social network analysis", null, null, null, 0, 20, null, null );

		if ( (Long) publicationMap.get( "totalCount" ) > 0 )
			for ( Publication publication : (List<Publication>) publicationMap.get( "publications" ) )
			{
				System.out.println( "title : " + publication.getTitle() );
			}

//		Map<String, Object> results2 = persistenceStrategy.getPublicationDAO().getPublicationByFUllTextSearchWithPaging( "", 1, 20 );
//		@SuppressWarnings( "unchecked" )
//		List<Publication> publications2 = (List<Publication>) results2.get( "result" );
//
//		for ( Publication publication : publications2 )
//		{
//			System.out.println( "title : " + publication.getTitle() );
//		}
//
//		System.out.println( "total record2 " + results2.get( "count" ) );
	}
}

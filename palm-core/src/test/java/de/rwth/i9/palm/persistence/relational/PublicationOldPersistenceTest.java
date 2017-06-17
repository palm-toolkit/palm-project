package de.rwth.i9.palm.persistence.relational;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.i9.palm.config.DatabaseConfigCoreTest;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = DatabaseConfigCoreTest.class, loader = AnnotationConfigContextLoader.class )
@TransactionConfiguration
@Transactional
public class PublicationOldPersistenceTest extends AbstractTransactionalJUnit4SpringContextTests
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	private final static Logger log = LoggerFactory.getLogger( PublicationOldPersistenceTest.class );

	@BeforeTransaction
	public void beforeTransaction()
	{
	}

	@Test
	@Rollback( false )
	@Ignore
	public void test()
	{
		//List<PublicationOld> pubOlds = persistenceStrategy.getPublicationOldDAO().getAll();

		//for ( PublicationOld pubOld : pubOlds )
		//{
		//	String s = pubOld.getAuthors();
		/*
		 * s = s.replace( "Ã¶", "ö" ) .replace( "Ã©", "é" ) .replace( "Ã¼", "ü"
		 * ) .replace( "Ã¤", "ä" ) .replace( "ÃŸ", "ß" ) .replace( "Ã­", "i" )
		 * .replace( "Ã¸", "ø" ) .replace( "Ã¡", "á" ) .replace( "Ã¯", "ï" )
		 * .replace( "Ã§", "ç" ) .replace( "Ã¥", "å" ) .replace( "Ã±", "ñ" )
		 * .replace( "Ã±", "ñ" ) .replace( "Ãº", "ú" ) .replace( "Ã«", "ë" )
		 * .replace( "Ã³", "ó" ) .replace( "Ã¨", "è" ) .replace( "Ã–", "Ö" );
		 */
			//pubOld.setAuthors( s );
			//persistenceStrategy.getPublicationOldDAO().persist( pubOld );
	//		System.out.println( s );
		//}
		
		// PublicationOld pubOld = new PublicationOld();
		// pubOld.setAbstractText( "something" );
		// persistenceStrategy.getPublicationOldDAO().persist( pubOld );
	}

	@Test
	@Transactional
	@Ignore
	public void testInsert()
	{
		// PublicationOld pubOld = new PublicationOld();
		// pubOld.setAuthors( "test" );
		// persistenceStrategy.getPublicationOldDAO().persist( pubOld );
	}

	@AfterTransaction
	public void afterTransaction()
	{
	}

}

package de.rwth.i9.palm.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.i9.palm.analytics.util.TopicMiningConstants;
import de.rwth.i9.palm.config.DatabaseConfigCoreTest;
import de.rwth.i9.palm.config.WebAppConfigTest;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = { WebAppConfigTest.class, DatabaseConfigCoreTest.class }, loader = AnnotationConfigContextLoader.class )
@TransactionConfiguration
@Transactional

public class TestGetDataCircle extends AbstractTransactionalJUnit4SpringContextTests
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	String path = TopicMiningConstants.USER_DESKTOP_PATH;

	@Test
	@Ignore
	public void testGetCirclePublicationsFromDatabase() throws FileNotFoundException, UnsupportedEncodingException
	{
		System.out.println( "\n========== TEST 0 - Get publications for Circles ==========" );

		List<Circle> circles = (List<Circle>) persistenceStrategy.getCircleDAO().getAll();
		
		if ( !circles.isEmpty() )
			for ( Circle circle : circles )

		{
				PrintWriter writer = new PrintWriter( path + "Circles/Circles/" + circle.getId() + ".txt", "UTF-8" );
				writer.println( "Circle Name : " + circle.getName() );
				for ( Publication publication : circle.getPublications() )
			{
					if ( publication.getAbstractText() != null )
					{
						writer.println( publication.getTitle() );
						writer.println( publication.getAbstractText() );
						writer.println();

					}
			}
				writer.println();
				writer.close();
		}
	}
	
	@Test
	@Ignore
	public void testcreateEntityDirectories() throws IOException
	{
		System.out.println( "\n========== TEST 1 - Create Architecture for Circle-Test Collection ==========" );
		List<Circle> circles =  persistenceStrategy.getCircleDAO().getAll();
		
		if ( !circles.isEmpty() )
			for ( Circle circle : circles )
			{

				File theDir = new File( path + "Circle-Test/" + circle.getId().toString() );

				// if the directory does not exist, create it
				if ( !theDir.exists() )
				{
					boolean result = false;

					try
					{
						theDir.mkdirs();
						result = true;
					}
					catch ( SecurityException se )
					{
						// handle it
					}
					if ( result )
					{
						System.out.println( "DIR created" );
					}
				}
			}
	}
	
	@Test
	@Ignore
	public void testGetDatabaseFromDatabase() throws FileNotFoundException, UnsupportedEncodingException
	{
		System.out.println( "\n========== TEST 2 - Fetch publications for Circle-Test database ==========" );
		List<Circle> circles = persistenceStrategy.getCircleDAO().getAll();
		
		if ( !circles.isEmpty() )
			for ( Circle circle : circles )
			{
				for ( Publication publication : circle.getPublications() )
				{
					if ( publication.getAbstractText() != "null" )
					{
						PrintWriter writer = new PrintWriter( path +"Circle-Test/" + circle.getId() + "/" + publication.getId() + ".txt", "UTF-8" );
						writer.print( publication.getTitle() + " " );
						writer.print( publication.getAbstractText() );
						writer.println();
						writer.close();
					}
					else
					{
						continue;
					}
				}
			}
	}
	
	@Test
	@Ignore
	public void testcreateEntityDirectoriesYear() throws IOException
	{
		System.out.println( "\n========== TEST 3 - Create Architecture for Circle-Year-Test==========" );
		List<Circle> circles = persistenceStrategy.getCircleDAO().getAll();
		
		if ( !circles.isEmpty() )
			for ( Circle circle : circles )
			{

				File theDir = new File( path +"Circle-Year-Test/" + circle.getId().toString() );

				// if the directory does not exist, create it
				if ( !theDir.exists() )
				{
					boolean result = false;

					try
					{
						theDir.mkdirs();
						result = true;
					}
					catch ( SecurityException se )
					{
						// handle it
					}
					if ( result )
					{
						System.out.println( "DIR created" );
					}
				}
			}
	}

	@Test
	@Ignore
	public void testGetCirclePublicationsFromDatabaseYearly() throws FileNotFoundException, UnsupportedEncodingException
	{
		System.out.println( "\n========== TEST 4 - Get Circle publications for Circle-Year-Test ==========" );

		List<Circle> circles = (List<Circle>) persistenceStrategy.getCircleDAO().getAll();

		if ( !circles.isEmpty() )
			for ( Circle circle : circles )
			{
				System.out.println( circle.getName().toString() );
				for ( int year = 1980; year < 2017; year++ )
				{
					for ( Author author : circle.getAuthors() )
					{
						if ( !author.getPublicationsByYear( year ).isEmpty() )
						{
							PrintWriter writer = new PrintWriter( path + "Circle-Year-Test/" + circle.getId().toString() + "/" + year + ".txt" );
							for ( Publication publication : author.getPublicationsByYear( year ) )
							{
								writer.print( publication.getTitle() + " " );
								writer.println( publication.getAbstractText() + " " );
							}
							writer.close();
							System.out.println( year );
						}
					}
				}
			}
	}
}
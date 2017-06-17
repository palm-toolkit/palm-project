package de.rwth.i9.palm.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.analytics.util.TopicMiningConstants;
import de.rwth.i9.palm.config.DatabaseConfigCoreTest;
import de.rwth.i9.palm.config.WebAppConfigTest;
import de.rwth.i9.palm.feature.researcher.ResearcherFeature;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = { WebAppConfigTest.class, DatabaseConfigCoreTest.class }, loader = AnnotationConfigContextLoader.class )
@TransactionConfiguration
@Transactional

public class TestGetDataAndMahout extends AbstractTransactionalJUnit4SpringContextTests
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;
	
	String path = TopicMiningConstants.USER_DESKTOP_PATH;

	@Autowired // actually this one is for the API, so I guess you don't need to
				// use this
	private ResearcherFeature researcherFeature;

	@Autowired
	private PalmAnalytics palmAnalytics;

	private final static Logger log = LoggerFactory.getLogger( TestGetDataAndMahout.class );

	@Test
	@Ignore
	public void getResearcherPublication()
	{
		String authorId = "07397ed7-3deb-442f-a297-bdb5b476d3e6";

		Author author = persistenceStrategy.getAuthorDAO().getById( authorId );

		// now get by year, basically, you can get all of publications from this
		// author and just filter it based on year
		List<Publication> publications = new ArrayList<Publication>();

		for ( int i = 2005; i < 2016; i++ )
		{
			System.out.println( i );
			int count = 0;
			for ( Publication publication : author.getPublications() )
			{

				if ( !publication.getYear().equals( i + "" ) )
					continue;
				
				publications.add( publication );
				System.out.println( publication.getTitle() );
				System.out.println( publication.getAbstractText() );
				count++;
			}
			System.out.println( count );
		}


	}

	@Test
	@Ignore
	public void testGetDataFromDatabase() throws FileNotFoundException, UnsupportedEncodingException
	{
		System.out.println( "\n========== TEST 0 - Fetch All Authors from database ==========" );

		List<Author> authors = persistenceStrategy.getAuthorDAO().getAll();
		//Author authors = persistenceStrategy.getAuthorDAO().getById("");
		if ( authors != null )
			for ( Author author : authors )
			{
				PrintWriter writer = new PrintWriter(path + "Authors/Authors/" + author.getId() + ".txt", "UTF-8" );

				if ( author.getPublications() != null )
				{
					for ( Publication publication : author.getPublications() )
					{
						writer.print( publication.getTitle() + " " );
						writer.print( publication.getAbstractText() );
						writer.println();
					}
					writer.close();
				}
			}
	}
	
	@Test
	@Ignore
	public void testcreateAuthorDirectories() throws IOException
	{
		System.out.println( "\n========== TEST 1 - Create Architecture for the Author-Test Collection ==========" );
		List<Author> authors = persistenceStrategy.getAuthorDAO().getAll();
		if ( !authors.isEmpty() )
			for ( Author author : authors )
			{

				File theDir = new File( path + "Author-Test/" + author.getId().toString() );

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
		System.out.println( "\n========== TEST 2 - Fetch publications for each Author-Test from database ==========" );
		List<Author> authors = persistenceStrategy.getAuthorDAO().getAll();
		// Author authors = persistenceStrategy.getAuthorDAO().getById("");
		if( !authors.isEmpty())
			for (Author author:authors)
			{	
				for(Publication publication : author.getPublications()){
					if ( publication.getAbstractText() != "null" )
					{
						PrintWriter writer = new PrintWriter( path + "Author-Test/" + author.getId() + "/" + publication.getId() + ".txt", "UTF-8" );
						writer.print( publication.getTitle() + " " );
						writer.print( publication.getAbstractText() );
						writer.println();
						writer.close();
					}
				}
				
			}
	}
	
	@Test
	@Ignore
	public void testcreateAuthorDirectoriesYearly() throws IOException
	{
		System.out.println( "\n========== TEST 3 - Create Architecture for the Author-Year-Test Collection Yearly ==========" );
		List<Author> authors = persistenceStrategy.getAuthorDAO().getAll();
		
		if ( !authors.isEmpty() )
			for ( Author author : authors )
			{

				File theDir = new File( path + "Author-Year-Test/" + author.getId().toString() );

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
	public void testGetDatabaseFromDatabaseOnSpecificYear() throws IOException
	{
		System.out.println( "\n========== TEST 4 - Fetch publications per Author-Year-Test from database ==========" );
		List<Author> authors = persistenceStrategy.getAuthorDAO().getAll();
		
		 if ( !authors.isEmpty() )
			for ( Author author : authors )
			{
				for ( int year = 1980; year < 2017; year++ )
				{
					//System.out.println( year );
						PrintWriter writer = new PrintWriter( path + "Author-Year-Test/" + author.getId().toString() + "/" + year + ".txt" );
						for ( Publication publication : author.getPublicationsByYear( year ) )
						{
							writer.print( publication.getTitle() + " " );
							writer.println( publication.getAbstractText() + " " );
						}
						writer.close();
					}
				}
	}
}

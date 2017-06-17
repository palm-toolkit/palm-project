package documentExtractionTopicModeling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import de.rwth.i9.palm.analytics.util.TopicMiningConstants;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;


public class TestGetDataAndMahout extends AbstractTransactionalJUnit4SpringContextTests
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;
	
	String path = TopicMiningConstants.USER_DESKTOP_PATH;

	public void testGetDataFromDatabase() throws FileNotFoundException, UnsupportedEncodingException
	{
		//System.out.println( "\n========== TEST 0 - Fetch All Authors from database ==========" );
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
	
	public void testcreateAuthorDirectories() throws IOException
	{
		//System.out.println( "\n========== TEST 1 - Create Architecture for the Author-Test Collection ==========" );
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
						theDir.mkdir();
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

	public void testGetDatabaseFromDatabase() throws FileNotFoundException, UnsupportedEncodingException
	{
		//System.out.println( "\n========== TEST 2 - Fetch publications for each Author-Test from database ==========" );
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
					else
					{
						continue;
					}
				}
			}
	}
	
	public void testcreateAuthorDirectoriesYearly() throws IOException
	{
		//System.out.println( "\n========== TEST 3 - Create Architecture for the Author-Year-Test Collection Yearly ==========" );
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
						theDir.mkdir();
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
	
	
	public void testGetDatabaseFromDatabaseOnSpecificYear2() throws IOException
	{
		//System.out.println( "\n========== TEST 4 - Fetch publications per Author-Year-Test from database ==========" );
		List<Author> authors = persistenceStrategy.getAuthorDAO().getAll();
		
		 if ( !authors.isEmpty() )
			for ( Author author : authors )
			{
				for ( int year = 1980; year < 2017; year++ )
				{
					if ( !author.getPublicationsByYear( year ).isEmpty() )
					{
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
}

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
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

public class TestGetDataCircle extends AbstractTransactionalJUnit4SpringContextTests
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	String path = TopicMiningConstants.USER_DESKTOP_PATH;

	public void testGetCirclePublicationsFromDatabase() throws FileNotFoundException, UnsupportedEncodingException
	{
		//System.out.println( "\n========== TEST 0 - Get publications for Circles ==========" );
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
	
	public void testcreateEntityDirectories() throws IOException
	{
		//System.out.println( "\n========== TEST 1 - Create Architecture for Circle-Test Collection ==========" );
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
		//System.out.println( "\n========== TEST 2 - Fetch publications for Circle-Test database ==========" );
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
	
	public void testcreateEntityDirectoriesYear() throws IOException
	{
		//System.out.println( "\n========== TEST 3 - Create Architecture for Circle-Year-Test==========" );
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

	public void testGetCirclePublicationsFromDatabaseYearly() throws FileNotFoundException, UnsupportedEncodingException
	{
		//System.out.println( "\n========== TEST 4 - Get Circle publications for Circle-Year-Test ==========" );

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
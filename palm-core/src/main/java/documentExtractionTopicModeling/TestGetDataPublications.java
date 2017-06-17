package documentExtractionTopicModeling;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import de.rwth.i9.palm.analytics.util.TopicMiningConstants;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

public class TestGetDataPublications extends AbstractTransactionalJUnit4SpringContextTests
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;
	
	String path = TopicMiningConstants.USER_DESKTOP_PATH;

	public void testGetDatabaseFromDatabase() throws FileNotFoundException, UnsupportedEncodingException
	{
		//System.out.println( "\n========== TEST 0 - Fetch publications from database ==========" );
		List<Author> authors = persistenceStrategy.getAuthorDAO().getAll();
		if ( !authors.isEmpty() )
			for ( Author author : authors )
			{
				for ( Publication publication : author.getPublications() )
				{
					if ( publication.getAbstractText() != "null" )
					{
						PrintWriter writer = new PrintWriter( path + "Publications/Publications/" + publication.getId() + ".txt", "UTF-8" );
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

}
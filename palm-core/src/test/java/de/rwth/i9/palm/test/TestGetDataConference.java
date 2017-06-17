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
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = { WebAppConfigTest.class, DatabaseConfigCoreTest.class }, loader = AnnotationConfigContextLoader.class )
@TransactionConfiguration
@Transactional

public class TestGetDataConference extends AbstractTransactionalJUnit4SpringContextTests
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;
	
	String path = TopicMiningConstants.USER_DESKTOP_PATH;

	@Test
	@Ignore
	public void testGetEventPublicationsFromDatabase() throws FileNotFoundException, UnsupportedEncodingException
	{
		System.out.println( "\n========== TEST 0 - Get Conferences publications ==========" );

		List<Event> events = persistenceStrategy.getEventDAO().getAll();

		if ( !events.isEmpty() )
			for ( Event event : events )
			{
					PrintWriter writer = new PrintWriter( path + "Conferences/Conferences/" + event.getId() + ".txt", "UTF-8" );
					for ( Publication publication : event.getPublications() )
				{
						if ( publication.getAbstractText() != null )
						{
							writer.println( publication.getTitle() );
							writer.println( publication.getAbstractText() );
							writer.println();
						}
				}
			writer.close();
			}
	}
	
	@Test
	@Ignore
	public void testGetEventGroupsPublicationsFromDatabase() throws FileNotFoundException, UnsupportedEncodingException
	{
		System.out.println( "\n========== TEST 1 - Get Conferences publications ==========" );
		List<Event> events = persistenceStrategy.getEventDAO().getAll();

		if ( !events.isEmpty() )
			for ( Event event : events )

		{
				PrintWriter writer = new PrintWriter( path + "Conferences/Conferences/" + event.getId() + ".txt", "UTF-8" );
				for ( Publication publication : event.getPublications() )
				{
					if ( publication.getAbstractText() != null )
					{
						writer.println( publication.getTitle() );
						writer.println( publication.getAbstractText() );
						writer.println();
					}
				}
				writer.close();
		}
	}
	
	@Test
	@Ignore
	public void testcreateEntityDirectories() throws IOException
	{
		System.out.println( "\n========== TEST 2 - Create Architecture for Event-Test Collection ==========" );
		List<Event> events = persistenceStrategy.getEventDAO().getAll();
		
		if ( !events.isEmpty() )
			for ( Event event : events )
			{

				File theDir = new File( path + "Event-Test/" + event.getId().toString() + "/" );

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
	public void testGetEventGroupPublicationsFromDatabase() throws FileNotFoundException, UnsupportedEncodingException
	{
		System.out.println( "\n========== TEST 2 - Fetch publications for Event-Test from database ==========" );
		List<EventGroup> eventgroups = persistenceStrategy.getEventGroupDAO().getAll();

		if ( !eventgroups.isEmpty() )
			for ( EventGroup eventgroup : eventgroups )
			{
				System.out.println( eventgroup.getName() );
				PrintWriter writer = new PrintWriter( path + "Conferences/Conferences/" + eventgroup.getId().toString() + ".txt", "UTF-8" );
				for ( Event event : eventgroup.getEvents() )
				{
					for ( Publication publication : event.getPublications() )
					{
						if ( publication.getAbstractText() != "null" )
						{
							writer.print( publication.getTitle() + " " );
							writer.print( publication.getAbstractText() );
							writer.println();
						}
						else
						{
							continue;
						}
					}
				}
				writer.close();
			}
	}
	
	@Test
	@Ignore
	public void testGetDatabaseFromDatabase6() throws FileNotFoundException, UnsupportedEncodingException
	{
		System.out.println( "\n========== TEST 2 - Fetch publications for each Author-Test from database ==========" );
		List<Event> events = persistenceStrategy.getEventDAO().getAll();
		
		if( !events.isEmpty())
			for (Event event:events)
			{	
				for(Publication publication : event.getPublications()){
					if ( publication.getAbstractText() != "null" )
					{
						PrintWriter writer = new PrintWriter( path + "Event-Test/" + event.getId() + "/" + publication.getId() + ".txt", "UTF-8" );
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
	public void testcreateEntityDirectoriesEventGroups() throws IOException
	{
		System.out.println( "\n========== TEST 3 - Create Architecture for EventGroups ==========" );
		List<EventGroup> eventgroups = persistenceStrategy.getEventGroupDAO().getAll();
	
		if ( !eventgroups.isEmpty() )
			for ( EventGroup eventgroup : eventgroups )
			{

				File theDir = new File( path + "EventGroups/" + eventgroup.getId().toString() + "/" );

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
		System.out.println( "\n========== TEST 4 - Fetch publications per Eventgroups from database ==========" );
		List<EventGroup> eventgroups = persistenceStrategy.getEventGroupDAO().getAll();
		if ( !eventgroups.isEmpty() )
			for ( EventGroup eventgroup : eventgroups )
			{
				System.out.println( eventgroup.getName() );
				for ( Event event : eventgroup.getEvents() )
				{
					PrintWriter writer = new PrintWriter( path + "EventGroups/" + eventgroup.getId() + "/" + event.getId().toString() + ".txt", "UTF-8" );

					for ( Publication publication : event.getPublications() )
					{
						if ( publication.getAbstractText() != "null" )
						{
							writer.print( publication.getTitle() + " " );
							writer.print( publication.getAbstractText() );
							writer.println();
						}
						else
						{
							continue;
						}
					}
					writer.close();
				}
			}
	}
	
	@Test
	@Ignore
	public void testcreateEntityDirectoriesEventGroupsClustered() throws IOException
	{
		System.out.println( "\n========== TEST 3 - Create Architecture for EventGroups ==========" );
		List<EventGroup> eventgroups = persistenceStrategy.getEventGroupDAO().getAll();
		if ( !eventgroups.isEmpty() )
			for ( EventGroup eventgroup : eventgroups )
			{

				File theDir = new File( path + "EventGroupsClustered/" + eventgroup.getId().toString() + "/" );

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
	public void testGetDatabaseFromDatabaseConferenceClustered() throws FileNotFoundException, UnsupportedEncodingException
	{
		System.out.println( "\n========== TEST 4 - Fetch publications per Eventgroups from database ==========" );
		List<EventGroup> eventgroups = persistenceStrategy.getEventGroupDAO().getAll();
		if ( !eventgroups.isEmpty() )
			for ( EventGroup eventgroup : eventgroups )
			{
				System.out.println( eventgroup.getName() );
				for ( Event event : eventgroup.getEvents() )
				{
					PrintWriter writer = new PrintWriter( path + "EventGroupsClustered/" + eventgroup.getId() + "/" + event.getYear().toString() + ".txt", "UTF-8" );

					for ( Publication publication : event.getPublications() )
					{
						if ( publication.getAbstractText() != "null" )
						{
							writer.print( publication.getTitle() + " " );
							writer.print( publication.getAbstractText() );
							writer.println();
						}
						else
						{
							continue;
						}
					}
					writer.close();
				}
			}
	}

}
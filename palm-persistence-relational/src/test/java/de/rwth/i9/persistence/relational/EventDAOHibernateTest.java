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
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.persistence.EventDAO;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = DatabaseConfigTest.class, loader = AnnotationConfigContextLoader.class )
@Transactional
public class EventDAOHibernateTest
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	private EventDAO eventDAO;

	@Before
	public void init()
	{
		eventDAO = persistenceStrategy.getEventDAO();
		assertNotNull( eventDAO );
	}

	@Test
	@Ignore
	public void test()
	{
		Map<String, Event> notationEventMaps = persistenceStrategy.getEventDAO().getNotationEventMaps();

		for ( Map.Entry<String, Event> entry : notationEventMaps.entrySet() )
		{
			System.out.println( entry.getKey() + "/" + entry.getValue().getYear() );
		}

		int totalEvents = persistenceStrategy.getEventDAO().countTotal();
		System.out.println( "total record : " + totalEvents );
	}
	
	@Test
	@Ignore
	public void fullTextSearchPagging() throws InterruptedException
	{
		// do reindexing first
		persistenceStrategy.getEventDAO().doReindexing();
		
		Map<String, Object> results = persistenceStrategy.getEventDAO().getEventByFullTextSearchWithPaging( "data mining", 0, 20 );

		System.out.println( "total record " + results.get( "count" ) );
		@SuppressWarnings( "unchecked" )
		List<Event> events = (List<Event>) results.get( "result" );

		for ( Event event : events )
		{
			System.out.println( "title : " + event.getEventGroup().getName() + event.getYear() );
		}
		
	}

}

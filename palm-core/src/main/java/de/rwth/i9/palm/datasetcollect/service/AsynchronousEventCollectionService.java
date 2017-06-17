package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

//import com.google.common.base.Stopwatch;

import de.rwth.i9.palm.model.Source;

/**
 * 
 * @author sigit
 *
 */
@Service
public class AsynchronousEventCollectionService
{
	private final static Logger log = LoggerFactory.getLogger( AsynchronousEventCollectionService.class );

	/**
	 * Asynchronously gather publication list from google scholar
	 * 
	 * @param url
	 * @param source
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<Map<String, Object>> getEventDetailfromDBLP( String url, Source source ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "get Event details from DBLP with url " + url + " starting" );

		Map<String, Object> eventDetailMap = DblpEventCollection.getEventDetailByVenueUrl( url, source );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "get Event details from DBLP with url " + url + " complete in " + stopwatch );

		return new AsyncResult<Map<String, Object>>( eventDetailMap );
	}

}

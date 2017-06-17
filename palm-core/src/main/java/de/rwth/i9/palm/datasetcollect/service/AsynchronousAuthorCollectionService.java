package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;

import org.apache.http.ParseException;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
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
public class AsynchronousAuthorCollectionService
{
	private final static Logger log = LoggerFactory.getLogger( AsynchronousAuthorCollectionService.class );

	/**
	 * Asynchronously gather author list from google scholar
	 * 
	 * @param authorName
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<List<Map<String, String>>> getListOfAuthorsGoogleScholar( String authorName, Source source ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "get author from google scholar with query " + authorName + " starting" );

		List<Map<String, String>> authorMap = GoogleScholarPublicationCollection.getListOfAuthors( authorName, source );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "get author from google scholar with query " + authorName + " complete in " + stopwatch );

		return new AsyncResult<List<Map<String, String>>>( authorMap );
	}

	/**
	 * Asynchronously gather author list from citeseerx
	 * 
	 * @param authorName
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<List<Map<String, String>>> getListOfAuthorsCiteseerX( String authorName, Source source ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "get author from citeseerX with query " + authorName + " starting" );

		List<Map<String, String>> authorMap = CiteseerXPublicationCollection.getListOfAuthors( authorName, source );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "get author from citeSeerX with query " + authorName + " complete in " + stopwatch );

		return new AsyncResult<List<Map<String, String>>>( authorMap );
	}

	/**
	 * Asynchronously gather author list from dblp
	 * 
	 * @param authorName
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<List<Map<String, String>>> getListOfAuthorsDblp( String authorName, Source source ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "get author from DBLP with query " + authorName + " starting" );

		List<Map<String, String>> authorMap = DblpPublicationCollection.getListOfAuthors( authorName, source );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "get author from DBLP with query " + authorName + " complete in " + stopwatch );

		return new AsyncResult<List<Map<String, String>>>( authorMap );
	}

	/**
	 * Asynchronously gather author list from Mendeley
	 * 
	 * @param authorName
	 * @return
	 * @throws IOException
	 * @throws OAuthProblemException 
	 * @throws OAuthSystemException 
	 * @throws ParseException 
	 */
	@Async
	public Future<List<Map<String, String>>> getListOfAuthorsMendeley( String authorName, Source source ) throws IOException, ParseException, OAuthSystemException, OAuthProblemException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "get author from Mendeley with query " + authorName + " starting" );
			
		List<Map<String, String>> authorMap = MendeleyPublicationCollection.getListOfAuthors( authorName, source );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "get author from Mendeley with query " + authorName + " complete in " + stopwatch );

		return new AsyncResult<List<Map<String, String>>>( authorMap );
	}

}

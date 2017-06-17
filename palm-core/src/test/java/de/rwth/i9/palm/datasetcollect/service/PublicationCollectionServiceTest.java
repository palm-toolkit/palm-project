package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import de.rwth.i9.palm.config.WebAppConfigTest;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = WebAppConfigTest.class, loader = AnnotationConfigContextLoader.class )
public class PublicationCollectionServiceTest
{
	private final static Logger log = LoggerFactory.getLogger( PublicationCollectionServiceTest.class );

	@Autowired
	private AsynchronousAuthorCollectionService asynchronousCollectionService;

	@Test
	@Ignore
	public void test2() throws IOException, InterruptedException, ExecutionException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//
//		Future<List<Map<String, String>>> authorGoogleScholar = asynchronousCollectionService.getListOfAuthorsGoogleScholar( "chatti" );
//		Future<List<Map<String, String>>> authorCiteseerX = asynchronousCollectionService.getListOfAuthorsCiteseerX( "chatti" );
//
//		for ( Map<String, String> eachAuthor : authorGoogleScholar.get() )
//		{
//			for ( Entry<String, String> eachAuthorDetail : eachAuthor.entrySet() )
//				System.out.println( eachAuthorDetail.getKey() + " : " + eachAuthorDetail.getValue() );
//			System.out.println();
//		}
//
//		for ( Map<String, String> eachAuthor : authorCiteseerX.get() )
//		{
//			for ( Entry<String, String> eachAuthorDetail : eachAuthor.entrySet() )
//				System.out.println( eachAuthorDetail.getKey() + " : " + eachAuthorDetail.getValue() );
//			System.out.println();
//		}
//
//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "time it took to perform work " + stopwatch );
	}

	@Test
	@Ignore
	public void getPublicationInformationFromPdf() throws IOException, InterruptedException, ExecutionException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//
//		Future<Map<String, String>> authorGoogleScholar = asynchronousCollectionService.getPublicationInformationFromPdf( "http://dspace.learningnetworks.org/bitstream/1820/3180/1/Chatti_ETS.pdf" );
//
//		if ( authorGoogleScholar.isDone() )
//			for ( Entry<String, String> pubDetail : authorGoogleScholar.get().entrySet() )
//				System.out.println( pubDetail.getKey() + " : " + pubDetail.getValue() );
//			System.out.println();
//
//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "time it took to perform work " + stopwatch );
	}
}

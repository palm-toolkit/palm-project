package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( loader = AnnotationConfigContextLoader.class )
public class DblpEventCollectionTest
{
	@SuppressWarnings( "unchecked" )
	@Test
	public void getPublicationListByVenueUrlTest() throws IOException
	{
		String url = "http://dblp.uni-trier.de/db/journals/jkm/jkm16.html#Chatti12";
		url = "http://dblp.uni-trier.de/db/journals/tlt/tlt5.html#ChattiSJ12";
		url = "http://dblp.uni-trier.de/db/conf/mlearn/mlearn2014.html#GrevenCTS14";
		url = "http://dblp.uni-trier.de/db/conf/iassist/iassist2013.html";
		url = "http://dblp.uni-trier.de/db/journals/icom/icom11.html";
		url = "http://dblp.uni-trier.de/db/journals/corr/corr1601.html";
		Map<String, Object> venueDetailMap = DblpEventCollection.getEventDetailByVenueUrl( url, null );

		// List<Map<String,String>> publicationMapList = (List<Map<String,
		// String>>) venueDetail.get( "publications" );

		// print map
		for ( Map.Entry<String, Object> entry : venueDetailMap.entrySet() )
		{
			if ( entry.getKey().equals( "publications" ) )
			{
				// print publication detail
				System.out.println( "\nPublications : " );
				for ( Map<String, String> eachPublicationMap : (List<Map<String, String>>) entry.getValue() )
				{
					for ( Entry<String, String> eachPublicationDetail : eachPublicationMap.entrySet() )
						System.out.println( eachPublicationDetail.getKey() + " : " + eachPublicationDetail.getValue() );
					System.out.println();
				}
			}
			else
			{
				System.out.println( entry.getKey() + " : " + entry.getValue() );
			}
		}

	}
	
	@Test
	@Ignore
	public void searchVenueOnDBLPTest() throws IOException
	{
		String url = "http://dblp.uni-trier.de/search/venue?q=educational";
//		Map<String, String> venueListMap = DblpEventCollection.getEventFromDBLPSearch( url, null );
//
//		// print map
//		for ( Map.Entry<String, String> entry : venueListMap.entrySet() )
//		{
//			System.out.println( entry.getKey() + " > url : " + entry.getValue() );
//		}

	}
	
	@SuppressWarnings( "unchecked" )
	@Test
	@Ignore
	public void getEventMainPageTest() throws IOException
	{
		String url = "http://dblp.uni-trier.de/db/conf/accv/";
		url = "http://dblp.uni-trier.de/db/journals/ai/";
		url = "http://dblp.uni-trier.de/db/conf/edm/";
		url = "http://dblp.uni-trier.de/db/conf/csedu/";
		url = "http://dblp.uni-trier.de/db/conf/edm/";
		url = "http://dblp.uni-trier.de/db/journals/tlt/index.html";
		url = "http://dblp.uni-trier.de/db/journals/ets/";
		url = "http://dblp.uni-trier.de/db/journals/scientometrics/index.html";

		Map<String, Object> venueDetailMap = DblpEventCollection.getEventListFromDBLP( url, null );

		// List<Map<String,String>> publicationMapList = (List<Map<String,
		// String>>) venueDetail.get( "publications" );

		// print map
		for ( Map.Entry<String, Object> entry : venueDetailMap.entrySet() )
		{
			if ( entry.getKey().equals( "events" ) )
			{
				// print Event detail
				System.out.println( "Events : \n" );
				for ( Map<String, Object> eachEventYearMap : (List<Map<String, Object>>) entry.getValue() )
				{

					for ( Entry<String, Object> eachEventYearEntry : eachEventYearMap.entrySet() )
					{
						if ( eachEventYearEntry.getKey().equals( "volume" ) )
						{

							for ( Entry<String, String> eachEventVolumeEntry : ( (Map<String, String>) eachEventYearEntry.getValue() ).entrySet() )
								System.out.println( eachEventVolumeEntry.getKey() + " : " + eachEventVolumeEntry.getValue() );
							System.out.println();
						}
						else
						{
							System.out.println( eachEventYearEntry.getKey() + " : " + eachEventYearEntry.getValue() );
						}

					}
				}
			}
			else
			{
				System.out.println( entry.getKey() + " : " + entry.getValue() );
			}
		}
	}

}

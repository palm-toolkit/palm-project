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
@Ignore
public class GoogleScholarPublicationCollectionTest
{
	@Test
	public void getListOfAuthorsTest() throws IOException
	{
		List<Map<String, String>> authorList = GoogleScholarPublicationCollection.getListOfAuthors( "Marcus Specht", null );

		for ( Map<String, String> eachAuthor : authorList )
		{
			for ( Entry<String, String> eachAuthorDetail : eachAuthor.entrySet() )
				System.out.println( eachAuthorDetail.getKey() + " : " + eachAuthorDetail.getValue() );
			System.out.println();
		}
	}

	@Test
	@Ignore
	public void getListOfPublicationTest() throws IOException
	{
		List<Map<String, String>> publicationMapLists = GoogleScholarPublicationCollection.getPublicationListByAuthorUrl( "https://scholar.google.com/citations?user=gyLI8FYAAAAJ&hl=en", null );

		for ( Map<String, String> eachPublicationMap : publicationMapLists )
		{
			for ( Entry<String, String> eachPublicationDetail : eachPublicationMap.entrySet() )
				System.out.println( eachPublicationDetail.getKey() + " : " + eachPublicationDetail.getValue() );
			System.out.println();
		}
	}

	@Test
	@Ignore
	public void getPublicationDetailByPublicationUrlTest() throws IOException
	{
		Map<String, String> publicationDetailMaps = GoogleScholarPublicationCollection.getPublicationDetailByPublicationUrl( "https://scholar.google.ca/citations?view_op=view_citation&hl=en&user=gyLI8FYAAAAJ&citation_for_view=gyLI8FYAAAAJ:u5HHmVD_uO8C", null );

		for ( Entry<String, String> eachPublicationDetail : publicationDetailMaps.entrySet() )
			System.out.println( eachPublicationDetail.getKey() + " : " + eachPublicationDetail.getValue() );

	}
}

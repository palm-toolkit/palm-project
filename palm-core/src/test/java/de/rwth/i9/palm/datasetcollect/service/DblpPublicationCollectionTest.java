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

import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceProperty;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( loader = AnnotationConfigContextLoader.class )
@Ignore
public class DblpPublicationCollectionTest
{
	@Test
	@Ignore
	public void getListOfAuthorsTest() throws IOException
	{
		SourceProperty sourceProperty1 = new SourceProperty();
		sourceProperty1.setMainIdentifier( "cookie" );
		sourceProperty1.setSecondaryIdentifier( "dblp-view" );
		sourceProperty1.setValue( "t" );

		SourceProperty sourceProperty2 = new SourceProperty();
		sourceProperty2.setMainIdentifier( "cookie" );
		sourceProperty2.setSecondaryIdentifier( "dblp-search-mode" );
		sourceProperty2.setValue( "c" );

		Source source = new Source();
		source.addSourceProperty( sourceProperty1 );
		source.addSourceProperty( sourceProperty2 );

		List<Map<String, String>> authorList = DblpPublicationCollection.getListOfAuthors( "hendrik thüs", source );


		for ( Map<String, String> eachAuthor : authorList )
		{

			for ( Entry<String, String> eachAuthorDetail : eachAuthor.entrySet() )
				System.out.println( eachAuthorDetail.getKey() + " : " + eachAuthorDetail.getValue() );
			System.out.println();

		}
	}

	@Test
	public void getListOfPublicationTest() throws IOException
	{
		SourceProperty sourceProperty1 = new SourceProperty();
		sourceProperty1.setMainIdentifier( "cookie" );
		sourceProperty1.setSecondaryIdentifier( "dblp-view" );
		sourceProperty1.setValue( "t" );

		SourceProperty sourceProperty2 = new SourceProperty();
		sourceProperty2.setMainIdentifier( "cookie" );
		sourceProperty2.setSecondaryIdentifier( "dblp-search-mode" );
		sourceProperty2.setValue( "c" );

		Source source = new Source();
		source.addSourceProperty( sourceProperty1 );
		source.addSourceProperty( sourceProperty2 );

		List<Map<String, String>> publicationMapLists = DblpPublicationCollection.getPublicationListByAuthorUrl( "http://dblp.uni-trier.de/pers/hd/k/Krau=szlig=:Christopher", source );
		int count = 1;

		for ( Map<String, String> eachPublicationMap : publicationMapLists )
		{
			System.out.println( count );
			for ( Entry<String, String> eachPublicationDetail : eachPublicationMap.entrySet() )
				System.out.println( eachPublicationDetail.getKey() + " : " + eachPublicationDetail.getValue() );
			System.out.println();
			count++;
		}
	}
	
}

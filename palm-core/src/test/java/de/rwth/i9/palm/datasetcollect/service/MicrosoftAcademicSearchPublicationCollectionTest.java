package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceProperty;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( loader = AnnotationConfigContextLoader.class )
@Ignore
public class MicrosoftAcademicSearchPublicationCollectionTest
{
	@Test
	public void getListOfPublicationTest() throws IOException, OAuthSystemException, OAuthProblemException
	{
		Source source = new Source();
		source.addSourceProperty( new SourceProperty( "OLD_API", "END_POINT", "http://academic.research.microsoft.com/json.svc/search" ) );
		source.addSourceProperty( new SourceProperty( "OLD_API", "APP_ID", "e028e2a5-972d-4ba5-a4c0-2f25860b1203" ) );

		Author author = new Author();
		author.setName( "Mohamed Amine Chatti" );

		List<Map<String, String>> authorList = MicrosoftAcademicSearchPublicationCollection.getPublicationDetailList( author, source );

		for ( Map<String, String> eachAuthor : authorList )
		{
			for ( Entry<String, String> eachAuthorDetail : eachAuthor.entrySet() )
				System.out.println( eachAuthorDetail.getKey() + " : " + eachAuthorDetail.getValue() );
			System.out.println();
		}
	}


}

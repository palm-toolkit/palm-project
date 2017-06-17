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
import de.rwth.i9.palm.oauth2.Oauth2Client;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( loader = AnnotationConfigContextLoader.class )
@Ignore
public class MendeleyPublicationCollectionTest
{
	@Test
	@Ignore
	public void getListOfAuthorsTest() throws IOException, OAuthSystemException, OAuthProblemException
	{
		String tokenUrl = "https://api-oauth2.mendeley.com/oauth/token";
		String clientId = "392";
		String clientSecret = "VWheQe6qKEGeUXQcLj1NDTCqxkP29PyJ";

		String token = Oauth2Client.Oauth2ClientRequestToken( tokenUrl, clientId, clientSecret );
		
		Source source = new Source();
		source.addSourceProperty( new SourceProperty( "catalog", "SEARCH_PROFILE", "https://api.mendeley.com:443/search/profiles" ) );
		source.addSourceProperty( new SourceProperty( "oauth2", "TOKEN", token ) );
		
		List<Map<String, String>> authorList = MendeleyPublicationCollection.getListOfAuthors( "chatti", source );

		for ( Map<String, String> eachAuthor : authorList )
		{
			for ( Entry<String, String> eachAuthorDetail : eachAuthor.entrySet() )
				System.out.println( eachAuthorDetail.getKey() + " : " + eachAuthorDetail.getValue() );
			System.out.println();
		}
	}

	@Test
	public void getListOfPublicationTest() throws IOException, OAuthSystemException, OAuthProblemException
	{
		String tokenUrl = "https://api-oauth2.mendeley.com/oauth/token";
		String clientId = "392";
		String clientSecret = "VWheQe6qKEGeUXQcLj1NDTCqxkP29PyJ";

		String token = Oauth2Client.Oauth2ClientRequestToken( tokenUrl, clientId, clientSecret );

		Source source = new Source();
		source.addSourceProperty( new SourceProperty( "catalog", "SEARCH_CATALOG", "https://api.mendeley.com:443/search/catalog" ) );
		source.addSourceProperty( new SourceProperty( "oauth2", "TOKEN", token ) );

		Author author = new Author();
		author.setName( "Mohamed Amine Chatti" );

		List<Map<String, String>> authorList = MendeleyPublicationCollection.getPublicationDetailList( author, source );

		for ( Map<String, String> eachAuthor : authorList )
		{
			for ( Entry<String, String> eachAuthorDetail : eachAuthor.entrySet() )
				System.out.println( eachAuthorDetail.getKey() + " : " + eachAuthorDetail.getValue() );
			System.out.println();
		}
	}


}

package de.rwth.i9.palm.oauth2;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

@Ignore
public class Oauth2ClientTest
{

	@Test
	public void test() throws ParseException, IOException, OAuthSystemException, OAuthProblemException
	{
		String TOKEN_URL = "https://api-oauth2.mendeley.com/oauth/token";
		String TRUSTED_CLIENT_ID = "392";
		String TRUSTED_SECRET = "VWheQe6qKEGeUXQcLj1NDTCqxkP29PyJ";
		String CATALOG_URL = "https://api.mendeley.com:443/search/catalog?author=mohamed%20amine%20chatti&limit=5";
		//CATALOG_URL = "https://api.mendeley.com:443/search/profiles?query=mohamed%20amine%20chatti";
		//CATALOG_URL = "https://api.mendeley.com:443/documents?profile_id=1ffa2c9e-d32b-32ea-82e2-23b25c3af724";
		//
		// institution
		// https://api.mendeley.com:443/institutions?hint=mellon&limit=10
		// location
		// https://api.mendeley.com:443/locations?prefix=aachen&limit=10
		//

		JsonNode jsonNode = Oauth2Client.Oauth2ClientRequestCatalog( TOKEN_URL, TRUSTED_CLIENT_ID, TRUSTED_SECRET, CATALOG_URL );

		System.out.println( jsonNode.toString() );

		// mendeley print
		if ( jsonNode.isArray() )
		{
			for ( JsonNode j : jsonNode )
			{
				JsonNode titleNode = j.path( "title" );
				if ( !titleNode.isMissingNode() )
					System.out.println( "title : " + titleNode.toString() );
			}
		}

	}

}

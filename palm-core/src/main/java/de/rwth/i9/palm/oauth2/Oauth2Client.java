package de.rwth.i9.palm.oauth2;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.apache.ApacheHttpTransport;

public class Oauth2Client
{
	/**
	 * Get resource using Oauth2, in this case Mendeley API
	 * 
	 * @param tokenUrl
	 * @param clientId
	 * @param clientSecret
	 * @param CatalogUrl
	 * @return Jackson JsonNode
	 * @throws ParseException
	 * @throws IOException
	 * @throws OAuthSystemException
	 * @throws OAuthProblemException
	 */
	public static JsonNode Oauth2ClientRequestCatalog( String tokenUrl, String clientId, String clientSecret, String catalogUrl ) throws ParseException, IOException, OAuthSystemException, OAuthProblemException
	{
		// configure Oauth2 and get access token
		OAuthClientRequest request = OAuthClientRequest
	            .tokenLocation( tokenUrl )
	            .setClientId( clientId )
	            .setClientSecret( clientSecret )
	            .setGrantType(GrantType.CLIENT_CREDENTIALS)
	            .setScope("all")
	            .buildBodyMessage();
	    OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
	    OAuthJSONAccessTokenResponse tokenResponse = oAuthClient.accessToken(
	            request, OAuthJSONAccessTokenResponse.class);

		// get the resources ( authors or publications )
	    HttpGet httpGet = new HttpGet( catalogUrl );
	    httpGet.setHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());
	    DefaultHttpClient apacheHttpClient = ApacheHttpTransport.newDefaultHttpClient();
	    HttpResponse httpResponse = apacheHttpClient.execute(httpGet);

		// map the results into jsonMap
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree( httpResponse.getEntity().getContent() );
	}
	
	/**
	 * Request token key using Oauth2
	 * 
	 * @param tokenUrl
	 * @param clientId
	 * @param clientSecret
	 * @return tokenkey
	 * @throws ParseException
	 * @throws IOException
	 * @throws OAuthSystemException
	 * @throws OAuthProblemException
	 */
	public static String Oauth2ClientRequestToken( String tokenUrl, String clientId, String clientSecret ) throws ParseException, IOException
	{
		// configure Oauth2 and get access token
		OAuthClientRequest request = null;
		OAuthClient oAuthClient = null;
		OAuthJSONAccessTokenResponse tokenResponse = null;

		try
		{
			request = OAuthClientRequest.tokenLocation( tokenUrl ).setClientId( clientId ).setClientSecret( clientSecret ).setGrantType( GrantType.CLIENT_CREDENTIALS ).setScope( "all" ).buildBodyMessage();

			oAuthClient = new OAuthClient( new URLConnectionClient() );
			tokenResponse = oAuthClient.accessToken( request, OAuthJSONAccessTokenResponse.class );
		}
		catch ( OAuthSystemException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( OAuthProblemException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if ( tokenResponse == null )
			return null;
	    
	    // return token
	    return tokenResponse.getAccessToken();
	}
}

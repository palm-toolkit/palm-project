package de.rwth.i9.palm.topicextraction.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.rwth.i9.palm.model.ExtractionService;
import de.rwth.i9.palm.model.ExtractionServiceProperty;

/**
 * 
 * modified from http://www.opencalais.com/opencalais-api/
 * 
 * @author sigit
 *
 */
public class OpenCalaisAPITopicExtraction
{

	/**
	 * Extracts social tags with Opencalais API, given text
	 * 
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, Object> getTopicsFromText( String text ) throws UnsupportedEncodingException
	{
		return getTopicsFromText( text, null );
	}

	/**
	 * Extracts social tags with Opencalais API, given text and
	 * ExtractionService
	 * 
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, Object> getTopicsFromText( String text, ExtractionService extractionService ) throws UnsupportedEncodingException
	{
		// default values
		String endpoint = "https://api.thomsonreuters.com/permid/calais";
		String token = "TPolW4hYpmQqjYSZFn7YGv2ek0crk7dV";
		String contentType = "text/raw";
		String outputformat = "application/json";
		String omitOutputtingOriginalText = "true";
		String xCalaisSelectiveTags = "socialtags";
		String xCalaisContentClass = "research";

		if ( extractionService != null )
			for ( ExtractionServiceProperty esp : extractionService.getExtractionServiceProperties() )
			{
				if ( esp.getMainIdentifier().equals( "api" ) )
				{
					if ( esp.getSecondaryIdentifier().equals( "endpoint" ) )
						endpoint = esp.getValue();
					else if ( esp.getSecondaryIdentifier().equals( "token" ) )
						token = esp.getValue();
					else if ( esp.getSecondaryIdentifier().equals( "contentType" ) )
						contentType = esp.getValue();
					else if ( esp.getSecondaryIdentifier().equals( "outputformat" ) )
						outputformat = esp.getValue();
					else if ( esp.getSecondaryIdentifier().equals( "omitOutputtingOriginalText" ) )
						omitOutputtingOriginalText = esp.getValue();
					else if ( esp.getSecondaryIdentifier().equals( "xCalaisSelectiveTags" ) )
						xCalaisSelectiveTags = esp.getValue();
					else if ( esp.getSecondaryIdentifier().equals( "xCalaisContentClass" ) )
						xCalaisContentClass = esp.getValue();
				}
			}

		Map<String, Object> mapResults = new LinkedHashMap<String, Object>();

		HttpClient client = new HttpClient();
		client.getParams().setParameter( "http.useragent", "Calais Rest Client" );

		PostMethod method = new PostMethod( endpoint );

		// === Set mandatory parameters
		// Set token
		method.setRequestHeader( "x-ag-access-token", token );
		// Set input content type
		method.setRequestHeader( "Content-Type", contentType );
		// Set response/output format
		method.setRequestHeader( "outputformat", outputformat );

		// === Set optional parameters
		// prevent original text to be sent back
		method.setRequestHeader( "omitOutputtingOriginalText", omitOutputtingOriginalText );
		// Only request socialtag
		method.setRequestHeader( "x-calais-selectiveTags", xCalaisSelectiveTags );
		// Lets you specify the genre of the input files, to optimize
		// extraction.
		method.setRequestHeader( "x-calais-contentClass", xCalaisContentClass );

		// Set content
		method.setRequestEntity( new StringRequestEntity( text, "text/plain", "UTF-8" ) );

		try
		{
			int returnCode = client.executeMethod( method );
			if ( returnCode == HttpStatus.SC_NOT_IMPLEMENTED )
			{
				System.err.println( "The Post method is not implemented by this URI" );
				// still consume the response body
				method.getResponseBodyAsString();
			}
			else if ( returnCode == HttpStatus.SC_OK )
			{
				BufferedReader reader = new BufferedReader( new InputStreamReader( method.getResponseBodyAsStream(), "UTF-8" ) );

				StringBuilder jsonString = new StringBuilder();
				String line;
				while ( ( line = reader.readLine() ) != null )
				{
					jsonString.append( line );
				}

				return extractOpencalaisJson( mapResults, jsonString.toString() );
			}
			else
			{
				System.err.println( "Got code: " + returnCode );
				System.err.println( "response: " + method.getResponseBodyAsString() );
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			method.releaseConnection();
		}

		return mapResults;
	}

	private static Map<String, Object> extractOpencalaisJson( Map<String, Object> mapResults, String jsonString )
	{

		// map the results into jsonMap
		ObjectMapper mapper = new ObjectMapper();

		JsonNode resultsNodes = null;
		try
		{
			resultsNodes = mapper.readTree( jsonString );
		}
		catch ( Exception e )
		{
		}

		if ( resultsNodes == null )
			return Collections.emptyMap();

		// get language
		if ( !resultsNodes.path( "doc" ).path( "meta" ).path( "language" ).isMissingNode() )
			mapResults.put( "language", resultsNodes.path( "doc" ).path( "meta" ).path( "language" ).asText().toLowerCase() );

		// get term values
		Map<String, Double> termsMapResults = new LinkedHashMap<String, Double>();
		for ( JsonNode jsonNode : resultsNodes )
		{
			if ( !jsonNode.path( "_typeGroup" ).isMissingNode() && jsonNode.path( "_typeGroup" ).asText().equals( "socialTag" ) )
			{
				double termValue = 3.0;
				if ( jsonNode.path( "importance" ).asText().equals( "2" ) )
					termValue = 2.0;
				else if ( jsonNode.path( "importance" ).asText().equals( "2" ) )
					termValue = 1.0;

				termsMapResults.put( jsonNode.path( "name" ).asText().toLowerCase(), termValue );
			}
		}

		if ( termsMapResults.isEmpty() )
			return Collections.emptyMap();
		// put term values
		mapResults.put( "termvalue", termsMapResults );

		return mapResults;
	}
}

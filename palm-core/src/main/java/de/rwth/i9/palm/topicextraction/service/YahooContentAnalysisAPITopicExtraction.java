package de.rwth.i9.palm.topicextraction.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

import de.rwth.i9.palm.model.ExtractionService;
import de.rwth.i9.palm.model.ExtractionServiceProperty;

public class YahooContentAnalysisAPITopicExtraction
{

	/**
	 * Extract keywords given text
	 * 
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 */
	public static Map<String, Object> getTextContentAnalysis( String text ) throws UnsupportedEncodingException, URISyntaxException
	{
		return getTextContentAnalysis( text, null );
	}

	/**
	 * Extract keywords given text and ExtractionService
	 * 
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 */
	public static Map<String, Object> getTextContentAnalysis( String text, ExtractionService extractionService ) throws UnsupportedEncodingException, URISyntaxException
	{
		// default value
		String endpoint = "https://query.yahooapis.com/v1/public/yql";
		String query = "select%20*%20from%20contentanalysis.analyze%20where%20text%3D%22";
		String endQuery = "%22&format=json";

		if ( extractionService != null )
			for ( ExtractionServiceProperty esp : extractionService.getExtractionServiceProperties() )
			{
				if ( esp.getMainIdentifier().equals( "api" ) )
				{
					if ( esp.getSecondaryIdentifier().equals( "endpoint" ) )
						endpoint = esp.getValue();
				}
			}

		Map<String, Object> mapResults = new LinkedHashMap<String, Object>();

		Map<String, Double> termsMapResults = new LinkedHashMap<String, Double>();

		RestTemplate restTemplate = new RestTemplate();

		URI uri = new URI( endpoint + "?q=" + query + URLEncoder.encode( text, "UTF-8" ).replace( "+", "%20" ) + endQuery );

		@SuppressWarnings( "unchecked" )
		Map<String, Object> resultsMap = restTemplate.getForObject( uri, Map.class );

		@SuppressWarnings( "unchecked" )
		Map<String, Object> queryMap = (Map<String, Object>) resultsMap.get( "query" );

//		String language = (String) queryMap.get( "lang" );
//		if ( language.equalsIgnoreCase( "en-US" ) )
//			language = "english";
//		else if ( language.equalsIgnoreCase( "de" ) )
//			language = "german";

		if ( queryMap.get( "results" ) != null )
		{
			@SuppressWarnings( "unchecked" )
			Map<String, Object> yahooResultsMap = (Map<String, Object>) queryMap.get( "results" );
			if ( yahooResultsMap.get( "entities" ) != null )
			{
				@SuppressWarnings( "unchecked" )
				Map<String, Object> yahooEntitiesMap = (Map<String, Object>) yahooResultsMap.get( "entities" );
				if ( yahooEntitiesMap.get( "entity" ) != null )
				{
					@SuppressWarnings( "unchecked" )
					List<Map<String, Object>> entityList = (List<Map<String, Object>>) yahooEntitiesMap.get( "entity" );
					for ( Map<String, Object> entity : entityList )
					{
						Double score = Double.parseDouble( entity.get( "score" ).toString() );
						if ( entity.get( "text" ) != null )
						{
							@SuppressWarnings( "unchecked" )
							Map<String, Object> textMap = (Map<String, Object>) entity.get( "text" );
							termsMapResults.put( textMap.get( "content" ).toString(), score );
						}
					}
				}
			}
		}

//		mapResults.put( "language", language );
		mapResults.put( "termvalue", termsMapResults );

		return mapResults;
	}
}

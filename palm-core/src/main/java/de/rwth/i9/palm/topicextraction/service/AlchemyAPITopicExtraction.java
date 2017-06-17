package de.rwth.i9.palm.topicextraction.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.web.client.RestTemplate;

import de.rwth.i9.palm.model.ExtractionService;
import de.rwth.i9.palm.model.ExtractionServiceProperty;

public class AlchemyAPITopicExtraction
{
	/**
	 * Alchemy API get rangked keywords given text
	 * 
	 * @param text
	 * @return
	 */
	public static Map<String, Object> getTextRankedKeywords( String text )
	{
		return getTextRankedKeywords( text, null );
	}

	/**
	 * Alchemy API get rangked keywords given text
	 * 
	 * @param text
	 * @return
	 */
	public static Map<String, Object> getTextRankedKeywords( String text, ExtractionService extractionService )
	{
		// default values
		String endpoint = "http://access.alchemyapi.com/calls/text/TextGetRankedKeywords";
		String apikey = "apikey=dc59f0b1685b54093b73725f900335f9684343d8";
		String outputMode = "outputMode=json";
		String keywordExtractMode = "keywordExtractMode=strict";
		int maximumResultsRetrieved = 10;
		float minimumScoreThreshold = 0.3f;

		if ( extractionService != null )
			for ( ExtractionServiceProperty esp : extractionService.getExtractionServiceProperties() )
			{
				if ( esp.getMainIdentifier().equals( "api" ) )
				{
					if ( esp.getSecondaryIdentifier().equals( "endpoint" ) )
						endpoint = esp.getValue();
					else if ( esp.getSecondaryIdentifier().equals( "keywordExtractMode" ) )
						keywordExtractMode = esp.getValue();
					else if ( esp.getSecondaryIdentifier().equals( "outputMode" ) )
						outputMode = esp.getValue();
					else if ( esp.getSecondaryIdentifier().equals( "apikey" ) )
						apikey = esp.getValue();
				}
				else if ( esp.getMainIdentifier().equals( "result" ) )
				{
					if ( esp.getSecondaryIdentifier().equals( "numberCollected" ) )
						try
						{
							maximumResultsRetrieved = Integer.parseInt( esp.getValue() );
						}
						catch ( Exception e )
						{
						}
					else if ( esp.getSecondaryIdentifier().equals( "minimumScoreThreshold" ) )
						try
						{
							minimumScoreThreshold = Float.parseFloat( esp.getValue() );
						}
						catch ( Exception e )
						{
						}
				}
			}

		Map<String, Object> mapResults = new LinkedHashMap<String, Object>();

		Map<String, Double> termsMapResults = new LinkedHashMap<String, Double>();
		
		RestTemplate restTemplate = new RestTemplate();

		// error on restTemplate due to this character
		text = text.replaceAll( "\\{", "" );

		@SuppressWarnings( "unchecked" )
		Map<String, Object> resultsMap = restTemplate.getForObject( endpoint + "?" + apikey + "&" + outputMode + "&" + keywordExtractMode + "&text=" + text, Map.class );


		if ( resultsMap.get( "status" ).equals( "OK" ) )
		{
			@SuppressWarnings( "unchecked" )
			List<Map<String, String>> termsList = (List<Map<String, String>>) resultsMap.get( "keywords" );

			if ( termsList == null || termsList.isEmpty() )
				return Collections.emptyMap();

			int indexTerm = 1;
			for ( Map<String, String> termsMap : termsList )
			{
				if ( indexTerm > maximumResultsRetrieved )
					break;

				double score = Double.parseDouble( termsMap.get( "relevance" ) );
				if ( score < minimumScoreThreshold )
					continue;

				termsMapResults.put( termsMap.get( "text" ), score );
				indexTerm++;
			}
			mapResults.put( "language", resultsMap.get( "language" ) );
			mapResults.put( "termvalue", termsMapResults );
		}
		else
			return Collections.emptyMap();

		return mapResults;
	}
	
	/**
	 * Alchemy API get ranked keywords given url
	 * 
	 * @param text
	 * @return
	 */
	public static Map<String, Double> getUrlRankedKeywords( String url )
	{
		Map<String, Double> termsMapResults = new LinkedHashMap<String, Double>();

		RestTemplate restTemplate = new RestTemplate();

		String endpoint = "http://access.alchemyapi.com/calls/url/URLGetRankedKeywords";
		String apikey = "apikey=dc59f0b1685b54093b73725f900335f9684343d8";
		String outputMode = "outputMode=json";
		String keywordExtractMode = "keywordExtractMode=strict";

		@SuppressWarnings( "unchecked" )
		Map<String, Object> resultsMap = restTemplate.getForObject( endpoint + "?" + apikey + "&" + outputMode + "&" + keywordExtractMode + "&text=" + url, Map.class );

		// check print
		for ( Entry<String, Object> results : resultsMap.entrySet() )
		{
			if ( results.getKey().equals( "keywords" ) )
			{
				// System.out.println( "Keyword:" );

				@SuppressWarnings( "unchecked" )
				List<Map<String, String>> termsList = (List<Map<String, String>>) results.getValue();

				if ( termsList == null || termsList.isEmpty() )
					return Collections.emptyMap();

				for ( Map<String, String> termsMap : termsList )
					termsMapResults.put( termsMap.get( "text" ), Double.parseDouble( termsMap.get( "relevance" ) ) );

			}
			else
				// System.out.println( result.getKey() + " : " +
				// result.getValue().toString() );
				if ( results.getKey().equals( "text" ) )
				if ( !results.getValue().toString().equals( "Ok" ) )
					return Collections.emptyMap();
		}
		return termsMapResults;
	}

}

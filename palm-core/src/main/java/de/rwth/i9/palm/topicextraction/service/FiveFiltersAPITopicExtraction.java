package de.rwth.i9.palm.topicextraction.service;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

import de.rwth.i9.palm.model.ExtractionService;
import de.rwth.i9.palm.model.ExtractionServiceProperty;

public class FiveFiltersAPITopicExtraction
{
	/**
	 * Extracts keywords with Fivefilters API, given text
	 * 
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */

	public static Map<String, Object> getTextTermExtract( String text ) throws UnsupportedEncodingException
	{
		return getTextTermExtract( text, null );
	}

	/**
	 * Extracts keywords with Fivefilters API, given text and ExtractionService
	 * 
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */

	public static Map<String, Object> getTextTermExtract( String text, ExtractionService extractionService ) throws UnsupportedEncodingException
	{
		// default values
		String endpoint = "http://termextract.fivefilters.org/extract.php";
		String outputMode = "output=json";

		if ( extractionService != null )
			for ( ExtractionServiceProperty esp : extractionService.getExtractionServiceProperties() )
			{
				if ( esp.getMainIdentifier().equals( "api" ) )
				{
					if ( esp.getSecondaryIdentifier().equals( "endpoint" ) )
						endpoint = esp.getValue();
					else if ( esp.getSecondaryIdentifier().equals( "token" ) )
						outputMode = esp.getValue();
				}
			}

		Map<String, Object> mapResults = new LinkedHashMap<String, Object>();

		Map<String, Double> termsMapResults = new LinkedHashMap<String, Double>();

		RestTemplate restTemplate = new RestTemplate();


		@SuppressWarnings( "unchecked" )
		List<List<Object>> resultsList = (List<List<Object>>) restTemplate.getForObject( endpoint + "?text=" + text + "&" + outputMode, List.class );

		for ( List<Object> termObject : resultsList )
		{
			termsMapResults.put( termObject.get( 0 ).toString(), ( Double.parseDouble( termObject.get( 1 ).toString() ) + Double.parseDouble( termObject.get( 2 ).toString() ) ) );
		}

		mapResults.put( "termvalue", termsMapResults );

		return mapResults;
	}
}

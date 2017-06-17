package de.rwth.i9.palm.topicextraction.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestTemplate;


@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( loader = AnnotationConfigContextLoader.class )
@Ignore
public class FiveFiltersAPITopicExtractionTest
{

	@Test
	@Ignore
	public void getTextRankedKeywordsTest() throws UnsupportedEncodingException, URISyntaxException
	{
		String endpoint = "http://query.yahooapis.com/v1/public/yql";

		String properties = "&format=json&diagnostics=true&callback=";
		String text = "The main aim of Knowledge Management (KM) is to connect people to quality knowledge as well as people to people in order to peak performance. This is also the primary goal of Learning Management (LM). In fact, in the world of e-learning, it is more widely recognised that how learning content is used and distributed by learners might be more important than how it is designed. In the last few years, there has been an increasing focus on social software applications and services as a result of the rapid development of Web 2.0 concepts. In this paper, we argue that LM and KM can be viewed as two sides of the same coin, and explore how Web 2.0 technologies can leverage knowledge sharing and learning and enhance individual performance whereas previous models of LM and KM have failed, and present a social software driven approach to LM and KM";
		String query = "select * from contentanalysis.analyze where text=\"" + text + "\"";

		String alreadyEncoded = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20contentanalysis.analyze%20where%20text%3D%22The%20main%20aim%20of%20Knowledge%20Management%20(KM)%20is%20to%20connect%20people%20to%20quality%20knowledge%20as%20well%20as%20people%20to%20people%20in%20order%20to%20peak%20performance.%20This%20is%20also%20the%20primary%20goal%20of%20Learning%20Management%20(LM).%20In%20fact%2C%20in%20the%20world%20of%20e-learning%2C%20it%20is%20more%20widely%20recognised%20that%20how%20learning%20content%20is%20used%20and%20distributed%20by%20learners%20might%20be%20more%20important%20than%20how%20it%20is%20designed.%20In%20the%20last%20few%20years%2C%20there%20has%20been%20an%20increasing%20focus%20on%20social%20software%20applications%20and%20services%20as%20a%20result%20of%20the%20rapid%20development%20of%20Web%202.0%20concepts.%20In%20this%20paper%2C%20we%20argue%20that%20LM%20and%20KM%20can%20be%20viewed%20as%20two%20sides%20of%20the%20same%20coin%2C%20and%20explore%20how%20Web%202.0%20technologies%20can%20leverage%20knowledge%20sharing%20and%20learning%20and%20enhance%20individual%20performance%20whereas%20previous%20models%20of%20LM%20and%20KM%20have%20failed%2C%20and%20present%20a%20social%20software%20driven%20approach%20to%20LM%20and%20KM%22&format=json&diagnostics=true&callback=";

		URI uri = new URI( alreadyEncoded );
		// query = URLEncoder.encode( query, "UTF-8" );
		//URI expanded = new UriTemplate( query ).;

		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject( uri, String.class );
//
		System.out.println( result );
		
		System.out.println( uri );
	}

	@SuppressWarnings( "unchecked" )
	@Test
	public void getTextContentAnalysis() throws UnsupportedEncodingException, URISyntaxException
	{
		String text = "The main aim of Knowledge Management (KM) is to connect people to quality knowledge as well as people to people in order to peak performance. This is also the primary goal of Learning Management (LM). In fact, in the world of e-learning, it is more widely recognised that how learning content is used and distributed by learners might be more important than how it is designed. In the last few years, there has been an increasing focus on social software applications and services as a result of the rapid development of Web 2.0 concepts. In this paper, we argue that LM and KM can be viewed as two sides of the same coin, and explore how Web 2.0 technologies can leverage knowledge sharing and learning and enhance individual performance whereas previous models of LM and KM have failed, and present a social software driven approach to LM and KM";
		Map<String, Object> resultsMap = FiveFiltersAPITopicExtraction.getTextTermExtract( text );


		if ( resultsMap.get( "termvalue" ) != null )
			for ( Entry<String, Object> termValue : ( (Map<String, Object>) resultsMap.get( "termvalue" ) ).entrySet() )
				System.out.println( termValue.getKey() + " : " + termValue.getValue() );
	}

	public String cutToLength( String text, int maxLength )
	{
		if ( text.length() > maxLength )
			return text.substring( 0, maxLength );
		return text;
	}
}

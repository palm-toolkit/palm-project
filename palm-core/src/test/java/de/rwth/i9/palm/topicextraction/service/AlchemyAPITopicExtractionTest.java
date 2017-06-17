package de.rwth.i9.palm.topicextraction.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( loader = AnnotationConfigContextLoader.class )
@Ignore
public class AlchemyAPITopicExtractionTest
{
	@Test
	@Ignore
	public void getListOfAuthorsTest() throws RestClientException, UnsupportedEncodingException
	{
		String endpoint = "http://access.alchemyapi.com/calls/text/TextGetRankedKeywords";
		String apikey = "apikey=dc59f0b1685b54093b73725f900335f9684343d8";
		String outputMode = "outputMode=json";
		String keywordExtractMode = "keywordExtractMode=strict";
		String text = "The main aim of Knowledge Management (KM) is to connect people to quality knowledge as well as people to people in order to peak performance. This is also the primary goal of Learning Management (LM). In fact, in the world of e-learning, it is more widely recognised that how learning content is used and distributed by learners might be more important than how it is designed. In the last few years, there has been an increasing focus on social software applications and services as a result of the rapid development of Web 2.0 concepts. In this paper, we argue that LM and KM can be viewed as two sides of the same coin, and explore how Web 2.0 technologies can leverage knowledge sharing and learning and enhance individual performance whereas previous models of LM and KM have failed, and present a social software driven approach to LM and KM";
		String text2 = "Kompetenzentwicklung in Lernnetzwerken für das lebenslange Lernen Lebenslanges Lernen ist eines der Schlüsselthemen für die Wissensgesellschaft. Abseits der formal organisierten Bildungsangebote hat sich mit der Verbreitung und Nutzung von Social Software eine neue und sehr heterogene Organisationsform des technologiegestützten Lernens entwickelt, die große Potenziale für die lebenslange Kompetenzentwicklung bietet. Dieser Beitrag beschreibt diese neue Organisationsform, stellt das Konzept der Social Software sowie einige beispielhafte Applikationen vor und";
		String text3 = "Sensor technology and sensor networks have evolved so rapidly that they are now considered a core driver of the Internet of Things (IoT), however data analytics on IoT streams is still in its infancy. This paper introduces an approach to sensor data analytics by using the OpenIoT1 middleware; real time event processing and clustering algorithms have been used for this purpose. The OpenIoT platform has been extended to support stream processing and thus we demonstrate its flexibility in enabling real time on-demand application domain analytics. We use mobile crowd-sensed data, provided in real time from wearable sensors, to analyse and infer air quality conditions. This experimental evaluation has been implemented using the design principles and methods for IoT data interoperability specified by the OpenIoT project. We describe an event and clustering analytics server that acts as an interface for novel analytical IoT services. The approach presented in this paper also demonstrates how sensor data acquired from mobile devices can be integrated within IoT platforms to enable analytics on data streams. It can be regarded as a valuable tool to understand complex phenomena, e.g., air pollution dynamics and its impact on human health.";
		String encodedText = text3;// URLEncoder.encode( text, "UTF-8"
									// ).replace( "\\+", "%20" );

		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject( endpoint + "?" + apikey + "&" + outputMode + "&" + keywordExtractMode + "&text=" + encodedText, String.class );

		System.out.println( result );
	}

	@SuppressWarnings( "unchecked" )
	@Test
	public void getTextRankedKeywordsTest()
	{
		String text = "The main aim of Knowledge Management (KM) is to connect people to quality knowledge as well as people to people in order to peak performance. This is also the primary goal of Learning Management (LM). In fact, in the world of e-learning, it is more widely recognised that how learning content is used and distributed by learners might be more important than how it is designed. In the last few years, there has been an increasing focus on social software applications and services as a result of the rapid development of Web 2.0 concepts. In this paper, we argue that LM and KM can be viewed as two sides of the same coin, and explore how Web 2.0 technologies can leverage knowledge sharing and learning and enhance individual performance whereas previous models of LM and KM have failed, and present a social software driven approach to LM and KM";
		text = "In the past several years, the World Wide Web has experienced a new era, in which user communities are greatly involved and digital content explodes via the Internet. Community information systems have been highlighted with the emerging term ``Social Software{}. In this paper, we explore the impact of social software on the community of cultural heritage management. Furthermore, mobile and ubiquitous technologies have provided capabilities for more sophisticated approach to cultural heritage management. We analyze these features of mobile information systems for cultural communities. We also present a mobile community framework with mobile Web Services to enable professionals to collect, manage and retrieve cultural heritage information in wide user communities.";
		text = "eAIXESSOR - A MODULAR FRAMEWORK FOR AUTOMATIC ASSESSMENT OF WEEKLY ASSIGNMENTS IN HIGHER EDUCATION Eva Altenbernd-Giani, Ulrik Schroeder, Patrick W. Stalljohann Computer-Supported Learning Research Group Rheinisch-Westf¨alische Technische Hochschule Aachen Aachen, NRW, Germany {giani, schroeder}@informatik.rwth-aachen.de stalljohann@cil.rwth-aachen.de ABSTRACT Defining computer based tests which can be analyzed au- tomatically in order to generate feedback for the learner can either be";
		Map<String, Object> resultsMap = AlchemyAPITopicExtraction.getTextRankedKeywords( text );

		if ( !resultsMap.isEmpty() )
		{
		System.out.println( "language: " + resultsMap.get( "language" ).toString() );

		if ( resultsMap.get( "termvalue" ) != null )
			for ( Entry<String, Object> termValue : ( (Map<String, Object>) resultsMap.get( "termvalue" ) ).entrySet() )
				System.out.println( termValue.getKey() + " : " + termValue.getValue() );
		}
	}

	@Test
	@Ignore
	public void extractWebTest() throws RestClientException, UnsupportedEncodingException
	{
		String endpoint = "http://access.alchemyapi.com/calls/url/URLGetRankedKeywords";
		String apikey = "apikey=dc59f0b1685b54093b73725f900335f9684343d8";
		String outputMode = "outputMode=json";
		String keywordExtractMode = "keywordExtractMode=strict";
		String url = "url=http://edition.cnn.com/2015/06/24/europe/france-wikileaks-nsa-spying-claims/index.html";

		// String encodedText = cutToLength( URLEncoder.encode( text, "UTF-8" ),
		// 6000 );

		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject( endpoint + "?" + apikey + "&" + outputMode + "&" + keywordExtractMode + "&" + url, String.class );

		System.out.println( result );
	}

	public String cutToLength( String text, int maxLength )
	{
		if ( text.length() > maxLength )
			return text.substring( 0, maxLength );
		return text;
	}
}

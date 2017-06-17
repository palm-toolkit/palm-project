package de.rwth.i9.palm.topicextraction.service;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestClientException;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( loader = AnnotationConfigContextLoader.class )
@Ignore
public class OpenCalaisAPITopicExtractionTest
{
	@Test
	public void getTopicsFromTextTest() throws RestClientException, IOException
	{
		String text = "The main aim of Knowledge Management (KM) is to connect people to quality knowledge as well as people to people in order to peak performance. This is also the primary goal of Learning Management (LM). In fact, in the world of e-learning, it is more widely recognised that how learning content is used and distributed by learners might be more important than how it is designed. In the last few years, there has been an increasing focus on social software applications and services as a result of the rapid development of Web 2.0 concepts. In this paper, we argue that LM and KM can be viewed as two sides of the same coin, and explore how Web 2.0 technologies can leverage knowledge sharing and learning and enhance individual performance whereas previous models of LM and KM have failed, and present a social software driven approach to LM and KM";
		String text2 = "Kompetenzentwicklung in Lernnetzwerken für das lebenslange Lernen Lebenslanges Lernen ist eines der Schlüsselthemen für die Wissensgesellschaft. Abseits der formal organisierten Bildungsangebote hat sich mit der Verbreitung und Nutzung von Social Software eine neue und sehr heterogene Organisationsform des technologiegestützten Lernens entwickelt, die große Potenziale für die lebenslange Kompetenzentwicklung bietet. Dieser Beitrag beschreibt diese neue Organisationsform, stellt das Konzept der Social Software sowie einige beispielhafte Applikationen vor und";
		text = "In North-Rhine Westphalia, the most populated state in Germany, Computer Science (CS) has been taught in secondary schools since the early 1970s. This article provides an overview of the past and current situation of CS education in North-Rhine Westphalia, including lessons learned through efforts to introduce and to maintain CS in secondary education. In particular, we focus on the differential school system and the educational landscape of CS education, the different facets of CS teacher education, and";
		text = " A typical computer user’s desktop contains large amount of formal data, such as addresses, events or bibliopraphies. Especially within a corporate or organizational environment, it is often important to exchange this data between employees. However, state-of-the-art communication technologies such as email or bulletin boards don’t allow to easily integrate desktop data in the communication process, with the effect that the data remains locked within a user’s computer. In this paper, we propose that the recent phenomenon of blogging, combined with a tool to easily generate Semantic Web (SW) data from existing formal desktop data, can result in a form of semantic blogging which would help to overcome the aforementioned problem. We discuss a number of preconditions which must be met in order to allow semantic blogging and encourage users to author a semantic blog, and we present a prototype of the semiBlog editor, which was created with the purpose of user-friendly semantic blogging in mind. We argue that such a semantic blog editor should integrate tightly with a user’s desktop environment, as this would make integration of existing data into the blog as easy as possible. 1";
		Map<String, Object> resultsMap = OpenCalaisAPITopicExtraction.getTopicsFromText( text );

		System.out.println( "language: " + resultsMap.get( "language" ).toString() );

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

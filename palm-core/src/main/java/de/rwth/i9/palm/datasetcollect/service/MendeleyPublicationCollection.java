package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.apache.ApacheHttpTransport;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.PublicationType;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceProperty;
import de.rwth.i9.palm.model.SourceType;

public class MendeleyPublicationCollection extends PublicationCollection
{

	private final static Logger log = LoggerFactory.getLogger( MendeleyPublicationCollection.class );

	public MendeleyPublicationCollection()
	{
		super();
	}
	/**
	 * Get possible author
	 * 
	 * @param authorName
	 * @return
	 * @throws IOException
	 */
	public static List<Map<String, String>> getListOfAuthors( String authorName, Source source ) throws IOException
	{
		List<Map<String, String>> authorList = new ArrayList<Map<String, String>>();
		// for search profile properties
		SourceProperty searchProfileProperty = source.getSourcePropertyByIdentifiers( "catalog", "SEARCH_PROFILE" );
		// for token properties
		SourceProperty tokenProperty = source.getSourcePropertyByIdentifiers( "oauth2", "TOKEN" );

		// if token null
		if ( tokenProperty.getValue() == null )
			return Collections.emptyList();

		String authorCatalog = searchProfileProperty.getValue() + "?query=" + URLEncoder.encode( authorName, "UTF-8" ).replace( "+", "%20" );
		// get the resources ( authors or publications )
		HttpGet httpGet = new HttpGet( authorCatalog );
		httpGet.setHeader( "Authorization", "Bearer " + tokenProperty.getValue() );
	    DefaultHttpClient apacheHttpClient = ApacheHttpTransport.newDefaultHttpClient();
	    HttpResponse httpResponse = apacheHttpClient.execute(httpGet);


		// map the results into jsonMap
		ObjectMapper mapper = new ObjectMapper();

		JsonNode resultsNode = null;
		try
		{
			resultsNode = mapper.readTree( httpResponse.getEntity().getContent() );
		}
		catch ( Exception e )
		{
		}

		if ( resultsNode == null )
			return Collections.emptyList();

		if ( resultsNode.isArray() )
			for ( JsonNode publicationNode : resultsNode )
				authorList.add( extractAuthorDetail( publicationNode, tokenProperty ) );
		else
			authorList.add( extractAuthorDetail( resultsNode, tokenProperty ) );

		return authorList;
	}

	/**
	 * Parse Authors JSON into Java Map
	 * 
	 * @param authorNode
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private static Map<String, String> extractAuthorDetail( JsonNode authorNode, SourceProperty tokenProperty ) throws ClientProtocolException, IOException
	{
		Map<String, String> authorDetailMap = new LinkedHashMap<String, String>();

		if ( !authorNode.path( "link" ).isMissingNode() )
		{
			String authURL = authorNode.path( "link" ).textValue();
			try
			{
				Document document = PublicationCollectionHelper.getDocumentWithJsoup( authURL, 1000 );
				if ( document != null )
				{
					Elements hindexRowList = document.select( HtmlSelectorConstant.MDY_AUTHOR_STATISTICS_ROW_DETAIL ).select( ".stat-hindex" );
					if ( hindexRowList != null && hindexRowList.size() > 0 )
					{
						String hindex = hindexRowList.first().select( ".number" ).text();
						authorDetailMap.put( "hindex", hindex );
					}

					Elements citationsRowList = document.select( HtmlSelectorConstant.MDY_AUTHOR_STATISTICS_ROW_DETAIL ).select( ".stat-citations" );
					if ( citationsRowList != null && citationsRowList.size() > 0 )
					{
						String citedBy = citationsRowList.first().select( ".number" ).text();
						authorDetailMap.put( "citedby", citedBy );
					}
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
		}
		if ( !authorNode.path( "display_name" ).isMissingNode() )
			authorDetailMap.put( "name", authorNode.path( "display_name" ).textValue() );
		if ( !authorNode.path( "last_name" ).isMissingNode() )
			authorDetailMap.put( "lastName", authorNode.path( "last_name" ).textValue() );
		if ( !authorNode.path( "first_name" ).isMissingNode() )
			authorDetailMap.put( "firstName", authorNode.path( "first_name" ).textValue() );
		if ( !authorNode.path( "email" ).isMissingNode() )
			authorDetailMap.put( "email", authorNode.path( "email" ).textValue() );
		if ( !authorNode.path( "institution_details" ).isMissingNode() )
		{
			JsonNode institutionNode = authorNode.path( "institution_details" );
			if ( !institutionNode.path( "name" ).isMissingNode() )
				authorDetailMap.put( "institutionName", institutionNode.path( "name" ).textValue() );
			if ( !institutionNode.path( "city" ).isMissingNode() )
				authorDetailMap.put( "institutionCity", institutionNode.path( "city" ).textValue() );
			if ( !institutionNode.path( "state" ).isMissingNode() )
				authorDetailMap.put( "institutionState", institutionNode.path( "state" ).textValue() );
			if ( !institutionNode.path( "country" ).isMissingNode() )
				authorDetailMap.put( "institutionCountry", institutionNode.path( "country" ).textValue() );
		}
		if ( !authorNode.path( "research_interests" ).isMissingNode() )
			authorDetailMap.put( "researchInterests", authorNode.path( "research_interests" ).textValue() );
		if ( !authorNode.path( "academic_status" ).isMissingNode() )
			authorDetailMap.put( "academicStatus", authorNode.path( "academic_status" ).textValue() );
		if ( !authorNode.path( "discipline" ).isMissingNode() )
		{
			JsonNode disciplineNode = authorNode.path( "discipline" );
			if ( !disciplineNode.path( "name" ).isMissingNode() )
				authorDetailMap.put( "discipline", disciplineNode.path( "name" ).textValue() );
		}
		if ( !authorNode.path( "photo" ).isMissingNode() )
		{
			JsonNode authorPhoto = authorNode.path( "photo" );
			if ( !authorPhoto.path( "standard" ).isMissingNode() )
			{
				String photoPath = authorPhoto.path( "standard" ).textValue();
				if ( !photoPath.contains( "awaiting" ) )
					authorDetailMap.put( "photo", photoPath );
			}
		}
		if ( !authorNode.path( "location" ).isMissingNode() )
		{
			JsonNode authorLocation = authorNode.path( "location" );
			if ( !authorLocation.path( "latitude" ).isMissingNode() )
				authorDetailMap.put( "locationLatitude", authorLocation.path( "latitude" ).textValue() );
			if ( !authorLocation.path( "longitude" ).isMissingNode() )
				authorDetailMap.put( "locationLongitude", authorLocation.path( "longitude" ).textValue() );
			if ( !authorLocation.path( "city" ).isMissingNode() )
				authorDetailMap.put( "locationCity", authorLocation.path( "city" ).textValue() );
			if ( !authorLocation.path( "state" ).isMissingNode() )
				authorDetailMap.put( "locationState", authorLocation.path( "state" ).textValue() );
			if ( !authorLocation.path( "country" ).isMissingNode() )
				authorDetailMap.put( "locationCountry", authorLocation.path( "country" ).textValue() );
		}

		authorDetailMap.put( "source", SourceType.MENDELEY.toString() );
		authorDetailMap.put( "url", "MENDELEY" );

		return authorDetailMap;
	}

	/**
	 * Get List of Publication Detail from Mendeley in Java Map
	 * 
	 * @param author
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public static List<Map<String, String>> getPublicationDetailList( Author author, Source source ) throws IOException
	{
		List<Map<String, String>> publicationMapLists = new ArrayList<Map<String, String>>();

		// for search catalog properties
		SourceProperty searchCatalogProperty = source.getSourcePropertyByIdentifiers( "catalog", "SEARCH_CATALOG" );
		// for token properties
		SourceProperty tokenProperty = source.getSourcePropertyByIdentifiers( "oauth2", "TOKEN" );

		// if token null
		if ( tokenProperty.getValue() == null )
			return Collections.emptyList();

		String publicationCatalog = searchCatalogProperty.getValue() + "?author=" + URLEncoder.encode( author.getName(), "UTF-8" ).replace( "+", "%20" ) + "&limit=100";
		// get the resources ( authors or publications )
		HttpGet httpGet = new HttpGet( publicationCatalog );
		httpGet.setHeader( "Authorization", "Bearer " + tokenProperty.getValue() );
		DefaultHttpClient apacheHttpClient = ApacheHttpTransport.newDefaultHttpClient();
		HttpResponse httpResponse = apacheHttpClient.execute( httpGet );

		// TODO: check for invalid token

		// map the results into jsonMap
		ObjectMapper mapper = new ObjectMapper();
		JsonNode resultsNode = mapper.readTree( httpResponse.getEntity().getContent() );
		if ( resultsNode.isArray() )
			for ( JsonNode publicationNode : resultsNode )
			{
				if ( isPublicationAuthorCorrect( author, publicationNode ) )
					publicationMapLists.add( extractPublicationDetail( publicationNode ) );
			}
		else
		{
			if ( isPublicationAuthorCorrect( author, resultsNode ) )
				publicationMapLists.add( extractPublicationDetail( resultsNode ) );
		}

		return publicationMapLists;
	}

	/*
	 * check if author on publication match with target author
	 */
	public static boolean isPublicationAuthorCorrect( Author author, JsonNode publicationNode )
	{
		if ( !publicationNode.path( "authors" ).isMissingNode() )
		{
			if ( publicationNode.path( "authors" ).isArray() )
			{
				for ( JsonNode authorNode : publicationNode.path( "authors" ) )
				{
					String authorName = "";
					if ( !authorNode.path( "first_name" ).isMissingNode() )
						authorName += authorNode.path( "first_name" ).textValue().toLowerCase() + " ";
					if ( !authorNode.path( "last_name" ).isMissingNode() )
						authorName += authorNode.path( "last_name" ).textValue().toLowerCase();

					authorName = authorName.trim();

					if ( !authorName.equals( "" ) && authorName.equals( author.getName().toLowerCase() ) )
						return true;
				}
			}
		}

		return false;
	}

	/**
	 * Parse publication detail into java map
	 * 
	 * @param publicationNode
	 * @return
	 */
	private static Map<String, String> extractPublicationDetail( JsonNode publicationNode )
	{
		Map<String, String> publicationDetailMap = new LinkedHashMap<String, String>();

		if ( publicationNode.path( "authors" ).isArray() )
		{
			String coauthor = "";
			int index = 0;
			for ( JsonNode authorNode : publicationNode.path( "authors" ) )
			{
				if ( index > 0 )
					coauthor += ",";
				String authorName = "";
				if ( !authorNode.path( "first_name" ).isMissingNode() )
					authorName += authorNode.path( "first_name" ).textValue().toLowerCase() + " ";
				if ( !authorNode.path( "last_name" ).isMissingNode() )
					authorName += authorNode.path( "last_name" ).textValue().toLowerCase();

				coauthor += authorName;
				index++;
			}
			publicationDetailMap.put( "coauthor", coauthor );
		}

		if ( !publicationNode.path( "title" ).isMissingNode() )
			publicationDetailMap.put( "title", publicationNode.path( "title" ).textValue() );
		if ( !publicationNode.path( "type" ).isMissingNode() )
		{
			String pubType = publicationNode.path( "type" ).textValue().toUpperCase();
			if ( isPublicationTypeContains( pubType ) )
				publicationDetailMap.put( "type", pubType );
		}
		if ( !publicationNode.path( "year" ).isMissingNode() )
			publicationDetailMap.put( "datePublished", String.valueOf( publicationNode.path( "year" ).intValue() ) );
		if ( !publicationNode.path( "abstract" ).isMissingNode() )
			publicationDetailMap.put( "abstract", publicationNode.path( "abstract" ).textValue().replaceAll( "\\\\n", " " ) );
		if ( !publicationNode.path( "keywords" ).isMissingNode() )
		{
			String keyword = "";
			if ( publicationNode.path( "keywords" ).isArray() )
			{
				for ( JsonNode keywordNode : publicationNode.path( "keywords" ) )
				{
					if ( !keyword.equals( "" ) )
						keyword += ",";
					keyword += keywordNode.textValue();
				}
			}
			if ( !keyword.equals( "" ) )
				publicationDetailMap.put( "keyword", keyword );
		}
		// add other attributes here

		publicationDetailMap.put( "source", SourceType.MENDELEY.toString() );

		return publicationDetailMap;
	}

	/**
	 * Check accepted publication type
	 * 
	 * @param pubType
	 * @return
	 */
	public static boolean isPublicationTypeContains( String pubType )
	{

		for ( PublicationType pt : PublicationType.values() )
		{
			if ( pt.name().equals( pubType ) )
			{
				return true;
			}
		}

		return false;
	}
	/*
	 * 
	 * [ { "title":
	 * "PLEF: A Conceptual Framework for Mashup Personal Learning Environments",
	 * "type":"journal", "authors":[ { "first_name":"Mohamed Amine",
	 * "last_name":"Chatti" } ], "year":2009, "source":
	 * "Learning Technology Newsletter", "identifiers":{
	 * "scopus":"2-s2.0-78650291287" },
	 * "id":"ed205f4d-f930-3c76-b25a-35836fef4337", "link":
	 * "http://www.mendeley.com/research/plef-conceptual-framework-mashup-personal-learning-environments",
	 * "abstract":
	 * "This master thesis proposes to develop Personal Learning Environment Framework (PLEF), a framework that enable the creation of PLEs. This master thesis will also provide supporting components of the framework and a front end interface of the system. The whole result of the master thesis will prove that the framework is usable and useful."
	 * }
	 */

	// public
	/**
	 * Logged in as sigit nugraha Display name sigit nugraha Id
	 * cbebc27d-600d-35db-9e57-1ecbc24229de
	 * 
	 * oAuth info Access token
	 * MSwxNDM3NTg5NzI0Mjk3LDQ0NTI3OTA2MSwxMDI4LGFsbCwsRmd3RmhaUktBUkpOM192Uk1YeGp6Zmt0S1BJ
	 * Refresh token
	 * MSw0NDUyNzkwNjEsMTAyOCxhbGwsLCwsRjVNNU1HZ0UtbTB2d01yM3NjcHNXTGl0TUVr>
	 * 
	 * $config['mendeleyclientid'] = '392'; $config['mendeleykey'] =
	 * 'VWheQe6qKEGeUXQcLj1NDTCqxkP29PyJ';
	 */
}

package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.apache.ApacheHttpTransport;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceProperty;
import de.rwth.i9.palm.model.SourceType;

public class MicrosoftAcademicSearchPublicationCollection extends PublicationCollection
{

	private final static Logger log = LoggerFactory.getLogger( MicrosoftAcademicSearchPublicationCollection.class );

	/**
	 * Old API url
http://academic.research.microsoft.com/json.svc/search?AppId=e028e2a5-972d-4ba5-a4c0-2f25860b1203&AuthorQuery=mohamed%20amine%20chatti&ResultObjects=Publication&PublicationContent=AllInfo&StartIdx=0&EndIdx=99
	 * 
	 * New API == get author id
	 * https://api.datamarket.azure.com/Data.ashx/MRC/MicrosoftAcademic/v2/
	 * Author?$filter=Name+eq+%27mohamed%20amine%20chatti%27&$format=json == get
	 * publication id list
	 * https://api.datamarket.azure.com/Data.ashx/MRC/MicrosoftAcademic/v2/
	 * Paper_Author?$filter=AuthorID%20+eq+220509&$format=json&$skip=0 == get
	 * publication detail
	 * 
	 */
	
	public static List<Map<String, String>> getPublicationDetailList( Author author, Source source ) throws IOException
	{
		List<Map<String, String>> publicationMapLists = new ArrayList<Map<String, String>>();
		
		// for search profile properties
		SourceProperty endPointProperty = source.getSourcePropertyByIdentifiers( "OLD_API", "END_POINT" );
		SourceProperty appIdProperty = source.getSourcePropertyByIdentifiers( "OLD_API", "APP_ID" );
		
		String publicationCatalog = endPointProperty.getValue() + 
				"?AppId=" + appIdProperty.getValue() + "&AuthorQuery=" + URLEncoder.encode( author.getName(), "UTF-8" ).replace( "+", "%20" ) 
				+ "&ResultObjects=Publication&PublicationContent=AllInfo&StartIdx=0&EndIdx=99";
		// get the resources ( authors or publications )
		HttpGet httpGet = new HttpGet( publicationCatalog );
		DefaultHttpClient apacheHttpClient = ApacheHttpTransport.newDefaultHttpClient();
		HttpResponse httpResponse = apacheHttpClient.execute( httpGet );

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
		
		JsonNode publicationResultNode = null;

		try
		{
			publicationResultNode = resultsNode.path( "d" ).path( "Publication" ).path( "Result" );
		}
		catch ( Exception e )
		{
			// TODO: handle exception
			log.error( "Publication result node not found" );
		}

		if ( publicationResultNode == null )
			return Collections.emptyList();

		if ( publicationResultNode.isArray() )
			for ( JsonNode publicationNode : publicationResultNode )
				if ( isPublicationAuthorCorrect( author, publicationNode ) )
					publicationMapLists.add( extractPublicationDetail( publicationNode ) );
		else
			if ( isPublicationAuthorCorrect( author, publicationResultNode ) )
				publicationMapLists.add( extractPublicationDetail( publicationResultNode ) );

		return publicationMapLists;
	}
	
	/*
	 * check if author on publication match with target author
	 */
	public static boolean isPublicationAuthorCorrect( Author author, JsonNode publicationNode )
	{
		if ( !publicationNode.path( "Author" ).isMissingNode() )
		{
			if ( publicationNode.path( "Author" ).isArray() )
			{
				for ( JsonNode authorNode : publicationNode.path( "Author" ) )
				{
					String authorName = "";
					if ( !authorNode.path( "FirstName" ).isMissingNode() )
						authorName += authorNode.path( "FirstName" ).textValue().toLowerCase() + " ";
					if ( !authorNode.path( "MiddleName" ).isMissingNode() )
						authorName += authorNode.path( "MiddleName" ).textValue().toLowerCase() + " ";
					if ( !authorNode.path( "LastName" ).isMissingNode() )
						authorName += authorNode.path( "LastName" ).textValue().toLowerCase();

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
		
		if ( !publicationNode.path( "Title" ).isMissingNode() )
			publicationDetailMap.put( "title", publicationNode.path( "Title" ).textValue() );

		if ( !publicationNode.path( "Journal" ).isMissingNode() )
			publicationDetailMap.put( "type", "JOURNAL" );
		if ( !publicationNode.path( "Conference" ).isMissingNode() )
			publicationDetailMap.put( "type", "CONFERENCE" );

		if ( !publicationNode.path( "Year" ).isMissingNode() )
		{
			int year = publicationNode.path( "Year" ).intValue();
			if ( year > 1800 )
				publicationDetailMap.put( "datePublished", Integer.toString( year ) );
		}
		if ( !publicationNode.path( "Author" ).isMissingNode() )
		{
			String coauthor = "";
			String msAuthorId = "";
			if ( publicationNode.path( "Author" ).isArray() )
			{
				for ( JsonNode coauthorNode : publicationNode.path( "Author" ) )
				{
					if ( !coauthor.equals( "" ) )
					{
						coauthor += ",";
						msAuthorId += ",";
					}
					if ( !coauthorNode.path( "FirstName" ).textValue().equals( "" ) )
						coauthor += coauthorNode.path( "FirstName" ).textValue() + " ";
					if ( !coauthorNode.path( "MiddleName" ).textValue().equals( "" ) )
						coauthor += coauthorNode.path( "MiddleName" ).textValue() + " ";
					if ( !coauthorNode.path( "LastName" ).textValue().equals( "" ) )
						coauthor += coauthorNode.path( "LastName" ).textValue();

					msAuthorId += coauthorNode.path( "ID" ).intValue();
				}
			}

			if ( !coauthor.equals( "" ) )
			{
				publicationDetailMap.put( "coauthor", coauthor );
				publicationDetailMap.put( "coauthorId", msAuthorId );
			}
		}

		if ( !publicationNode.path( "Abstract" ).isMissingNode() )
		{
			String abstractText = publicationNode.path( "Abstract" ).textValue();
			if ( !abstractText.equals( "" ) )
				publicationDetailMap.put( "abstract", abstractText );
		}

		if ( !publicationNode.path( "CitationCount" ).isMissingNode() )
			publicationDetailMap.put( "citedby", Integer.toString( ( publicationNode.path( "CitationCount" ).intValue() ) ) );

		if ( !publicationNode.path( "Keyword" ).isMissingNode() )
		{
			String keyword = "";
			if ( publicationNode.path( "Keyword" ).isArray() )
			{
				for ( JsonNode keywordNode : publicationNode.path( "Keyword" ) )
				{
					if ( !keyword.equals( "" ) )
						keyword += ",";
					keyword += keywordNode.path( "Name" ).textValue();
				}
			}

			if ( !keyword.equals( "" ) )
				publicationDetailMap.put( "keyword", keyword );
		}

		// add other attributes here

		publicationDetailMap.put( "source", SourceType.MAS.toString() );
		
		return publicationDetailMap;
	}
}

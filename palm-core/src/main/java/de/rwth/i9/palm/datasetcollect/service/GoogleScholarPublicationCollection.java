package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceProperty;
import de.rwth.i9.palm.model.SourceType;

public class GoogleScholarPublicationCollection extends PublicationCollection
{
	private final static Logger log = LoggerFactory.getLogger( GoogleScholarPublicationCollection.class );

	public GoogleScholarPublicationCollection()
	{
		super();
	}

	public static List<Map<String, String>> getListOfAuthors( String authorName, Source source ) throws IOException
	{
		List<Map<String, String>> authorList = new ArrayList<Map<String, String>>();

		String url = "https://scholar.google.com/citations?view_op=search_authors&mauthors=" + authorName.replace( " ", "-" );

		// Using jsoup java html parser library
		Document document = PublicationCollectionHelper.getDocumentWithJsoup( url, 5000, getGoogleScholarCookie( source ) );

		if ( document == null )
			return Collections.emptyList();

		Elements authorListNodes = document.select( HtmlSelectorConstant.GS_AUTHOR_LIST_CONTAINER );

		if ( authorListNodes.size() == 0 )
		{
			// log.info( "No author with name '{}' with selector '{}' on google
			// scholar '{}'", authorName,
			// HtmlSelectorConstant.GS_AUTHOR_LIST_CONTAINER, url );
			return Collections.emptyList();
		}

		// if the authors is present
		for ( Element authorListNode : authorListNodes )
		{
			Map<String, String> eachAuthorMap = new LinkedHashMap<String, String>();
			String name = authorListNode.select( HtmlSelectorConstant.GS_AUTHOR_LIST_NAME ).text();
			// get author name
			eachAuthorMap.put( "name", name );
			// set source
			eachAuthorMap.put( "source", SourceType.GOOGLESCHOLAR.toString() );
			// get author url
			eachAuthorMap.put( "url", authorListNode.select( "a" ).first().absUrl( "href" ) );
			// get author photo
			String photoUrl = authorListNode.select( "img" ).first().absUrl( "src" );
			if ( !photoUrl.contains( "avatar_scholar" ) )
				eachAuthorMap.put( "photo", photoUrl );
			// get author affiliation
			eachAuthorMap.put( "affiliation", authorListNode.select( HtmlSelectorConstant.GS_AUTHOR_LIST_AFFILIATION ).html().replace( "&#x2026;", "" ).trim() );
			
			String citedBy = authorListNode.select( HtmlSelectorConstant.GS_AUTHOR_LIST_NOCITATION ).html();
			if( citedBy != null && citedBy.length() > 10)
				eachAuthorMap.put( "citedby", citedBy.substring( "Cited by".length() ).trim() );

			String authURL = authorListNode.select( "a" ).first().absUrl( "href" ) + "&view_op=list_works";
			Document authDocument = PublicationCollectionHelper.getDocumentWithJsoup( authURL, 5000, getGoogleScholarCookie( source ) );

			if ( authDocument != null )
			{
				Elements authorDetailsNodes = authDocument.select( HtmlSelectorConstant.GS_INDICES_ROW_LIST );

				if ( authorDetailsNodes.size() != 0 )
				{
					// get author hindex
					eachAuthorMap.put( "hindex", authorDetailsNodes.get( 2 ).select( "td" ).get( 1 ).text() );
				}
			}
			authorList.add( eachAuthorMap );
		}

		return authorList;
	}

	public static List<Map<String, String>> getPublicationListByAuthorUrl( String url, Source source ) throws IOException
	{
		List<Map<String, String>> publicationMapLists = new ArrayList<Map<String, String>>();

		// Using jsoup java html parser library
		Document document = PublicationCollectionHelper.getDocumentWithJsoup( url + "&view_op=list_works&cstart=0&pagesize=100", 5000, getGoogleScholarCookie( source ) );

		if ( document == null )
			return Collections.emptyList();

		Elements publicationRowList = document.select( HtmlSelectorConstant.GS_PUBLICATION_ROW_LIST );

		if ( publicationRowList.size() == 0 )
		{
			log.info( "No publication found " );
			return Collections.emptyList();
		}

		for ( Element eachPublicationRow : publicationRowList )
		{
			Map<String, String> publicationDetails = new LinkedHashMap<String, String>();
			// set source
			publicationDetails.put( "source", SourceType.GOOGLESCHOLAR.toString() );
			publicationDetails.put( "url", eachPublicationRow.select( "a" ).first().absUrl( "href" ) );

			String title = eachPublicationRow.select( "a" ).first().text();
			if ( title.toLowerCase().contains( "special issue article" ) )
				continue;

			publicationDetails.put( "title", title );
			publicationDetails.put( "coauthor", eachPublicationRow.select( HtmlSelectorConstant.GS_PUBLICATION_COAUTHOR_AND_VENUE ).first().text() );
			String venue = eachPublicationRow.select( HtmlSelectorConstant.GS_PUBLICATION_COAUTHOR_AND_VENUE ).get( 1 ).text().trim();
			if ( !venue.equals( "" ) && venue.length() < 80 )
				publicationDetails.put( "eventName", venue );
			String noCitation = eachPublicationRow.select( HtmlSelectorConstant.GS_PUBLICATION_NOCITATION ).text().replaceAll( "[^\\d]", "" );
			if ( !noCitation.equals( "" ) )
			{
				publicationDetails.put( "citedby", noCitation );
				// get citedby url
				publicationDetails.put( "citedbyUrl", eachPublicationRow.select( HtmlSelectorConstant.GS_PUBLICATION_NOCITATION ).select( "a" ).first().absUrl( "href" ) );
			}
			String date = eachPublicationRow.select( HtmlSelectorConstant.GS_PUBLICATION_DATE ).text().trim();
			if ( date.equals( "" ) )
				continue;

			// only pick publication with date
			publicationDetails.put( "datePublished", date );

			publicationMapLists.add( publicationDetails );
		}

		return publicationMapLists;
	}

	public static Map<String, String> getPublicationDetailByPublicationUrl( String url, Source source ) throws IOException
	{
		Map<String, String> publicationDetailMaps = new LinkedHashMap<String, String>();
		// Using jsoup java html parser library
		Document document = PublicationCollectionHelper.getDocumentWithJsoup( url, 5000, getGoogleScholarCookie( source ) );

		if ( document == null )
			return Collections.emptyMap();

		Elements publicationDetailContainer = document.select( HtmlSelectorConstant.GS_PUBLICATION_DETAIL_CONTAINER );

		if ( publicationDetailContainer.size() == 0 )
		{
			log.info( "No publication detail found " );
			return Collections.emptyMap();
		}

		publicationDetailMaps.put( "title", publicationDetailContainer.get( 0 ).select( HtmlSelectorConstant.GS_PUBLICATION_DETAIL_TITLE ).text() );



		try
		{
			Elements publicationPdfUrl = publicationDetailContainer.get( 0 ).select( HtmlSelectorConstant.GS_PUBLICATION_DETAIL_PDF );
			if ( publicationPdfUrl != null )
			{
				publicationDetailMaps.put( "doc_url", publicationPdfUrl.select( "a" ).first().absUrl( "href" ) );

				String docName = publicationDetailContainer.get( 0 ).select( HtmlSelectorConstant.GS_PUBLICATION_DETAIL_PDF ).text();
				if ( docName != null )
					publicationDetailMaps.put( "doc", docName );
				else
					publicationDetailMaps.put( "doc", "null" );
			}

		}
		catch ( Exception e )
		{
			// TODO: handle exception
		}

		Elements publicationDetailsRows = publicationDetailContainer.get( 0 ).select( HtmlSelectorConstant.GS_PUBLICATION_DETAIL_PROP );

		for ( Element publicationDetail : publicationDetailsRows )
		{
			if ( publicationDetail.select( HtmlSelectorConstant.GS_PUBLICATION_DETAIL_PROP_LABEL ).text().equals( "Total citations" ) )
			{
				String citations = publicationDetail.select( HtmlSelectorConstant.GS_PUBLICATION_DETAIL_PROP_VALUE ).select( "a" ).text();
				publicationDetailMaps.put( "citedby", citations.split( " " )[2] );
				// TODO: record publication citation yearly
			}
			else
			{
				publicationDetailMaps.put( publicationDetail.select( HtmlSelectorConstant.GS_PUBLICATION_DETAIL_PROP_LABEL ).text(), publicationDetail.select( HtmlSelectorConstant.GS_PUBLICATION_DETAIL_PROP_VALUE ).text() );
			}
		}

		return publicationDetailMaps;
	}

	/**
	 * Google Scholar cache, update in case IP being blocked by google
	 * 
	 * @return
	 */
	private static Map<String, String> getGoogleScholarCookie( Source source )
	{
		if ( source == null )
			return Collections.emptyMap();

		Map<String, String> cookies = new HashMap<String, String>();

		for ( SourceProperty sourceProperty : source.getSourceProperties() )
		{
			if ( sourceProperty.getMainIdentifier().equals( "cookie" ) && sourceProperty.isValid() )
				cookies.put( sourceProperty.getSecondaryIdentifier(), sourceProperty.getValue() );
		}
		return cookies;
	}
}

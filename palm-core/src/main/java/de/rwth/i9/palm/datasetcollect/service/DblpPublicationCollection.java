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
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth.i9.palm.model.PublicationType;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceProperty;
import de.rwth.i9.palm.model.SourceType;

public class DblpPublicationCollection extends PublicationCollection
{
	private final static Logger log = LoggerFactory.getLogger( DblpPublicationCollection.class );

	public DblpPublicationCollection()
	{
		super();
	}

	/**
	 * Get possible author
	 * 
	 * @param authorName
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public static List<Map<String, String>> getListOfAuthors( String authorName, Source source ) throws IOException
	{
		List<Map<String, String>> authorList = new ArrayList<Map<String, String>>();

		String url = "http://dblp.uni-trier.de/search/author?q=" + authorName.replace( " ", "+" );
		// Using jsoup java html parser library
		Document document = PublicationCollectionHelper.getDocumentWithJsoup( url, 15000, getDblpCookie( source ) );

		if ( document == null )
			return Collections.emptyList();

		// find out page is author page or search page
		String pageTitle = document.select( "title" ).text();
		if ( pageTitle.toLowerCase().contains( "author search" ) )
		{

			Element authorContainer = document.select( "div#completesearch-authors>div" ).first();

			Element authorListCont = authorContainer.select( "ul" ).first();

			if ( authorListCont == null )
				return Collections.emptyList();

			Elements authorListNodes = authorListCont.select( "li" );

			if ( authorListNodes == null )
				return Collections.emptyList();

			if ( authorListNodes.size() == 0 )
			{
				// log.info( "No author with name '{}' with selector '{}' on
				// CiteSeerX '{}'", authorName,
				// HtmlSelectorConstant.CSX_AUTHOR_LIST, url );
				return Collections.emptyList();
			}

			// if the authors is present
			for ( Element authorListNode : authorListNodes )
			{
				Map<String, String> eachAuthorMap = new LinkedHashMap<String, String>();
				String name = authorListNode.select( "a" ).first().text();
				// get author name
				eachAuthorMap.put( "name", name );
				// set source
				eachAuthorMap.put( "source", SourceType.DBLP.toString() );
				// get author url
				eachAuthorMap.put( "url", authorListNode.select( "a" ).first().absUrl( "href" ) );

				authorList.add( eachAuthorMap );
			}
		}
		else
		{
			Map<String, String> eachAuthorMap = new LinkedHashMap<String, String>();

			// get author name from URL
			// e.g:http://dblp.uni-trier.de/pers/hd/c/Chatti:Mohamed_Amine?q=mohamed+amin+chatti
			String[] urlAuthorQuery = document.baseUri().split( "\\?" );

			if ( pageTitle.startsWith( "dblp: " ) )
			{
				// get author name from page title
				String authorNameFromPageTitle = pageTitle.substring( 6 ).trim();
				String[] authorNameArray = authorNameFromPageTitle.split( " " );
				String lastName = authorNameArray[authorNameArray.length - 1];
				String firstName = authorNameFromPageTitle.substring( 0, authorNameFromPageTitle.length() - lastName.length() ).trim();

				// set author name
				eachAuthorMap.put( "name", firstName + " " + lastName );
				eachAuthorMap.put( "lastName", lastName );
				eachAuthorMap.put( "firstName", firstName );
				// set source
				eachAuthorMap.put( "source", SourceType.DBLP.toString() );
				// set author url
				eachAuthorMap.put( "url", urlAuthorQuery[0] );

				authorList.add( eachAuthorMap );
			}
			else
			{

				// e.g:http://dblp.uni-trier.de/pers/hd/c/Chatti:Mohamed_Amine
				String[] urlAuthor = urlAuthorQuery[0].split( "/" );

				// Chatti:Mohamed_Amine
				String[] authorSplitName = urlAuthor[urlAuthor.length - 1].split( ":" );

				String firstName = authorSplitName[1].replace( "_", " " ).toLowerCase();
				String lastName = authorSplitName[0].toLowerCase();

				// set author name
				eachAuthorMap.put( "name", firstName + " " + lastName );
				eachAuthorMap.put( "lastName", lastName );
				eachAuthorMap.put( "firstName", firstName );
				// set source
				eachAuthorMap.put( "source", SourceType.DBLP.toString() );
				// set author url
				eachAuthorMap.put( "url", urlAuthorQuery[0] );

				authorList.add( eachAuthorMap );
			}
		}

		return authorList;
	}

	/**
	 * get author page and publication list
	 * 
	 * @param url
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public static List<Map<String, String>> getPublicationListByAuthorUrl( String url, Source source ) throws IOException
	{
		List<Map<String, String>> publicationMapLists = new ArrayList<Map<String, String>>();

			// Using jsoup java html parser library
		Document document = PublicationCollectionHelper.getDocumentWithJsoup( url, 5000, getDblpCookie( source ) );

		if ( document == null )
			return Collections.emptyList();

		Element publicationContainer = document.select( "#publ-section" ).first();

		if ( publicationContainer == null )
		{
			log.info( "No publication found " );
			return Collections.emptyList();
		}

		// get publication categories
		Elements publicationSections = publicationContainer.select( "div.hideable" );

		for ( Element publicationSection : publicationSections )
		{

			Element sectionHeader = publicationSection.select( "header" ).first();

			if ( sectionHeader.attr( "id" ).equals( "book" ) )
			{

				Elements publicationList = publicationSection.select( "ul.publ-list li.book" );

				for ( Element eachPublication : publicationList )
				{
					Map<String, String> publicationDetails = new LinkedHashMap<String, String>();
					publicationDetails.put( "type", PublicationType.BOOK.toString() );

					Element sourceElementContainer = eachPublication.select( "nav.publ" ).select( "li" ).first();
					Elements sourceElements = sourceElementContainer.select( "div.body" ).first().select( "a" );
					if ( sourceElements != null && sourceElements.size() > 0 )
					{
						String docUrl = "";
						String doc = "";
						for ( Element sourceElement : sourceElements )
						{
							docUrl += sourceElement.absUrl( "href" ) + " ";
							doc += sourceElement.text().replace( ",", "" ) + ",";
						}
						publicationDetails.put( "doc_url", docUrl.substring( 0, docUrl.length() - 1 ) );
						publicationDetails.put( "doc", doc.substring( 0, doc.length() - 1 ) );
					}
					Element dataElement = eachPublication.select( "div.data" ).first();

					Elements authorElements = dataElement.select( "[itemprop=author]" );
					String authorNames = "";
					String authorUrl = "";
					for ( Element authorElement : authorElements )
					{
						authorNames += authorElement.text().replace( ",", " " ) + ",";
						Element authorUrlElement = authorElement.select( "a" ).first();
						if ( authorUrlElement != null )
							authorUrl += authorUrlElement.absUrl( "href" ) + " ";
						else
							authorUrl += "null ";
					}
					publicationDetails.put( "coauthor", authorNames.substring( 0, authorNames.length() - 1 ) );
					publicationDetails.put( "coauthorUrl", authorUrl );

					publicationDetails.put( "title", dataElement.select( "span.title" ).text() );
					publicationDetails.put( "datePublished", dataElement.select( "[itemprop=datePublished]" ).text() );
					if ( dataElement.select( "[itemprop=pagination]" ) != null )
						publicationDetails.put( "page", dataElement.select( "[itemprop=pagination]" ).text() );

					// other information
					String otherInformation = "";
					for ( Node child : dataElement.childNodes() )
					{
						if ( child instanceof TextNode )
						{
							otherInformation += ( (TextNode) child ).text() + " ";
						}
					}

					otherInformation = otherInformation.replaceAll( "[\\.:]*", "" ).trim();
					if ( !otherInformation.equals( "" ) )
						publicationDetails.put( "otherInformation", otherInformation );

					publicationDetails.put( "source", SourceType.DBLP.toString() );

					publicationMapLists.add( publicationDetails );
				}

			}
			else if ( sectionHeader.attr( "id" ).equals( "article" ) )
			{
				Elements publicationList = publicationSection.select( "ul.publ-list li.article" );

				for ( Element eachPublication : publicationList )
				{
					Map<String, String> publicationDetails = new LinkedHashMap<String, String>();
					publicationDetails.put( "type", PublicationType.JOURNAL.toString() );

					Element sourceElementContainer = eachPublication.select( "nav.publ" ).select( "li" ).first();
					Elements sourceElements = sourceElementContainer.select( "div.body" ).first().select( "a" );
					if ( sourceElements != null && sourceElements.size() > 0 )
					{
						String docUrl = "";
						String doc = "";
						for ( Element sourceElement : sourceElements )
						{
							docUrl += sourceElement.absUrl( "href" ) + " ";
							doc += sourceElement.text().replace( ",", "" ) + ",";
						}
						publicationDetails.put( "doc_url", docUrl.substring( 0, docUrl.length() - 1 ) );
						publicationDetails.put( "doc", doc.substring( 0, doc.length() - 1 ) );
					}

					Element dataElement = eachPublication.select( "div.data" ).first();

					Elements authorElements = dataElement.select( "[itemprop=author]" );
					String authorNames = "";
					String authorUrl = "";
					for ( Element authorElement : authorElements )
					{
						authorNames += authorElement.text().replace( ",", " " ) + ",";
						Element authorUrlElement = authorElement.select( "a" ).first();
						if ( authorUrlElement != null )
							authorUrl += authorUrlElement.absUrl( "href" ) + " ";
						else
							authorUrl += "null ";
					}
					publicationDetails.put( "coauthor", authorNames.substring( 0, authorNames.length() - 1 ) );
					publicationDetails.put( "coauthorUrl", authorUrl );

					publicationDetails.put( "title", dataElement.select( "span.title" ).text() );
					publicationDetails.put( "datePublished", dataElement.select( "[itemprop=datePublished]" ).text() );
					publicationDetails.put( "source", SourceType.DBLP.toString() );

					Element eventElement = dataElement.select( "> a" ).first();
					if ( eventElement != null )
					{
						String eventUrl = eventElement.absUrl( "href" );
						if ( eventUrl.contains( "#" ) )
							publicationDetails.put( "eventUrl", eventUrl.split( "#" )[0] );
						else
							publicationDetails.put( "eventUrl", eventUrl );

						publicationDetails.put( "eventName", eventElement.select( "[itemprop=name]" ).text() );
						if ( eventElement.select( "[itemprop=volumeNumber]" ) != null )
							publicationDetails.put( "eventVolume", eventElement.select( "[itemprop=volumeNumber]" ).text() );
						if ( eventElement.select( "[itemprop=issueNumber]" ) != null )
							publicationDetails.put( "eventNumber", eventElement.select( "[itemprop=issueNumber]" ).text() );
					}

					String page = dataElement.select( "[itemprop=pagination]" ).text();
					if ( page != null )
						publicationDetails.put( "page", page );

					publicationMapLists.add( publicationDetails );
				}

			}
			else if ( sectionHeader.attr( "id" ).equals( "inproceedings" ) )
			{
				Elements publicationList = publicationSection.select( "ul.publ-list li.inproceedings" );

				for ( Element eachPublication : publicationList )
				{
					Map<String, String> publicationDetails = new LinkedHashMap<String, String>();
					publicationDetails.put( "type", PublicationType.CONFERENCE.toString() );

					Element sourceElementContainer = eachPublication.select( "nav.publ" ).select( "li" ).first();
					Elements sourceElements = sourceElementContainer.select( "div.body" ).first().select( "a" );
					if ( sourceElements != null && sourceElements.size() > 0 )
					{
						String docUrl = "";
						String doc = "";
						for ( Element sourceElement : sourceElements )
						{
							docUrl += sourceElement.absUrl( "href" ) + " ";
							doc += sourceElement.text().replace( ",", "" ) + ",";
						}
						publicationDetails.put( "doc_url", docUrl.substring( 0, docUrl.length() - 1 ) );
						publicationDetails.put( "doc", doc.substring( 0, doc.length() - 1 ) );
					}

					Element dataElement = eachPublication.select( "div.data" ).first();

					if ( dataElement == null )
						continue;

					Elements authorElements = dataElement.select( "[itemprop=author]" );
					String authorNames = "";
					String authorUrl = "";
					for ( Element authorElement : authorElements )
					{
						authorNames += authorElement.text().replace( ",", " " ) + ",";
						Element authorUrlElement = authorElement.select( "a" ).first();
						if ( authorUrlElement != null )
							authorUrl += authorUrlElement.absUrl( "href" ) + " ";
						else
							authorUrl += "null ";
					}
					publicationDetails.put( "coauthor", authorNames.substring( 0, authorNames.length() - 1 ) );
					publicationDetails.put( "coauthorUrl", authorUrl );

					publicationDetails.put( "title", dataElement.select( "span.title" ).text() );
					publicationDetails.put( "datePublished", dataElement.select( "[itemprop=datePublished]" ).text() );
					publicationDetails.put( "source", SourceType.DBLP.toString() );

					Element eventElement = dataElement.select( "> a" ).first();
					if ( eventElement != null )
					{
						String eventUrl = eventElement.absUrl( "href" );
						if ( eventUrl.contains( "#" ) )
							publicationDetails.put( "eventUrl", eventUrl.split( "#" )[0] );
						else
							publicationDetails.put( "eventUrl", eventUrl );

						String eventShort = eventElement.select( "[itemprop=name]" ).text();
						if ( eventShort.contains( ")" ) )
						{
							eventShort = eventShort.replace( ")", "" );
							String[] eventShortSplit = eventShort.split( "\\(" );
							publicationDetails.put( "eventName", eventShortSplit[0].trim() );
							publicationDetails.put( "eventVolume", eventShortSplit[1].trim() );
						}
						else
						{
							publicationDetails.put( "eventShortName", eventShort.trim() );
						}
					}

					String page = dataElement.select( "[itemprop=pagination]" ).text();
					if ( page != null )
						publicationDetails.put( "page", page );

					publicationMapLists.add( publicationDetails );
				}
			}
			else if ( sectionHeader.attr( "id" ).equals( "informal" ) )
			{
				Elements publicationList = publicationSection.select( "ul.publ-list li.informal" );

				for ( Element eachPublication : publicationList )
				{
					Map<String, String> publicationDetails = new LinkedHashMap<String, String>();
					publicationDetails.put( "type", PublicationType.INFORMAL.toString() );

					Element sourceElementContainer = eachPublication.select( "nav.publ" ).select( "li" ).first();
					Elements sourceElements = sourceElementContainer.select( "div.body" ).first().select( "a" );
					if ( sourceElements != null && sourceElements.size() > 0 )
					{
						String docUrl = "";
						String doc = "";
						for ( Element sourceElement : sourceElements )
						{
							docUrl += sourceElement.absUrl( "href" ) + " ";
							doc += sourceElement.text().replace( ",", "" ) + ",";
						}
						publicationDetails.put( "doc_url", docUrl.substring( 0, docUrl.length() - 1 ) );
						publicationDetails.put( "doc", doc.substring( 0, doc.length() - 1 ) );
					}

					Element dataElement = eachPublication.select( "div.data" ).first();

					if ( dataElement == null )
						continue;

					Elements authorElements = dataElement.select( "[itemprop=author]" );
					String authorNames = "";
					String authorUrl = "";
					for ( Element authorElement : authorElements )
					{
						authorNames += authorElement.text().replace( ",", " " ) + ",";
						Element authorUrlElement = authorElement.select( "a" ).first();
						if ( authorUrlElement != null )
							authorUrl += authorUrlElement.absUrl( "href" ) + " ";
						else
							authorUrl += "null ";
					}
					publicationDetails.put( "coauthor", authorNames.substring( 0, authorNames.length() - 1 ) );
					publicationDetails.put( "coauthorUrl", authorUrl );

					publicationDetails.put( "title", dataElement.select( "span.title" ).text() );
					publicationDetails.put( "datePublished", dataElement.select( "[itemprop=datePublished]" ).text() );
					publicationDetails.put( "source", SourceType.DBLP.toString() );

					Element eventElement = dataElement.select( "> a" ).first();
					if ( eventElement != null )
					{
						String eventUrl = eventElement.absUrl( "href" );
						if ( eventUrl.contains( "#" ) )
							publicationDetails.put( "eventUrl", eventUrl.split( "#" )[0] );
						else
							publicationDetails.put( "eventUrl", eventUrl );

						String eventShort = eventElement.select( "[itemprop=name]" ).text();
						if ( eventShort.contains( ")" ) )
						{
							eventShort = eventShort.replace( ")", "" );
							String[] eventShortSplit = eventShort.split( "\\(" );
							publicationDetails.put( "eventName", eventShortSplit[0].trim() );
							publicationDetails.put( "eventVolume", eventShortSplit[1].trim() );
						}
						else
						{
							publicationDetails.put( "eventShortName", eventShort.trim() );
						}
					}

					String page = dataElement.select( "[itemprop=pagination]" ).text();
					if ( page != null )
						publicationDetails.put( "page", page );

					publicationMapLists.add( publicationDetails );
				}
			}
		}
		return publicationMapLists;
	}

	/**
	 * DBLP cache, important for select correct DBLP page before crawling
	 * 
	 * @return
	 */
	private static Map<String, String> getDblpCookie( Source source )
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

	/**
	 * There is an API available, but not really useful for getting the complete
	 * information of author and venue
	 * http://www.dblp.org/search/api/?q=ulrik%20schroeder&h=1000&c=4&f=0&format
	 * =json
	 */
}

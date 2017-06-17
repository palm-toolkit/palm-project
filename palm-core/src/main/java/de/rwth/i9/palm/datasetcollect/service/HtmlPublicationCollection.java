package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlPublicationCollection
{
	private final static Logger log = LoggerFactory.getLogger( HtmlPublicationCollection.class );

	/**
	 * Get keyword and abstract of a publication from any HTML page
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> getPublicationInformationFromHtmlPage( String url ) throws IOException
	{
		Map<String, String> publicationDetailMaps = new LinkedHashMap<String, String>();

		if ( url.contains( "doi.acm.org" ) )
		{
			Document document = PublicationCollectionHelper.getDocumentWithJsoup( url, 10000 );
			if ( document != null )
				url = document.baseUri();
		}
		if ( url.contains( "dl.acm.org/" ) )
		{
			url = url.replace( "citation.cfm?do", "tab_abstract.cfm?" );
			url = url.replace( "citation.cfm", "tab_abstract.cfm" );

			String[] urlSplit = url.split( "\\?id=" );
			if ( urlSplit.length == 2 )
			{
				if ( urlSplit[1].contains( "." ) )
				{
					String[] urlId = urlSplit[1].split( "\\." );
					if ( urlId.length == 2 && !urlId[1].trim().isEmpty() )
					{
						url = urlSplit[0] + "?id=" + urlId[1];
					}
				}
			}
		}
		else if ( url.contains( "aaai.org/" ) )
		{
			url = url.replace( "/view/", "/viewPaper/" );
		}

		// Using jsoup java html parser library
		Document document = PublicationCollectionHelper.getDocumentWithJsoup( url, 12000 );

		if ( document == null )
			return Collections.emptyMap();

		// Special case
		// usually URL is the DOI, therefore use document.baseUri() to get real URL
		if ( document.baseUri().contains( "ieeexplore.ieee.org" ) )
		{
			Element elementOfInterest = document.select( "#articleDetails" ).select( ".article" ).first();
			if ( elementOfInterest != null )
			{
				publicationDetailMaps.put( "abstract", elementOfInterest.text() );
			}
		}
		else if ( document.baseUri().contains( "igi-global.com" ) )
		{
			Element elementOfInterest = document.select( "#abstract" ).parents().first();
			if ( elementOfInterest != null )
			{
				for ( Node child : elementOfInterest.childNodes() )
					if ( child instanceof TextNode )
						publicationDetailMaps.put( "abstract", ( (TextNode) child ).text() );
			}
		}
		else if ( document.baseUri().contains( "dl.acm.org" ) )
		{
			if ( !document.text().startsWith( "Site Error" ) )
				publicationDetailMaps.put( "abstract", document.text() );
		}

		else if ( document.baseUri().contains( "scitepress.org" ) )
		{
			Element elementOfInterestAbstract = document.select( "#ContentPlaceHolder1_LinkPaperPage_LinkPaperContent_LabelAbstract" ).first();
			if ( elementOfInterestAbstract != null )
			{
				publicationDetailMaps.put( "abstract", elementOfInterestAbstract.text() );
			}

			Element elementOfInterestKeyword = document.select( "#ContentPlaceHolder1_LinkPaperPage_LinkPaperContent_LabelPublicationDetailKeywords" ).first();
			if ( elementOfInterestKeyword != null )
			{
				publicationDetailMaps.put( "keyword", elementOfInterestKeyword.text() );
			}
		}

		else if ( document.baseUri().contains( "iassistdata.org" ) )
		{
			Element elementOfInterest = document.select( ".content" ).select( "p" ).first();
			if ( elementOfInterest != null )
			{
				publicationDetailMaps.put( "abstract", elementOfInterest.text() );
			}
		}

		else if ( document.baseUri().contains( "http://arxiv.org/" ) )
		{
			Element elementOfInterest = document.select( "blockquote" ).first();
			if ( elementOfInterest != null )
			{
				publicationDetailMaps.put( "abstract", elementOfInterest.text() );
			}
		}

		// General case
		else
		{
			if ( document.body() == null )
				return Collections.emptyMap();

			Elements elements = document.body().select( "*" );

			// find either keyword or abstract header
			Element elementOfInterest = null;
			String elementOfInterestType = null;
			for ( Element element : elements )
			{
				String elementText = element.text().toLowerCase();
				if ( elementText.length() < 10 )
				{
					if ( elementText.contains( "keyword" ) )
					{
						elementOfInterest = element;
						elementOfInterestType = "keyword";
						break;
					}
					if ( elementText.contains( "abstract" ) || elementText.contains( "summary" ) )
					{
						elementOfInterest = element;
						elementOfInterestType = "abstract";
						break;
					}
				}
			}

			if ( elementOfInterest == null )
				return Collections.emptyMap();

			int numberOfCheckedSiblings = 8;
			int elementLevel = 0;
			boolean keywordFound = false;
			boolean abstractFound = false;

			// check by siblings
			for ( int i = 0; i < numberOfCheckedSiblings; i++ )
			{
				// get text
				String elementText = "";
				// change element pointer to next sibling
				if ( elementOfInterest.nextElementSibling() != null )
					elementOfInterest = elementOfInterest.nextElementSibling();
				else
				{
					// level up until next sibling not null
					while ( elementOfInterest.parent() != null )
					{
						if ( elementLevel > 0 )
						{
							for ( Node child : elementOfInterest.childNodes() )
								if ( child instanceof TextNode )
									if ( ( (TextNode) child ).text().length() > 200 )
									{
										elementText = ( (TextNode) child ).text();
//										break;
									}
						}
//						if ( !elementText.equals( "" ) )
//							break;

						if ( elementOfInterest.nextElementSibling() == null )
						{
							elementOfInterest = elementOfInterest.parent();
							elementLevel++;
						}
						else
						{
							// select next parent
							elementOfInterest = elementOfInterest.nextElementSibling();
							break;
						}
					};

					// check if there is large text on the node
					// if ( elementOfInterest.nextElementSibling() == null )
					// continue;

					// level down until at the same level before level up
					while ( elementOfInterest.childNodes() != null && elementLevel > 0 )
					{
						try
						{
							elementOfInterest = elementOfInterest.child( 0 );
						}
						catch ( Exception e )
						{
							break;
						}
						elementLevel--;
					};
				}

				// get text
				if ( elementText.equals( "" ) )
					elementText = elementOfInterest.text();

				// check for keyword
				if ( elementOfInterestType.equals( "keyword" ) && !keywordFound )
				{
					// just check any text that contain text longer than 20
					// character
					if ( elementText.length() > 8 )
					{
						if ( publicationDetailMaps.get( "keyword" ) != null )
							publicationDetailMaps.put( "keyword", publicationDetailMaps.get( "keyword" ) + ", " + elementText );
						else
							publicationDetailMaps.put( "keyword", elementText );

						if ( elementOfInterest.nextElementSibling() == null || elementText.length() > 10 )
							keywordFound = true;
					}
					else
					{
						if ( elementText.toLowerCase().contains( "abstract" ) )
							elementOfInterestType = "abstract";
						// special case http://www.computer.org/, keyword null
						else if ( elementText.toLowerCase().equals( "null" ) )
							break;
					}
				}

				// check for abstract
				else if ( elementOfInterestType.equals( "abstract" ) && !abstractFound )
				{
					// just check any text that contain text longer than 100
					// character
					if ( elementText.length() > 100 )
					{
						publicationDetailMaps.put( "abstract", elementText );
						abstractFound = true;
					}
					else
					{
						if ( elementText.toLowerCase().contains( "keyword" ) )
							elementOfInterestType = "keyword";
					}

				}
				else if ( elementOfInterestType.equals( "keyword" ) && keywordFound )
				{
					if ( elementText.length() < 20 )
						if ( elementText.toLowerCase().contains( "abstract" ) )
							elementOfInterestType = "abstract";
				}
				else if ( elementOfInterestType.equals( "abstract" ) && abstractFound )
				{
					if ( elementText.length() < 20 )
						if ( elementText.toLowerCase().contains( "keyword" ) || elementText.toLowerCase().contains( "index terms" ) )
							elementOfInterestType = "keyword";
				}

				// both keyword and abstract found
				if ( keywordFound && abstractFound )
					break;

			}

			// try to find by traversing down
			if ( publicationDetailMaps.get( "abstract" ) == null && elementOfInterest != null )
			{
				// check by traversing down
				int numberOfTraversingDown = 5;
				int maxNumberTraversingParent = 2;
				int currentNumberTraversingParent = 0;
				Node nodeOfInterest = elementOfInterest;
				// only traversing sibling and parents
				if ( elementOfInterestType.equals( "abstract" ) )
				{
					for ( int i = 0; i < numberOfTraversingDown; i++ )
					{
						String text = null;
						if ( nodeOfInterest instanceof TextNode )
						{
							text = ( (TextNode) nodeOfInterest ).text();
						}
						else
						{
							text = ( (Element) nodeOfInterest ).text();
						}

						if ( text != null && text.length() > 200 )
						{
							publicationDetailMaps.put( "abstract", text );
							break;
						}
						// check parent or sibling element
						if ( nodeOfInterest.nextSibling() != null )
						{
							nodeOfInterest = nodeOfInterest.nextSibling();
						}
						else
						{
							if ( maxNumberTraversingParent < currentNumberTraversingParent )
							{
								if ( nodeOfInterest.parent() != null )
								{
									nodeOfInterest = nodeOfInterest.parent();
									currentNumberTraversingParent++;
								}
								else
									break;
							}
							else
								break;
						}
					}
				}
			}

			// last attempt try to find by searching last abstract
			if ( publicationDetailMaps.get( "abstract" ) == null && elementOfInterest != null )
			{
				for ( Element element : elements )
				{
					String elementText = element.text().toLowerCase();
					if ( elementText.length() < 10 )
					{
						if ( elementText.contains( "abstract" ) || elementText.contains( "summary" ) )
						{
							elementOfInterest = element;
							elementOfInterestType = "abstract";
						}
					}
				}
				// check by traversing down
				int numberOfTraversingDown = 5;
				int maxNumberTraversingParent = 2;
				int currentNumberTraversingParent = 0;
				Node nodeOfInterest = elementOfInterest;
				// only traversing sibling and parents
				if ( elementOfInterestType.equals( "abstract" ) )
				{
					for ( int i = 0; i < numberOfTraversingDown; i++ )
					{
						String text = null;
						if ( nodeOfInterest instanceof TextNode )
						{
							text = ( (TextNode) nodeOfInterest ).text();
						}
						else
						{
							text = ( (Element) nodeOfInterest ).text();
						}

						if ( text != null && text.length() > 200 )
						{
							publicationDetailMaps.put( "abstract", text );
							break;
						}
						// check parent or sibling element
						if ( nodeOfInterest.nextSibling() != null )
						{
							nodeOfInterest = nodeOfInterest.nextSibling();
						}
						else
						{
							if ( maxNumberTraversingParent < currentNumberTraversingParent )
							{
								if ( nodeOfInterest.parent() != null )
								{
									nodeOfInterest = nodeOfInterest.parent();
									currentNumberTraversingParent++;
								}
								else
									break;
							}
							else
								break;
						}
					}
				}
			}
		}

		return publicationDetailMaps;
	}

	public static String getIeeePdfUrl( String url )
	{
		// Using jsoup java html parser library
		Document document = PublicationCollectionHelper.getDocumentWithJsoup( url, 10000 );

		if ( document == null )
			return null;

		Element frameElement = document.select( "frame" ).get( 1 );

		if ( frameElement == null )
			return null;

		return frameElement.absUrl( "src" );

	}
}

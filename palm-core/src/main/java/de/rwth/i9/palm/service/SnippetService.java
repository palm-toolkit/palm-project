package de.rwth.i9.palm.service;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SnippetService
{

	private final static Logger log = LoggerFactory.getLogger( SnippetService.class );

	/**
	 * Get the snippet (in html string) from specific element/node of a web page
	 * 
	 * @param url
	 *            the URL of the web page
	 * @param cssQuerySelector
	 *            the node element selector like jquery selector e.g.
	 *            #nodeElementId or .nodeElementClass
	 * @return specific html content of the node element
	 */
	public String getSnippet( final String url, final String cssQuerySelector )
	{
		// Using jsoup java html parser library
		Document document;
		try
		{
			document = Jsoup.connect( url ).get();

			Elements elementNode = document.select( cssQuerySelector );

			if ( elementNode.size() == 0 )
				log.info( "Empty element node for css selector '{}' on url '{}'", cssQuerySelector, url );

			return elementNode.html();
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			log.error( "getSnippet() cannot connect to " + url );
		}

		return null;
	}

}
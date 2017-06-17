package de.rwth.i9.palm.helper;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupParsePage
{
	private String path;
	Connection.Response response = null;

	private JsoupParsePage( String langLocale )
	{
		try
		{
			response = Jsoup.connect( path ).userAgent( "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21" ).timeout( 10000 ).execute();
		}
		catch ( IOException e )
		{
			System.out.println( "io - " + e );
		}
	}

	public int getSitemapStatus()
	{
		int statusCode = response.statusCode();
		return statusCode;
	}

	public Document getDocument() throws IOException
	{
		return response.parse();
	}
}

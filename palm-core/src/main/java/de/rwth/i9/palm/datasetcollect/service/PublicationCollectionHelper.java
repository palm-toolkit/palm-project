package de.rwth.i9.palm.datasetcollect.service;

import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicationCollectionHelper
{
	private final static Logger log = LoggerFactory.getLogger( PublicationCollectionHelper.class );
	
	public static Document getDocumentWithJsoup( String url, int timeout){
		return getDocumentWithJsoup(url, timeout, null);
	}
	
	public static Document getDocumentWithJsoup( String url, int timeout, Map<String, String> cookies){
		Document document = null;
		try
		{
			Connection jsoupConnection = 
					 Jsoup
						.connect( url )
						.userAgent( "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36" )
						.header( "Accept", "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8" )
						.timeout( timeout )
						.maxBodySize(0);
			
			if( cookies != null )
				jsoupConnection.cookies( cookies );
			
			// get the html page
			document = jsoupConnection.get();
		}
		catch ( Exception e )
		{
			log.debug( e.toString() );
			log.info( e.toString() );
			return null;
		}
		
		return document;
	}
	
	public static Connection.Response getConnectionResponseWithJsoup( String url, int timeout, Map<String, String> cookies){
		Connection.Response response = null;
		try
		{
			Connection jsoupConnection = 
					 Jsoup
						.connect( url )
						.userAgent( "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36" )
						.header( "Accept", "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8" )
						.timeout( timeout );
			
			if( cookies != null )
				jsoupConnection.cookies( cookies );
			
			// get the html page
			response = jsoupConnection.execute();
		}
		catch ( Exception e )
		{
			log.debug( e.toString() );
			log.info( e.toString() );
			return null;
		}
		
		return response;
	}
	
}

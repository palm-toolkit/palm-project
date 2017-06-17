package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

//import com.google.common.base.Stopwatch;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.CompletionStatus;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationFile;
import de.rwth.i9.palm.model.PublicationSource;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.pdfextraction.service.ItextPdfExtraction;
import de.rwth.i9.palm.pdfextraction.service.TextSection;
import de.rwth.i9.palm.utils.TextUtils;

/**
 * 
 * @author sigit
 *
 */
@Service
public class AsynchronousCollectionService
{
	private final static Logger log = LoggerFactory.getLogger( AsynchronousCollectionService.class );

	/**
	 * Asynchronously gather publication list from google scholar
	 * 
	 * @param url
	 * @param source
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<List<Map<String, String>>> getListOfPublicationsGoogleScholar( String url, Source source ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "get publication list from google scholar with url " + url + " starting" );

		List<Map<String, String>> publicationMapList = GoogleScholarPublicationCollection.getPublicationListByAuthorUrl( url, source );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "get publication list from google scholar with url " + url + " complete in " + stopwatch );

		return new AsyncResult<List<Map<String, String>>>( publicationMapList );
	}

	/**
	 * Asynchronously gather publication list from citeseerx
	 * 
	 * @param url
	 * @param source
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<List<Map<String, String>>> getListOfPublicationCiteseerX( String url, Source source ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "get publication list from citeseerX with query " + url + " starting" );

		List<Map<String, String>> publicationMapList = CiteseerXPublicationCollection.getPublicationListByAuthorUrl( url, source );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "get publication list from citeSeerX with url " + url + " complete in " + stopwatch );

		return new AsyncResult<List<Map<String, String>>>( publicationMapList );
	}

	/**
	 * Asynchronously gather publication list from DBLP
	 * 
	 * @param url
	 * @param source
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<List<Map<String, String>>> getListOfPublicationDBLP( String url, Source source ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "get publication list from DBLP with query " + url + " starting" );

		List<Map<String, String>> publicationMapList = DblpPublicationCollection.getPublicationListByAuthorUrl( url, source );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "get publication list from DBLP with url " + url + " complete in " + stopwatch );

		return new AsyncResult<List<Map<String, String>>>( publicationMapList );
	}

	/**
	 * Asynchronously gather publication detail from Microsoft Academic Search
	 * Old API
	 * 
	 * @param url
	 * @param source
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<List<Map<String, String>>> getListOfPublicationDetailMicrosoftAcademicSearch( Author author, Source source ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "get publication detail list from Microsoft Academic Search with author " + author.getName() + " starting" );

		List<Map<String, String>> publicationMapList = MicrosoftAcademicSearchPublicationCollection.getPublicationDetailList( author, source );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "get publication detail list from Microsoft Academic Search with author " + author.getName() + " complete in " + stopwatch );

		return new AsyncResult<List<Map<String, String>>>( publicationMapList );
	}

	/**
	 * Asynchronously gather publication detail from Mendeley API
	 * 
	 * @param url
	 * @param source
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<List<Map<String, String>>> getListOfPublicationDetailMendeley( Author author, Source source ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "get publication detail list from Mendeley with author " + author.getName() + " starting" );

		List<Map<String, String>> publicationMapList = MendeleyPublicationCollection.getPublicationDetailList( author, source );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "get publication detail list from Mendeley with author " + author.getName() + " complete in " + stopwatch );

		return new AsyncResult<List<Map<String, String>>>( publicationMapList );
	}

	/**
	 * Asynchronously gather publication detail from google scholar
	 * 
	 * @param publicationSource
	 * @param source
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<PublicationSource> getPublicationInformationFromGoogleScholar( PublicationSource publicationSource, Source source ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "get publication detail from google scholar with url " + publicationSource.getSourceUrl() + " starting" );

		// scrap the webpage
		Map<String, String> publicationDetailMap = GoogleScholarPublicationCollection.getPublicationDetailByPublicationUrl( publicationSource.getSourceUrl(), source );

		// assign the information gathered into publicationSource object
		// this.assignInformationFromGoogleScholar( publicationSource,
		// publicationDetailMap );

		if ( publicationDetailMap.get( "doc" ) != null )
			publicationSource.setMainSource( publicationDetailMap.get( "doc" ) );

		if ( publicationDetailMap.get( "doc_url" ) != null )
			publicationSource.setMainSourceUrl( publicationDetailMap.get( "doc_url" ) );

		if ( publicationDetailMap.get( "Authors" ) != null )
			publicationSource.setCoAuthors( publicationDetailMap.get( "Authors" ) );

		if ( publicationDetailMap.get( "Publication date" ) != null )
			publicationSource.setDate( publicationDetailMap.get( "Publication date" ) );

		if ( publicationDetailMap.get( "Journal" ) != null )
		{
			publicationSource.setPublicationType( "JOURNAL" );
			if ( publicationDetailMap.get( "Journal" ).length() < 100 )
				publicationSource.setVenue( publicationDetailMap.get( "Journal" ) );
		}

		if ( publicationDetailMap.get( "Book" ) != null )
		{
			publicationSource.setPublicationType( "BOOK" );
			if ( publicationDetailMap.get( "Book" ).length() < 100 )
				publicationSource.setVenue( publicationDetailMap.get( "Book" ) );
		}

		if ( publicationDetailMap.get( "Conference" ) != null )
		{
			publicationSource.setPublicationType( "CONFERENCE" );
			if ( publicationDetailMap.get( "Conference" ).length() < 100 )
				publicationSource.setVenue( publicationDetailMap.get( "Conference" ) );
		}

		if ( publicationDetailMap.get( "Pages" ) != null )
			publicationSource.setPages( publicationDetailMap.get( "Pages" ) );

		if ( publicationDetailMap.get( "Publisher" ) != null )
			publicationSource.addOrUpdateAdditionalInformation( "publisher", publicationDetailMap.get( "Publisher" ) );

		if ( publicationDetailMap.get( "Volume" ) != null )
			publicationSource.addOrUpdateAdditionalInformation( "volume", publicationDetailMap.get( "Volume" ) );

		if ( publicationDetailMap.get( "Issue" ) != null )
			publicationSource.addOrUpdateAdditionalInformation( "issue", publicationDetailMap.get( "Issue" ) );

		if ( publicationDetailMap.get( "Description" ) != null )
		{
			String abstractText = publicationDetailMap.get( "Description" );
			if ( abstractText.length() > 200 )
			{
				if ( abstractText.substring( 0, 8 ).equalsIgnoreCase( "abstract" ) )
					abstractText = abstractText.substring( 9 );
				if ( abstractText.endsWith( "..." ) )
					abstractText = abstractText.substring( 0, abstractText.length() - 4 );
				publicationSource.setAbstractText( abstractText );
			}
		}

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "get publication detail from google scholar with url " + publicationSource.getSourceUrl() + " complete in " + stopwatch );

		return new AsyncResult<PublicationSource>( publicationSource );
	}

	/**
	 * Asynchronously gather publication detail from citeseerx
	 * 
	 * @param publicationSource
	 * @param source
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<PublicationSource> getPublicationInformationFromCiteseerX( PublicationSource publicationSource, Source source ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "get publication detail from citeseerX with url " + publicationSource.getSourceUrl() + " starting" );

		// scrap the webpage
		Map<String, String> publicationDetailMap = CiteseerXPublicationCollection.getPublicationDetailByPublicationUrl( publicationSource.getSourceUrl(), source );

		// assign the information gathered into publicationSource object
		// this.assignInformationFromCiteseerx( publicationSource,
		// publicationDetailMap );

		if ( publicationDetailMap.get( "doc" ) != null )
			publicationSource.setMainSource( publicationDetailMap.get( "doc" ) );

		if ( publicationDetailMap.get( "doc_url" ) != null )
			publicationSource.setMainSourceUrl( publicationDetailMap.get( "doc_url" ) );

		if ( publicationDetailMap.get( "coauthor" ) != null )
			publicationSource.setCoAuthors( publicationDetailMap.get( "coauthor" ) );

		if ( publicationDetailMap.get( "eventName" ) != null )
			publicationSource.setVenue( TextUtils.cutTextToLength( publicationDetailMap.get( "eventName" ), 200 ) );

		if ( publicationDetailMap.get( "abstract" ) != null )
		{

			String abstractText = publicationDetailMap.get( "abstract" );
			if ( abstractText.length() > 200 )
			{
				if ( abstractText.substring( 0, 8 ).equalsIgnoreCase( "abstract" ) )
					abstractText = abstractText.substring( 9 );
				publicationSource.setAbstractText( abstractText );
			}
		}

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "get publication detail from citeSeerX with url " + publicationSource.getSourceUrl() + " complete in " + stopwatch );

		return new AsyncResult<PublicationSource>( publicationSource );
	}

	/**
	 * Asynchronously gather publication information (keywords and abstract)
	 * from HtmlPage
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<PublicationSource> getPublicationInfromationFromHtml( Publication publication, PublicationSource publicationSource, PublicationFile publicationFile ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "start : get publication information from Htmlpage " + publicationSource.getSourceUrl() + " starting" );

		Map<String, String> publicationInformationMap = HtmlPublicationCollection.getPublicationInformationFromHtmlPage( publicationSource.getSourceUrl() );

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "done :get publication information from Htmlpage " + publicationSource.getSourceUrl() + " complete in " + stopwatch );

		if ( publicationInformationMap != null && !publicationInformationMap.isEmpty() )
		{
			if ( publicationInformationMap.get( "abstract" ) != null || publicationInformationMap.get( "keyword" ) != null )
			{
				publicationSource.setPublication( publication );
				publication.addPublicationSource( publicationSource );
			}
			if ( publicationInformationMap.get( "abstract" ) != null )
			{
				publicationSource.setAbstractText( publicationInformationMap.get( "abstract" ) );
				publication.setAbstractText( publicationSource.getAbstractText() );
				publication.setAbstractStatus( CompletionStatus.COMPLETE );
				publication.setContentUpdated( true );
			}
			if ( publicationInformationMap.get( "keyword" ) != null )
			{
				publicationSource.setKeyword( publicationInformationMap.get( "keyword" ) );
				publication.setKeywordText( publicationSource.getKeyword() );
				publication.setKeywordStatus( CompletionStatus.COMPLETE );
			}

			publicationFile.setReadable( true );
		}

		return new AsyncResult<PublicationSource>( publicationSource );
	}

	/**
	 * Asynchronously gather publication detail from citeseerx
	 * 
	 * @param publicationSource
	 * @return
	 * @throws IOException
	 */
	@Async
	public Future<PublicationSource> getPublicationInformationFromPdf( Publication publication, PublicationSource publicationSource, PublicationFile publicationFile ) throws IOException
	{
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "start : get publication information from Pdf " + publicationSource.getSourceUrl() + " starting" );

		List<TextSection> textSections = null;
		
		if ( publicationSource.getSourceUrl().contains( "ieeexplore.ieee.org" ) )
			publicationSource.setSourceUrl( HtmlPublicationCollection.getIeeePdfUrl( publicationSource.getSourceUrl() ) );

		try
		{
			// extract pdf until second page
			textSections = ItextPdfExtraction.extractPdf( publicationSource.getSourceUrl(), 2 );
		}
		catch ( Exception e )
		{
			log.info( "error e " + e.getMessage() );
		}
		
		Map<String, String> publicationInformationMap = new LinkedHashMap<String, String>();

		// only take information until abstract and keyword
		if ( textSections != null && !textSections.isEmpty() )
		{
			for ( TextSection textSection : textSections )
			{
				if ( textSection.getContent() == null || textSection.getName() == null )
					continue;

				if ( publicationInformationMap.get( textSection.getName() ) != null )
				{
					String concatenationSign = " ";
					if ( textSection.getName().equals( "author" ) )
						concatenationSign = "_#_";
					if ( textSection.getName().equals( "keyword" ) )
						concatenationSign = ", ";
					publicationInformationMap.put( textSection.getName(), publicationInformationMap.get( textSection.getName() ) + concatenationSign + textSection.getContent() );
				}
				else
				{
					if ( textSection.getName().equals( "title" ) )
					{
						publicationInformationMap.put( "title", textSection.getContent() );
					}
					else if ( textSection.getName().equals( "author" ) )
					{
						publicationInformationMap.put( "author", textSection.getContent() );
					}
					else if ( textSection.getName().equals( "abstract-header" ) )
					{
						if ( textSection.getContent().replaceAll( "[^a-zA-Z]", "" ).length() > 20 )
							publicationInformationMap.put( "abstract", textSection.getContent() );
					}
					else if ( textSection.getName().equals( "abstract" ) )
					{
						publicationInformationMap.put( "abstract", textSection.getContent() );
					}
					else if ( textSection.getName().equals( "keyword-header" ) )
					{
						if ( textSection.getContent().replaceAll( "[^a-zA-Z]", "" ).length() > 20 )
							publicationInformationMap.put( "keyword", textSection.getContent() );
					}
					else if ( textSection.getName().equals( "keyword" ) )
					{
						if ( textSection.getContent().replaceAll( "[^a-zA-Z]", "" ).length() > 20 )
							publicationInformationMap.put( "keyword", textSection.getContent() );
					}
					else if ( textSection.getName().equals( "content-header" ) || textSection.getName().equals( "content" ) )
					{
						break;
					}
				}
			}
		}

		if ( publicationInformationMap != null && !publicationInformationMap.isEmpty() )
		{
			if ( publicationInformationMap.get( "title" ) != null )
				publicationSource.setTitle( publicationInformationMap.get( "title" ) );
			if ( publicationInformationMap.get( "author" ) != null )
				publicationSource.setCoAuthors( publicationInformationMap.get( "author" ) );
			if ( publicationInformationMap.get( "abstract" ) != null )
			{
				publicationSource.setAbstractText( publicationInformationMap.get( "abstract" ) );
				publication.setAbstractText( publicationSource.getAbstractText() );
				publication.setAbstractStatus( CompletionStatus.PARTIALLY_COMPLETE );
				publication.setContentUpdated( true );
			}
			if ( publicationInformationMap.get( "keyword" ) != null )
			{
				publicationSource.setKeyword( publicationInformationMap.get( "keyword" ) );
				publication.setKeywordText( publicationSource.getKeyword() );
				publication.setKeywordStatus( CompletionStatus.PARTIALLY_COMPLETE );
			}

			publicationSource.setPublication( publication );
			publication.addPublicationSource( publicationSource );

			publicationFile.setReadable( true );
		}

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );
//		log.info( "done : get publication information from pdf " + publicationSource.getSourceUrl() + " complete in " + stopwatch );

		return new AsyncResult<PublicationSource>( publicationSource );
	}
}

package de.rwth.i9.palm.pdfextraction.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Service
public class PdfExtractionService
{
	private final static Logger log = LoggerFactory.getLogger( PdfExtractionService.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private AsynchronousPdfExtractionService asynchronousPdfExtractionService;

	@Autowired
	private PalmAnalytics palmAnalitics;

	/**
	 * Batch extraction of publications from pdf files on specific author
	 * 
	 * @param author
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void extractPublicationPdfFromSpecificAuthor( Author author ) throws IOException, InterruptedException, ExecutionException
	{
		// get publication that has pdf source
		List<Publication> publications = new ArrayList<Publication>();
		for ( Publication publication : author.getPublications() )
		{
			if ( publication.getPublicationFiles() != null )
				publications.add( publication );
		}

		Map<Publication, Future<List<TextSection>>> extractedPfdFutureMap = new LinkedHashMap<Publication, Future<List<TextSection>>>();

		for ( Publication publication : publications )
		{
			extractedPfdFutureMap.put( publication, this.asynchronousPdfExtractionService.extractPublicationPdfIntoTextSections( publication ) );
		}

		// check whether thread worker is done
		// Wait until they are all done
		// give 100-millisecond pause ( for downloading the pdf)
//		Thread.sleep( 100 );
//		boolean processIsDone = true;
//		do
//		{
//			processIsDone = true;
//			for ( Entry<Publication, Future<List<TextSection>>> futureMap : extractedPfdFutureMap.entrySet() )
//			{
//				if ( !futureMap.getValue().isDone() )
//				{
//					processIsDone = false;
//					break;
//				}
//			}
//			// 10-millisecond pause between each check
//			Thread.sleep( 10 );
//		} while ( !processIsDone );
		
		// wait until everything complete
		for ( Entry<Publication, Future<List<TextSection>>> futureMap : extractedPfdFutureMap.entrySet() )
			futureMap.getValue().get();

		// process the extracted text sections further
		this.processExtractedPublication( extractedPfdFutureMap, null );
	}

	public void extractPdfFromSpecificPublication( Publication publication, Map<String, Object> responseMap ) throws IOException, InterruptedException, ExecutionException
	{

		Map<Publication, Future<List<TextSection>>> extractedPfdFutureMap = new LinkedHashMap<Publication, Future<List<TextSection>>>();
		extractedPfdFutureMap.put( publication, this.asynchronousPdfExtractionService.extractPublicationPdfIntoTextSections( publication ) );

		// check whether thread worker is done
		// Wait until they are all done
		// give 100-millisecond pause ( for downloading the pdf)
//		Thread.sleep( 100 );
//		boolean processIsDone = true;
//		do
//		{
//			processIsDone = true;
//			for ( Entry<Publication, Future<List<TextSection>>> futureMap : extractedPfdFutureMap.entrySet() )
//			{
//				if ( !futureMap.getValue().isDone() )
//				{
//					processIsDone = false;
//					break;
//				}
//			}
//			// 10-millisecond pause between each check
//			Thread.sleep( 10 );
//		} while ( !processIsDone );
		
		// wait till complete
		for ( Entry<Publication, Future<List<TextSection>>> futureMap : extractedPfdFutureMap.entrySet() )
			futureMap.getValue().get();

		// process the extracted text sections further
		this.processExtractedPublication( extractedPfdFutureMap, responseMap );

	}

	/**
	 * Extract pdf from specific url
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> extractPdfFromSpecificUrl( String url ) throws IOException, InterruptedException, ExecutionException
	{
		return extractPdfFromSpecificUrl( url, 0 );
	}

	/**
	 * Extract pdf from specific url limited to pages
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> extractPdfFromSpecificUrl( String url, int untilPage ) throws IOException, InterruptedException, ExecutionException
	{
		List<Future<List<TextSection>>> extractedPfdFutureList = new ArrayList<Future<List<TextSection>>>();
		extractedPfdFutureList.add( this.asynchronousPdfExtractionService.extractPublicationPdfIntoTextSections( url, untilPage ) );

		// wait till complete
		for ( Future<List<TextSection>> futureMap : extractedPfdFutureList )
			futureMap.get();

		return (Map<String, Object>) this.processExtractedPdf( extractedPfdFutureList ).get( 0 );
	}

	/**
	 * Extract PDF from inputStream
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Map<String, Object> extractPdfFromInputStream( InputStream pdfInputStream ) throws IOException, InterruptedException, ExecutionException
	{
		return this.getMappedPdfSection( ItextPdfExtraction.extractPdfFromInputStream( pdfInputStream, 0 ) );
	}

	/**
	 * Here the extracted text section will be merged into publication
	 * 
	 * @param extractedPfdFutureMap
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void processExtractedPublication( Map<Publication, Future<List<TextSection>>> extractedPfdFutureMap, Map<String, Object> responseMap ) throws InterruptedException, ExecutionException
	{

		for ( Entry<Publication, Future<List<TextSection>>> extractedPfdFuture : extractedPfdFutureMap.entrySet() )
		{
			Publication publication = extractedPfdFuture.getKey();
			List<TextSection> textSections = extractedPfdFuture.getValue().get();
			StringBuilder publicationAbstract = new StringBuilder();
			StringBuilder publicationContent = new StringBuilder();
			StringBuilder publicationKeyword = new StringBuilder();
			StringBuilder contentSection = new StringBuilder();
			StringBuilder contentSectionWithName = new StringBuilder();

			for( TextSection textSection : textSections ){
				if ( textSection.getName() != null )
				{
					if ( textSection.getName().equals( "content" ) )
					{
						publicationContent.append( textSection.getContent() + "\n" );
						contentSectionWithName.append( textSection.getContent() + "\n" );
					}
					else if ( textSection.getName().equals( "content-header" ) )
					{
						publicationContent.append( "\t\n" + textSection.getContent() + "\n\t" );
						contentSection.setLength( 0 );
						contentSectionWithName.setLength( 0 );
						contentSectionWithName.append( "\t\n" + textSection.getContent() + "\n\t" );
					}
					else if ( textSection.getName().equals( "content-cont" ) )
					{
						publicationContent.setLength( publicationContent.length() - 1 );

						if ( publicationContent.toString().endsWith( "-" ) )
							publicationContent.setLength( publicationContent.length() - 1 );

						publicationContent.append( textSection.getContent() + "\n" );
						contentSectionWithName.append( textSection.getContent() + "\n" );
					}
					else if ( textSection.getName().equals( "keyword" ) )
					{
						publicationKeyword.append( textSection.getContent() + "\n" );
					}
					else if ( textSection.getName().equals( "abstract" ) )
					{
						publicationAbstract.append( textSection.getContent() + "\n" );
					}
					else if ( textSection.getName().equals( "abstract-header" ) )
					{
						if ( textSection.getContent().length() > 100 )
							publicationAbstract.append( textSection.getContent() + "\n" );
					}
					else if ( textSection.getName().equals( "author" ) )
					{
						// TODO process author
					}
					else if ( textSection.getName().equals( "title" ) )
					{
						if ( palmAnalitics.getTextCompare().getDistanceByLuceneLevenshteinDistance( textSection.getContent().toLowerCase(), publication.getTitle().toLowerCase() ) > 0.8f )
						{
							publication.setTitle( textSection.getContent() );
							responseMap.put( "status", "Ok" );
						}
						else
						{
							responseMap.put( "status", "Error - publication not found" );
							break;
						}
					}
				}
				else
				{
					if ( textSection.getContent().length() > 5 )
						contentSection.append( textSection.getContent() );
				}
			}
			publicationContent.setLength( publicationContent.length() - contentSectionWithName.length() );
			publication.setContentText( Jsoup.parse( publicationContent.toString() ).text() );
			publication.setAbstractText( Jsoup.parse( publicationAbstract.toString() ).text() );
			publication.setReferenceText( Jsoup.parse( contentSection.toString() ).text() );
			if ( publicationKeyword.length() > 0 )
				publication.setKeywordText( publicationKeyword.toString() );

		}
	}

	/**
	 * Here the extracted text section will be merged into publication
	 * 
	 * @param extractedPfdFutureMap
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public List<Object> processExtractedPdf( List<Future<List<TextSection>>> extractedPfdFutureList ) throws InterruptedException, ExecutionException
	{
		List<Object> extractedPdfList = new ArrayList<Object>();
		for ( Future<List<TextSection>> extractedPfdFuture : extractedPfdFutureList )
			extractedPdfList.add( getMappedPdfSection( extractedPfdFuture.get() ) );
		return extractedPdfList;
	}

	/**
	 * Extract Pdf sections into Map
	 * 
	 * @param textSections
	 * @return
	 */
	private Map<String, Object> getMappedPdfSection( List<TextSection> textSections )
	{
		Map<String, Object> extractedPdfMap = new LinkedHashMap<String, Object>();

		StringBuilder publicationTitle = new StringBuilder();
		StringBuilder publicationAuthor = new StringBuilder();
		StringBuilder publicationAbstract = new StringBuilder();
		StringBuilder publicationContent = new StringBuilder();
		StringBuilder publicationKeyword = new StringBuilder();
		StringBuilder contentSection = new StringBuilder();
		StringBuilder contentSectionWithName = new StringBuilder();

		if ( textSections == null || textSections.isEmpty() )
			return Collections.emptyMap();

		for ( TextSection textSection : textSections )
		{
			if ( textSection.getName() != null )
			{
				if ( textSection.getName().equals( "content" ) )
				{
					publicationContent.append( textSection.getContent() + "\n" );
					contentSectionWithName.append( textSection.getContent() + "\n" );
				}
				else if ( textSection.getName().equals( "content-header" ) )
				{
					publicationContent.append( "\t\n" + textSection.getContent() + "\n\t" );
					contentSection.setLength( 0 );
					contentSectionWithName.setLength( 0 );
					contentSectionWithName.append( "\t\n" + textSection.getContent() + "\n\t" );
				}
				else if ( textSection.getName().equals( "content-cont" ) )
				{
					publicationContent.setLength( publicationContent.length() - 1 );

					if ( publicationContent.toString().endsWith( "-" ) )
						publicationContent.setLength( publicationContent.length() - 1 );

					publicationContent.append( textSection.getContent() + "\n" );
					contentSectionWithName.append( textSection.getContent() + "\n" );
				}
				else if ( textSection.getName().equals( "keyword" ) )
				{
					publicationKeyword.append( textSection.getContent() + "\n" );
				}
				else if ( textSection.getName().equals( "abstract" ) )
				{
					publicationAbstract.append( textSection.getContent() + "\n" );
				}
				else if ( textSection.getName().equals( "abstract-header" ) )
				{
					if ( textSection.getContent().length() > 100 )
						publicationAbstract.append( textSection.getContent() + "\n" );
				}
				else if ( textSection.getName().equals( "author" ) )
				{
					publicationAuthor.append( textSection.getContent() );
				}
				else if ( textSection.getName().equals( "title" ) )
				{
					publicationTitle.append( textSection.getContent() );
				}
			}
			else
			{
				if ( textSection.getContent().length() > 5 )
					contentSection.append( textSection.getContent() );
			}
		}
		if ( publicationContent.length() - contentSectionWithName.length() > 0 )
			publicationContent.setLength( publicationContent.length() - contentSectionWithName.length() );

		extractedPdfMap.put( "title", publicationTitle.toString() );
		extractedPdfMap.put( "author", Jsoup.parse( publicationAuthor.toString() ).text() );
		extractedPdfMap.put( "abstract", Jsoup.parse( publicationAbstract.toString() ).text() );
		if ( publicationKeyword.length() > 0 )
			extractedPdfMap.put( "keyword", publicationKeyword.toString() );
		try
		{
			extractedPdfMap.put( "content", publicationContent.toString() );
		}
		catch ( Exception e )
		{
		}
		extractedPdfMap.put( "reference", Jsoup.parse( contentSection.toString() ).text() );

		return extractedPdfMap;

	}

	private void processExtractedPublicationAuthor( Publication publication, List<TextSection> textAuthorSections )
	{

	}

	private void processExtractedPublicationReferences( Publication publication, List<TextSection> textReferenceSections )
	{

	}
}

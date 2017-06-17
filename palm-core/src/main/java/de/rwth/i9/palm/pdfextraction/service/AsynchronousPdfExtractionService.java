package de.rwth.i9.palm.pdfextraction.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

//import com.google.common.base.Stopwatch;

import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationFile;

@Service
public class AsynchronousPdfExtractionService
{
	private final static Logger log = LoggerFactory.getLogger( AsynchronousPdfExtractionService.class );

	@Async
	public Future<List<TextSection>> extractPublicationPdfIntoTextSections( Publication publication ) throws IOException
	{

//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "Download and Extract pdf " + publication.getTitle() + " starting" );

		List<TextSection> textSections = null;

		// TODO: debugging this part
		for ( PublicationFile publicationFile : publication.getPublicationFiles() )
		{
			try
			{
				textSections = ItextPdfExtraction.extractPdf( publicationFile.getUrl() );
				if ( textSections != null || !textSections.isEmpty() )
					break;
			}
			catch ( Exception e )
			{
				// TODO: handle exception
			}
		}

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );

//		log.info( "Download and Extract pdf " + publication.getTitle() + " complete in " + stopwatch );

		return new AsyncResult<List<TextSection>>( textSections );
	}

	@Async
	public Future<List<TextSection>> extractPublicationPdfIntoTextSections( String url, int untilPage ) throws IOException
	{

//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info( "Download and Extract pdf " + url + " starting" );

		List<TextSection> textSections = null;

		try
		{
			textSections = ItextPdfExtraction.extractPdf( url, untilPage );
		}
		catch ( Exception e )
		{
			// TODO: handle exception
		}

//		stopwatch.elapsed( TimeUnit.MILLISECONDS );

//		log.info( "Download and Extract pdf " + url + " complete in " + stopwatch );

		return new AsyncResult<List<TextSection>>( textSections );
	}
}

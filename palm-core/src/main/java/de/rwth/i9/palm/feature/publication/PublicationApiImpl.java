package de.rwth.i9.palm.feature.publication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.datasetcollect.service.HtmlPublicationCollection;
import de.rwth.i9.palm.helper.comparator.PublicationFileBySourceNameNaturalOrderComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.FileType;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationFile;
import de.rwth.i9.palm.model.PublicationType;
import de.rwth.i9.palm.pdfextraction.service.PdfExtractionService;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;

@Component
public class PublicationApiImpl implements PublicationApi
{
	@Autowired
	private PdfExtractionService pdfExtractionService;

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private ApplicationService applicationService;

	@Override
	public Map<String, Object> extractPfdFile( String url ) throws IOException, InterruptedException, ExecutionException
	{
		return pdfExtractionService.extractPdfFromSpecificUrl( url );
	}

	@Override
	public Map<String, String> extractHtmlFile( String url ) throws IOException
	{
		return HtmlPublicationCollection.getPublicationInformationFromHtmlPage( url );
	}

	@Override
	public Map<String, Object> extractPublicationFromPdfHtml( String id, String pid ) throws IOException, InterruptedException, ExecutionException
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		if ( id == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "id missing" );
			return responseMap;
		}

		// get publication
		Publication publication = persistenceStrategy.getPublicationDAO().getById( id );
		if ( publication == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "publication not found" );
			return responseMap;
		}
		if ( publication.getPublicationFiles() == null || publication.getPublicationFiles().isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "publication contains no source files" );
			return responseMap;
		}

		List<Object> publicationFileList = new ArrayList<Object>();
		List<PublicationFile> pubFiles = new ArrayList<PublicationFile>();
		pubFiles.addAll( publication.getPublicationFiles() );
		// sort
		Collections.sort( pubFiles, new PublicationFileBySourceNameNaturalOrderComparator() );

		for ( PublicationFile pubFile : pubFiles )
		{
			Map<String, Object> publicationFileMap = new LinkedHashMap<String, Object>();
			publicationFileMap.put( "type", pubFile.getFileType().toString() );
			publicationFileMap.put( "source", pubFile.getSourceType().toString().toLowerCase() );
			publicationFileMap.put( "label", pubFile.getSource() );
			publicationFileMap.put( "url", pubFile.getUrl() );

			if ( pubFile.getFileType().equals( FileType.HTML ) )
			{
				// process log
				applicationService.putProcessLog( pid, "Scraping Web Page " + pubFile.getUrl() + " <br>", "append" );
				publicationFileMap.put( "result", HtmlPublicationCollection.getPublicationInformationFromHtmlPage( pubFile.getUrl() ) );
				// process log
				applicationService.putProcessLog( pid, "Done Scraping Web Page " + pubFile.getUrl() + " <br>", "append" );
			}
			if ( pubFile.getFileType().equals( FileType.PDF ) )
			{
				// process log
				applicationService.putProcessLog( pid, "Extract PDF " + pubFile.getUrl() + " <br>", "append" );
				// extract first page
				publicationFileMap.put( "result", pdfExtractionService.extractPdfFromSpecificUrl( pubFile.getUrl(), 3 ) );
				// process log
				applicationService.putProcessLog( pid, "Done extract PDF " + pubFile.getUrl() + " <br>", "append" );
			}
			publicationFileList.add( publicationFileMap );
		}
		responseMap.put( "status", "ok" );
		responseMap.put( "files", publicationFileList );

		return responseMap;
	}

	@Override
	public Map<String, Object> getPublicationBibTex( String id, String retrieve )
	{
		Publication publication = persistenceStrategy.getPublicationDAO().getById( id );

		// JSON respnse
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		if ( publication == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "Publication with id " + id + " not found" );
			return responseMap;
		}

		retrieve = retrieve.toLowerCase();
		boolean isGenerateBibtex = false;
		boolean isGenerateMla = false;
		boolean isGenerateApa = false;
		boolean isGenerateChicago = false;
		if ( retrieve.equals( "all" ) )
		{
			isGenerateBibtex = true;
			isGenerateMla = true;
			isGenerateApa = true;
			isGenerateChicago = true;
		}
		else
		{
			if ( retrieve.equals( "bibtex" ) )
				isGenerateBibtex = true;
			if ( retrieve.equals( "mla" ) )
				isGenerateMla = true;
			if ( retrieve.equals( "apa" ) )
				isGenerateApa = true;
			if ( retrieve.equals( "chicago" ) )
				isGenerateChicago = true;
		}

		Map<String, String> citeMap = new HashMap<String, String>();

		generateCiteText( publication, citeMap );

		if ( isGenerateBibtex )
			responseMap.put( "BibTeX", citeMap.get( "bibtex" ) );
		if ( isGenerateMla )
			responseMap.put( "MLA", citeMap.get( "mla" ) );
		if ( isGenerateApa )
			responseMap.put( "APA", citeMap.get( "apa" ) );
		if ( isGenerateChicago )
			responseMap.put( "Chicago", citeMap.get( "chicago" ) );
		return responseMap;
	}

	private void generateCiteText( Publication publication, Map<String, String> citeMap )
	{
		StringBuilder citeBibtex = new StringBuilder();
		StringBuilder citeMla = new StringBuilder();
		StringBuilder citeApa = new StringBuilder();
		StringBuilder citeChicago = new StringBuilder();
		// publication type
		if ( publication.getPublicationType().equals( PublicationType.CONFERENCE ) || publication.getPublicationType().equals( PublicationType.WORKSHOP ) )
		{
			citeBibtex.append( "@inproceedings{" + publication.getId() + ",\n" );
		}
		else if ( publication.getPublicationType().equals( PublicationType.BOOK ) )
		{
			citeBibtex.append( "@book{" + publication.getId() + ",\n" );
		}
		else
		{
			citeBibtex.append( "@article{" + publication.getId() + ",\n" );
		}

		// author
		citeBibtex.append( "author = {" );
		int index = 0;
		boolean mlaAuthorComplete = false;
		for ( Author author : publication.getAuthors() )
		{
			if ( index == 0 )
			{
				citeBibtex.append( WordUtils.capitalize( author.getName() ) );
				citeMla.append( WordUtils.capitalize( author.getLastName() ) );
				citeApa.append( WordUtils.capitalize( author.getLastName() ) );
				citeChicago.append( WordUtils.capitalize( author.getLastName() ) );
				if ( author.getFirstName() != null )
				{
					citeMla.append( ", " + WordUtils.capitalize( author.getFirstName() ) );
					citeApa.append( ", " + WordUtils.capitalize( author.getFirstName().substring( 0, 1 ) ) + "." );
					citeChicago.append( ", " + WordUtils.capitalize( author.getFirstName() ) );
				}
			}
			else
			{
				citeBibtex.append( ", " + WordUtils.capitalize( author.getName() ) );
				citeApa.append(", " +  WordUtils.capitalize( author.getLastName() ) );
				citeChicago.append( ", " + WordUtils.capitalize( author.getName() ) );
				if ( !mlaAuthorComplete )
				{
					citeMla.append( ", et al. " );
					mlaAuthorComplete = true;
				}

				if ( author.getFirstName() != null )
				{
					citeApa.append( ", " + WordUtils.capitalize( author.getFirstName().substring( 0, 1 ) ) + "." );
				}
			}
			index++;
		}
		citeBibtex.append( "},\n" );
		citeApa.append(" . ");
		citeChicago.append( ". " );
		
		// year
		if( publication.getYear() != null )
			citeApa.append( "(" + publication.getYear() + "). " );
		
		// title
		citeBibtex.append( "title = {" + publication.getTitle() + "},\n" );
		citeMla.append( "\"" + publication.getTitle() + "\". " );
		citeApa.append( publication.getTitle() + ". " );
		citeChicago.append( "\"" + publication.getTitle() + "\". " );

		// event
		String eventString = "";
		if ( publication.getEvent() != null )
		{
			eventString = publication.getEvent().getEventGroup().getName();
			if ( !eventString.equals( publication.getEvent().getEventGroup().getNotation() ) )
			{
				eventString += " (" + publication.getEvent().getEventGroup().getNotation() + ") ";
			}
		}
		else
		{
			eventString = publication.getAdditionalInformationByKey( "venue" ).toString();
		}

		String eventYear = "";
		if ( publication.getYear() != null )
			eventYear = publication.getYear();

		if ( eventString != null && !eventString.isEmpty() )
		{
			if ( publication.getPublicationType().equals( PublicationType.CONFERENCE ) )
			{
				citeBibtex.append( "conference = {" + eventString + "},\n" );
			}
			else if ( publication.getPublicationType().equals( PublicationType.WORKSHOP ) )
			{
				citeBibtex.append( "workshop = {" + eventString + "},\n" );
			}
			else if ( publication.getPublicationType().equals( PublicationType.BOOK ) )
			{
			}
			else
			{
				citeBibtex.append( "journal = {" + eventString + "},\n" );
			}

			citeMla.append( eventString + " " );
			citeApa.append( eventString + ". " );
			citeChicago.append( eventString + " " );
		}

		Object volumeObj = publication.getAdditionalInformationByKey( "volume" );
		if ( volumeObj != null )
		{
			String volume = volumeObj.toString();
			volume = volume.replace( "\"", "" );
			if ( volume != null && !volume.isEmpty() )
			{
				citeBibtex.append( "volume = {" + volume + "},\n" );
				citeMla.append( volume + " " );
				citeApa.append( volume + " " );
				citeChicago.append( volume + " " );
			}
		}
		if ( eventYear != null )
		{
			citeBibtex.append( "year = {" + eventYear + "},\n" );
			citeMla.append( "(" + eventYear + ")" );
			citeChicago.append( "(" + eventYear + ")" );
		}
		if ( publication.getStartPage() > 0 )
		{
			String pages = Integer.toString( publication.getStartPage() );
			if ( publication.getEndPage() > 0 )
				pages += " - " + publication.getEndPage();

			citeBibtex.append( "pages = {" + pages + "},\n" );
			citeMla.append( ": " + pages );
			citeApa.append( ", " + pages );
			citeChicago.append( ": " + pages );

		}

		citeBibtex.append( "}" );
		citeMla.append( "." );
		citeApa.append( "." );
		citeChicago.append( "." );
		// at the end put to map
		citeMap.put( "bibtex", citeBibtex.toString() );
		citeMap.put( "mla", citeMla.toString() );
		citeMap.put( "apa", citeApa.toString() );
		citeMap.put( "chicago", citeChicago.toString() );
	}

}

package de.rwth.i9.palm.feature.publication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationFile;
import de.rwth.i9.palm.model.PublicationSource;
import de.rwth.i9.palm.model.SourceType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class PublicationBasicStatisticImpl implements PublicationBasicStatistic
{

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Override
	public Map<String, Object> getPublicationBasicStatisticById( String publicationId )
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// get author
		Publication publication = persistenceStrategy.getPublicationDAO().getById( publicationId );
		if ( publication == null )
		{
			responseMap.put( "status", "Error - publication not found" );
			return responseMap;
		}

		responseMap.put( "status", "ok" );

		// put publication detail
		Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();

		publicationMap.put( "id", publication.getId() );
		publicationMap.put( "title", publication.getTitle() );

		List<Map<String, Object>> coathorList = new ArrayList<Map<String, Object>>();
		for ( Author author : publication.getCoAuthors() )
		{
			Map<String, Object> authorMap = new LinkedHashMap<String, Object>();
			authorMap.put( "id", author.getId() );
			authorMap.put( "name", WordUtils.capitalize( author.getName() ) );
			if ( author.getInstitution() != null )
				authorMap.put( "aff", author.getInstitution().getName() );
			if ( author.getPhotoUrl() != null )
				authorMap.put( "photo", author.getPhotoUrl() );

			authorMap.put( "isAdded", author.isAdded() );

			coathorList.add( authorMap );
		}
		publicationMap.put( "authors", coathorList );

		if ( publication.getPublicationDate() != null )
		{
			SimpleDateFormat sdf = new SimpleDateFormat( publication.getPublicationDateFormat() );
			publicationMap.put( "date", sdf.format( publication.getPublicationDate() ) );
		}

		if ( publication.getLanguage() != null )
			publicationMap.put( "language", publication.getLanguage() );

		if ( publication.getCitedBy() != 0 )
		{
			publicationMap.put( "cited", publication.getCitedBy() );
			if ( publication.getCitedByUrl() != null )
				publicationMap.put( "citedUrl", publication.getCitedByUrl() );
		}

		if ( publication.getPublicationType() != null )
		{
			String publicationType = publication.getPublicationType().toString();
			publicationType = publicationType.substring( 0, 1 ).toUpperCase() + publicationType.toLowerCase().substring( 1 );
			publicationMap.put( "type", publicationType );
		}

		if ( publication.getEvent() != null )
		{

			System.out.println( "EVENT NOT NULL: " + publication.getTitle() );
			Map<String, Object> eventMap = new LinkedHashMap<String, Object>();
			eventMap.put( "id", publication.getEvent().getId() );
			eventMap.put( "name", publication.getEvent().getEventGroup().getName() );
			if ( publication.getEvent().getEventGroup().getNotation() != null )
				if ( !publication.getEvent().getEventGroup().getNotation().equals( publication.getEvent().getEventGroup().getName() ) )
					eventMap.put( "abbr", publication.getEvent().getEventGroup().getNotation() );
			eventMap.put( "isAdded", publication.getEvent().isAdded() );
			if ( publication.getEvent().getEventGroup() != null )
				eventMap.put( "isGroupAdded", publication.getEvent().getEventGroup().isAdded() );
			publicationMap.put( "event", eventMap );
		}

		if ( publication.getAdditionalInformation() != null )
			publicationMap.putAll( publication.getAdditionalInformationAsMap() );

		if ( publication.getStartPage() > 0 )
			publicationMap.put( "pages", publication.getStartPage() + " - " + publication.getEndPage() );

		List<Object> sources = new ArrayList<Object>();
		for ( PublicationSource pubSource : publication.getPublicationSources() )
		{
			if ( pubSource.getSourceType().equals( SourceType.GOOGLESCHOLAR ) || pubSource.getSourceType().equals( SourceType.CITESEERX ) )
			{
				Map<String, Object> sourceMap = new LinkedHashMap<String, Object>();
				String label = "Google Scholar";
				if ( pubSource.getSourceType().equals( SourceType.CITESEERX ) )
					label = "CiteseerX";
				sourceMap.put( "source", label );
				sourceMap.put( "url", pubSource.getSourceUrl() );
				sources.add( sourceMap );
			}
		}
		if ( !sources.isEmpty() )
			publicationMap.put( "sources", sources );

		List<Object> files = new ArrayList<Object>();
		if ( publication.getPublicationFiles() != null )
		{
			for ( PublicationFile pubFile : publication.getPublicationFiles() )
			{
				Map<String, Object> fileMap = new LinkedHashMap<String, Object>();
				fileMap.put( "type", pubFile.getFileType().toString() );
				fileMap.put( "source", pubFile.getSourceType().toString().toLowerCase() );
				if ( pubFile.getSource() != null && !pubFile.getSource().equals( "" ) )
					fileMap.put( "label", pubFile.getSource() );
				fileMap.put( "url", pubFile.getUrl() );

				files.add( fileMap );
			}
		}

		if ( !files.isEmpty() )
			publicationMap.put( "files", files );

		responseMap.put( "publication", publicationMap );

		return responseMap;
	}

}

package de.rwth.i9.palm.feature.publication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.helper.comparator.PublicationFileBySourceNameNaturalOrderComparator;
import de.rwth.i9.palm.helper.comparator.PublicationSourceBySourceTypeComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationFile;
import de.rwth.i9.palm.model.PublicationSource;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class PublicationDetailImpl implements PublicationDetail
{

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Override
	public Map<String, Object> getPublicationDetailById( String publicationId, String section )
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// get publication
		Publication publication = persistenceStrategy.getPublicationDAO().getById( publicationId );
		if ( publication == null )
		{
			responseMap.put( "status", "Error - publication not found" );
			return responseMap;
		}

		responseMap.put( "status", "ok" );

		// put into section
		Map<String, Boolean> sectionMap = new HashMap<String, Boolean>();
		if ( !section.equals( "all" ) )
		{
			String[] sectionArray = section.split( "-" );
			for ( String eachSection : sectionArray )
			{
				if ( eachSection.isEmpty() )
					continue;
				sectionMap.put( eachSection, true );
			}
		}

		// put publication detail
		Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();
		publicationMap.put( "id", publication.getId() );
		
		if ( section.equals( "all" ) || sectionMap.get( "title" ) != null )
			publicationMap.put( "title", publication.getTitle() );

		if ( section.equals( "all" ) || sectionMap.get( "type" ) != null )
			if ( publication.getPublicationType() != null )
				publicationMap.put( "type", publication.getPublicationType() );

		if ( section.equals( "all" ) || sectionMap.get( "abstract" ) != null )
			if ( publication.getAbstractText() != null )
			{
				publicationMap.put( "abstract", publication.getAbstractText() );
				publicationMap.put( "abstractStatus", publication.getAbstractStatus().getValue() );
			}

		if ( section.equals( "all" ) || sectionMap.get( "keyword" ) != null )
			if ( publication.getKeywordText() != null )
			{
				publicationMap.put( "keyword", publication.getKeywordText().replace( ",", ", " ) );
				publicationMap.put( "keywordStatus", publication.getKeywordStatus().getValue() );
			}

		if ( section.equals( "all" ) || sectionMap.get( "coauthor" ) != null )
		{
			// coauthor
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
			publicationMap.put( "coauthor", coathorList );
		}

		if ( section.equals( "all" ) || sectionMap.get( "content" ) != null )
			if ( publication.getContentText() != null )
				publicationMap.put( "content", publication.getContentText() );

		if ( section.equals( "all" ) || sectionMap.get( "sources" ) != null )
		{
			List<Object> publicationSourceList = new ArrayList<Object>();

			List<PublicationSource> publicationSources = new ArrayList<PublicationSource>( publication.getPublicationSources() );
			// sort publicationSource asc
			Collections.sort( publicationSources, new PublicationSourceBySourceTypeComparator() );
			// put publicationSource to Map and add into list
			for ( PublicationSource publicationSource : publicationSources )
			{
				Map<String, Object> publicationSourceMap = new LinkedHashMap<String, Object>();

				String sourceType = publicationSource.getSourceType().toString().toLowerCase();
				publicationSourceMap.put( "source", sourceType );

				if ( publicationSource.getTitle() != null )
					publicationSourceMap.put( "title", publicationSource.getTitle() );

				if ( publicationSource.getCoAuthors() != null )
					publicationSourceMap.put( "authors", publicationSource.getCoAuthors().replace( ",", ", " ) );

				if ( publicationSource.getAbstractText() != null )
					publicationSourceMap.put( "abstract", publicationSource.getAbstractText() );

				if ( publicationSource.getKeyword() != null )
					publicationSourceMap.put( "keyword", publicationSource.getKeyword().replace( ",", ", " ) );

				if ( publicationSource.getDate() != null )
					publicationSourceMap.put( "date", publicationSource.getDate() );

				if ( publicationSource.getCitedBy() > 0 )
					publicationSourceMap.put( "cited by", publicationSource.getCitedBy() );

				if ( publicationSource.getVenue() != null )
				{
					if ( publicationSource.getPublicationType() != null )
						publicationSourceMap.put( publicationSource.getPublicationType().toLowerCase(), publicationSource.getVenue() );
					else
						publicationSourceMap.put( "unknown", publicationSource.getVenue() );
				}

				if ( publicationSource.getAdditionalInformation() != null )
					publicationSourceMap.putAll( publicationSource.getAdditionalInformationAsMap() );

				if ( publicationSource.getPages() != null )
					publicationSourceMap.put( "page", publicationSource.getPages() );
				// add into list
				publicationSourceList.add( publicationSourceMap );
			}
			// put publicationSource into JSON
			publicationMap.put( "sources", publicationSourceList );
		}

		// publication files
		if ( section.equals( "all" ) || sectionMap.get( "files" ) != null )
		{
			List<Object> publicationFileList = new ArrayList<Object>();
			if ( publication.getPublicationFiles() != null )
			{
				List<PublicationFile> pubFiles = new ArrayList<PublicationFile>();
				pubFiles.addAll( publication.getPublicationFiles() );
				// sort
				Collections.sort( pubFiles, new PublicationFileBySourceNameNaturalOrderComparator() );
				for ( PublicationFile pubFile : pubFiles )
				{
					Map<String, Object> publicationFileMap = new LinkedHashMap<String, Object>();
					publicationFileMap.put( "type", pubFile.getFileType().toString() );
					publicationFileMap.put( "source", pubFile.getSourceType().toString().toLowerCase() );
					if ( pubFile.getSource() != null )
						publicationFileMap.put( "label", pubFile.getSource() );
					publicationFileMap.put( "url", pubFile.getUrl() );
					publicationFileList.add( publicationFileMap );
				}
			}
			publicationMap.put( "files", publicationFileList );
		}

		responseMap.put( "publication", publicationMap );

		return responseMap;
	}

}

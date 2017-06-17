package de.rwth.i9.palm.feature.researcher;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.AuthorSource;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.SourceType;

public class ResearcherBasicInformationImpl implements ResearcherBasicInformation
{
	@Override
	public Map<String, Object> getResearcherBasicInformationMap( Author author )
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

//		if ( author.getPublications() == null || author.getPublications().isEmpty() )
//		{
//			responseMap.put( "count", 0 );
//			return responseMap;
//		}
		// author data
		responseMap.put( "author", printAuthorInformation( author ) );

		// sources data
		List<Object> sources = new ArrayList<Object>();
		for ( AuthorSource authorSource : author.getAuthorSources() )
		{
			if ( authorSource.getSourceType().equals( SourceType.GOOGLESCHOLAR ) || 
					authorSource.getSourceType().equals( SourceType.CITESEERX ) ||
					authorSource.getSourceType().equals( SourceType.DBLP))
			{
				Map<String, Object> sourceMap = new LinkedHashMap<String, Object>();
				String label = "Google Scholar";
				if ( authorSource.getSourceType().equals( SourceType.CITESEERX ) )
					label = "CiteseerX";
				if ( authorSource.getSourceType().equals( SourceType.DBLP ) )
					label = "DBLP";
				sourceMap.put( "source", label );
				sourceMap.put( "url", authorSource.getSourceUrl() );
				sources.add( sourceMap );
			}
		}
		if ( !sources.isEmpty() )
			responseMap.put( "sources", sources );

		// prepare a list of map object containing year as the key and number of
		// publication and citation ays value
		Map<Integer, Object> publicationCitationYearlyMap = new HashMap<Integer, Object>();

		// prapare data format for year
		SimpleDateFormat df = new SimpleDateFormat( "yyyy" );

		// get maximum and minimum of year value
		int minYear = 0, maxYear = 0;

		// count number of publication and citation per year
		for ( Publication publication : author.getPublications() )
		{
			// just skip publication without date
			if ( publication.getPublicationDate() == null )
				continue;
			// get publication year
			Integer year = Integer.parseInt( df.format( publication.getPublicationDate() ) );

			// get year timespan
			if ( minYear == 0 && maxYear == 0 )
			{
				minYear = year;
				maxYear = year;
			}

			if ( minYear > year )
				minYear = year;

			if ( maxYear < year )
				maxYear = year;

			// check whether the year is available on the map key
			if ( publicationCitationYearlyMap.get( year ) == null )
			{
				// still not available put new map
				Map<String, Integer> publicationCitationMap = new LinkedHashMap<String, Integer>();
				publicationCitationMap.put( "totalPublication", 1 );
				publicationCitationMap.put( "totalCitation", publication.getCitedBy() );
				// put into yearly map
				publicationCitationYearlyMap.put( year, publicationCitationMap );
			}
			else
			{
				Map<String, Integer> publicationCitationMap = (Map<String, Integer>) publicationCitationYearlyMap.get( year );
				publicationCitationMap.put( "totalPublication", publicationCitationMap.get( "totalPublication" ) + 1 );
				publicationCitationMap.put( "totalCitation", publicationCitationMap.get( "totalCitation" ) + publication.getCitedBy() );
			}
		}

		// put coauthor to responseMap
		responseMap.put( "yearlyPublicationData", publicationCitationYearlyMap );


		// D3 visualization data
		responseMap.put( "d3data", printYearlyPublicationInformation( publicationCitationYearlyMap, minYear, maxYear ) );

		return responseMap;
	}

	private List<Object> printYearlyPublicationInformation( Map<Integer, Object> publicationCitationYearlyMap, int minYear, int maxYear )
	{
		if ( minYear == 0 && maxYear == 0 )
		{
			return Collections.emptyList();
		}
		// main list contain 2 map
		List<Object> visualList = new ArrayList<Object>();

		// publication information
		Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();
		publicationMap.put( "key", "Publication" );
		publicationMap.put( "bar", true );
		List<Object> publicationValues = new ArrayList<Object>();
		publicationMap.put( "values", publicationValues );

		//Map<String, Object> citationMap = new LinkedHashMap<String, Object>();
		//citationMap.put( "key", "Citation" );
		//List<Object> citationValues = new ArrayList<Object>();
		//citationMap.put( "values", citationValues );

		// put into main list
		visualList.add( publicationMap );
		//visualList.add( citationMap );

		for ( int i = minYear; i <= maxYear; i++ )
		{
			String string = Integer.toString( i );
			DateFormat format = new SimpleDateFormat( "yyyy", Locale.ENGLISH );
			Long unixDate = (long) 0;
			try
			{
				Date date = format.parse( string );
				unixDate = date.getTime();
			}
			catch ( ParseException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if ( publicationCitationYearlyMap.get( i ) != null )
			{
				@SuppressWarnings( "unchecked" )
				Map<String, Integer> publicationCitationMap = (Map<String, Integer>) publicationCitationYearlyMap.get( i );
				publicationValues.add( new Object[] { unixDate, publicationCitationMap.get( "totalPublication" ), Integer.toString( i ) } );
//				citationValues.add( new Object[] { unixDate, publicationCitationMap.get( "totalCitation" ) } );
			}
			else
			{
				publicationValues.add( new Object[] { unixDate, 0 } );
//				citationValues.add( new Object[] { unixDate, 0 } );
			}
		}

		return visualList;
	}

	/**
	 * Print researcher data
	 * 
	 * @param researcher
	 * @return
	 */
	private Map<String, Object> printAuthorInformation( Author researcher )
	{

		Map<String, Object> researcherMap = new LinkedHashMap<String, Object>();
		researcherMap.put( "id", researcher.getId() );
		researcherMap.put( "name", WordUtils.capitalize( researcher.getName() ) );
		if ( researcher.getPhotoUrl() != null )
			researcherMap.put( "photo", researcher.getPhotoUrl() );
		if ( researcher.getAcademicStatus() != null )
			researcherMap.put( "status", researcher.getAcademicStatus() );
		if ( researcher.getInstitution() != null )
			researcherMap.put( "aff", researcher.getInstitution().getName() );
		if ( researcher.getCitedBy() > 0 )
			researcherMap.put( "citedBy", researcher.getCitedBy() );

		if ( researcher.getPublicationAuthors() != null )
			researcherMap.put( "publicationsNumber", researcher.getNoPublication() );
		else
			researcherMap.put( "publicationsNumber", 0 );

		if ( researcher.getEmail() != null && researcher.getEmail() != "" )
			researcherMap.put( "email", researcher.getEmail() );

		if ( researcher.getHomepage() != null && researcher.getHomepage() != "" )
			researcherMap.put( "homepage", researcher.getHomepage() );

		String otherDetail = "";
		if ( researcher.getOtherDetail() != null )
			otherDetail += researcher.getOtherDetail();
		if ( researcher.getDepartment() != null )
			otherDetail += ", " + researcher.getDepartment();
		if ( !otherDetail.equals( "" ) )
			researcherMap.put( "detail", otherDetail );

		researcherMap.put( "isAdded", researcher.isAdded() );

		return researcherMap;
	}

}

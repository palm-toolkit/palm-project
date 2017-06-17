package de.rwth.i9.palm.feature.researcher;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.datasetcollect.service.ResearcherCollectionService;
import de.rwth.i9.palm.helper.DateTimeHelper;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.RequestType;
import de.rwth.i9.palm.model.UserRequest;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class ResearcherSearchImpl implements ResearcherSearch
{
	private final static Logger LOGGER = LoggerFactory.getLogger( ResearcherSearchImpl.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private ResearcherCollectionService researcherCollectionService;

	@Override
	public Map<String, Object> getResearcherMapByQuery( 
			String query, 
			String queryType, 
			Integer startPage, 
			Integer maxresult, 
			String source, 
 String addedAuthor, 
			String fulltextSearch, 
			boolean persist ) throws IOException, InterruptedException, ExecutionException, org.apache.http.ParseException, OAuthSystemException, OAuthProblemException
	{
		// researchers list container
		Map<String, Object> authorMap = new LinkedHashMap<String, Object>();

		// get authors from the datasource
		if ( source.equals( "internal" ) )
		{
			// the authors is querying from database
			if ( fulltextSearch.equals( "no" ) )
				authorMap = persistenceStrategy.getAuthorDAO().getAuthorWithPaging( query, addedAuthor, startPage, maxresult );
			else
			{
				authorMap = persistenceStrategy.getAuthorDAO().getAuthorByFullTextSearchWithPaging( query, addedAuthor, startPage, maxresult );
			}
		}
		else if ( source.equals( "external" ) )
		{
			// TODO: the authors are querying from external (academic networks)

		}
		else if ( source.equals( "all" ) )
		{
			// the authors are combination from internal and external sources
			if ( !query.equals( "" ) && startPage == 0 )
			{
				// collect author from network
//				if ( isCollectAuthorFromNetworkNeeded( query ) )
				List<Author> researcherList = researcherCollectionService.collectAuthorInformationFromNetwork( query, persist );
				authorMap.put( "totalCount", researcherList.size() );
				authorMap.put( "authors", researcherList );
				
				//				else
//				{
//					// the authors is querying from database
//					if ( fulltextSearch.equals( "no" ) )
//						researcherList.addAll( persistenceStrategy.getAuthorDAO().getAuthorListWithPaging( query, startPage, maxresult ) );
//					else
//						researcherList.addAll( persistenceStrategy.getAuthorDAO().getAuthorListByFullTextSearchWithPaging( query, startPage, maxresult ) );
//				}
			}
		}

		return authorMap;
	}

	/**
	 * Print the result to Map object (JSON)
	 * 
	 * @param responseMap
	 * @param researcherMap
	 */
	public Map<String, Object> printJsonOutput( Map<String, Object> responseMap, List<Author> researchers )
	{
		// create the json structure for researcher list
		if ( researchers == null || researchers.isEmpty() )
		{
			responseMap.put( "count", 0 );
			return responseMap;
		}

		// sort based on citedby
		// Collections.sort( researchers, new AuthorByNoCitationComparator() );

		List<Map<String, Object>> researcherList = new ArrayList<Map<String, Object>>();

		for ( Author researcher : researchers )
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
				researcherMap.put( "citedBy", Integer.toString( researcher.getCitedBy() ) );
			if ( researcher.getHindex() > 0 )
				researcherMap.put( "hindex", Integer.toString( researcher.getHindex() ) );

			if ( researcher.getPublicationAuthors() != null )
				researcherMap.put( "publicationsNumber", researcher.getNoPublication() );
			else
				researcherMap.put( "publicationsNumber", 0 );
			String otherDetail = "";
			if ( researcher.getOtherDetail() != null )
				otherDetail += researcher.getOtherDetail();
			if ( researcher.getDepartment() != null )
				otherDetail += ", " + researcher.getDepartment();
			if ( !otherDetail.equals( "" ) )
				researcherMap.put( "detail", otherDetail );

			researcherMap.put( "isAdded", researcher.isAdded() );

			researcherList.add( researcherMap );
		}
		responseMap.put( "count", researcherList.size() );
		responseMap.put( "researchers", researcherList );

		return responseMap;
	}

	/**
	 * Check whether a query have been executed before and not more than a week
	 * since query is executed
	 * 
	 * @param query
	 * @return
	 */
	private boolean isCollectAuthorFromNetworkNeeded( String query )
	{
		boolean collectFromNetwork = false;
		// check whether the author query ever executed before
		UserRequest userRequest = persistenceStrategy.getUserRequestDAO().getByTypeAndQuery( RequestType.SEARCHAUTHOR, query );

		// get current timestamp
		java.util.Date date = new java.util.Date();
		Timestamp currentTimestamp = new Timestamp( date.getTime() );

		if ( userRequest == null )
		{ // there is no kind of request before
			// perform fetching data through academic network
			collectFromNetwork = true;
			// persist current request
			userRequest = new UserRequest();
			userRequest.setQueryString( query );
			userRequest.setRequestDate( currentTimestamp );
			userRequest.setRequestType( RequestType.SEARCHAUTHOR );
			persistenceStrategy.getUserRequestDAO().persist( userRequest );
		}
		else
		{
			// check if the existing userRequest obsolete (longer than a
			// week)
			if ( DateTimeHelper.substractTimeStampToHours( currentTimestamp, userRequest.getRequestDate() ) > 24 * 7 )
			{
				// update current timestamp
				userRequest.setRequestDate( currentTimestamp );
				persistenceStrategy.getUserRequestDAO().persist( userRequest );
				collectFromNetwork = true;
			}
		}

		return collectFromNetwork;
	}

}

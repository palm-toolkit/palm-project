package de.rwth.i9.palm.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.rwth.i9.palm.model.Institution;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Controller
@RequestMapping( value = "/institution" )
public class InstitutionController
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;
	/**
	 * Get list of author given query ( author name )
	 * 
	 * @param query
	 */
	@Transactional
	@RequestMapping( value = "/search", method = RequestMethod.GET )
	public @ResponseBody Map<String, Object> getInstitution( 
			@RequestParam( value = "query", required = false ) String query,
			@RequestParam( value = "maxresult", required = false ) String maxresult,
			HttpServletRequest request,
			HttpServletResponse response )
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		if ( query == null || query.isEmpty() )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "query empty" );
			return responseMap;
		}

		int maxResultReturned = 10;
		if ( maxresult != null )
		{
			maxResultReturned = Integer.parseInt( maxresult );
		}

		// get institution
		List<Institution> institutions = persistenceStrategy.getInstitutionDAO().getWithFullTextSearch( query );

		// if institution return empty map
		if ( institutions.isEmpty() )
		{
			responseMap.put( "status", "ok" );
			responseMap.put( "query", query );
			responseMap.put( "statusMessage", "no result found" );
			return responseMap;
		}

		List<Object> institutionList = new ArrayList<Object>();
		for ( int i = 0; i < institutions.size(); i++ )
		{
			Map<String, String> institutionMap = new LinkedHashMap<String, String>();
			institutionMap.put( "id", institutions.get( i ).getId() );
			institutionMap.put( "name", institutions.get( i ).getName() );
			if ( institutions.get( i ).getAbbr() != null )
				institutionMap.put( "abbr", institutions.get( i ).getAbbr() );
			if ( institutions.get( i ).getUrl() != null )
				institutionMap.put( "url", institutions.get( i ).getUrl() );

			institutionList.add( institutionMap );
			
			if( i > maxResultReturned )
				break;
		}
		
		responseMap.put( "status", "ok" );
		responseMap.put( "query", query );
		responseMap.put( "count", institutionList.size() );
		responseMap.put( "institutions", institutionList );
		
		return responseMap;
	}

}
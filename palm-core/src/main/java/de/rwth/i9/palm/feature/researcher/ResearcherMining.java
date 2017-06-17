package de.rwth.i9.palm.feature.researcher;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

public interface ResearcherMining
{
	/**
	 * Fetch author's publications from academic networks, if it's necessary
	 * 
	 * @param id
	 * @param name
	 * @param uri
	 * @param affiliation
	 * @param pid
	 * @param force
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ParseException
	 * @throws TimeoutException
	 * @throws org.apache.http.ParseException
	 * @throws OAuthSystemException
	 * @throws OAuthProblemException
	 */
	public Map<String, Object> fetchResearcherData( String id, String name, String uri, String affiliation, String pid, String force, HttpServletRequest request ) throws IOException, InterruptedException, ExecutionException, ParseException, TimeoutException, org.apache.http.ParseException, OAuthSystemException, OAuthProblemException;

	/**
	 * Fetch author's publications details from academic networks and do
	 * information enrichment by extracting PDF or webpage (digital libraries)
	 * 
	 * @param id
	 * @param pid
	 * @param force
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ParseException
	 */
	public Map<String, Object> fetchResearcherPublicationData( String id, String pid, String force, HttpServletRequest request ) throws IOException, InterruptedException, ExecutionException, ParseException;
}

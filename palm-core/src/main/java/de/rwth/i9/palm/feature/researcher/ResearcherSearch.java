package de.rwth.i9.palm.feature.researcher;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import de.rwth.i9.palm.model.Author;

public interface ResearcherSearch
{
	public Map<String, Object> getResearcherMapByQuery( String query, String queryType, Integer startPage, Integer maxresult, String source, String fulltext, String fulltextSearch, boolean persist ) throws IOException, InterruptedException, ExecutionException, org.apache.http.ParseException, OAuthSystemException, OAuthProblemException;

	public Map<String, Object> printJsonOutput( Map<String, Object> responseMap, List<Author> researchers );
}

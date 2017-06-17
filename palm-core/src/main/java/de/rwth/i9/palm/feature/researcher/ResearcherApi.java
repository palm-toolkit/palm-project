package de.rwth.i9.palm.feature.researcher;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.ParseException;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

public interface ResearcherApi
{
	public Map<String, Object> getAuthorAutoComplete( String namePrefix );

	public Map<String, Object> getAuthorAutoCompleteFromNetworkAndDb( String namePrefix ) throws ParseException, IOException, InterruptedException, ExecutionException, OAuthSystemException, OAuthProblemException;
}

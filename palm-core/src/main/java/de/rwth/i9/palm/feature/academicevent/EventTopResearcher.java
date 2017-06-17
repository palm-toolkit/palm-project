package de.rwth.i9.palm.feature.academicevent;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface EventTopResearcher
{
	public Map<String, Object> getResearcherListByEventId( String query, String eventId, String pid, Integer maxresult, String orderBy ) throws UnsupportedEncodingException, InterruptedException, URISyntaxException, ExecutionException;
}

package de.rwth.i9.palm.feature.academicevent;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface EventPublication
{
	public Map<String, Object> getPublicationListByEventId( String eventId, String query, String publicationId );

	public Map<String, Object> getPublicationTopListByEventId( String eventId, String pid, Integer maxresult, String orderBy ) throws UnsupportedEncodingException, InterruptedException, URISyntaxException, ExecutionException;
}

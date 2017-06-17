package de.rwth.i9.palm.feature.academicevent;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.http.ParseException;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import de.rwth.i9.palm.model.EventGroup;

public interface EventMining
{
	public Map<String, Object> fetchEventData( String id, String pid, String force ) throws ParseException, IOException, InterruptedException, ExecutionException, java.text.ParseException, TimeoutException, OAuthSystemException, OAuthProblemException;

	public Map<String, Object> fetchEventGroupData( String id, String pid, List<EventGroup> sessionEventGroups );
}

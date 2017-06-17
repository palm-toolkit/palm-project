package de.rwth.i9.palm.feature.academicevent;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.EventGroup;

public interface EventSearch
{
	public List<EventGroup> getEventGroupListByQuery( String query, Integer startPage, Integer maxresult, String source, String type, boolean persistResult, String addedVenue );

	public Map<String, Object> getEventGroupMapByQuery( String query, String notation, Integer startPage, Integer maxresult, String source, String type, boolean persistResult, String eventId, String addedVenue );

	public Map<String, Object> printJsonOutput( Map<String, Object> responseMap, List<EventGroup> eventGroups );
}

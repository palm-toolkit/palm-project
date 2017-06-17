package de.rwth.i9.palm.feature.academicevent;

import java.io.IOException;
import java.util.Map;

import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.EventGroup;

public interface EventTopicModeling
{
	public Map<String, Object> getTopicModeling( String eventId, boolean isReplaceExistingResult );

	public Map<String, Object> getStaticTopicModelingNgrams( String eventId, boolean isReplaceExistingResult ) throws IOException;

	public Map<String, Object> getStaticTopicModelingNgramsEventGroup( String eventId, boolean isReplaceExistingResult ) throws IOException;

	public Map<String, Object> getTopicModelUniCloud( Event event, boolean isReplaceExistingResult ) throws IOException;

	public Map<String, Object> getTopicModelNCloud( Event event, boolean isReplaceExistingResult ) throws IOException;

	public Map<String, Object> getSimilarEventsMap( Event event, int startPage, int maxresult );
	
	public Map<String, Object> getSimilarEvents( Event event, int startPage, int maxresult ) throws IOException;

	public Map<String, Object> getEventGroupTopicEvolutionTest( Event event ) throws IOException;

	public Map<String, Object> getTopicModelEventGroupUniCloud( Event event, boolean isReplaceExistingResult ) throws IOException;

	public Map<String, Object> getTopicModelEventGroupNCloud( Event event, boolean isReplaceExistingResult ) throws IOException;
}

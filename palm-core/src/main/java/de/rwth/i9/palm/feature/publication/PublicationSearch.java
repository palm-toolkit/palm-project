package de.rwth.i9.palm.feature.publication;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.Publication;

public interface PublicationSearch
{
	public Map<String, Object> getPublicationListByQuery( String query, String publicationType, String authorId, String eventId, Integer page, Integer maxresult, String source, String fulltextSearch, String year, String orderBy );

	public Map<String, Object> printJsonOutput( Map<String, Object> responseMap, List<Publication> publications );

	public Map<String, Object> printElementAsJsonOutput( Publication publication );
}

package de.rwth.i9.palm.feature.researcher;

import java.util.Map;

public interface ResearcherPublication
{
	public Map<String, Object> getPublicationListByAuthorId( String authorId, String query, String year, Integer startPage, Integer maxresult, String orderBy );

	public Map<String, Object> getPublicationListByAuthorIdAndTopic( String authorId, String topic, String query, String year, Integer startPage, Integer maxresult, String orderBy );
}

package de.rwth.i9.palm.feature.researcher;

import java.util.Map;

public interface ResearcherTopPublication
{
	public Map<String, Object> getTopPublicationListByAuthorId( String authorId, Integer startPage, Integer maxresult );
}

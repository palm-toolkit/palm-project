package de.rwth.i9.palm.feature.researcher;

import java.io.IOException;
import java.util.Map;

import de.rwth.i9.palm.model.Author;

public interface ResearcherSimilarauthor
{
	public Map<String, Object> getResearcherSimilarAuthorMap( Author author, int startPage, int maxresult );

	public Map<String, Object> getResearcherSimilarAuthorTopicLevelMap( Author author, int startPage, int maxresult );

	public Map<String, Object> getResearcherSimilarAuthorTopicLevelRevised(Author author, int startPage, int maxresult) throws NullPointerException, IOException;
}

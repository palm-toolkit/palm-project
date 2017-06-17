package de.rwth.i9.palm.feature.researcher;

import java.util.Map;

import de.rwth.i9.palm.model.Author;

public interface ResearcherCoauthor
{
	public Map<String, Object> getResearcherCoAuthorMap( Author author, int startPage, int maxresultCoauthor, int maxresultTopics );

}

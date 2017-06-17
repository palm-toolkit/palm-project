package de.rwth.i9.palm.feature.researcher;

import java.util.Map;

public interface ResearcherTopicModeling
{
	public Map<String, Object> getTopicModeling( String authorId, boolean isReplaceExistingResult );

	public Map<String, Object> getStaticTopicModelingNgrams( String authorId, boolean isReplaceExistingResult );

}

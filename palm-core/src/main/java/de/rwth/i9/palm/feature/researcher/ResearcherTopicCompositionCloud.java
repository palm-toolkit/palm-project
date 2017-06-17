package de.rwth.i9.palm.feature.researcher;

import java.io.IOException;
import java.util.Map;

import de.rwth.i9.palm.model.Author;

public interface ResearcherTopicCompositionCloud {

	Map<String, Object> getTopicModelUniCloud(Author author, boolean isReplaceExistingResult) throws IOException;

	Map<String, Object> getTopicModelNCloud(Author author, boolean isReplaceExistingResult) throws IOException;

	Map<String, Object> getTopicModelTagCloud(Author author, boolean isReplaceExistingResult);

}


package de.rwth.i9.palm.feature.publication;

import java.util.Map;

import de.rwth.i9.palm.model.Publication;

public interface PublicationTopicModeling
{
	public Map<String, Object> getTopicModeling( String publicationId, boolean isReplaceExistingResult );
	
	public Map<String, Object> getTopicComposition( String publicationId, boolean isReplaceExistingResult );

	public Map<String, Object> getStaticTopicModelingNgrams( String publicationId, boolean isReplaceExistingResult );

	public Map<String, Object> getTopicModelUniCloud( String publicationId, boolean isReplaceExistingResult );

	public Map<String, Object> getTopicModelNCloud( String publicationId, boolean isReplaceExistingResult );

	public Map<String, Object> getResearcherSimilarPublicationMap( Publication publication, int startPage, int maxresult );
}

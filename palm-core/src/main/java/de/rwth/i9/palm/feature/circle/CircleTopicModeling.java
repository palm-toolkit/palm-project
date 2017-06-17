package de.rwth.i9.palm.feature.circle;

import java.io.IOException;
import java.util.Map;

import de.rwth.i9.palm.model.Circle;

public interface CircleTopicModeling
{
	public Map<String, Object> getTopicModeling( String circleId, boolean isReplaceExistingResult );

	public Map<String, Object> getStaticTopicModelingNgrams( String circleId, boolean isReplaceExistingResult );

	public Map<String, Object> getTopicModelUniCloud( Circle circle, boolean isReplaceResult ) throws IOException;

	public Map<String, Object> getTopicModelNCloud( Circle circle, boolean isReplaceResult ) throws IOException;

	public Map<String, Object> getCircleTopicEvolutionTest( Circle circle ) throws IOException;

	public Map<String, Object> getSimilarCirclesMap( Circle circle, int startPage, int maxresult );
	
	public Map<String, Object> getSimilarCircles( Circle circle, int startPage, int maxresult ) throws IOException;

}

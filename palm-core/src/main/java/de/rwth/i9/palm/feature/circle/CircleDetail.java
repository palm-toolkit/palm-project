package de.rwth.i9.palm.feature.circle;

import java.util.Map;

public interface CircleDetail
{
	public Map<String, Object> getCircleDetailById( String circleId, boolean isRetrieveAuthorDetail, boolean isRetrievePublicationDetail );
}

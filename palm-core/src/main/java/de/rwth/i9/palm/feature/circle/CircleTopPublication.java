package de.rwth.i9.palm.feature.circle;

import java.util.Map;

public interface CircleTopPublication
{
	public Map<String, Object> getTopPublicationListByCircleId( String circleId, Integer startPage, Integer maxresult );
}

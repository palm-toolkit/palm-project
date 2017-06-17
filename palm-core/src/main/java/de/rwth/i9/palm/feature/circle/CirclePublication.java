package de.rwth.i9.palm.feature.circle;

import java.util.Map;

import de.rwth.i9.palm.model.Circle;

public interface CirclePublication
{
	public Map<String, Object> getCirclePublicationMap( Circle circle );

	public Map<String, Object> getCirclePublicationByCircleId( String circleId, String query, String year, Integer startPage, Integer maxresult, String orderBy );

	public Map<String, Object> getCircleMemberPublication( String circleId, String author_id, String query, Integer yearMin, Integer yearMax, Integer startPage, Integer maxresult, String orderBy );

	Map<String, Object> getCirclePublicationByCircleIdAndTimePeriod( String circleId, String query, Integer yearMin, Integer yearMax, Integer startPage, Integer maxresult, String orderBy );
}

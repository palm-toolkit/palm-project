package de.rwth.i9.palm.feature.circle;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.Circle;

public interface CircleSearch
{
	public Map<String, Object> getCircleListByQuery( String query, String creatorId, Integer page, Integer maxresult, String fulltextSearch, String orderBy );

	public Map<String, Object> printJsonOutput( Map<String, Object> responseMap, List<Circle> circles );
}

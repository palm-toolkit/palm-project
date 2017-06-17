package de.rwth.i9.palm.feature.circle;

import java.util.Map;

import de.rwth.i9.palm.model.Circle;

public interface CircleResearcher
{
	public Map<String, Object> getCircleResearcherMap( Circle circle, Integer yearMin, Integer yearMax, Integer maxresult );
}

package de.rwth.i9.palm.graph.feature;

import java.util.Map;

import de.rwth.i9.palm.model.Author;

public interface GraphFeature
{
	public Map<String, Object> graphData( Author author );

	public void newFunc();

	public void testFunction();
}

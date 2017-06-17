package de.rwth.i9.palm.feature.publication;

import java.util.List;
import java.util.Map;

import de.rwth.i9.palm.model.Publication;

public interface PublicationSimilar
{
	public Map<String, Object> getSimilarPublication( String title, String authorString );

	public Map<String, Object> printJsonOutput( Map<String, Object> responseMap, List<Publication> publications );
}

package de.rwth.i9.palm.feature.publication;

import java.util.Map;

public interface PublicationDetail
{
	public Map<String, Object> getPublicationDetailById( String publicationId, String section );
}

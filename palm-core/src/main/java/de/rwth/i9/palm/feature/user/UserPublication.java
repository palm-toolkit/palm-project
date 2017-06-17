package de.rwth.i9.palm.feature.user;

import java.util.Map;

public interface UserPublication {
	public Map<String, Object> getPublicationListByAuthorId( String authorId, String query, String year, Integer startPage, Integer maxresult, String orderBy );
}

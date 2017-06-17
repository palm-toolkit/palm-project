package de.rwth.i9.palm.feature.publication;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface PublicationMining
{
	public Map<String, Object> getPublicationExtractedTopicsById( String publicationId, String pid, String maxRetrieve ) throws UnsupportedEncodingException, InterruptedException, URISyntaxException, ExecutionException;
}

package de.rwth.i9.palm.feature.researcher;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.rwth.i9.palm.feature.AcademicFeature;

public interface ResearcherInterest extends AcademicFeature
{
	public Map<String, Object> getAuthorInterestById( String authorId, boolean isReplaceExistingResult ) throws InterruptedException, UnsupportedEncodingException, URISyntaxException, ParseException, ExecutionException;
}

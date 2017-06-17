package de.rwth.i9.palm.feature.academicevent;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.rwth.i9.palm.feature.AcademicFeature;

public interface EventInterest extends AcademicFeature
{
	public Map<String, Object> getEventInterestById( String eventId, boolean isReplaceExistingResult ) throws InterruptedException, UnsupportedEncodingException, URISyntaxException, ParseException, ExecutionException;
}

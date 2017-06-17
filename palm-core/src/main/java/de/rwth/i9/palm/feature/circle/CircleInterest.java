package de.rwth.i9.palm.feature.circle;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.rwth.i9.palm.feature.AcademicFeature;

public interface CircleInterest extends AcademicFeature
{
	public Map<String, Object> getCircleInterestById( String circleId, boolean isReplaceExistingResult ) throws InterruptedException, UnsupportedEncodingException, URISyntaxException, ParseException, ExecutionException;
}

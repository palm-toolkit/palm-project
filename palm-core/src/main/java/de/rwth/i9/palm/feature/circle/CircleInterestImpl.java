package de.rwth.i9.palm.feature.circle;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.feature.AcademicFeatureImpl;
import de.rwth.i9.palm.interestmining.service.CircleInterestMiningService;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.topicextraction.service.TopicExtractionService;

@Component
public class CircleInterestImpl extends AcademicFeatureImpl implements CircleInterest
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private TopicExtractionService topicExtractionService;
	
	@Autowired
	private CircleInterestMiningService circleInterestMiningService;

	@Override
	public Map<String, Object> getCircleInterestById( String circleId, boolean isReplaceExistingResult ) throws InterruptedException, UnsupportedEncodingException, URISyntaxException, ParseException, ExecutionException
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		if ( circleId == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "circle id missing" );
			return responseMap;
		}

		// get the author
		Circle circle = persistenceStrategy.getCircleDAO().getById( circleId );

		if ( circle == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "circle not found" );
			return responseMap;
		}

		// put some information
		responseMap.put( "status", "Ok" );

		Map<String, Object> targetCircleMap = new LinkedHashMap<String, Object>();
		targetCircleMap.put( "id", circle.getId() );
		targetCircleMap.put( "name", circle.getName() );
		responseMap.put( "circle", targetCircleMap );

		// check whether publication has been extracted
		// later add extractionServiceType checking
		topicExtractionService.extractTopicFromPublicationByCircle( circle );
		
		// check if interest need to be recalculate
		if ( !isReplaceExistingResult && circle.isUpdateInterest() )
		{
			isReplaceExistingResult = true;

			circle.setUpdateInterest( false );
			persistenceStrategy.getCircleDAO().persist( circle );
		}

		// mining the author interest
		circleInterestMiningService.getInterestFromCircle( responseMap, circle, isReplaceExistingResult );

		return responseMap;
	}
	
}

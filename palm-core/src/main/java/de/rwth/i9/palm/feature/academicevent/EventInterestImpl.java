package de.rwth.i9.palm.feature.academicevent;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.feature.AcademicFeatureImpl;
import de.rwth.i9.palm.interestmining.service.EventInterestMiningService;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.topicextraction.service.TopicExtractionService;

@Component
public class EventInterestImpl extends AcademicFeatureImpl implements EventInterest
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private TopicExtractionService topicExtractionService;
	
	@Autowired
	private EventInterestMiningService eventInterestMiningService;

	@Override
	public Map<String, Object> getEventInterestById( String eventId, boolean isReplaceExistingResult ) throws InterruptedException, UnsupportedEncodingException, URISyntaxException, ParseException, ExecutionException
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		if ( eventId == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "event id missing" );
			return responseMap;
		}

		// get the author
		Event event = persistenceStrategy.getEventDAO().getById( eventId );

		if ( event == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "event not found" );
			return responseMap;
		}

		// put some information
		responseMap.put( "status", "Ok" );

		Map<String, Object> targetEventMap = new LinkedHashMap<String, Object>();
		targetEventMap.put( "id", event.getId() );
		targetEventMap.put( "name", event.getName() );
		responseMap.put( "event", targetEventMap );

		// check whether publication has been extracted
		// later add extractionServiceType checking
		topicExtractionService.extractTopicFromPublicationByEvent( event );
		
		// check if interest need to be recalculate
		if ( !isReplaceExistingResult && event.isUpdateInterest() )
		{
			isReplaceExistingResult = true;

			event.setUpdateInterest( false );
			persistenceStrategy.getEventDAO().persist( event );
		}

		// mining the author interest
		eventInterestMiningService.getInterestFromEvent( responseMap, event, isReplaceExistingResult );

		return responseMap;
	}
	
}

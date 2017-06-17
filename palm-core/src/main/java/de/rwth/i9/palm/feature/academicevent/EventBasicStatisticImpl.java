package de.rwth.i9.palm.feature.academicevent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserEventGroupBookmark;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.SecurityService;

@Component
public class EventBasicStatisticImpl implements EventBasicStatistic
{

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private SecurityService securityService;

	@Override
	public Map<String, Object> getEventGroupBasicStatisticById( String eventGroupId )
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		EventGroup eventGroup = persistenceStrategy.getEventGroupDAO().getById( eventGroupId );
		if ( eventGroup == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "EventGroup not found" );
			return responseMap;
		}

		responseMap.put( "status", "ok" );
		responseMap.put( "eventGroup", this.getEventGroupDetails( eventGroup ) );

		return responseMap;
	}

	@Override
	public Map<String, Object> getEventBasicStatisticById( String eventId )
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		Event event = persistenceStrategy.getEventDAO().getById( eventId );
		if ( event == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "Event not found" );
			return responseMap;
		}

		if ( event.getEventGroup() != null )
			responseMap.put( "eventGroup", this.getEventGroupDetails( event.getEventGroup() ) );

		// check whether eventGroup is already booked or not
		User user = securityService.getUser();
		if ( user != null )
		{

			UserEventGroupBookmark upb = persistenceStrategy.getUserEventGroupBookmarkDAO().getByUserAndEventGroup( user, event.getEventGroup() );
			if ( upb != null )
				responseMap.put( "booked", true );
			else
				responseMap.put( "booked", false );
		}
		responseMap.put( "event", this.getEventDetails( event ) );

		return responseMap;
	}

	/**
	 * Gather EventGroup details
	 * 
	 * @param eventGroup
	 * @return
	 */
	private Map<String, Object> getEventGroupDetails( EventGroup eventGroup )
	{
		Map<String, Object> eventGroupMap = new LinkedHashMap<String, Object>();
		eventGroupMap.put( "type", eventGroup.getPublicationType().toString().toLowerCase() );
		eventGroupMap.put( "id", eventGroup.getId() );
		eventGroupMap.put( "name", eventGroup.getName() );
		if ( eventGroup.getNotation() != null && !eventGroup.getNotation().equals( eventGroup.getName() ) )
			eventGroupMap.put( "abbreviation", eventGroup.getNotation() );
		if ( eventGroup.getDescription() != null && !eventGroup.getDescription().equals( "" ) )
			eventGroupMap.put( "description", eventGroup.getDescription() );

		List<Object> sources = new ArrayList<Object>();
		/* Source, currently only DBLP */
		if ( eventGroup.getDblpUrl() != null )
		{

			Map<String, Object> sourcesMap = new LinkedHashMap<String, Object>();
			sourcesMap.put( "source", "DBLP" );
			sourcesMap.put( "url", eventGroup.getDblpUrl() );

			sources.add( sourcesMap );
		}

		if ( !sources.isEmpty() )
			eventGroupMap.put( "sources", sources );

		return eventGroupMap;
	}

	/**
	 * Gather Event details
	 * 
	 * @param event
	 * @return
	 */
	private Map<String, Object> getEventDetails( Event event )
	{
		Map<String, Object> eventMap = new LinkedHashMap<String, Object>();

		eventMap.put( "id", event.getId() );
		eventMap.put( "name", WordUtils.capitalize( event.getName() ) );
		eventMap.put( "year", event.getYear() );

		if ( event.getVolume() != null )
			eventMap.put( "volume", event.getVolume() );

		Map<String, Object> additionalInformationMap = null;
		if ( event.getAdditionalInformation() != null )
			additionalInformationMap = event.getAdditionalInformationAsMap();

		if ( additionalInformationMap != null && !additionalInformationMap.isEmpty() )
		{
			if ( additionalInformationMap.get( "number" ) != null )
				eventMap.put( "number", additionalInformationMap.get( "number" ) );

			if ( additionalInformationMap.get( "date" ) != null )
				eventMap.put( "date", additionalInformationMap.get( "date" ) );

			String location = "";
			if ( event.getLocation() != null )
			{
				if ( event.getLocation().getCity() != null )
					location = event.getLocation().getCity();
				if ( event.getLocation().getState() != null )
					location += ", " + event.getLocation().getState();
				if ( event.getLocation().getCountry() != null )
					location += ", " + event.getLocation().getCountry().getName();
			}

			if ( location.equals( "" ) )
			{
				if ( additionalInformationMap.get( "city" ) != null )
					location = (String) additionalInformationMap.get( "city" );
				if ( additionalInformationMap.get( "state" ) != null )
					location += ", " + (String) additionalInformationMap.get( "state" );
				if ( additionalInformationMap.get( "country" ) != null )
					location += ", " + (String) additionalInformationMap.get( "country" );
			}

			if ( !location.equals( "" ) )
				eventMap.put( "location", location );
		}

		eventMap.put( "isAdded", event.isAdded() );

		if ( event.getNumberPaper() > 0 )
			eventMap.put( "numberOfPaper", event.getNumberPaper() );
		if ( event.getNumberParticipant() > 0 )
			eventMap.put( "numberOfParticipant", event.getNumberParticipant() );

		List<Object> sources = new ArrayList<Object>();
		/* Source, currently only DBLP */
		if ( event.getDblpUrl() != null )
		{

			Map<String, Object> sourcesMap = new LinkedHashMap<String, Object>();
			sourcesMap.put( "source", "DBLP" );
			sourcesMap.put( "url", event.getDblpUrl() );

			sources.add( sourcesMap );
		}

		if ( !sources.isEmpty() )
			eventMap.put( "sources", sources );

		return eventMap;
	}
}

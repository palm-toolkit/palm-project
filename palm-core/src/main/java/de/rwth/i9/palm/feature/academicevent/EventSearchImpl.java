package de.rwth.i9.palm.feature.academicevent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.datasetcollect.service.DblpEventCollection;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.model.PublicationType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class EventSearchImpl implements EventSearch
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Override
	public List<EventGroup> getEventGroupListByQuery( String query, Integer startPage, Integer maxResult, String source, String type, boolean persistResult, String addedVenue )
	{
		List<EventGroup> eventGroups = new ArrayList<EventGroup>();

		// get authors from the datasource
		if ( source.equals( "internal" ) )
		{
			// set lucene fulltext search by default
			eventGroups.addAll( persistenceStrategy.getEventGroupDAO().getEventGroupListFullTextSearchWithPaging( query, type, startPage, maxResult, addedVenue ) );
		}
		else if ( source.equals( "all" ) )
		{
			// TODO: change implementation
			// get event from DBLP
			List<Object> dblpEvents = DblpEventCollection.getEventFromDBLPSearch( query, type, null );

			// combine
			eventGroups.addAll( persistenceStrategy.getEventGroupDAO().getEventGroupListWithPaging( query, type, startPage, maxResult, addedVenue ) );

			if ( dblpEvents != null && !dblpEvents.isEmpty() )
			{
				for ( Object dblpEventObject : dblpEvents )
				{
					Map<String, String> dblpEventMap = (Map<String, String>) dblpEventObject;

					String eventGroupUrl = dblpEventMap.get( "url" );

					// check if there is already in eventgroup
					boolean isExist = false;
					if ( !eventGroups.isEmpty() )
					{
						for ( EventGroup eachEventGroup : eventGroups )
						{
							if ( eventGroupUrl.equals( eachEventGroup.getDblpUrl() ) )
							{
								isExist = true;
								break;
							}
						}
					}

					if ( !isExist )
					{
						EventGroup newEventGroup = new EventGroup();
						newEventGroup.setDblpUrl( eventGroupUrl );
						newEventGroup.setName( dblpEventMap.get( "name" ) );
						newEventGroup.setNotation( dblpEventMap.get( "abbr" ) );
						newEventGroup.setPublicationType( PublicationType.valueOf( dblpEventMap.get( "type" ).toUpperCase() ) );

						eventGroups.add( newEventGroup );
					}

				}
			}
		}

		return eventGroups;
	}

	@Override
	public Map<String, Object> getEventGroupMapByQuery( String query, String notation, Integer startPage, Integer maxresult, String source, String type, boolean persistResult, String eventId, String addedVenue )
	{
		Map<String, Object> eventGroupMap = new LinkedHashMap<String, Object>();

		// get authors from the datasource
		if ( source.equals( "internal" ) )
		{
			// set lucene fulltext search by default
			eventGroupMap = persistenceStrategy.getEventGroupDAO().getEventGroupMapFullTextSearchWithPaging( query, notation, type, startPage, maxresult, addedVenue );
		}
		else if ( source.equals( "all" ) )
		{
			// get event target if available
			Event eventTarget = null;
			if ( eventId != null )
			{
				eventTarget = persistenceStrategy.getEventDAO().getById( eventId );
			}
			// TODO: change implementation if another source is added instead of
			// DBLP

			// get event from DBLP
			List<Object> dblpEvents = DblpEventCollection.getEventFromDBLPSearch( query, type, null );

			// combine with internal
			List<EventGroup> eventGroups = new ArrayList<EventGroup>();
			if ( eventTarget != null && eventTarget.getDblpUrl() != null && !eventTarget.getDblpUrl().isEmpty() )
			{
				if ( dblpEvents != null && !dblpEvents.isEmpty() )
					for ( Object dblpEventObject : dblpEvents )
					{
						@SuppressWarnings( "unchecked" )
						Map<String, String> dblpEventMap = (Map<String, String>) dblpEventObject;

						String eventGroupUrl = dblpEventMap.get( "url" );
						if ( isDblpUrlSimilar( eventGroupUrl, eventTarget.getDblpUrl() ) )
						{
							eventTarget.getEventGroup().setDblpUrl( eventGroupUrl );
							eventTarget.getEventGroup().setName( dblpEventMap.get( "name" ) );
							eventTarget.getEventGroup().setNotation( dblpEventMap.get( "abbr" ) );
							eventGroups.add( eventTarget.getEventGroup() );
							break;
						}
					}

			}
			else
			{
				eventGroups.addAll( persistenceStrategy.getEventGroupDAO().getEventGroupListWithPaging( query, type, startPage, maxresult, addedVenue ) );

				// find conferences / journal candidates
				// flag indicated that target
				if ( dblpEvents != null && !dblpEvents.isEmpty() )
				{
					// additional event group from DBLP
					List<EventGroup> additionalEventGroup = new ArrayList<EventGroup>();

					for ( Object dblpEventObject : dblpEvents )
					{
						@SuppressWarnings( "unchecked" )
						Map<String, String> dblpEventMap = (Map<String, String>) dblpEventObject;


						String eventGroupUrl = dblpEventMap.get( "url" );

						// check if there is already in eventgroup
						boolean isExist = false;
						if ( !eventGroups.isEmpty() )
						{
							for ( EventGroup eachEventGroup : eventGroups )
							{
								if ( eachEventGroup.getDblpUrl() != null && eventGroupUrl.replace( "/", "" ).equals( eachEventGroup.getDblpUrl().replace( "/", "" ) ) )
								{
									isExist = true;
									break;
								}
								else
								{
									if ( ( dblpEventMap.get( "name" ).toLowerCase().equals( eachEventGroup.getName().toLowerCase() ) || 
											( dblpEventMap.get( "abbr" ) != null && eachEventGroup.getNotation() != null && dblpEventMap.get( "abbr" ).toLowerCase().equals( eachEventGroup.getNotation().toLowerCase() ) ) ) )
									{
										eachEventGroup.setDblpUrl( eventGroupUrl );
										eachEventGroup.setName( dblpEventMap.get( "name" ) );
										eachEventGroup.setNotation( dblpEventMap.get( "notation" ) );
										isExist = true;
										break;
									}
								}
							}
						}

						if ( !isExist )
						{
							EventGroup newEventGroup = new EventGroup();
							newEventGroup.setDblpUrl( eventGroupUrl );
							newEventGroup.setName( dblpEventMap.get( "name" ) );
							newEventGroup.setNotation( dblpEventMap.get( "abbr" ) );
							newEventGroup.setPublicationType( PublicationType.valueOf( dblpEventMap.get( "type" ).toUpperCase() ) );

							additionalEventGroup.add( newEventGroup );
						}

					}
					// merge event group
					eventGroups.addAll( additionalEventGroup );
				}
			}

			eventGroupMap.put( "totalCount", eventGroups.size() );
			eventGroupMap.put( "eventGroups", eventGroups );
		}

		return eventGroupMap;
	}

	private boolean isDblpUrlSimilar( String eventGroupUrl, String eventUrl )
	{
		eventGroupUrl = eventGroupUrl.replace( "/index.html", "" );
		if ( eventUrl.contains( eventGroupUrl ) )
			return true;
		return false;
	}

	@Override
	public Map<String, Object> printJsonOutput( Map<String, Object> responseMap, List<EventGroup> eventGroups )
	{
		if ( eventGroups == null || eventGroups.isEmpty() )
		{
			return responseMap;
		}

		List<Map<String, Object>> eventGroupList = new ArrayList<Map<String, Object>>();

		for ( EventGroup eventGroup : eventGroups )
		{
			Map<String, Object> eventGroupMap = new LinkedHashMap<String, Object>();
			eventGroupMap.put( "id", eventGroup.getId() );
			eventGroupMap.put( "name", WordUtils.capitalize( eventGroup.getName() ) );
			if ( eventGroup.getNotation() != null )
				eventGroupMap.put( "abbr", eventGroup.getNotation() );
			eventGroupMap.put( "url", eventGroup.getDblpUrl() );
			if ( eventGroup.getDescription() != null )
				eventGroupMap.put( "description", eventGroup.getDescription() );
			eventGroupMap.put( "type", eventGroup.getPublicationType().toString().toLowerCase() );

			eventGroupMap.put( "isAdded", eventGroup.isAdded() );
			// TODO:Event
			// List<Event> events = eventGroup.getEvents();

			eventGroupList.add( eventGroupMap );
		}

		responseMap.put( "count", eventGroupList.size() );
		responseMap.put( "eventGroups", eventGroupList );

		return responseMap;
	}

}

package de.rwth.i9.palm.feature.user;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.helper.comparator.UserAuthorBookmarkByDateComparator;
import de.rwth.i9.palm.helper.comparator.UserCircleBookmarkByDateComparator;
import de.rwth.i9.palm.helper.comparator.UserEventGroupBookmarkByDateComparator;
import de.rwth.i9.palm.helper.comparator.UserPublicationBookmarkByDateComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserAuthorBookmark;
import de.rwth.i9.palm.model.UserCircleBookmark;
import de.rwth.i9.palm.model.UserEventGroupBookmark;
import de.rwth.i9.palm.model.UserPublicationBookmark;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class UserBookmarkImpl implements UserBookmark
{
	private final static Logger log = LoggerFactory.getLogger( UserBookmarkImpl.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getUserBookmark( String bookmarkType, User user )
	{
		// set response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// get researcher bookmark
		if ( bookmarkType.equals( "author" ) )
		{
			if ( user.getUserAuthorBookmarks() != null && !user.getUserAuthorBookmarks().isEmpty() )
			{
				List<Object> responseListAuthor = new ArrayList<Object>();

				List<UserAuthorBookmark> userAuthorBookmarks = new ArrayList<UserAuthorBookmark>();
				userAuthorBookmarks.addAll( user.getUserAuthorBookmarks() );
				Collections.sort( userAuthorBookmarks, new UserAuthorBookmarkByDateComparator() );

				for ( UserAuthorBookmark userAuthorBookmark : userAuthorBookmarks )
				{
					printJSONAuthorBookmark( responseListAuthor, userAuthorBookmark.getAuthor() );
				}
				responseMap.put( "researchers", responseListAuthor );
				responseMap.put( "count", responseListAuthor.size() );
			}
		}

		// get publication bookmark
		else if ( bookmarkType.equals( "publication" ) )
		{
			if ( user.getUserPublicationBookmarks() != null && !user.getUserPublicationBookmarks().isEmpty() )
			{
				// preparing data format
				DateFormat dateFormat = new SimpleDateFormat( "yyyy", Locale.ENGLISH );

				List<Object> responseListPublication = new ArrayList<Object>();

				List<UserPublicationBookmark> userPublicationBookmarks = new ArrayList<UserPublicationBookmark>();
				userPublicationBookmarks.addAll( user.getUserPublicationBookmarks() );
				Collections.sort( userPublicationBookmarks, new UserPublicationBookmarkByDateComparator() );

				for ( UserPublicationBookmark userPublicationBookmark : userPublicationBookmarks )
				{
					printJSONPublicationBookmark( responseListPublication, userPublicationBookmark.getPublication(), dateFormat );
				}
				responseMap.put( "publications", responseListPublication );
				responseMap.put( "count", responseListPublication.size() );
			}
		}

		// get circle bookmark
		else if ( bookmarkType.equals( "circle" ) )
		{
			if ( user.getUserCircleBookmarks() != null && !user.getUserCircleBookmarks().isEmpty() )
			{
				// preparing data format
				DateFormat dateFormat = new SimpleDateFormat( "dd/mm/yyyy", Locale.ENGLISH );

				List<Object> responseListCircle = new ArrayList<Object>();

				List<UserCircleBookmark> userCircleBookmarks = new ArrayList<UserCircleBookmark>();
				userCircleBookmarks.addAll( user.getUserCircleBookmarks() );
				Collections.sort( userCircleBookmarks, new UserCircleBookmarkByDateComparator() );

				for ( UserCircleBookmark userCircleBookmark : userCircleBookmarks )
				{
					printJSONCircleBookmark( responseListCircle, userCircleBookmark.getCircle(), dateFormat );
				}
				responseMap.put( "circles", responseListCircle );
				responseMap.put( "count", responseListCircle.size() );
			}
		}

		// get eventGroup bookmark
		else if ( bookmarkType.equals( "eventGroup" ) )
		{
			if ( user.getUserEventGroupBookmarks() != null && !user.getUserEventGroupBookmarks().isEmpty() )
			{
				List<Object> responseListEventGroup = new ArrayList<Object>();

				List<UserEventGroupBookmark> userEventGroupBookmarks = new ArrayList<UserEventGroupBookmark>();
				userEventGroupBookmarks.addAll( user.getUserEventGroupBookmarks() );
				Collections.sort( userEventGroupBookmarks, new UserEventGroupBookmarkByDateComparator() );

				for ( UserEventGroupBookmark userEventGroupBookmark : userEventGroupBookmarks )
				{
					printJSONEventGroupBookmark( responseListEventGroup, userEventGroupBookmark.getEventGroup() );
				}
				responseMap.put( "eventGroups", responseListEventGroup );
				responseMap.put( "count", responseListEventGroup.size() );
			}
		}

		responseMap.put( "status", "ok" );
		return responseMap;
	}

	/**
	 * Generate JSON for researchers
	 * 
	 * @param responseListAuthor
	 * @param researcher
	 */
	private void printJSONAuthorBookmark( List<Object> responseListAuthor, Author researcher )
	{
		Map<String, Object> researcherMap = new LinkedHashMap<String, Object>();
		researcherMap.put( "id", researcher.getId() );
		researcherMap.put( "name", WordUtils.capitalize( researcher.getName() ) );
		if ( researcher.getPhotoUrl() != null )
			researcherMap.put( "photo", researcher.getPhotoUrl() );
		if ( researcher.getAcademicStatus() != null )
			researcherMap.put( "status", researcher.getAcademicStatus() );
		if ( researcher.getInstitution() != null )
			researcherMap.put( "aff", researcher.getInstitution().getName() );
		if ( researcher.getCitedBy() > 0 )
			researcherMap.put( "citedBy", Integer.toString( researcher.getCitedBy() ) );

		if ( researcher.getPublicationAuthors() != null )
			researcherMap.put( "publicationsNumber", researcher.getNoPublication() );
		else
			researcherMap.put( "publicationsNumber", 0 );
		String otherDetail = "";
		if ( researcher.getOtherDetail() != null )
			otherDetail += researcher.getOtherDetail();
		if ( researcher.getDepartment() != null )
			otherDetail += ", " + researcher.getDepartment();
		if ( !otherDetail.equals( "" ) )
			researcherMap.put( "detail", otherDetail );

		researcherMap.put( "isAdded", researcher.isAdded() );

		responseListAuthor.add( researcherMap );
	}

	/**
	 * Generate JSON for publication
	 * 
	 * @param responseListPublication
	 * @param researcher
	 */
	private void printJSONPublicationBookmark( List<Object> responseListPublication, Publication publication, DateFormat dateFormat )
	{
		Map<String, Object> publicationMap = new LinkedHashMap<String, Object>();
		publicationMap.put( "id", publication.getId() );

		if ( publication.getPublicationType() != null )
		{
			String publicationType = publication.getPublicationType().toString();
			publicationType = publicationType.substring( 0, 1 ).toUpperCase() + publicationType.toLowerCase().substring( 1 );
			publicationMap.put( "type", publicationType );
		}

		publicationMap.put( "title", publication.getTitle() );
		if ( publication.getCitedBy() > 0 )
			publicationMap.put( "cited", Integer.toString( publication.getCitedBy() ) );

		if ( publication.getPublicationDate() != null )
			publicationMap.put( "date published", dateFormat.format( publication.getPublicationDate() ) );
		List<Object> authorObject = new ArrayList<Object>();

		for ( Author author : publication.getCoAuthors() )
		{
			Map<String, Object> authorMap = new LinkedHashMap<String, Object>();
			authorMap.put( "id", author.getId() );
			authorMap.put( "name", WordUtils.capitalize( author.getName() ) );
			if ( author.getInstitution() != null )
				authorMap.put( "aff", author.getInstitution().getName() );
			// if ( author.getPhotoUrl() != null )
			// authorMap.put( "photo", author.getPhotoUrl() );

			authorMap.put( "isAdded", author.isAdded() );

			authorObject.add( authorMap );
		}
		publicationMap.put( "authors", authorObject );

		if ( publication.getPublicationDate() != null )
		{
			SimpleDateFormat sdf = new SimpleDateFormat( publication.getPublicationDateFormat() );
			publicationMap.put( "date", sdf.format( publication.getPublicationDate() ) );
		}

		if ( publication.getLanguage() != null )
			publicationMap.put( "language", publication.getLanguage() );

		if ( publication.getCitedBy() != 0 )
			publicationMap.put( "cited", publication.getCitedBy() );

		if ( publication.getEvent() != null )
		{
			Map<String, Object> eventMap = new LinkedHashMap<String, Object>();
			eventMap.put( "id", publication.getEvent().getId() );
			String eventName = publication.getEvent().getEventGroup().getName();
			if ( publication.getEvent().getEventGroup().getNotation() != null )
				if ( !publication.getEvent().getEventGroup().getNotation().equals( eventName ) )
					eventName += " - " + publication.getEvent().getEventGroup().getNotation() + ",";
			eventMap.put( "name", eventName );
			eventMap.put( "isAdded", publication.getEvent().isAdded() );
			publicationMap.put( "event", eventMap );
		}

		if ( publication.getAdditionalInformation() != null )
			publicationMap.putAll( publication.getAdditionalInformationAsMap() );

		if ( publication.getStartPage() > 0 )
			publicationMap.put( "pages", publication.getStartPage() + " - " + publication.getEndPage() );

		responseListPublication.add( publicationMap );
	}

	/**
	 * Generate JSON for circle
	 * 
	 * @param responseListCircle
	 * @param circle
	 * @param dateFormat
	 */
	private void printJSONCircleBookmark( List<Object> responseListCircle, Circle circle, DateFormat dateFormat )
	{
		Map<String, Object> circleMap = new LinkedHashMap<String, Object>();
		circleMap.put( "id", circle.getId() );

		circleMap.put( "name", circle.getName() );
		circleMap.put( "dateCreated", dateFormat.format( circle.getCreationDate() ) );

		if ( circle.getCreator() != null )
		{
			Map<String, Object> creatorMap = new LinkedHashMap<String, Object>();
			creatorMap.put( "id", circle.getCreator().getId() );
			creatorMap.put( "name", WordUtils.capitalize( circle.getCreator().getName() ) );
			if ( circle.getCreator().getAuthor() != null )
				creatorMap.put( "authorId", circle.getCreator().getAuthor().getId() );

			circleMap.put( "creator", creatorMap );
		}

		if ( circle.getAuthors() != null )
			circleMap.put( "numberAuthors", circle.getAuthors().size() );
		else
			circleMap.put( "numberAuthors", 0 );

		if ( circle.getPublications() != null )
			circleMap.put( "numberPublications", circle.getPublications().size() );
		else
			circleMap.put( "numberPublications", 0 );

		if ( circle.getDescription() != null && !circle.getDescription().equals( "" ) )
			circleMap.put( "description", circle.getDescription() );

		// check autowired with security service here
		circleMap.put( "isLock", circle.isLock() );
		circleMap.put( "isValid", circle.isValid() );

		responseListCircle.add( circleMap );
	}

	/**
	 * Generate JSON for EventGroup
	 * 
	 * @param responseListEventGroup
	 * @param eventGroup
	 */
	private void printJSONEventGroupBookmark( List<Object> responseListEventGroup, EventGroup eventGroup )
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

		responseListEventGroup.add( eventGroupMap );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> addUserBookmark( String bookmarkType, String userId, String bookId )
	{
		// set response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		User user = persistenceStrategy.getUserDAO().getById( userId );

		if ( user == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "error - user not found" );
			return responseMap;
		}

		// saving researcher bookmark
		if ( bookmarkType.equals( "author" ) )
		{
			Author author = persistenceStrategy.getAuthorDAO().getById( bookId );

			if ( author == null )
			{
				responseMap.put( "status", "error" );
				responseMap.put( "statusMessage", "error - author not found" );
				return responseMap;
			}
			UserAuthorBookmark userAuthorBookmark = new UserAuthorBookmark();
			userAuthorBookmark.setUser( user );
			userAuthorBookmark.setAuthor( author );

			// get current timestamp
			java.util.Date date = new java.util.Date();
			Timestamp currentTimestamp = new Timestamp( date.getTime() );

			userAuthorBookmark.setBookedDate( currentTimestamp );

			user.addUserAuthorBookmark( userAuthorBookmark );
			persistenceStrategy.getUserDAO().persist( user );
		}

		// saving publication bookmark
		else if ( bookmarkType.equals( "publication" ) )
		{
			Publication publication = persistenceStrategy.getPublicationDAO().getById( bookId );

			if ( publication == null )
			{
				responseMap.put( "status", "error" );
				responseMap.put( "statusMessage", "error - publication not found" );
				return responseMap;
			}
			UserPublicationBookmark userPublicationBookmark = new UserPublicationBookmark();
			userPublicationBookmark.setUser( user );
			userPublicationBookmark.setPublication( publication );

			// get current timestamp
			java.util.Date date = new java.util.Date();
			Timestamp currentTimestamp = new Timestamp( date.getTime() );

			userPublicationBookmark.setBookedDate( currentTimestamp );

			user.addUserPublicationBookmark( userPublicationBookmark );
			persistenceStrategy.getUserDAO().persist( user );
		}

		// saving circle bookmark
		else if ( bookmarkType.equals( "circle" ) )
		{
			Circle circle = persistenceStrategy.getCircleDAO().getById( bookId );

			if ( circle == null )
			{
				responseMap.put( "status", "error" );
				responseMap.put( "statusMessage", "error - circle not found" );
				return responseMap;
			}
			UserCircleBookmark userCircleBookmark = new UserCircleBookmark();
			userCircleBookmark.setUser( user );
			userCircleBookmark.setCircle( circle );

			// get current timestamp
			java.util.Date date = new java.util.Date();
			Timestamp currentTimestamp = new Timestamp( date.getTime() );

			userCircleBookmark.setBookedDate( currentTimestamp );

			user.addUserCircleBookmark( userCircleBookmark );
			persistenceStrategy.getUserDAO().persist( user );
		}

		// saving eventGroup bookmark
		else if ( bookmarkType.equals( "eventGroup" ) )
		{
			EventGroup eventGroup = persistenceStrategy.getEventGroupDAO().getById( bookId );

			if ( eventGroup == null )
			{
				responseMap.put( "status", "error" );
				responseMap.put( "statusMessage", "error - eventGroup not found" );
				return responseMap;
			}
			UserEventGroupBookmark userEventGroupBookmark = new UserEventGroupBookmark();
			userEventGroupBookmark.setUser( user );
			userEventGroupBookmark.setEventGroup( eventGroup );

			// get current timestamp
			java.util.Date date = new java.util.Date();
			Timestamp currentTimestamp = new Timestamp( date.getTime() );

			userEventGroupBookmark.setBookedDate( currentTimestamp );

			user.addUserEventGroupBookmark( userEventGroupBookmark );
			persistenceStrategy.getUserDAO().persist( user );
		}

		responseMap.put( "status", "ok" );
		return responseMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> removeUserBookmark( String bookmarkType, String userId, String bookId )
	{
		// set response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		User user = persistenceStrategy.getUserDAO().getById( userId );

		if ( user == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "error - user not found" );
			return responseMap;
		}

		// remove bookmark researcher
		if ( bookmarkType.equals( "author" ) )
		{
			Author author = persistenceStrategy.getAuthorDAO().getById( bookId );

			if ( author == null )
			{
				responseMap.put( "status", "error" );
				responseMap.put( "statusMessage", "error - author not found" );
				return responseMap;
			}

			// get object and remove link
			UserAuthorBookmark userAuthorBookmark = persistenceStrategy.getUserAuthorBookmarkDAO().getByUserAndAuthor( user, author );
			user.removeUserAuthorBookmark( userAuthorBookmark );
			userAuthorBookmark.setUser( null );
			userAuthorBookmark.setAuthor( null );

			// update and store objects
			persistenceStrategy.getUserAuthorBookmarkDAO().delete( userAuthorBookmark );
			persistenceStrategy.getUserDAO().persist( user );
		}

		// remove bookmark publication
		else if ( bookmarkType.equals( "publication" ) )
		{
			Publication publication = persistenceStrategy.getPublicationDAO().getById( bookId );

			if ( publication == null )
			{
				responseMap.put( "status", "error" );
				responseMap.put( "statusMessage", "error - publication not found" );
				return responseMap;
			}

			// get object and remove link
			UserPublicationBookmark userPublicationBookmark = persistenceStrategy.getUserPublicationBookmarkDAO().getByUserAndPublication( user, publication );
			user.removeUserPublicationBookmark( userPublicationBookmark );
			userPublicationBookmark.setUser( null );
			userPublicationBookmark.setPublication( null );

			// update and store objects
			persistenceStrategy.getUserPublicationBookmarkDAO().delete( userPublicationBookmark );
			persistenceStrategy.getUserDAO().persist( user );
		}

		// remove bookmark circle
		else if ( bookmarkType.equals( "circle" ) )
		{
			Circle circle = persistenceStrategy.getCircleDAO().getById( bookId );

			if ( circle == null )
			{
				responseMap.put( "status", "error" );
				responseMap.put( "statusMessage", "error - circle not found" );
				return responseMap;
			}

			// get object and remove link
			UserCircleBookmark userCircleBookmark = persistenceStrategy.getUserCircleBookmarkDAO().getByUserAndCircle( user, circle );
			user.removeUserCircleBookmark( userCircleBookmark );
			userCircleBookmark.setUser( null );
			userCircleBookmark.setCircle( null );

			// update and store objects
			persistenceStrategy.getUserCircleBookmarkDAO().delete( userCircleBookmark );
			persistenceStrategy.getUserDAO().persist( user );
		}

		// remove bookmark eventGroup
		if ( bookmarkType.equals( "eventGroup" ) )
		{
			EventGroup eventGroup = persistenceStrategy.getEventGroupDAO().getById( bookId );

			if ( eventGroup == null )
			{
				responseMap.put( "status", "error" );
				responseMap.put( "statusMessage", "error - eventGroup not found" );
				return responseMap;
			}

			// get object and remove link
			UserEventGroupBookmark userEventGroupBookmark = persistenceStrategy.getUserEventGroupBookmarkDAO().getByUserAndEventGroup( user, eventGroup );
			user.removeUserEventGroupBookmark( userEventGroupBookmark );
			userEventGroupBookmark.setUser( null );
			userEventGroupBookmark.setEventGroup( null );

			// update and store objects
			persistenceStrategy.getUserEventGroupBookmarkDAO().delete( userEventGroupBookmark );
			persistenceStrategy.getUserDAO().persist( user );
		}

		responseMap.put( "status", "ok" );
		return responseMap;
	}

}

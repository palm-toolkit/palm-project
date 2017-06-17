package de.rwth.i9.palm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserPublicationBookmark;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

/**
 * Service class which should provide methods for any affairs between modelviews
 * and back end
 * 
 * @author sigit
 */
@Service
public class TemplateService
{
	private final Logger LOGGER = LoggerFactory.getLogger( TemplateService.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Transactional
	public boolean isPublicationBooked( String userId, String publicationId )
	{
		User user = persistenceStrategy.getUserDAO().getById( userId );
		if ( user == null )
			return false;

		Publication publication = persistenceStrategy.getPublicationDAO().getById( publicationId );
		if ( publication == null )
			return false;

		UserPublicationBookmark upb = persistenceStrategy.getUserPublicationBookmarkDAO().getByUserAndPublication( user, publication );
		if ( upb != null )
			return true;

		return false;
	}

	@Transactional
	public boolean isWidgetActive( String uniqueWidgetName )
	{

		Widget widget = persistenceStrategy.getWidgetDAO().getByUniqueName( uniqueWidgetName );
		if ( widget == null )
			return false;

		if ( widget.getWidgetStatus().equals( WidgetStatus.NONACTIVE ) )
			return false;

		return true;
	}

	public String test()
	{
		return "test";
	}
}

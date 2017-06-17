package de.rwth.i9.palm.persistence;

import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserPublicationBookmark;

public interface UserPublicationBookmarkDAO extends GenericDAO<UserPublicationBookmark>, InstantiableDAO
{
	/**
	 * Get UserPublicationBookmark object given User and Publication
	 * 
	 * @param user
	 * @param publication
	 * @return
	 */
	UserPublicationBookmark getByUserAndPublication( User user, Publication publication );
}

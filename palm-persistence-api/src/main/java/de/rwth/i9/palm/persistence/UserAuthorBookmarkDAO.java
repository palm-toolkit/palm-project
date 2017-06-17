package de.rwth.i9.palm.persistence;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserAuthorBookmark;

public interface UserAuthorBookmarkDAO extends GenericDAO<UserAuthorBookmark>, InstantiableDAO
{
	/**
	 * Get UserAuthorBookmark object given User and Author
	 * 
	 * @param user
	 * @param publication
	 * @return
	 */
	UserAuthorBookmark getByUserAndAuthor( User user, Author publication );
}

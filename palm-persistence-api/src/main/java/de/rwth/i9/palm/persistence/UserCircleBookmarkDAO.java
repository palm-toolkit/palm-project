package de.rwth.i9.palm.persistence;

import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserCircleBookmark;

public interface UserCircleBookmarkDAO extends GenericDAO<UserCircleBookmark>, InstantiableDAO
{
	/**
	 * Get UserCircleBookmark object given User and Circle
	 * 
	 * @param user
	 * @param publication
	 * @return
	 */
	UserCircleBookmark getByUserAndCircle( User user, Circle publication );
}

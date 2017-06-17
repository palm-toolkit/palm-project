package de.rwth.i9.palm.persistence;

import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserEventGroupBookmark;

public interface UserEventGroupBookmarkDAO extends GenericDAO<UserEventGroupBookmark>, InstantiableDAO
{
	/**
	 * Get UserEventGroupBookmark object given User and EventGroup
	 * 
	 * @param user
	 * @param publication
	 * @return
	 */
	UserEventGroupBookmark getByUserAndEventGroup( User user, EventGroup publication );
}

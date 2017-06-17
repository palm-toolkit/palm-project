package de.rwth.i9.palm.feature.user;

import java.util.Map;

import de.rwth.i9.palm.model.User;

public interface UserBookmark
{
	/**
	 * Add user's bookmark for researcher, publication, conference and circle
	 * 
	 * @param bookmarkType
	 * @param userId
	 * @param bookId
	 * @return
	 */
	public Map<String, Object> addUserBookmark( String bookmarkType, String userId, String bookId );

	/**
	 * Remove user's bookmark for researcher, publication, conference and circle
	 * 
	 * @param bookmarkType
	 * @param userId
	 * @param bookId
	 * @return
	 */
	public Map<String, Object> removeUserBookmark( String bookmarkType, String userId, String bookId );

	/**
	 * Get user's bookmarks for researcher, publication, conference and circle
	 * 
	 * @param bookmarkType
	 * @param user
	 * @return
	 */
	public Map<String, Object> getUserBookmark( String bookmarkType, User user );
}

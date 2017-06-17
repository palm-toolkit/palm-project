package de.rwth.i9.palm.persistence;

import java.util.List;

/**
 * Generic interface for DAOs (Data Access Object), which defines some basic
 * methods to access objects of type <T>.

 * @param <T>
 */
public interface GenericDAO<T>
{

	/**
	 * Returns the object by <i>id</i>. With <i>id</id> is meant the technical
	 * id of an object by means of the physical data storage that is used. This
	 * <i>id</i> is not considered as the functional identifier of an object of
	 * type T. For instance, in case a relational database is used the id could
	 * be a uuid.
	 * 
	 * @param id
	 * @param lock
	 * @return
	 */
	T getById( String id );

	/**
	 * Returns a list of all objects.
	 * 
	 * @return
	 */
	List<T> getAll();

	/**
	 * Persists or updates <i>entity</i>.
	 * 
	 * @param entity
	 * @return
	 */
	T persist( T entity );

	/**
	 * Deletes <i>entity</i>.
	 * 
	 * @param entity
	 * @return
	 */
	boolean delete( T entity );
	
	void insert( T entity );

	int countTotal();

}
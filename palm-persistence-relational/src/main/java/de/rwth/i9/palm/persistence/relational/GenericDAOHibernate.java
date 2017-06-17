package de.rwth.i9.palm.persistence.relational;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.i9.palm.persistence.GenericDAO;

public abstract class GenericDAOHibernate<T> implements GenericDAO<T>
{

	private SessionFactory sessionFactory;
	private T persistenceClass;

	@SuppressWarnings( { "unchecked" } )
	public GenericDAOHibernate( SessionFactory sessionFactory )
	{
		if ( getClass().getGenericSuperclass() instanceof ParameterizedType )
			persistenceClass = (T) ( (ParameterizedType) getClass().getGenericSuperclass() ).getActualTypeArguments()[0];
		else if ( getClass().getSuperclass().getGenericSuperclass() instanceof ParameterizedType )
			persistenceClass = (T) ( (ParameterizedType) getClass().getSuperclass().getGenericSuperclass() ).getActualTypeArguments()[0];
		else
			persistenceClass = null;
		this.sessionFactory = sessionFactory;
	}

	protected Session getCurrentSession()
	{
		return sessionFactory.getCurrentSession();
	}

	public void setSessionFactory( SessionFactory sessionFactory )
	{
		this.sessionFactory = sessionFactory;
	}

	public T getPersistenceClass()
	{
		return persistenceClass;
	}

	@Transactional
	public T getById( String id )
	{
		@SuppressWarnings( "unchecked" )
		T entity = (T) sessionFactory.getCurrentSession().get( (Class<T>) getPersistenceClass(), id );
		return entity;

	}

	@SuppressWarnings( "unchecked" )
	@Transactional
	public List<T> getAll()
	{
		@SuppressWarnings( "rawtypes" )
		List allObjects = sessionFactory.getCurrentSession().createCriteria( (Class<T>) getPersistenceClass() ).list();
		if ( allObjects == null )
			return Collections.emptyList();
		else
			return allObjects;
	}

	@Transactional
	public T persist( T entity )
	{
		// if(entity instanceof PersistableResource)
		// log.debug((new
		// StringBuilder("Persisting entity with id:")).append(((PersistableResource)entity).getId()).append(" urn:").append(((PersistableResource)entity).getURN()).toString());
		sessionFactory.getCurrentSession().saveOrUpdate( entity );
		return entity;

	}

	@Transactional
	public boolean delete( T entity )
	{
		sessionFactory.getCurrentSession().delete( entity );
		return true;
	}

	@Transactional
	public void insert( T entity )
	{
		sessionFactory.getCurrentSession().save( entity );
	}

	@SuppressWarnings( "unchecked" )
	@Transactional
	public int countTotal()
	{
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria( (Class<T>) getPersistenceClass() );
		criteria.setProjection( Projections.rowCount() );

		return ( (Long) criteria.uniqueResult() ).intValue();
	}

}
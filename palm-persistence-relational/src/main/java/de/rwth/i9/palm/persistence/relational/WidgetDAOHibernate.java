package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.InstantiableDAO;
import de.rwth.i9.palm.persistence.WidgetDAO;

public class WidgetDAOHibernate extends GenericDAOHibernate<Widget> implements WidgetDAO, InstantiableDAO
{

	public WidgetDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<Widget> getWidgetByWidgetType( WidgetType widgetType )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Widget " );
		queryString.append( "WHERE widgetType = :widgetType " );
		queryString.append( "ORDER BY position ASC" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );

		@SuppressWarnings( "unchecked" )
		List<Widget> widgets = query.list();

		if ( widgets == null || widgets.isEmpty() )
			return Collections.emptyList();

		return widgets;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<Widget> getActiveWidgetByWidgetType( WidgetType widgetType )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Widget " );
		queryString.append( "WHERE widgetType = :widgetType " );
		queryString.append( "AND widgetStatus = 'ACTIVE'" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );

		@SuppressWarnings( "unchecked" )
		List<Widget> widgets = query.list();

		if ( widgets == null || widgets.isEmpty() )
			return Collections.emptyList();

		return widgets;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<Widget> getActiveWidgetByWidgetTypeAndGroup( WidgetType widgetType, String widgetGroup )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Widget " );
		queryString.append( "WHERE widgetType = :widgetType " );
		queryString.append( "AND widgetGroup = :widgetGroup " );
		queryString.append( "AND widgetStatus = 'ACTIVE'" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );
		query.setParameter( "widgetGroup", widgetGroup );

		@SuppressWarnings( "unchecked" )
		List<Widget> widgets = query.list();

		if ( widgets == null || widgets.isEmpty() )
			return Collections.emptyList();

		return widgets;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<Widget> getWidget( WidgetType widgetType, WidgetStatus... widgetStatuses )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Widget " );
		queryString.append( "WHERE widgetType = :widgetType " );
		if ( widgetStatuses.length > 0 )
		{
			if ( widgetStatuses.length == 1 )
			{
				queryString.append( "AND widgetStatus = :widgetStatus0 " );
			}
			else
			{
				for ( int i = 0; i < widgetStatuses.length; i++ )
				{
					if ( i == 0 )
						queryString.append( "AND ( " );
					else
						queryString.append( "OR " );
					queryString.append( "widgetStatus = :widgetStatus" + i + " " );
				}
				queryString.append( ") " );
			}
		}
		queryString.append( "ORDER BY position ASC" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );
		if ( widgetStatuses.length > 0 )
			for ( int i = 0; i < widgetStatuses.length; i++ )
				query.setParameter( "widgetStatus" + i, widgetStatuses[i] );

		@SuppressWarnings( "unchecked" )
		List<Widget> widgets = query.list();

		if ( widgets == null || widgets.isEmpty() )
			return Collections.emptyList();

		return widgets;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<Widget> getWidget( WidgetType widgetType, String widgetGroup, WidgetStatus... widgetStatuses )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Widget " );
		queryString.append( "WHERE widgetType = :widgetType " );
		queryString.append( "AND widgetGroup = :widgetGroup " );
		if ( widgetStatuses.length > 0 )
		{
			if ( widgetStatuses.length == 1 )
			{
				queryString.append( "AND widgetStatus = :widgetStatus0 " );
			}
			else
			{
				for ( int i = 0; i < widgetStatuses.length; i++ )
				{
					if ( i == 0 )
						queryString.append( "AND ( " );
					else
						queryString.append( "OR " );
					queryString.append( "widgetStatus = :widgetStatus" + i + " " );
				}
				queryString.append( ") " );
			}
		}
		queryString.append( "ORDER BY position ASC" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );
		query.setParameter( "widgetGroup", widgetGroup );
		if ( widgetStatuses.length > 0 )
			for ( int i = 0; i < widgetStatuses.length; i++ )
				query.setParameter( "widgetStatus" + i, widgetStatuses[i] );

		@SuppressWarnings( "unchecked" )
		List<Widget> widgets = query.list();

		if ( widgets == null || widgets.isEmpty() )
			return Collections.emptyList();

		return widgets;
	}

	@Override
	public Widget getByUniqueName( String uniqueWidgetName )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Widget " );
		queryString.append( "WHERE uniqueName = :uniqueWidgetName " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "uniqueWidgetName", uniqueWidgetName );

		@SuppressWarnings( "unchecked" )
		List<Widget> widgets = query.list();

		if ( widgets == null || widgets.isEmpty() )
			return null;

		return widgets.get( 0 );
	}

	@Override
	public List<Widget> getAllWidgets()
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Widget " );

		Query query = getCurrentSession().createQuery( queryString.toString() );

		@SuppressWarnings( "unchecked" )
		List<Widget> widgets = query.list();

		if ( widgets == null || widgets.isEmpty() )
			return Collections.emptyList();

		return widgets;
	}

}

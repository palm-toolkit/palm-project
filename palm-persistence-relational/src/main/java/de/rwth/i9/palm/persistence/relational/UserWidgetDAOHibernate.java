package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserWidget;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.InstantiableDAO;
import de.rwth.i9.palm.persistence.UserWidgetDAO;

public class UserWidgetDAOHibernate extends GenericDAOHibernate<UserWidget> implements UserWidgetDAO, InstantiableDAO
{

	public UserWidgetDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<UserWidget> getWidget( User user, WidgetType widgetType, WidgetStatus... widgetStatuses )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT uw " );
		queryString.append( "FROM UserWidget uw " );
		queryString.append( "JOIN uw.widget w " );
		queryString.append( "WHERE w.widgetType = :widgetType " );
		queryString.append( "AND uw.user = :user " );
		queryString.append( "AND ( w.widgetStatus = :wwidgetStatus1 OR w.widgetStatus = :wwidgetStatus2 ) " );
		if ( widgetStatuses.length > 0 )
		{
			if ( widgetStatuses.length == 1 )
			{
				queryString.append( "AND uw.widgetStatus = :widgetStatus0 " );
			}
			else
			{
				for ( int i = 0; i < widgetStatuses.length; i++ )
				{
					if ( i == 0 )
						queryString.append( "AND ( " );
					else
						queryString.append( "OR " );
					queryString.append( "uw.widgetStatus = :widgetStatus" + i + " " );
				}
				queryString.append( ") " );
			}
		}
		queryString.append( "ORDER BY uw.position ASC" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );
		query.setParameter( "user", user );
		// only include widget with DEFAULT and ACTIVE from original Widget
		// entity
		query.setParameter( "wwidgetStatus1", WidgetStatus.DEFAULT );
		query.setParameter( "wwidgetStatus2", WidgetStatus.ACTIVE );

		if ( widgetStatuses.length > 0 )
			for ( int i = 0; i < widgetStatuses.length; i++ )
				query.setParameter( "widgetStatus" + i, widgetStatuses[i] );

		@SuppressWarnings( "unchecked" )
		List<UserWidget> userWidgets = query.list();

		if ( userWidgets == null || userWidgets.isEmpty() )
			return Collections.emptyList();

		return userWidgets;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<UserWidget> getWidget( User user, WidgetType widgetType, String widgetGroup, WidgetStatus... widgetStatuses )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT uw " );
		queryString.append( "FROM UserWidget uw " );
		queryString.append( "JOIN uw.widget w " );
		queryString.append( "WHERE w.widgetType = :widgetType " );
		queryString.append( "AND w.widgetGroup = :widgetGroup " );
		queryString.append( "AND uw.user = :user " );
		queryString.append( "AND ( w.widgetStatus = :wwidgetStatus1 OR w.widgetStatus = :wwidgetStatus2 ) " );
		if ( widgetStatuses.length > 0 )
		{
			if ( widgetStatuses.length == 1 )
			{
				queryString.append( "AND uw.widgetStatus = :widgetStatus0 " );
			}
			else
			{
				for ( int i = 0; i < widgetStatuses.length; i++ )
				{
					if ( i == 0 )
						queryString.append( "AND ( " );
					else
						queryString.append( "OR " );
					queryString.append( "uw.widgetStatus = :widgetStatus" + i + " " );
				}
				queryString.append( ") " );
			}
		}
		queryString.append( "ORDER BY uw.position ASC" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );
		query.setParameter( "widgetGroup", widgetGroup );
		query.setParameter( "user", user );
		// only include widget with DEFAULT and ACTIVE from original Widget
		// entity
		query.setParameter( "wwidgetStatus1", WidgetStatus.DEFAULT );
		query.setParameter( "wwidgetStatus2", WidgetStatus.ACTIVE );

		if ( widgetStatuses.length > 0 )
			for ( int i = 0; i < widgetStatuses.length; i++ )
				query.setParameter( "widgetStatus" + i, widgetStatuses[i] );

		@SuppressWarnings( "unchecked" )
		List<UserWidget> userWidgets = query.list();

		if ( userWidgets == null || userWidgets.isEmpty() )
			return Collections.emptyList();

		return userWidgets;
	}

	@Override
	public List<UserWidget> getWidgetByColor( User user, WidgetType widgetType, WidgetStatus widgetStatus )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT uw " );
		queryString.append( "FROM UserWidget uw " );
		queryString.append( "JOIN uw.widget w " );
		queryString.append( "WHERE w.widgetType = :widgetType " );
		queryString.append( "AND uw.user = :user " );
		queryString.append( "AND uw.widgetStatus = :widgetStatus " );
		queryString.append( "ORDER BY uw.widgetColor DESC" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );
		query.setParameter( "user", user );
		query.setParameter( "widgetStatus", widgetStatus );

		@SuppressWarnings( "unchecked" )
		List<UserWidget> userWidgets = query.list();

		if ( userWidgets == null || userWidgets.isEmpty() )
			return Collections.emptyList();

		return userWidgets;
	}

}

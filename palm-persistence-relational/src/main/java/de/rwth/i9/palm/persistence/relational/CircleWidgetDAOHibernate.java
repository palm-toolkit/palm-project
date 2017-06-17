package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.CircleWidget;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.model.WidgetType;
import de.rwth.i9.palm.persistence.CircleWidgetDAO;
import de.rwth.i9.palm.persistence.InstantiableDAO;

public class CircleWidgetDAOHibernate extends GenericDAOHibernate<CircleWidget> implements CircleWidgetDAO, InstantiableDAO
{

	public CircleWidgetDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<CircleWidget> getWidget( Circle circle, WidgetType widgetType, WidgetStatus... widgetStatuses )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT cw " );
		queryString.append( "FROM CircleWidget cw " );
		queryString.append( "JOIN cw.widget w " );
		queryString.append( "WHERE w.widgetType = :widgetType " );
		queryString.append( "AND cw.circle = :circle " );
		queryString.append( "AND ( w.widgetStatus = :wwidgetStatus1 OR w.widgetStatus = :wwidgetStatus2 ) " );
		if ( widgetStatuses.length > 0 )
		{
			if ( widgetStatuses.length == 1 )
			{
				queryString.append( "AND cw.widgetStatus = :widgetStatus0 " );
			}
			else
			{
				for ( int i = 0; i < widgetStatuses.length; i++ )
				{
					if ( i == 0 )
						queryString.append( "AND ( " );
					else
						queryString.append( "OR " );
					queryString.append( "cw.widgetStatus = :widgetStatus" + i + " " );
				}
				queryString.append( ") " );
			}
		}
		queryString.append( "ORDER BY cw.position ASC" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );
		query.setParameter( "circle", circle );
		// only include widget with DEFAULT and ACTIVE from original Widget
		// entity
		query.setParameter( "wwidgetStatus1", WidgetStatus.DEFAULT );
		query.setParameter( "wwidgetStatus2", WidgetStatus.ACTIVE );

		if ( widgetStatuses.length > 0 )
			for ( int i = 0; i < widgetStatuses.length; i++ )
				query.setParameter( "widgetStatus" + i, widgetStatuses[i] );

		@SuppressWarnings( "unchecked" )
		List<CircleWidget> circleWidgets = query.list();

		if ( circleWidgets == null || circleWidgets.isEmpty() )
			return Collections.emptyList();

		return circleWidgets;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<CircleWidget> getWidget( Circle circle, WidgetType widgetType, String widgetGroup, WidgetStatus... widgetStatuses )
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append( "SELECT cw " );
		queryString.append( "FROM CircleWidget cw " );
		queryString.append( "JOIN cw.widget w " );
		queryString.append( "WHERE w.widgetType = :widgetType " );
		queryString.append( "AND w.widgetGroup = :widgetGroup " );
		queryString.append( "AND cw.circle = :circle " );
		queryString.append( "AND ( w.widgetStatus = :wwidgetStatus1 OR w.widgetStatus = :wwidgetStatus2 ) " );
		if ( widgetStatuses.length > 0 )
		{
			if ( widgetStatuses.length == 1 )
			{
				queryString.append( "AND cw.widgetStatus = :widgetStatus0 " );
			}
			else
			{
				for ( int i = 0; i < widgetStatuses.length; i++ )
				{
					if ( i == 0 )
						queryString.append( "AND ( " );
					else
						queryString.append( "OR " );
					queryString.append( "cw.widgetStatus = :widgetStatus" + i + " " );
				}
				queryString.append( ") " );
			}
		}
		queryString.append( "ORDER BY cw.position ASC" );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "widgetType", widgetType );
		query.setParameter( "widgetGroup", widgetGroup );
		query.setParameter( "circle", circle );
		// only include widget with DEFAULT and ACTIVE from original Widget
		// entity
		query.setParameter( "wwidgetStatus1", WidgetStatus.DEFAULT );
		query.setParameter( "wwidgetStatus2", WidgetStatus.ACTIVE );

		if ( widgetStatuses.length > 0 )
			for ( int i = 0; i < widgetStatuses.length; i++ )
				query.setParameter( "widgetStatus" + i, widgetStatuses[i] );

		@SuppressWarnings( "unchecked" )
		List<CircleWidget> circleWidgets = query.list();

		if ( circleWidgets == null || circleWidgets.isEmpty() )
			return Collections.emptyList();

		return circleWidgets;
	}

}

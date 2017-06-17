package de.rwth.i9.palm.persistence;

import java.util.List;

import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.model.WidgetType;

public interface WidgetDAO extends GenericDAO<Widget>, InstantiableDAO
{

	/**
	 * Get list of specific widgets by their type
	 * 
	 * @param widgetType
	 * @return
	 */
	List<Widget> getWidgetByWidgetType( WidgetType widgetType );

	/**
	 * Get list of specific of active widgets by their type
	 * 
	 * @param widgetType
	 * @return
	 */
	List<Widget> getActiveWidgetByWidgetType( WidgetType widgetType );

	/**
	 * Get list of specific widgets by their type and group
	 * 
	 * @param widgetType
	 * @return
	 */
	List<Widget> getActiveWidgetByWidgetTypeAndGroup( WidgetType widgetType, String widgetGroup );

	/**
	 * Get list of specific widgets by their type and status (DEFAULT, ACTIVE,
	 * etc)
	 * 
	 * @param widgetType
	 * @param widgetStatuses
	 * @return
	 */
	List<Widget> getWidget( WidgetType widgetType, WidgetStatus... widgetStatuses );

	/**
	 * Get list of specific widgets by their type, group and status (DEFAULT,
	 * ACTIVE, etc)
	 * 
	 * @param widgetType
	 * @param widgetGroup
	 * @param widgetStatuses
	 * @return
	 */
	List<Widget> getWidget( WidgetType widgetType, String widgetGroup, WidgetStatus... widgetStatuses );

	/**
	 * Get widget by unique name
	 * 
	 * @param uniqueWidgetName
	 * @return
	 */
	public Widget getByUniqueName( String uniqueWidgetName );

	/**
	 * Get all widgets
	 * 
	 * @param uniqueWidgetName
	 * @return
	 */
	public List<Widget> getAllWidgets();
}

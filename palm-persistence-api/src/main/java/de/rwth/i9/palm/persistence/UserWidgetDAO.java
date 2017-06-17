package de.rwth.i9.palm.persistence;

import java.util.List;

import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserWidget;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.model.WidgetType;

public interface UserWidgetDAO extends GenericDAO<UserWidget>, InstantiableDAO
{

	/**
	 * Get list of specific widgets by their type and status (DEFAULT, ACTIVE,
	 * etc)
	 * 
	 * @param widgetType
	 * @param widgetStatuses
	 * @return
	 */
	List<UserWidget> getWidget( User user, WidgetType widgetType, WidgetStatus... widgetStatuses );

	/**
	 * Get list of specific widgets by their type, group and status (DEFAULT,
	 * ACTIVE, etc)
	 * 
	 * @param widgetType
	 * @param widgetGroup
	 * @param widgetStatuses
	 * @return
	 */
	List<UserWidget> getWidget( User user, WidgetType widgetType, String widgetGroup, WidgetStatus... widgetStatuses );

	/**
	 * Get list of specific widgets by their type, group and status (DEFAULT,
	 * ACTIVE, etc)
	 * 
	 * @param widgetType
	 * @param widgetStatus
	 * @return
	 */
	List<UserWidget> getWidgetByColor( User user, WidgetType widgetType, WidgetStatus widgetStatus );
}

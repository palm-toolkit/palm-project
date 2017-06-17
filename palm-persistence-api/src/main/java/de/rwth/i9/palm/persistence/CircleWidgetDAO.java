package de.rwth.i9.palm.persistence;

import java.util.List;

import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.CircleWidget;
import de.rwth.i9.palm.model.WidgetStatus;
import de.rwth.i9.palm.model.WidgetType;

public interface CircleWidgetDAO extends GenericDAO<CircleWidget>, InstantiableDAO
{

	/**
	 * Get list of specific widgets by their type and status (DEFAULT, ACTIVE,
	 * etc)
	 * 
	 * @param widgetType
	 * @param widgetStatuses
	 * @return
	 */
	List<CircleWidget> getWidget( Circle circle, WidgetType widgetType, WidgetStatus... widgetStatuses );

	/**
	 * Get list of specific widgets by their type, group and status (DEFAULT,
	 * ACTIVE, etc)
	 * 
	 * @param widgetType
	 * @param widgetGroup
	 * @param widgetStatuses
	 * @return
	 */
	List<CircleWidget> getWidget( Circle circle, WidgetType widgetType, String widgetGroup, WidgetStatus... widgetStatuses );
}

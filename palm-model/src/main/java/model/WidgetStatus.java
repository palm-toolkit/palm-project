package de.rwth.i9.palm.model;

/**
 * <b>Documentation:</b>
 *	{@code NONACTIVE }
 *		The widget is obsolete / under development, thus it is not choosable by the users.
 *	{@code ACTIVE}
 *		The widget is ready and working properly, thus it is choosable by the users.
 *	{@code DEFAULT}
 *		The widget is ACTIVE and set as default widget for venue, researcher 
 *		and publication pages (even without user login).
 *	{@code UNAPPROVED}
 *		The widget is new and have not yet been approved by the administrations.
 *
 * @author sigit
 */
public enum WidgetStatus
{
	DEFAULT, ACTIVE, NONACTIVE, UNAPPROVED
}

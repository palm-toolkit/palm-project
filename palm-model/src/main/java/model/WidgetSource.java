package de.rwth.i9.palm.model;

/**
 * 
 * <b>Documentation:</b>
 * {@code INCLUDE}
 *		The widget content is from internal template file, included with Freemarker &lt;#include [FILE_NAME]&gt;
 * {@code AJAX}
 *		The widget content is from internal source and requested using ajax.
 * {@code EXTERNAL}
 *		The widget content is from external source, displayed using iFrame container
 * {@code BLANK}
 *		The widget content is blank 
 *
 * @author sigit 
 */
public enum WidgetSource
{
	INCLUDE, AJAX, EXTERNAL, BLANK
}

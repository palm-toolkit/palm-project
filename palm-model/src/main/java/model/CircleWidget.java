package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "circle_widget" )
public class CircleWidget extends PersistableResource
{
	@Column( name = "position_" )
	private int position;

	@Enumerated( EnumType.STRING )
	@Column( length = 16 )
	private WidgetStatus widgetStatus;

	@Enumerated( EnumType.STRING )
	@Column( length = 8 )
	private WidgetWidth widgetWidth;

	@Enumerated( EnumType.STRING )
	@Column( length = 16 )
	private Color widgetColor;

	@Column( length = 8 )
	private String widgetHeight;

	// relationships
	@ManyToOne
	@JoinColumn( name = "widget_id" )
	private Widget widget;

	@ManyToOne
	@JoinColumn( name = "circle_id" )
	private Circle circle;

	// getter / setter

	public int getPosition()
	{
		return position;
	}

	public void setPosition( int position )
	{
		this.position = position;
	}

	public WidgetStatus getWidgetStatus()
	{
		return widgetStatus;
	}

	public void setWidgetStatus( WidgetStatus widgetStatus )
	{
		this.widgetStatus = widgetStatus;
	}

	public String getWidgetHeight()
	{
		return widgetHeight;
	}

	public void setWidgetHeight( String widgetHeight )
	{
		this.widgetHeight = widgetHeight;
	}

	public Color getWidgetColor()
	{
		return widgetColor;
	}

	public void setWidgetColor( Color widgetColor )
	{
		this.widgetColor = widgetColor;
	}

	public Widget getWidget()
	{
		return widget;
	}

	public void setWidget( Widget widget )
	{
		this.widget = widget;
	}

	public WidgetWidth getWidgetWidth()
	{
		return widgetWidth;
	}

	public void setWidgetWidth( WidgetWidth widgetWidth )
	{
		this.widgetWidth = widgetWidth;
	}

	public Circle getCircle()
	{
		return circle;
	}

	public void setCircle( Circle circle )
	{
		this.circle = circle;
	}

}

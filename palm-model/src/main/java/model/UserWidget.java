package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "user_widget" )
public class UserWidget extends PersistableResource
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
	@ManyToOne( fetch = FetchType.EAGER )
	@JoinColumn( name = "widget_id" )
	private Widget widget;

	@ManyToOne
	@JoinColumn( name = "user_id" )
	private User user;

	// getter / setter

	public int getPosition()
	{
		return position;
	}

	public void setPosition( int position )
	{
		this.position = position;
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

	public String getWidgetHeight()
	{
		return widgetHeight;
	}

	public void setWidgetHeight( String widgetHeight )
	{
		this.widgetHeight = widgetHeight;
	}

	public WidgetStatus getWidgetStatus()
	{
		return widgetStatus;
	}

	public void setWidgetStatus( WidgetStatus widgetStatus )
	{
		this.widgetStatus = widgetStatus;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser( User user )
	{
		this.user = user;
	}

}

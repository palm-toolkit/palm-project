package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "widget" )
public class Widget extends PersistableResource
{

	@Column
	private String title;
	
	@Column( length = 50, unique = true )
	private String uniqueName;

	@Column( name = "position_", columnDefinition = "int default 0" )
	private int position;	

	@Enumerated( EnumType.STRING )
	@Column( length = 16 )
	private WidgetType widgetType;
	
	@Column
	private String widgetGroup;

	@Enumerated( EnumType.STRING )
	@Column( length = 16 )
	private WidgetSource widgetSource;

	@Enumerated( EnumType.STRING )
	@Column( length = 8 )
	private WidgetWidth widgetWidth;
	
	@Column( length = 8 )
	private String widgetHeight;

	@Enumerated( EnumType.STRING )
	@Column( length = 16 )
	private WidgetStatus widgetStatus;

	@Column
	@Lob
	private String sourcePath;

	@Column
	@Lob
	private String information;

	@Column( columnDefinition = "bit default 1" )
	private boolean closeEnabled = true;

	@Column( columnDefinition = "bit default 1" )
	private boolean minimizeEnabled = true;

	@Column( columnDefinition = "bit default 1" )
	private boolean resizeEnabled = true;

	@Column( columnDefinition = "bit default 1" )
	private boolean moveableEnabled = true;

	@Column( columnDefinition = "bit default 1" )
	private boolean colorEnabled = true;

	@Column( columnDefinition = "bit default 1" )
	private boolean headerVisible = true;

	@Enumerated( EnumType.STRING )
	@Column( length = 16 )
	private Color color;

	// not saved in database, act like form helper
	@Transient
	private int pos;

	@Transient
	private String width;

	@Transient
	private String height;

	@Transient
	private String status;

	// getter / setter

	public WidgetType getWidgetType()
	{
		return widgetType;
	}

	public void setWidgetType( WidgetType widgetType )
	{
		this.widgetType = widgetType;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle( String title )
	{
		this.title = title;
	}

	public String getSourcePath()
	{
		return this.sourcePath;
	}

	public void setSourcePath( String sourcePath )
	{
		this.sourcePath = sourcePath;
	}

	public String getInformation()
	{
		return information;
	}

	public void setInformation( String information )
	{
		this.information = information;
	}

	public boolean isCloseEnabled()
	{
		return closeEnabled;
	}

	public void setCloseEnabled( boolean closeEnabled )
	{
		this.closeEnabled = closeEnabled;
	}

	public boolean isMinimizeEnabled()
	{
		return minimizeEnabled;
	}

	public void setMinimizeEnabled( boolean minimizeEnabled )
	{
		this.minimizeEnabled = minimizeEnabled;
	}

	public WidgetWidth getWidgetWidth()
	{
		return widgetWidth;
	}

	public void setWidgetWidth( WidgetWidth widgetWidth )
	{
		this.widgetWidth = widgetWidth;
	}

	public String getWidgetGroup()
	{
		return widgetGroup;
	}

	public void setWidgetGroup( String widgetGroup )
	{
		this.widgetGroup = widgetGroup;
	}

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

	public WidgetSource getWidgetSource()
	{
		return widgetSource;
	}

	public void setWidgetSource( WidgetSource widgetSource )
	{
		this.widgetSource = widgetSource;
	}

	public boolean isResizeEnabled()
	{
		return resizeEnabled;
	}

	public void setResizeEnabled( boolean resizeEnabled )
	{
		this.resizeEnabled = resizeEnabled;
	}

	public boolean isMoveableEnabled()
	{
		return moveableEnabled;
	}

	public void setMoveableEnabled( boolean moveableEnabled )
	{
		this.moveableEnabled = moveableEnabled;
	}

	public boolean isColorEnabled()
	{
		return colorEnabled;
	}

	public void setColorEnabled( boolean colorEnabled )
	{
		this.colorEnabled = colorEnabled;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor( Color color )
	{
		this.color = color;
	}

	public boolean isHeaderVisible()
	{
		return headerVisible;
	}

	public void setHeaderVisible( boolean headerVisible )
	{
		this.headerVisible = headerVisible;
	}

	public String getUniqueName()
	{
		return uniqueName;
	}

	public void setUniqueName( String uniqueName )
	{
		this.uniqueName = uniqueName;
	}

	public String getWidgetHeight()
	{
		return widgetHeight;
	}

	public void setWidgetHeight( String widgetHeight )
	{
		this.widgetHeight = widgetHeight;
	}

	public String getWidth()
	{
		return width;
	}

	public void setWidth( String width )
	{
		this.width = width;
	}

	public String getHeight()
	{
		return height;
	}

	public void setHeight( String height )
	{
		this.height = height;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus( String status )
	{
		this.status = status;
	}

	public int getPos()
	{
		return pos;
	}

	public void setPos( int pos )
	{
		this.pos = pos;
	}

}

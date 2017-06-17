package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "config_property" )
public class ConfigProperty extends PersistableResource
{
	public ConfigProperty()
	{
	}

	public ConfigProperty( String mainIdentifier, String secondaryIdentifier, String value )
	{
		this.mainIdentifier = mainIdentifier;
		this.secondaryIdentifier = secondaryIdentifier;
		this.value = value;
	}

	@Column( length = 50 )
	private String mainIdentifier;

	@Column( length = 50 )
	private String secondaryIdentifier;

	@Column
	private String statement;

	@Column
	@Lob
	private String value;

	@Column
	private String defaultValue;

	@Column( name = "position_", columnDefinition = "int default 0" )
	private int position;

	@Column( length = 50 )
	private String groupName;

	@Column
	private java.sql.Timestamp lastModified;

	@Column( columnDefinition = "bit default 1" )
	private boolean valid = true;

	@Column( length = 20 )
	private String fieldType;

	@Column
	private String fieldOptions;

	// relation
	@ManyToOne
	@JoinColumn( name = "config_id" )
	private Config config;

	public String getMainIdentifier()
	{
		return mainIdentifier;
	}

	public void setMainIdentifier( String mainIdentifier )
	{
		this.mainIdentifier = mainIdentifier;
	}

	public String getSecondaryIdentifier()
	{
		return secondaryIdentifier;
	}

	public void setSecondaryIdentifier( String secondaryIdentifier )
	{
		this.secondaryIdentifier = secondaryIdentifier;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue( String value )
	{
		this.value = value;
	}

	public java.sql.Timestamp getLastModified()
	{
		return lastModified;
	}

	public void setLastModified( java.sql.Timestamp lastModified )
	{
		this.lastModified = lastModified;
	}

	public Config getConfig()
	{
		return config;
	}

	public void setConfig( Config config )
	{
		this.config = config;
	}

	public boolean isValid()
	{
		return valid;
	}

	public void setValid( boolean valid )
	{
		this.valid = valid;
	}

	public String getStatement()
	{
		return statement;
	}

	public void setStatement( String statement )
	{
		this.statement = statement;
	}

	public int getPosition()
	{
		return position;
	}

	public void setPosition( int position )
	{
		this.position = position;
	}

	public String getGroup()
	{
		return groupName;
	}

	public void setGroup( String groupName )
	{
		this.groupName = groupName;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue( String defaultValue )
	{
		this.defaultValue = defaultValue;
	}

	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName( String groupName )
	{
		this.groupName = groupName;
	}

	public String getFieldType()
	{
		return fieldType;
	}

	public void setFieldType( String fieldType )
	{
		this.fieldType = fieldType;
	}

	public String getFieldOptions()
	{
		return fieldOptions;
	}

	public void setFieldOptions( String fieldOptions )
	{
		this.fieldOptions = fieldOptions;
	}

}

package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "session_dataset" )
public class SessionDataSet extends PersistableResource
{
	@Column
	private String userName;

	@Column
	private String sessionName;

	public String getUserName()
	{
		return userName;
	}

	public String getSessionName()
	{
		return sessionName;
	}

	public void setSessionName( String sessionName )
	{
		this.sessionName = sessionName;
	}

	public void setUserName( String userName )
	{
		this.userName = userName;
	}
}

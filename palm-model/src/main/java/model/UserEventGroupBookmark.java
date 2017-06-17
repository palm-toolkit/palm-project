package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.search.annotations.Indexed;

import de.rwth.i9.palm.persistence.PersistableType;

@Entity
@Indexed
@Table( name = "user_eventGroup_bookmark" )
public class UserEventGroupBookmark extends PersistableType
{
	@ManyToOne
	@JoinColumn( name = "user_id" )
	private User user;

	@ManyToOne
	@JoinColumn( name = "eventGroup_id" )
	private EventGroup eventGroup;

	@Column
	private java.sql.Timestamp bookedDate;

	public User getUser()
	{
		return user;
	}

	public void setUser( User user )
	{
		this.user = user;
	}

	public EventGroup getEventGroup()
	{
		return eventGroup;
	}

	public void setEventGroup( EventGroup eventGroup )
	{
		this.eventGroup = eventGroup;
	}

	public java.sql.Timestamp getBookedDate()
	{
		return bookedDate;
	}

	public void setBookedDate( java.sql.Timestamp bookedDate )
	{
		this.bookedDate = bookedDate;
	}
}


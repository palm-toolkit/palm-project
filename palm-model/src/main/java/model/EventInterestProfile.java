package de.rwth.i9.palm.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "event_interest_profile" )
public class EventInterestProfile extends PersistableResource
{
	@Column( nullable = false )
	private String name;

	@Column
	private Date created;

	@Column( columnDefinition = "bit default 1" )
	private boolean valid = true;

	@Column
	@Lob
	private String description;

	// relation
	@ManyToOne
	@JoinColumn( name = "event_id" )
	private Event event;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "eventInterestProfile", orphanRemoval = true )
	Set<EventInterest> eventInterests;

	@ManyToOne
	@JoinColumn( name = "interest_profile_id" )
	private InterestProfileEvent interestProfileEvent;

	// getter & setter

	public EventInterestProfile addEventInterest( EventInterest eventInterest )
	{
		if ( this.eventInterests == null )
			eventInterests = new HashSet<EventInterest>();

		eventInterests.add( eventInterest );
		return this;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated( Date created )
	{
		this.created = created;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public Event getEvent()
	{
		return event;
	}

	public void setEvent( Event event )
	{
		this.event = event;
	}

	public Set<EventInterest> getEventInterests()
	{
		return eventInterests;
	}

	public void setEventInterests( Set<EventInterest> eventInterests )
	{
		if ( this.eventInterests == null )
			this.eventInterests = new HashSet<EventInterest>();
		this.eventInterests.clear();
		this.eventInterests.addAll( eventInterests );
	}

	public boolean isValid()
	{
		return valid;
	}

	public void setValid( boolean valid )
	{
		this.valid = valid;
	}

	public InterestProfileEvent getInterestProfileEvent()
	{
		return this.interestProfileEvent;
	}

	public void setInterestProfileEvent( InterestProfileEvent interestProfileEvent )
	{
		this.interestProfileEvent = interestProfileEvent;
	}

}

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
@Table( name = "circle_interest_profile" )
public class CircleInterestProfile extends PersistableResource
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
	@JoinColumn( name = "circle_id" )
	private Circle circle;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "circleInterestProfile", orphanRemoval = true )
	Set<CircleInterest> circleInterests;

	@ManyToOne
	@JoinColumn( name = "interest_profile_id" )
	private InterestProfileCircle interestProfileCircle;

	// getter & setter

	public CircleInterestProfile addCircleInterest( CircleInterest circleInterest )
	{
		if ( this.circleInterests == null )
			circleInterests = new HashSet<CircleInterest>();

		circleInterests.add( circleInterest );
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

	public Circle getCircle()
	{
		return circle;
	}

	public void setCircle( Circle circle )
	{
		this.circle = circle;
	}

	public Set<CircleInterest> getCircleInterests()
	{
		return circleInterests;
	}

	public void setCircleInterests( Set<CircleInterest> circleInterests )
	{
		if ( this.circleInterests == null )
			this.circleInterests = new HashSet<CircleInterest>();
		this.circleInterests.clear();
		this.circleInterests.addAll( circleInterests );
	}

	public boolean isValid()
	{
		return valid;
	}

	public void setValid( boolean valid )
	{
		this.valid = valid;
	}

	public InterestProfileCircle getInterestProfileCircle()
	{
		return this.interestProfileCircle;
	}

	public void setInterestProfileCircle( InterestProfileCircle interestProfileCircle )
	{
		this.interestProfileCircle = interestProfileCircle;
	}

}

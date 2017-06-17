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
@Table( name = "circle_topicmodel_profile" )
public class CircleTopicModelingProfile extends PersistableResource
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

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "circleTopicModelingProfile", orphanRemoval = true )
	Set<CircleTopicModeling> circleTopicModelings;

	@ManyToOne
	@JoinColumn( name = "topic_modeling_algorithm_circle_id" )
	private TopicModelingAlgorithmCircle topicModelingAlgorithmCircle;

	// getter & setter

	public CircleTopicModelingProfile addCircleTopicModeling( CircleTopicModeling circleTopicModeling )
	{
		if ( this.circleTopicModelings == null )
			circleTopicModelings = new HashSet<CircleTopicModeling>();

		circleTopicModelings.add( circleTopicModeling );
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

	public Set<CircleTopicModeling> getCircleTopicModelings()
	{
		return circleTopicModelings;
	}

	public void setCircleTopicModelings( Set<CircleTopicModeling> circleTopicModelings )
	{
		if ( this.circleTopicModelings == null )
			this.circleTopicModelings = new HashSet<CircleTopicModeling>();
		this.circleTopicModelings.clear();
		this.circleTopicModelings.addAll( circleTopicModelings );
	}

	public boolean isValid()
	{
		return valid;
	}

	public void setValid( boolean valid )
	{
		this.valid = valid;
	}

	public TopicModelingAlgorithmCircle getTopicModelingAlgorithmCircle()
	{
		return this.topicModelingAlgorithmCircle;
	}

	public void setTopicModelingAlgorithmCircle( TopicModelingAlgorithmCircle topicModelingAlgorithmCircle )
	{
		this.topicModelingAlgorithmCircle = topicModelingAlgorithmCircle;
	}

}

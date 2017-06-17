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
@Table( name = "publication_topicmodel_profile" )
public class PublicationTopicModelingProfile extends PersistableResource
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
	@JoinColumn( name = "publication_id" )
	private Publication publication;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "publicationTopicModelingProfile", orphanRemoval = true )
	Set<PublicationTopicModeling> publicationTopicModelings;

	@ManyToOne
	@JoinColumn( name = "topic_modeling_algorithm_publication_id" )
	private TopicModelingAlgorithmPublication topicModelingAlgorithmPublication;

	// getter & setter

	public PublicationTopicModelingProfile addPublicationTopicModeling( PublicationTopicModeling publicationTopicModeling )
	{
		if ( this.publicationTopicModelings == null )
			publicationTopicModelings = new HashSet<PublicationTopicModeling>();

		publicationTopicModelings.add( publicationTopicModeling );
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

	public Publication getPublication()
	{
		return publication;
	}

	public void setPublication( Publication publication )
	{
		this.publication = publication;
	}

	public Set<PublicationTopicModeling> getPublicationTopicModelings()
	{
		return publicationTopicModelings;
	}

	public void setPublicationTopicModelings( Set<PublicationTopicModeling> publicationTopicModelings )
	{
		if ( this.publicationTopicModelings == null )
			this.publicationTopicModelings = new HashSet<PublicationTopicModeling>();
		this.publicationTopicModelings.clear();
		this.publicationTopicModelings.addAll( publicationTopicModelings );
	}

	public boolean isValid()
	{
		return valid;
	}

	public void setValid( boolean valid )
	{
		this.valid = valid;
	}

	public TopicModelingAlgorithmPublication getTopicModelingAlgorithmPublication()
	{
		return this.topicModelingAlgorithmPublication;
	}

	public void setTopicModelingAlgorithmPublication( TopicModelingAlgorithmPublication topicModelingAlgorithmPublication )
	{
		this.topicModelingAlgorithmPublication = topicModelingAlgorithmPublication;
	}

}

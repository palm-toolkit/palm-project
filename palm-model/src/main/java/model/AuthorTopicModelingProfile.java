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
@Table( name = "author_topicmodel_profile" )
public class AuthorTopicModelingProfile extends PersistableResource
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
	@JoinColumn( name = "author_id" )
	private Author author;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "authorTopicModelingProfile", orphanRemoval = true )
	Set<AuthorTopicModeling> authorTopicModelings;

	@ManyToOne
	@JoinColumn( name = "topic_modeling_algorithm_author_id" )
	private TopicModelingAlgorithmAuthor topicModelingAlgorithmAuthor;

	// getter & setter

	public AuthorTopicModelingProfile addAuthorTopicModeling( AuthorTopicModeling authorTopicModeling )
	{
		if ( this.authorTopicModelings == null )
			authorTopicModelings = new HashSet<AuthorTopicModeling>();

		authorTopicModelings.add( authorTopicModeling );
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

	public Author getAuthor()
	{
		return author;
	}

	public void setAuthor( Author author )
	{
		this.author = author;
	}

	public Set<AuthorTopicModeling> getAuthorTopicModelings()
	{
		return authorTopicModelings;
	}

	public void setAuthorTopicModelings( Set<AuthorTopicModeling> authorTopicModelings )
	{
		if ( this.authorTopicModelings == null )
			this.authorTopicModelings = new HashSet<AuthorTopicModeling>();
		this.authorTopicModelings.clear();
		this.authorTopicModelings.addAll( authorTopicModelings );
	}

	public boolean isValid()
	{
		return valid;
	}

	public void setValid( boolean valid )
	{
		this.valid = valid;
	}

	public TopicModelingAlgorithmAuthor getTopicModelingAlgorithmAuthor()
	{
		return this.topicModelingAlgorithmAuthor;
	}

	public void setTopicModelingAlgorithmAuthor( TopicModelingAlgorithmAuthor topicModelingAlgorithmAuthor )
	{
		this.topicModelingAlgorithmAuthor = topicModelingAlgorithmAuthor;
	}

}

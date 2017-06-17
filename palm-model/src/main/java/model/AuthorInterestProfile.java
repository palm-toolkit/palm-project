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
@Table( name = "author_interest_profile" )
public class AuthorInterestProfile extends PersistableResource
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

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "authorInterestProfile", orphanRemoval = true )
	Set<AuthorInterest> authorInterests;

	@ManyToOne
	@JoinColumn( name = "interest_profile_id" )
	private InterestProfile interestProfile;

	// getter & setter

	public AuthorInterestProfile addAuthorInterest( AuthorInterest authorInterest )
	{
		if ( this.authorInterests == null )
			authorInterests = new HashSet<AuthorInterest>();

		authorInterests.add( authorInterest );
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

	public Set<AuthorInterest> getAuthorInterests()
	{
		return authorInterests;
	}

	public void setAuthorInterests( Set<AuthorInterest> authorInterests )
	{
		if ( this.authorInterests == null )
			this.authorInterests = new HashSet<AuthorInterest>();
		this.authorInterests.clear();
		this.authorInterests.addAll( authorInterests );
	}

	public boolean isValid()
	{
		return valid;
	}

	public void setValid( boolean valid )
	{
		this.valid = valid;
	}

	public InterestProfile getInterestProfile()
	{
		return interestProfile;
	}

	public void setInterestProfile( InterestProfile interestProfile )
	{
		this.interestProfile = interestProfile;
	}

}

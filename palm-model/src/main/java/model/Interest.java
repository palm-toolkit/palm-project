package de.rwth.i9.palm.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "interest" )
public class Interest extends PersistableResource
{
	@Column( unique = true, nullable = false, length = 50 )
	private String term;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "interest" )
	private Set<InterestAuthor> interestAuthors;

	public String getTerm()
	{
		return term;
	}

	public void setTerm( String term )
	{
		this.term = term;
	}

	public Set<InterestAuthor> getInterestAuthors()
	{
		return interestAuthors;
	}

	public void setInterestAuthors( Set<InterestAuthor> interestAuthors )
	{
		this.interestAuthors = interestAuthors;
	}

	public Interest addInterestAuthor( InterestAuthor interestAuthor )
	{
		if ( this.interestAuthors == null )
			this.interestAuthors = new HashSet<InterestAuthor>();
		this.interestAuthors.add( interestAuthor );

		return this;
	}
}

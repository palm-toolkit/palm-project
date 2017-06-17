package de.rwth.i9.palm.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "interest_profile" )
public class InterestProfile extends PersistableResource
{
	@Column( unique = true, nullable = false )
	private String name;

	@Column
	private Date created;

	@Column( columnDefinition = "bit default 1" )
	private boolean active = true;

	@Enumerated( EnumType.STRING )
	@Column( length = 16, nullable = false )
	private InterestProfileType interestProfileType;

	@Column
	@Lob
	private String description;

	@Column
	@Lob
	private String role;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "interestProfile", orphanRemoval = true )
	private List<InterestProfileProperty> interestProfileProperties;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "interestProfile", orphanRemoval = true )
	private Set<AuthorInterestProfile> authorInterestProfile;

	// getter & setter

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

	public boolean isActive()
	{
		return active;
	}

	public void setActive( boolean active )
	{
		this.active = active;
	}

	public InterestProfileType getInterestProfileType()
	{
		return interestProfileType;
	}

	public void setInterestProfileType( InterestProfileType interestProfileType )
	{
		this.interestProfileType = interestProfileType;
	}

	public String getRole()
	{
		return role;
	}

	public void setRole( String role )
	{
		this.role = role;
	}

	public List<InterestProfileProperty> getInterestProfileProperties()
	{
		return interestProfileProperties;
	}

	public void setInterestProfileProperties( List<InterestProfileProperty> interestProfileProperties )
	{
		this.interestProfileProperties = interestProfileProperties;
	}

	public Set<AuthorInterestProfile> getAuthorInterestProfile()
	{
		return authorInterestProfile;
	}

	public void setAuthorInterestProfile( Set<AuthorInterestProfile> authorInterestProfile )
	{
		this.authorInterestProfile = authorInterestProfile;
	}


}

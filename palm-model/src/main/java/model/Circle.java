package de.rwth.i9.palm.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "circle" )
@Indexed
public class Circle extends PersistableResource
{

	@Column( nullable = false )
	@Field( index = Index.YES, analyze = Analyze.NO, store = Store.YES )
	@Boost( 3.0f )
	private String name;

	@Column
	private java.sql.Timestamp creationDate;

	@ManyToOne( cascade = CascadeType.ALL )
	@JoinColumn( name = "creator_id" )
	private User creator;

	@Column
	@Lob
	@Field( index = Index.YES, analyze = Analyze.YES, store = Store.YES )
	@Analyzer( definition = "lowercaseSnowballAnalyzer" )
	private String description;

	@ManyToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	@JoinTable( name = "circle_author", joinColumns = @JoinColumn( name = "circle_id" ) , inverseJoinColumns = @JoinColumn( name = "author_id" ) )
	private Set<Author> authors;

	@ManyToMany( cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY )
	@JoinTable( name = "circle_publication", joinColumns = @JoinColumn( name = "circle_id" ) , inverseJoinColumns = @JoinColumn( name = "publication_id" ) )
	private Set<Publication> publications;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "circle", orphanRemoval = true )
	private List<CircleWidget> circleWidgets;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "circle", orphanRemoval = true )
	private Set<CircleInterestProfile> circleInterestProfiles;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author", orphanRemoval = true )
	private Set<AuthorTopicModelingProfile> authorTopicModelingProfiles;

	@Column( name = "_lock", columnDefinition = "bit default 1" )
	private boolean lock = true;

	@Column( columnDefinition = "bit default 1" )
	private boolean valid = true;

	@Column( columnDefinition = "bit default 0" )
	private boolean isUpdateInterest = false;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "circle", orphanRemoval = true )
	private Set<CircleTopicModelingProfile> circleTopicModelingProfiles;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "circle" )
	private Set<UserCircleBookmark> userCircleBookmarks;

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public java.sql.Timestamp getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate( java.sql.Timestamp creationDate )
	{
		this.creationDate = creationDate;
	}

	public User getCreator()
	{
		return creator;
	}

	public void setCreator( User creator )
	{
		this.creator = creator;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public Set<Author> getAuthors()
	{
		return authors;
	}

	public void setAuthors( Set<Author> authors )
	{
		if ( this.authors == null )
			this.authors = new HashSet<Author>();
		this.authors.clear();
		this.authors.addAll( authors );
	}

	public Circle addAuthor( Author author )
	{
		if ( this.authors == null )
			this.authors = new HashSet<Author>();

		this.authors.add( author );

		return this;
	}

	public Set<Publication> getPublications()
	{
		return publications;
	}

	public void setPublications( Set<Publication> publications )
	{
		if ( this.publications == null )
			this.publications = new HashSet<Publication>();
		this.publications.clear();
		this.publications.addAll( publications );
	}

	public Circle addPublication( Publication publication )
	{
		if ( this.publications == null )
			this.publications = new HashSet<Publication>();

		this.publications.add( publication );

		return this;
	}

	public Circle removePublication( Publication publication )
	{
		if ( this.publications == null || this.publications.isEmpty() )
			return this;

		for ( Iterator<Publication> i = this.publications.iterator(); i.hasNext(); )
		{
			Publication eachPublication = i.next();
			if ( eachPublication.equals( publication ) )
				i.remove();
		}
		return this;
	}

	public List<CircleWidget> getCircleWidgets()
	{
		return circleWidgets;
	}

	public void setCircleWidgets( List<CircleWidget> circleWidgets )
	{
		this.circleWidgets = circleWidgets;
	}

	public Circle addCircleWidget( CircleWidget circleWidget )
	{
		if ( this.circleWidgets == null )
			this.circleWidgets = new ArrayList<CircleWidget>();
		this.circleWidgets.add( circleWidget );
		return this;
	}

	public Circle addCircleWidgetList( List<CircleWidget> circleWidgets )
	{
		if ( this.circleWidgets == null )
			this.circleWidgets = new ArrayList<CircleWidget>();
		this.circleWidgets.addAll( circleWidgets );
		return this;
	}

	public Circle removeCircleWidget( CircleWidget circleWidget )
	{
		if ( this.circleWidgets == null || this.circleWidgets.isEmpty() )
			return this;

		for ( Iterator<CircleWidget> i = this.circleWidgets.iterator(); i.hasNext(); )
		{
			CircleWidget eachCircleWidget = i.next();
			if ( eachCircleWidget.equals( circleWidget ) )
				i.remove();
		}
		return this;
	}

	public boolean isLock()
	{
		return lock;
	}

	public void setLock( boolean lock )
	{
		this.lock = lock;
	}

	public boolean isValid()
	{
		return valid;
	}

	public void setValid( boolean valid )
	{
		this.valid = valid;
	}

	public Set<CircleInterestProfile> getCircleInterestProfiles()
	{
		return circleInterestProfiles;
	}

	public void setCircleInterestProfiles( Set<CircleInterestProfile> circleInterestProfiles )
	{
		this.circleInterestProfiles = circleInterestProfiles;
	}

	public Circle addCircleInterestProfiles( CircleInterestProfile circleInterestProfile )
	{
		if ( this.circleInterestProfiles == null )
			this.circleInterestProfiles = new LinkedHashSet<CircleInterestProfile>();

		this.circleInterestProfiles.add( circleInterestProfile );

		return this;
	}

	public CircleInterestProfile getSpecificCircleInterestProfile( String interestProfileName )
	{
		if ( interestProfileName == null || interestProfileName.equals( "" ) )
			return null;

		if ( this.circleInterestProfiles == null || this.circleInterestProfiles.isEmpty() )
			return null;

		for ( CircleInterestProfile aip : this.circleInterestProfiles )
		{
			if ( aip.getName().equals( interestProfileName ) )
			{
				return aip;
			}
		}

		return null;
	}

	public Set<CircleTopicModelingProfile> getCircleTopicModelingProfiles()
	{
		return circleTopicModelingProfiles;
	}

	public void setCircleTopicModelingProfiles( Set<CircleTopicModelingProfile> circleTopicModelingProfiles )
	{
		this.circleTopicModelingProfiles = circleTopicModelingProfiles;
	}

	public Circle addAuthorTopicModelingProfiles( CircleTopicModelingProfile circleTopicModelingProfile )
	{
		if ( this.circleTopicModelingProfiles == null )
			this.circleTopicModelingProfiles = new LinkedHashSet<CircleTopicModelingProfile>();

		this.circleTopicModelingProfiles.add( circleTopicModelingProfile );

		return this;
	}

	public boolean isUpdateInterest()
	{
		return isUpdateInterest;
	}

	public void setUpdateInterest( boolean isUpdateInterest )
	{
		this.isUpdateInterest = isUpdateInterest;
	}

	public Set<UserCircleBookmark> getUserCircleBookmarks()
	{
		return userCircleBookmarks;
	}

	public void setUserCircleBookmarks( Set<UserCircleBookmark> userCircleBookmarks )
	{
		this.userCircleBookmarks = userCircleBookmarks;
	}

	public Circle addCircleTopicModelingProfiles( CircleTopicModelingProfile circleTopicModelingProfile )
	{
		if ( this.circleTopicModelingProfiles == null )
			this.circleTopicModelingProfiles = new LinkedHashSet<CircleTopicModelingProfile>();

		this.circleTopicModelingProfiles.add( circleTopicModelingProfile );

		return this;
	}
}

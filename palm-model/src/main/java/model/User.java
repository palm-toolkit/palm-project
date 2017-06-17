package de.rwth.i9.palm.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "user" )
public class User extends PersistableResource
{
	@Column( length = 80 )
	private String name;

	@Column( unique = true, length = 80 )
	private String username;

	@Column( length = 80 )
	private String email;

	@Column
	private String password;

	@Column( columnDefinition = "bit default 1" )
	private boolean enabled = true;

	@Column
	private String academicStatus;

	@Column
	private String affiliation;

	@Column
	private Date lastLogin;

	@Column
	private Date lastLogout;

	@Column
	private Date joinDate;

	@Transient
	private String sessionId;

	// relations
	@ManyToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	@JoinTable( name = "user_function", joinColumns = @JoinColumn( name = "user_id" ), inverseJoinColumns = @JoinColumn( name = "function_id" ) )
	private List<Function> functions;

	@ManyToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
	@JoinColumn( name = "role_id" )
	private Role role;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	@JoinColumn( name = "user_id" )
	private List<UserWidget> userWidgets;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user" )
	private Set<UserAuthorBookmark> userAuthorBookmarks;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user" )
	private Set<UserCircleBookmark> userCircleBookmarks;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user" )
	private Set<UserEventGroupBookmark> userEventGroupBookmarks;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user" )
	private Set<UserPublicationBookmark> userPublicationBookmarks;

	/*
	 * Actually this is OneToOne connection,
	 * but it should be possible for users 
	 * to be linked with same author, since
	 * we couldn't check the users
	 */
	@ManyToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
	@JoinColumn( name = "author_id" )
	private Author author;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user" )
	private List<PublicationHistory> publicationHistories;

	// getter and setter

	public Author getAuthor()
	{
		return author;
	}

	public void setAuthor( Author author )
	{
		this.author = author;
	}

	public List<UserWidget> getUserWidgets()
	{
		return userWidgets;
	}

	public void setUserWidgets( List<UserWidget> userWidgets )
	{
		this.userWidgets = userWidgets;
	}

	public User addUserWidget( UserWidget userWidget )
	{
		if ( this.userWidgets == null )
			this.userWidgets = new ArrayList<UserWidget>();
		this.userWidgets.add( userWidget );
		return this;
	}

	public User addUserWidgetList( List<UserWidget> userWidgets )
	{
		if ( this.userWidgets == null )
			this.userWidgets = new ArrayList<UserWidget>();
		this.userWidgets.addAll( userWidgets );
		return this;
	}

	public User removeUserWidget( UserWidget userWidget )
	{
		if ( this.userWidgets == null || this.userWidgets.isEmpty() )
			return this;

		for ( Iterator<UserWidget> i = this.userWidgets.iterator(); i.hasNext(); )
		{
			UserWidget eachUserWidget = i.next();
			if ( eachUserWidget.equals( userWidget ) )
				i.remove();
		}
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

	public String getEmail()
	{
		return email;
	}

	public void setEmail( String email )
	{
		this.email = email;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername( String username )
	{
		this.username = username;
	}

	public Role getRole()
	{
		return role;
	}

	public void setRole( Role role )
	{
		this.role = role;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword( String password )
	{
		this.password = password;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled( boolean enabled )
	{
		this.enabled = enabled;
	}

	public Date getLastLogin()
	{
		return lastLogin;
	}

	public void setLastLogin( Date lastLogin )
	{
		this.lastLogin = lastLogin;
	}

	public Date getLastLogout()
	{
		return lastLogout;
	}

	public void setLastLogout( Date lastLogout )
	{
		this.lastLogout = lastLogout;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId( String sessionId )
	{
		this.sessionId = sessionId;
	}

	public List<Function> getFunctions()
	{
		return functions;
	}

	public User addFunction( final Function function )
	{
		if ( this.functions == null )
			this.functions = new ArrayList<Function>();

		this.functions.add( function );

		return this;
	}

	public void setFunctions( List<Function> functions )
	{
		this.functions = functions;
	}

	public List<PublicationHistory> getPublicationHistories()
	{
		return publicationHistories;
	}

	public void setPublicationHistories( List<PublicationHistory> publicationHistories )
	{
		this.publicationHistories = publicationHistories;
	}

	public User addPublicationHistory( PublicationHistory publicationHistory )
	{
		if ( this.publicationHistories == null )
			this.publicationHistories = new ArrayList<PublicationHistory>();

		this.publicationHistories.add( publicationHistory );

		return this;
	}

	public String getAcademicStatus()
	{
		return academicStatus;
	}

	public void setAcademicStatus( String academicStatus )
	{
		this.academicStatus = academicStatus;
	}

	public String getAffiliation()
	{
		return affiliation;
	}

	public void setAffiliation( String affiliation )
	{
		this.affiliation = affiliation;
	}

	public Date getJoinDate()
	{
		return joinDate;
	}

	public void setJoinDate( Date joinDate )
	{
		this.joinDate = joinDate;
	}

	public Set<UserAuthorBookmark> getUserAuthorBookmarks()
	{
		return userAuthorBookmarks;
	}

	public void setUserAuthorBookmarks( Set<UserAuthorBookmark> userAuthorBookmarks )
	{
		this.userAuthorBookmarks = userAuthorBookmarks;
	}

	public User addUserAuthorBookmark( final UserAuthorBookmark userAuthorBookmark )
	{
		if ( this.userAuthorBookmarks == null )
			this.userAuthorBookmarks = new HashSet<UserAuthorBookmark>();

		this.userAuthorBookmarks.add( userAuthorBookmark );

		return this;
	}

	public User removeUserAuthorBookmark( final UserAuthorBookmark userAuthorBookmark )
	{
		if ( this.userAuthorBookmarks != null )
		{
			for ( Iterator<UserAuthorBookmark> i = this.userAuthorBookmarks.iterator(); i.hasNext(); )
			{
				UserAuthorBookmark upb = i.next();
				if ( upb.equals( userAuthorBookmark ) )
					i.remove();
			}
		}
		return this;
	}

	public Set<UserCircleBookmark> getUserCircleBookmarks()
	{
		return userCircleBookmarks;
	}

	public void setUserCircleBookmarks( Set<UserCircleBookmark> userCircleBookmarks )
	{
		this.userCircleBookmarks = userCircleBookmarks;
	}

	public User addUserCircleBookmark( final UserCircleBookmark userCircleBookmark )
	{
		if ( this.userCircleBookmarks == null )
			this.userCircleBookmarks = new HashSet<UserCircleBookmark>();

		this.userCircleBookmarks.add( userCircleBookmark );

		return this;
	}

	public User removeUserCircleBookmark( final UserCircleBookmark userCircleBookmark )
	{
		if ( this.userCircleBookmarks != null )
		{
			for ( Iterator<UserCircleBookmark> i = this.userCircleBookmarks.iterator(); i.hasNext(); )
			{
				UserCircleBookmark upb = i.next();
				if ( upb.equals( userCircleBookmark ) )
					i.remove();
			}
		}
		return this;
	}

	public Set<UserEventGroupBookmark> getUserEventGroupBookmarks()
	{
		return userEventGroupBookmarks;
	}

	public void setUserEventGroupBookmarks( Set<UserEventGroupBookmark> userEventGroupBookmarks )
	{
		this.userEventGroupBookmarks = userEventGroupBookmarks;
	}

	public User addUserEventGroupBookmark( final UserEventGroupBookmark userEventGroupBookmark )
	{
		if ( this.userEventGroupBookmarks == null )
			this.userEventGroupBookmarks = new HashSet<UserEventGroupBookmark>();

		this.userEventGroupBookmarks.add( userEventGroupBookmark );

		return this;
	}

	public User removeUserEventGroupBookmark( final UserEventGroupBookmark userEventGroupBookmark )
	{
		if ( this.userEventGroupBookmarks != null )
		{
			for ( Iterator<UserEventGroupBookmark> i = this.userEventGroupBookmarks.iterator(); i.hasNext(); )
			{
				UserEventGroupBookmark upb = i.next();
				if ( upb.equals( userEventGroupBookmark ) )
					i.remove();
			}
		}
		return this;
	}

	public Set<UserPublicationBookmark> getUserPublicationBookmarks()
	{
		return userPublicationBookmarks;
	}

	public void setUserPublicationBookmarks( Set<UserPublicationBookmark> userPublicationBookmarks )
	{
		this.userPublicationBookmarks = userPublicationBookmarks;
	}

	public User addUserPublicationBookmark( final UserPublicationBookmark userPublicationBookmark )
	{
		if ( this.userPublicationBookmarks == null )
			this.userPublicationBookmarks = new HashSet<UserPublicationBookmark>();

		this.userPublicationBookmarks.add( userPublicationBookmark );

		return this;
	}

	public User removeUserPublicationBookmark( final UserPublicationBookmark userPublicationBookmark )
	{
		if ( this.userPublicationBookmarks != null )
		{
			for ( Iterator<UserPublicationBookmark> i = this.userPublicationBookmarks.iterator(); i.hasNext(); )
			{
				UserPublicationBookmark upb = i.next();
				if ( upb.equals( userPublicationBookmark ) )
					i.remove();
			}
		}
		return this;
	}

}

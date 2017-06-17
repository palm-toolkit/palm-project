package de.rwth.i9.palm.model;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "author" )
@Indexed
@AnalyzerDef( 
		name = "authoranalyzer", 
		tokenizer = @TokenizerDef( factory = StandardTokenizerFactory.class ), 
		filters = { 
			@TokenFilterDef( factory = LowerCaseFilterFactory.class ) 
			} 
		)
public class Author extends PersistableResource
{
	/* the full name of the author, most commonly used */
	@Column( length = 100 )
	@Field( index = Index.YES, analyze = Analyze.YES, store = Store.YES )
	@Analyzer( definition = "authoranalyzer" )
	private String name;

	@Column( length = 70 )
	@Field( index = Index.YES, analyze = Analyze.YES, store = Store.YES )
	@Analyzer( definition = "authoranalyzer" )
	private String firstName;

	@Column( length = 30 )
	@Field( index = Index.YES, analyze = Analyze.YES, store = Store.YES )
	@Analyzer( definition = "authoranalyzer" )
	@Boost( 3.0f )
	private String lastName;

	@Column
	private String otherDetail;

	@Column
	private String academicStatus;

	@Column
	private String department;

	@Column
	private String email;

	@Column
	private String photoUrl;
	
	@Column
	private String homepage;

	@Column
	private java.sql.Timestamp requestDate;
	
	@Column(columnDefinition = "int default 0")
	private int citedBy;

	@Column( columnDefinition = "int default 0" )
	private int noPublication;

	@Column( columnDefinition = "int default 0" )
	private int hindex;

	/* These 2 transient variables are important for adding new Author via GUI*/
	@Transient
	private String tempId;

	@Transient
	private String affiliation;

	@Column( columnDefinition = "bit default 0" )
	@Field( index = Index.YES, analyze = Analyze.NO, store = Store.YES )
	private boolean added = false;

	@Column( columnDefinition = "bit default 0" )
	private boolean isUpdateInterest = false;

	@Column( columnDefinition = "bit default 0" )
	private boolean isFetchPublicationDetail = false;

	// relations

	/* other name of the author */
	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author", orphanRemoval = true )
	private Set<AuthorAlias> aliases;

	@ManyToOne( cascade = CascadeType.ALL )
	@JoinColumn( name = "location_id" )
	private Location based_near;

	@OneToMany( fetch = FetchType.LAZY, mappedBy = "author" )
	@ContainedIn
	private Set<PublicationAuthor> publicationAuthors;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author" )
	private Set<InterestAuthor> interestAuthors;

	@ManyToOne( cascade = CascadeType.ALL )
	@JoinColumn( name = "institution_id" )
	@IndexedEmbedded
	private Institution institution;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author", orphanRemoval = true )
	private Set<AuthorSource> authorSources;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author", orphanRemoval = true )
	private Set<AuthorInterestProfile> authorInterestProfiles;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author", orphanRemoval = true )
	private Set<AuthorTopicModelingProfile> authorTopicModelingProfiles;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author" )
	private Set<UserAuthorBookmark> userAuthorBookmarks;

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

	public void setCompleteName( String name )
	{
		this.setName( name );
		String[] splitName = name.split( " " );
		if ( splitName.length > 0 )
			this.setLastName( splitName[splitName.length - 1] );

		if ( lastName != null )
		{
			String firstName = name.substring( 0, name.length() - lastName.length() ).trim();
			if ( !firstName.equals( "" ) )
				this.setFirstName( firstName );
		}
	}

	public void setEmail( String email )
	{
		this.email = email;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName( String firstName )
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName( String lastName )
	{
		this.lastName = lastName;
	}

	public String getDepartment()
	{
		return department;
	}

	public void setDepartment( String department )
	{
		this.department = department;
	}

	public Set<PublicationAuthor> getPublicationAuthors()
	{
		return publicationAuthors;
	}
	
	public List<Publication> getPublicationsByYear( int targetYear)
	{
		Calendar cal = Calendar.getInstance();
		
		if( this.publicationAuthors == null || this.publicationAuthors.isEmpty())
			return Collections.emptyList();
		
		List<Publication> publications = new ArrayList<Publication>();
		
		for( PublicationAuthor publicationAuthor : this.publicationAuthors ){
			Publication publication = publicationAuthor.getPublication();
			
			if( publication.getPublicationDate() == null)
				continue;
			
			cal.setTime(publication.getPublicationDate());
			
			int publicationYear = cal.get( Calendar.YEAR );
			
			if( publicationYear == targetYear )
				publications.add( publication );
		}
		
		return publications;
	}


	public void setPublicationAuthors( Set<PublicationAuthor> publicationAuthors )
	{
		this.publicationAuthors = publicationAuthors;
	}

	public Author addPublicationAuthor( final PublicationAuthor publicationAuthor )
	{
		if ( this.publicationAuthors == null )
			this.publicationAuthors = new LinkedHashSet<PublicationAuthor>();

		// skip duplicated item
		for ( PublicationAuthor eachPublicationAuthor : this.publicationAuthors )
		{
			if ( eachPublicationAuthor.getAuthor().equals( publicationAuthor.getAuthor() ) && eachPublicationAuthor.getPublication().equals( publicationAuthor.getPublication() ) )
				return this;
		}

		this.publicationAuthors.add( publicationAuthor );

		return this;
	}

	public Author removePublicationAuthor( PublicationAuthor publicationAuthor )
	{
		if ( this.publicationAuthors == null || this.publicationAuthors.isEmpty() )
			return this;

		for ( Iterator<PublicationAuthor> i = this.publicationAuthors.iterator(); i.hasNext(); )
		{
			PublicationAuthor eachPublicationAuthor = i.next();
			if ( eachPublicationAuthor.equals( publicationAuthor ) )
				i.remove();
		}
		return this;
	}

	public Location getBased_near()
	{
		return based_near;
	}

	public void setBased_near( Location based_near )
	{
		this.based_near = based_near;
	}

	public String getOtherDetail()
	{
		return otherDetail;
	}

	public void setOtherDetail( String otherDetail )
	{
		this.otherDetail = otherDetail;
	}

	public String getPhotoUrl()
	{
		return photoUrl;
	}

	public void setPhotoUrl( String photoUrl )
	{
		this.photoUrl = photoUrl;
	}

	public Set<AuthorSource> getAuthorSources()
	{
		return authorSources;
	}

	public void setAuthorSources( Set<AuthorSource> authorSources )
	{
		if ( this.authorSources == null )
			this.authorSources = new LinkedHashSet<AuthorSource>();
		this.authorSources.clear();
		this.authorSources.addAll( authorSources );
	}

	public Author removeAuthorSource( AuthorSource authorSource )
	{
		if ( this.authorSources == null || this.authorSources.isEmpty() )
			return this;

		for ( Iterator<AuthorSource> i = this.authorSources.iterator(); i.hasNext(); )
		{
			AuthorSource eachAuthorSource = i.next();
			if ( eachAuthorSource.equals( authorSource ) )
				i.remove();
		}
		return this;
	}

	public Author addAuthorSource( AuthorSource auhtorSource )
	{
		if ( this.authorSources == null )
		{
			this.authorSources = new LinkedHashSet<AuthorSource>();
			this.authorSources.add( auhtorSource );
		}
		else
		{
			boolean addSource = true;
			for ( AuthorSource eachAuhorSource : this.authorSources )
			{
				if ( eachAuhorSource.getSourceType().equals( auhtorSource.getSourceType() ) )
				{
					//if ( eachAuhorSource.getSourceUrl().equals( auhtorSource.getSourceUrl() ) )
					//{
						addSource = false;
						break;
					//}
				}
			}
			if ( addSource )
				this.authorSources.add( auhtorSource );
		}

		return this;
	}

	public java.sql.Timestamp getRequestDate()
	{
		return requestDate;
	}

	public void setRequestDate( java.sql.Timestamp requestDate )
	{
		this.requestDate = requestDate;
	}

	public int getCitedBy()
	{
		return citedBy;
	}

	public void setCitedBy( int citedBy )
	{
		this.citedBy = citedBy;
	}

	public Set<AuthorAlias> getAliases()
	{
		return aliases;
	}

	public void setAliases( Set<AuthorAlias> aliases )
	{
		this.aliases = aliases;
	}
	
	public Author addAlias( AuthorAlias authorAlias )
	{
		if ( this.aliases == null )
			this.aliases = new LinkedHashSet<AuthorAlias>();

		// check if alias already exist
		else
		{
			for ( AuthorAlias eachAuthorAlias : this.aliases )
			{
				if ( eachAuthorAlias.getName().equals( authorAlias.getName() ) )
					return this;
			}
		}

		// new or not duplicated, then added to hashset
		this.aliases.add( authorAlias );
		authorAlias.setAuthor( this );

		return this;
	}

	public Set<AuthorInterestProfile> getAuthorInterestProfiles()
	{
		return authorInterestProfiles;
	}

	public void setAuthorInterestProfiles( Set<AuthorInterestProfile> authorInterestProfiles )
	{
		this.authorInterestProfiles = authorInterestProfiles;
	}

	public Author addAuthorInterestProfiles( AuthorInterestProfile authorInterestProfile )
	{
		if ( this.authorInterestProfiles == null )
			this.authorInterestProfiles = new LinkedHashSet<AuthorInterestProfile>();

		this.authorInterestProfiles.add( authorInterestProfile );

		return this;
	}

	public Set<InterestAuthor> getInterestAuthors()
	{
		return interestAuthors;
	}

	public void setInterestAuthors( Set<InterestAuthor> interestAuthors )
	{
		this.interestAuthors = interestAuthors;
	}

	public Author addInterestAuthor( InterestAuthor interestAuthor )
	{
		if ( this.interestAuthors == null )
			this.interestAuthors = new HashSet<InterestAuthor>();

		this.interestAuthors.add( interestAuthor );

		return this;
	}

	public boolean hasCoAuthorWith( Publication publication, Author coAuthor )
	{
		if ( this.publicationAuthors == null || publicationAuthors.isEmpty() )
			return false;

		// Foreach publicationAuthor
		for ( PublicationAuthor pubAuthor : this.publicationAuthors )
		{
			// get the publication author
			if ( pubAuthor.getPublication().equals( publication ) && pubAuthor.getAuthor().equals( coAuthor ) )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether a name is the abbreviation of existing authors
	 * 
	 * @param firstNameSplit
	 * @return
	 */
	public boolean isAliasNameFromFirstName( String firstName )
	{
		if ( firstName == null || firstName.equals( "" ) || this.getFirstName() == null )
			return false;

		String shorterFirstName = null;
		String longerFirstName = null;

		if ( this.getFirstName().length() > firstName.length() )
		{
			longerFirstName = this.getFirstName();
			shorterFirstName = firstName;
		}
		else
		{
			longerFirstName = firstName;
			shorterFirstName = this.getFirstName();
		}

		String[] shorterFirstNameSplit = shorterFirstName.split( " " );
		String[] longerFirstNameSplit = longerFirstName.split( " " );

		// case :
		// "mohamed amine" chatti
		// aliass : "m" chatti, "m a" chatti, "ma" chatti, "mohamed" chatti

		// create possible abbreviations
		// abbr1 and abbr2 = use only first word of author name
		// e.g. m and mohamed
		String abbr1 = longerFirstNameSplit[0]; // e.g. mohamed
		String abbr2 = longerFirstNameSplit[0].substring( 0, 1 ); // e.g. m

		// abbr3 and abbr4 = use entire first name
		// e.g. ma and m a and mohamed a
		String abbr3 = "";
		String abbr4 = "";
		String abbr5 = "";

		if ( longerFirstNameSplit.length > 1 )
		{
			for ( int i = 0; i < longerFirstNameSplit.length; i++ )
			{
				if ( longerFirstNameSplit[i].length() == 0 )
					continue;

				if ( i > 0 )
				{
					abbr4 += " ";
					abbr5 += " ";
				}

				// TODO : error
				abbr3 += longerFirstNameSplit[i].substring( 0, 1 );
				abbr4 += longerFirstNameSplit[i].substring( 0, 1 );

				if ( i == 0 )
					abbr5 += longerFirstNameSplit[i];
				else
					abbr5 += longerFirstNameSplit[i].substring( 0, 1 );
			}
		}

		// check for alias name
		if ( !abbr5.equals( "" ) && shorterFirstName.equalsIgnoreCase( abbr5 ) )
		{
			this.setAuthorNameAndAddAlias( longerFirstName, shorterFirstName );
			return true;
		}

		if ( !abbr4.equals( "" ) && shorterFirstName.equalsIgnoreCase( abbr4 ) )
		{
			this.setAuthorNameAndAddAlias( longerFirstName, shorterFirstName );
			return true;
		}

		if ( !abbr3.equals( "" ) && shorterFirstName.equalsIgnoreCase( abbr3 ) )
		{
			this.setAuthorNameAndAddAlias( longerFirstName, shorterFirstName );
			return true;
		}

		if ( shorterFirstName.equalsIgnoreCase( abbr1 ) )
		{
			this.setAuthorNameAndAddAlias( longerFirstName, shorterFirstName );
			return true;
		}

		if ( shorterFirstName.equalsIgnoreCase( abbr2 ) )
		{
			this.setAuthorNameAndAddAlias( longerFirstName, shorterFirstName );
			return true;
		}
		// put other possibilities here

		return false;
	}

	public int getHindex()
	{
		return hindex;
	}

	public void setHindex( int hindex )
	{
		this.hindex = hindex;
	}

	public void setAuthorNameAndAddAlias( String longerFirstName, String shorterFirstName )
	{
		if ( !this.getFirstName().equalsIgnoreCase( longerFirstName ) )
			this.setLastName( longerFirstName );

		AuthorAlias newAuthorAlias = new AuthorAlias();
		newAuthorAlias.setCompleteName( shorterFirstName + " " + this.getLastName() );
		this.addAlias( newAuthorAlias );
	}

	public void setPossibleNames( String name )
	{
		// check name length after normalization to ASCII
		String nameAscii = name.replaceAll( "[^-a-zA-Z ]", "" );
		
		if ( nameAscii.length() == name.length() )
		{
			this.setCompleteName( name );
			if ( name.contains( "-" ) )
			{
				AuthorAlias authorAlias = new AuthorAlias();
				authorAlias.setCompleteName( name.replaceAll( "-", " " ) );
				authorAlias.setAuthor( this );
				this.addAlias( authorAlias );
			}
		}
		else
		{

			@SuppressWarnings( "serial" )
			Map<Character, String> LIGATURES = new HashMap<Character, String>()
			{
				{
					put( 'ä', "ae" );
					put( 'ü', "ue" );
					put( 'ö', "oe" );
					put( 'ß', "ss" );
					put( 'Æ', "AE" );
					put( 'æ', "ae" );
					put( 'œ', "oe" );
					put( 'þ', "th" );
					put( 'ĳ', "ij" );
					put( 'ð', "dh" );
					put( 'Æ', "AE" );
					put( 'Œ', "OE" );
					put( 'Þ', "TH" );
					put( 'Ð', "DH" );
					put( 'Ĳ', "IJ" );
				}
			};

			// name combination 1
			StringBuilder sb = new StringBuilder();
			for ( int i = 0; i < name.length(); i++ )
			{
				char c = name.charAt( i );
				String l = LIGATURES.get( c );
				if ( l != null )
				{
					sb.append( l );
				}
				else if ( c < 0xc0 )
				{
					sb.append( c ); // ASCII and C1 control codes
				}
				else
				{
					// anything else, including diacritics
					l = Normalizer.normalize( Character.toString( c ), Normalizer.Form.NFKD ).replaceAll( "[\\p{InCombiningDiacriticalMarks}]+", "" );
					sb.append( l );
				}
			}
			String aliasName = sb.toString();

			// name combination 2
			String nfdNormalizedString = Normalizer.normalize( name, Normalizer.Form.NFD );
			Pattern pattern = Pattern.compile( "\\p{InCombiningDiacriticalMarks}+" );
			String mainName = pattern.matcher( nfdNormalizedString ).replaceAll( "" );

			this.setCompleteName( mainName.replaceAll( "[^-a-zA-Z ]", "" ) );

			AuthorAlias authorAlias1 = new AuthorAlias();
			authorAlias1.setCompleteName( name );
			authorAlias1.setAuthor( this );
			this.addAlias( authorAlias1 );

			if ( !mainName.equals( aliasName ) )
			{
				AuthorAlias authorAlias2 = new AuthorAlias();
				authorAlias2.setCompleteName( aliasName );
				authorAlias2.setAuthor( this );
				this.addAlias( authorAlias2 );
			}

			if ( mainName.contains( "-" ) )
			{
				AuthorAlias authorAlias3 = new AuthorAlias();
				authorAlias3.setCompleteName( mainName.replaceAll( "-", " " ) );
				authorAlias3.setAuthor( this );
				this.addAlias( authorAlias3 );
			}
		}
	}

	public String getAcademicStatus()
	{
		return academicStatus;
	}

	public void setAcademicStatus( String academicStatus )
	{
		this.academicStatus = academicStatus;
	}

	public AuthorInterestProfile getSpecificAuthorInterestProfile( String interestProfileName )
	{
		if ( interestProfileName == null || interestProfileName.equals( "" ) )
			return null;

		if ( this.authorInterestProfiles == null || this.authorInterestProfiles.isEmpty() )
			return null;

		for ( AuthorInterestProfile aip : this.authorInterestProfiles )
		{
			if ( aip.getName().equals( interestProfileName ) )
			{
				return aip;
			}
		}

		return null;
	}
//
//	public Author addPublication( Publication publication )
//	{
//		PublicationAuthor publicationAuthor = new PublicationAuthor();
//		publicationAuthor.setPublication( publication );
//		publicationAuthor.setAuthor( this );
//
//		if ( this.publicationAuthors == null )
//			this.publicationAuthors = new HashSet<PublicationAuthor>();
//
//		publicationAuthors.add( publicationAuthor );
//
//		return this;
//	}

	public Set<Publication> getPublications()
	{
		if ( this.publicationAuthors == null || publicationAuthors.isEmpty() )
			return Collections.emptySet();

		Set<Publication> publications = new HashSet<Publication>();
		for ( PublicationAuthor publicationAuthor : this.publicationAuthors )
		{
			publications.add( publicationAuthor.getPublication() );
		}

		return publications;
	}

	public String getAffiliation()
	{
		return affiliation;
	}

	public void setAffiliation( String affiliation )
	{
		this.affiliation = affiliation;
	}

	public String getTempId()
	{
		return tempId;
	}

	public void setTempId( String tempId )
	{
		this.tempId = tempId;
	}

	public boolean isAdded()
	{
		return added;
	}

	public void setAdded( boolean added )
	{
		this.added = added;
	}

	public String getHomepage()
	{
		return homepage;
	}

	public void setHomepage( String homepage )
	{
		this.homepage = homepage;
	}

	public Institution getInstitution()
	{
		return institution;
	}

	public void setInstitution( Institution institution )
	{
		this.institution = institution;
	}

	public Set<AuthorTopicModelingProfile> getAuthorTopicModelingProfiles()
	{
		return authorTopicModelingProfiles;
	}

	public void setAuthorTopicModelingProfiles( Set<AuthorTopicModelingProfile> authorTopicModelingProfiles )
	{
		this.authorTopicModelingProfiles = authorTopicModelingProfiles;
	}

	public Author addAuthorTopicModelingProfiles( AuthorTopicModelingProfile authorTopicModelingProfile )
	{
		if ( this.authorTopicModelingProfiles == null )
			this.authorTopicModelingProfiles = new LinkedHashSet<AuthorTopicModelingProfile>();

		this.authorTopicModelingProfiles.add( authorTopicModelingProfile );

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

	public boolean isContainSource( Source source )
	{
		if ( this.authorSources == null )
			return false;

		else
		{
			for ( AuthorSource eachAuhorSource : this.authorSources )
				if ( eachAuhorSource.getSourceType().toString().equals( source.getSourceType().toString() ) )
					return true;
		}
		return false;
	}

	public boolean isContainAuthorSource( AuthorSource as )
	{
		if ( this.authorSources == null )
			return false;

		else
		{
			for ( AuthorSource eachAuhorSource : this.authorSources )
				if ( eachAuhorSource.getSourceType().toString().equals( as.getSourceType().toString() ) )
					return true;
		}
		return false;
	}

	public Set<UserAuthorBookmark> getUserAuthorBookmarks()
	{
		return userAuthorBookmarks;
	}

	public void setUserAuthorBookmarks( Set<UserAuthorBookmark> userAuthorBookmarks )
	{
		this.userAuthorBookmarks = userAuthorBookmarks;
	}

	public boolean isFetchPublicationDetail()
	{
		return isFetchPublicationDetail;
	}

	public void setFetchPublicationDetail( boolean isFetchPublicationDetail )
	{
		this.isFetchPublicationDetail = isFetchPublicationDetail;
	}

	public int getNoPublication()
	{
		return noPublication;
	}

	public void setNoPublication( int noPublication )
	{
		this.noPublication = noPublication;
	}

	/**
	 * Recalculate publication and citation
	 * 
	 * @param author
	 */
	public Author reCalculateNumberOfPublicationAndCitation()
	{
		// count total citation on author
		int citation = 0;
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy" );

		for ( Publication publication : this.getPublications() )
		{
			citation += publication.getCitedBy();
			// set publication year
			if ( publication.getPublicationDate() != null )
				publication.setYear( sdf.format( publication.getPublicationDate() ) );
		}

		this.setNoPublication( this.getPublications().size() );
		this.setCitedBy( citation );
		return this;
	}

}

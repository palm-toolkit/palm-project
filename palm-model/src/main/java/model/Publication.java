package de.rwth.i9.palm.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.rwth.i9.palm.helper.comparator.PublicationAuthorByPositionComparator;
import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "publication" )
@Indexed
@AnalyzerDef( 
		name = "lowercaseSnowballAnalyzer",
		tokenizer = @TokenizerDef( factory = StandardTokenizerFactory.class ), 
		filters = { 
			@TokenFilterDef( factory = LowerCaseFilterFactory.class ), 
			@TokenFilterDef( factory = SnowballPorterFilterFactory.class, params = { @Parameter( name = "language", value = "English" ) } ) 
			} 
		)
public class Publication extends PersistableResource
{
	@Column( nullable = false )
	@Field( index = Index.YES, analyze = Analyze.YES, store = Store.YES )
	@Analyzer( definition = "lowercaseSnowballAnalyzer" )
	@Boost( 3.0f )
	@Lob
	private String title;
	
	@Column
	private Date publicationDate;
	
	@Column( length = 10 )
	private String publicationDateFormat;

	@Column
	@Lob
	@Field( index = Index.YES, termVector = TermVector.WITH_POSITION_OFFSETS, store = Store.YES )
	@Analyzer( definition = "lowercaseSnowballAnalyzer" )
	private String abstractText;

	@Enumerated( EnumType.STRING )
	@Column( length = 20, columnDefinition = "varchar(20) default 'NOT_COMPLETE'" )
	private CompletionStatus abstractStatus;

	@Column
	@Lob
	@Field( index = Index.YES, termVector = TermVector.WITH_POSITION_OFFSETS, store = Store.YES )
	@Analyzer( definition = "lowercaseSnowballAnalyzer" )
	private String contentText;

	@Column
	@Lob
	private String keywordText;

	@Enumerated( EnumType.STRING )
	@Column( length = 20, columnDefinition = "varchar(20) default 'NOT_COMPLETE'" )
	private CompletionStatus keywordStatus;

	@Column
	@Lob
	private String referenceText;

	@Column( columnDefinition = "int default 0" )
	private int startPage;

	@Column( columnDefinition = "int default 0" )
	private int endPage;

	@Enumerated( EnumType.STRING )
	@Column( length = 16 )
	@Field( index = Index.YES, analyze = Analyze.NO, store = Store.YES )
	private PublicationType publicationType;

	@Enumerated( EnumType.STRING )
	@Column( length = 20, columnDefinition = "varchar(20) default 'NOT_COMPLETE'" )
	private CompletionStatus publicationTypeStatus;

	@Transient
	List<Author> authors;

	@Transient
	String publisher;

	// this is used in Lucene, since it's tricky to join index in lucene
	// if this problem solved, these attribute can be deleted (authorText and
	// year)
	@Column
	@Field( index = Index.YES, analyze = Analyze.NO, store = Store.YES )
	@Lob
	private String authorText;

	@Column( length = 4 )
	@Field( index = Index.YES, analyze = Analyze.NO, store = Store.YES )
	private String year;

	/* store any information in json format */
	@Column
	@Lob
	private String additionalInformation;

	@Column( columnDefinition = "int default 0" )
	private int citedBy;

	@Column( columnDefinition = "bit default 1" )
	private boolean contentUpdated = true;

	@Column( columnDefinition = "bit default 0" )
	private boolean pdfExtracted = false;

	@Column( length = 15 )
	private String language;

	/* store any citation information in jsonformat */
	@Column
	@Lob
	private String citedByUrl;

	// relations
	@ManyToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	@JoinTable( name = "publication_keyword", joinColumns = @JoinColumn( name = "publication_id" ), inverseJoinColumns = @JoinColumn( name = "keyword_id" ) )
	private Set<Subject> subjects;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "publication", orphanRemoval = true )
	private Set<PublicationTopic> publicationTopics;
	
	@ManyToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
	@JoinColumn( name = "event_id" )
	private Event event;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "publication" )
	@ContainedIn
	private Set<PublicationAuthor> publicationAuthors;

	@ManyToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	@JoinTable( name = "publication_cites", joinColumns = @JoinColumn( name = "publication_id" ), inverseJoinColumns = @JoinColumn( name = "publication_cites_id" ) )
	private Set<Publication> publicationCitess;

	@ManyToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	@JoinTable( name = "publication_citedby", joinColumns = @JoinColumn( name = "publication_id" ), inverseJoinColumns = @JoinColumn( name = "publication_citedby_id" ) )
	private Set<Publication> publicationCitedBys;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "publication", orphanRemoval = true )
	private Set<PublicationHistory> publicationHistories;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "publication", orphanRemoval = true )
	private Set<PublicationSource> publicationSources;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "publication", orphanRemoval = true )
	private Set<PublicationFile> publicationFiles;

	@ManyToMany( cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY )
	@JoinTable( name = "circle_publication", joinColumns = @JoinColumn( name = "publication_id" ), inverseJoinColumns = @JoinColumn( name = "circle_id" ) )
	private Set<Circle> circles;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "publication" )
	private Set<UserPublicationBookmark> userPublicationBookmarks;

	public Event getEvent()
	{
		return event;
	}

	public void setEvent( Event event )
	{
		this.event = event;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle( String title )
	{
		this.title = title;
	}

	public String getAbstractText()
	{
		return abstractText;
	}

	public void setAbstractText( String abstractText )
	{
		this.abstractText = abstractText;
	}

	public String getContentText()
	{
		return contentText;
	}

	public void setContentText( String contentText )
	{
		this.contentText = contentText;
	}

	public Set<Subject> getKeywords()
	{
		return subjects;
	}

	public void setKeywords( Set<Subject> subjects )
	{
		this.subjects = subjects;
	}

	public Publication addKeyword( final Subject subject )
	{
		if ( this.subjects == null )
			this.subjects = new LinkedHashSet<Subject>();

		subjects.add( subject );
		return this;
	}

	public Set<PublicationAuthor> getPublicationAuthors()
	{
		return publicationAuthors;
	}

	public void clearPublicationAuthors()
	{
		this.publicationAuthors.clear();
		this.publicationAuthors = null;
	}

	public void setPublicationAuthors( Set<PublicationAuthor> publicationAuthors )
	{
		if ( this.publicationAuthors == null )
			this.publicationAuthors = new LinkedHashSet<PublicationAuthor>();
		this.publicationAuthors.clear();
		this.publicationAuthors.addAll( publicationAuthors );
	}

	public Publication addPublicationAuthor( final PublicationAuthor publicationAuthor )
	{
		if ( this.publicationAuthors == null )
			this.publicationAuthors = new LinkedHashSet<PublicationAuthor>();

		// skip duplicated item
		for ( PublicationAuthor eachPublicationAuthor : this.publicationAuthors )
		{
			if ( eachPublicationAuthor.getAuthor().equals( publicationAuthor.getAuthor() ) && eachPublicationAuthor.getPublication().equals( publicationAuthor.getPublication() ) )
			{
				return this;
			}
		}

		this.publicationAuthors.add( publicationAuthor );
		// add also authorText;
		if ( this.publicationAuthors.size() > 1 )
			this.authorText += ",";
		else
			this.authorText = "";
		this.authorText += publicationAuthor.getAuthor().getName();
		
		return this;
	}

	public Set<Publication> getPublicationCitess()
	{
		return publicationCitess;
	}

	public void setPublicationCitess( Set<Publication> publicationCitess )
	{
		this.publicationCitess = publicationCitess;
	}

	public Publication addPublicationCites( final Publication publicationCites )
	{
		if ( this.publicationCitess == null )
			this.publicationCitess = new LinkedHashSet<Publication>();

		this.publicationCitess.add( publicationCites );
		return this;
	}

	public Set<Publication> getPublicationCitedBys()
	{
		return publicationCitedBys;
	}

	public void setPublicationCitedBys( Set<Publication> publicationCitedBys )
	{
		this.publicationCitedBys = publicationCitedBys;
	}

	public Publication addPublicationCiteBy( final Publication publicationCiteBy )
	{
		if ( this.publicationCitedBys == null )
			this.publicationCitedBys = new LinkedHashSet<Publication>();

		this.publicationCitedBys.add( publicationCiteBy );
		return this;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage( String language )
	{
		this.language = language;
	}

	public Set<Subject> getSubjects()
	{
		return subjects;
	}

	public void setSubjects( Set<Subject> subjects )
	{
		this.subjects = subjects;
	}

	public Publication addSubject( Subject subject )
	{
		if ( this.subjects == null )
			this.subjects = new LinkedHashSet<Subject>();

		this.subjects.add( subject );

		return this;
	}

	public Set<PublicationHistory> getPublicationHistories()
	{
		return publicationHistories;
	}

	public void setPublicationHistories( Set<PublicationHistory> publicationHistories )
	{
		this.publicationHistories = publicationHistories;
	}

	public Publication addPublicationHistory( PublicationHistory publicationHistory )
	{
		if ( this.publicationHistories == null )
			this.publicationHistories = new LinkedHashSet<PublicationHistory>();
		this.publicationHistories.add( publicationHistory );
		return this;
	}

	public Set<PublicationSource> getPublicationSources()
	{
		return publicationSources;
	}

	public void setPublicationSources( Set<PublicationSource> publicationSources )
	{
		if ( this.publicationSources == null )
			this.publicationSources = new LinkedHashSet<PublicationSource>();
		this.publicationSources.clear();
		this.publicationSources.addAll( publicationSources );
	}

	public Publication addPublicationSource( PublicationSource publicationSource )
	{
		if ( this.publicationSources == null )
			this.publicationSources = new LinkedHashSet<PublicationSource>();

		// check for duplication source
		PublicationSource existedPublicationSourceWithSameSourceType = this.getPublicationSourceBySourceType( publicationSource.getSourceType() );

		// update information
		if ( existedPublicationSourceWithSameSourceType != null )
		{
			// there are information from similar source, get the most complete
			// one
			// authors
			if ( existedPublicationSourceWithSameSourceType.getCoAuthors() == null && publicationSource.getCoAuthors() != null )
				existedPublicationSourceWithSameSourceType.setCoAuthors( publicationSource.getCoAuthors() );
			else if ( existedPublicationSourceWithSameSourceType.getCoAuthors() != null && publicationSource.getCoAuthors() != null )
			{
				// choose longer text
				if ( existedPublicationSourceWithSameSourceType.getCoAuthors().length() < publicationSource.getCoAuthors().length() )
					existedPublicationSourceWithSameSourceType.setCoAuthors( publicationSource.getCoAuthors() );
			}
			// keyword
			if ( existedPublicationSourceWithSameSourceType.getKeyword() == null && publicationSource.getKeyword() != null )
				existedPublicationSourceWithSameSourceType.setKeyword( publicationSource.getKeyword() );
			else if ( existedPublicationSourceWithSameSourceType.getKeyword() != null && publicationSource.getKeyword() != null )
			{
				// choose longer text
				if ( existedPublicationSourceWithSameSourceType.getKeyword().length() < publicationSource.getKeyword().length() )
					existedPublicationSourceWithSameSourceType.setKeyword( publicationSource.getKeyword() );
			}
			// abstract text
			if ( existedPublicationSourceWithSameSourceType.getAbstractText() == null && publicationSource.getAbstractText() != null )
				existedPublicationSourceWithSameSourceType.setAbstractText( publicationSource.getAbstractText() );
			else if ( existedPublicationSourceWithSameSourceType.getAbstractText() != null && publicationSource.getAbstractText() != null )
			{
				// choose longer text
				if ( existedPublicationSourceWithSameSourceType.getAbstractText().length() < publicationSource.getAbstractText().length() )
					existedPublicationSourceWithSameSourceType.setAbstractText( publicationSource.getAbstractText() );
			}
			if ( existedPublicationSourceWithSameSourceType.getPages() == null && publicationSource.getPages() != null )
			{
				existedPublicationSourceWithSameSourceType.setPages( publicationSource.getPages() );
			}
		}
		else
			this.publicationSources.add( publicationSource );
		return this;
	}

	public PublicationSource getPublicationSourceBySourceType( SourceType sourceType )
	{
		if ( this.publicationSources == null || this.publicationSources.isEmpty() )
			return null;

		for ( PublicationSource publicationSource : this.publicationSources )
		{
			if ( publicationSource.getSourceType().equals( sourceType ) )
				return publicationSource;
		}
		return null;
	}

	public void removeNonUserInputPublicationSource()
	{
		if ( this.publicationSources != null )
		{
			for ( Iterator<PublicationSource> ps = this.publicationSources.iterator(); ps.hasNext(); )
			{
				PublicationSource publicationSource = ps.next();
				if ( publicationSource.getSourceType() != SourceType.USER )
					ps.remove();
			}
		}
	}

	public Set<PublicationTopic> getPublicationTopics()
	{
		return publicationTopics;
	}

	public void setPublicationTopics( Set<PublicationTopic> publicationTopics )
	{
		this.publicationTopics = publicationTopics;
	}

	public Publication addPublicationTopic( PublicationTopic publicationTopic )
	{
		if ( publicationTopic == null )
			return this;
		if ( this.publicationTopics == null )
			this.publicationTopics = new LinkedHashSet<PublicationTopic>();
		this.publicationTopics.add( publicationTopic );
		return this;
	}

	public Date getPublicationDate()
	{
		return publicationDate;
	}

	public void setPublicationDate( Date publicationDate )
	{
		this.publicationDate = publicationDate;
	}

	public int getCitedBy()
	{
		return citedBy;
	}

	public void setCitedBy( int citedBy )
	{
		this.citedBy = citedBy;
	}

	public PublicationType getPublicationType()
	{
		return publicationType;
	}

	public void setPublicationType( PublicationType publicationType )
	{
		this.publicationType = publicationType;
	}

	public boolean isContentUpdated()
	{
		return contentUpdated;
	}

	public void setContentUpdated( boolean contentUpdated )
	{
		this.contentUpdated = contentUpdated;
	}

	public boolean isPdfExtracted()
	{
		return pdfExtracted;
	}

	public void setPdfExtracted( boolean pdfExtracted )
	{
		this.pdfExtracted = pdfExtracted;
	}

	public String getKeywordText()
	{
		return keywordText;
	}

	public void setKeywordText( String keywordText )
	{
		this.keywordText = keywordText;
	}

	public String getReferenceText()
	{
		return referenceText;
	}

	public void setReferenceText( String referenceText )
	{
		this.referenceText = referenceText;
	}

	public Set<PublicationFile> getPublicationFiles()
	{
		return publicationFiles;
	}

	public void setPublicationFiles( Set<PublicationFile> publicationFiles )
	{
		this.publicationFiles = publicationFiles;
	}

	public Publication addPublicationFile( PublicationFile publicationFile )
	{
		if ( this.publicationFiles == null )
			this.publicationFiles = new HashSet<PublicationFile>();
		else
		{
			// second check from file itself
			for ( PublicationFile pubFile : this.publicationFiles )
			{
				if ( publicationFile.getSourceType().equals( SourceType.CITESEERX ) && pubFile.getSourceType().equals( SourceType.CITESEERX ) )
					return this;
				if ( pubFile.getUrl().equals( publicationFile.getUrl() ) )
					return this;
			}
		}

		this.publicationFiles.add( publicationFile );
		return this;
	}

	public Object getAdditionalInformationByKey( String key )
	{
		if ( this.additionalInformation == null || this.additionalInformation.equals( "" ) )
			return null;

		// search object with jackson
		ObjectMapper mapper = new ObjectMapper();
		try
		{
			ObjectNode informationNode = (ObjectNode) mapper.readTree( this.additionalInformation );
			if ( informationNode.path( key ) != null )
				return informationNode.path( key );

			return null;
		}
		catch ( JsonProcessingException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}

	public boolean removeAdditionalInformation( String key )
	{
		if ( this.additionalInformation == null || this.additionalInformation.equals( "" ) )
			return false;

		// search object with jackson
		ObjectMapper mapper = new ObjectMapper();
		try
		{
			ObjectNode informationNode = (ObjectNode) mapper.readTree( this.additionalInformation );
			if ( informationNode.path( key ) != null )
			{
				informationNode.remove( key );
				this.additionalInformation = informationNode.toString();
				return true;
			}
			return false;
		}
		catch ( JsonProcessingException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}

		return false;
	}

	public void setAdditionalInformation( String additionalInformationInJsonString )
	{
		this.additionalInformation = additionalInformationInJsonString;
	}

	public String getAdditionalInformation()
	{
		return this.additionalInformation;
	}

	public Map<String, Object> getAdditionalInformationAsMap()
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode informationNode = null;
		try
		{
			informationNode = (ObjectNode) mapper.readTree( this.additionalInformation );
		}
		catch ( JsonProcessingException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}

		if ( informationNode == null )
			return Collections.emptyMap();

		@SuppressWarnings( "unchecked" )
		Map<String, Object> convertValue = mapper.convertValue( informationNode, Map.class );

		return convertValue;
	}

	public Publication addOrUpdateAdditionalInformation( String objectKey, Object objectValue )
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode informationNode = null;
		if ( this.additionalInformation != null && !this.additionalInformation.equals( "" ) )
		{
			try
			{
				informationNode = (ObjectNode) mapper.readTree( this.additionalInformation );
			}
			catch ( JsonProcessingException e )
			{
				e.printStackTrace();
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			informationNode = mapper.createObjectNode();
		}

		if ( objectValue instanceof String )
			try
			{
				informationNode.putPOJO( objectKey, mapper.writeValueAsString( objectValue ) );
			}
			catch ( JsonProcessingException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			informationNode.putPOJO( objectKey, objectValue );

		this.additionalInformation = informationNode.toString();

		return this;
	}

	public String getPublicationDateFormat()
	{
		return publicationDateFormat;
	}

	public void setPublicationDateFormat( String publicationDateFormat )
	{
		this.publicationDateFormat = publicationDateFormat;
	}

	public Set<String> getSourceFiles()
	{
		Set<String> sourceFileUrl = new HashSet<String>();
		if ( this.publicationFiles == null || publicationFiles.isEmpty() )
			return Collections.emptySet();
		else
			for ( PublicationFile pubFile : this.publicationFiles )
			{
				sourceFileUrl.add( pubFile.getUrl() );
			}

		return sourceFileUrl;
	}

	public Set<PublicationFile> getPublicationFilesPdf()
	{
		if ( this.publicationFiles == null || publicationFiles.isEmpty() )
			return Collections.emptySet();
		else
		{
			Set<PublicationFile> publicationFilePdf = new LinkedHashSet<>();
			for ( PublicationFile publicationFile : this.publicationFiles )
			{
				if ( publicationFile.getFileType().equals( FileType.PDF ) )
					publicationFilePdf.add( publicationFile );
			}
			return publicationFilePdf;
		}
	}

	public Set<PublicationFile> getPublicationFilesHtml()
	{
		if ( this.publicationFiles == null || publicationFiles.isEmpty() )
			return Collections.emptySet();
		else
		{
			Set<PublicationFile> publicationFileHtml = new LinkedHashSet<>();
			for ( PublicationFile publicationFile : this.publicationFiles )
			{
				if ( publicationFile.getFileType().equals( FileType.HTML ) )
					publicationFileHtml.add( publicationFile );
			}
			return publicationFileHtml;
		}
	}

	public CompletionStatus getAbstractStatus()
	{
		return abstractStatus;
	}

	public void setAbstractStatus( CompletionStatus abstractStatus )
	{
		this.abstractStatus = abstractStatus;
	}

	public CompletionStatus getKeywordStatus()
	{
		return keywordStatus;
	}

	public void setKeywordStatus( CompletionStatus keywordStatus )
	{
		this.keywordStatus = keywordStatus;
	}

//	public Publication addCoAuthor( Author author )
//	{
//		PublicationAuthor publicationAuthor = new PublicationAuthor();
//		publicationAuthor.setPublication( this );
//		publicationAuthor.setAuthor( author );
//
//		if ( this.publicationAuthors == null )
//			this.publicationAuthors = new HashSet<PublicationAuthor>();
//
//		publicationAuthors.add( publicationAuthor );
//
//		return this;
//	}

	public List<Author> getCoAuthors()
	{
		if ( this.publicationAuthors == null || publicationAuthors.isEmpty() )
			return Collections.emptyList();

		List<PublicationAuthor> publicationAuthorList = new ArrayList<PublicationAuthor>( this.publicationAuthors );

		// sort based on author position on paper
		Collections.sort( publicationAuthorList, new PublicationAuthorByPositionComparator() );

		List<Author> coAuthors = new ArrayList<Author>();
		for ( PublicationAuthor publicationAuthor : publicationAuthorList )
		{
			coAuthors.add( publicationAuthor.getAuthor() );
		}
		return coAuthors;
	}

	public int getStartPage()
	{
		return startPage;
	}

	public void setStartPage( int startPage )
	{
		this.startPage = startPage;
	}

	public int getEndPage()
	{
		return endPage;
	}

	public void setEndPage( int endPage )
	{
		this.endPage = endPage;
	}

	public List<Author> getAuthors()
	{
		this.setAuthors();
		return authors;
	}

	// fill transient attributes
	public void setAuthors()
	{
		this.authors = new ArrayList<Author>();

		List<PublicationAuthor> publicationAuthorList = new ArrayList<PublicationAuthor>( this.publicationAuthors );

		// sort based on author position on paper
		Collections.sort( publicationAuthorList, new PublicationAuthorByPositionComparator() );

		for ( PublicationAuthor publicationAuthor : publicationAuthorList )
			authors.add( publicationAuthor.getAuthor() );
	}

	public void setAuthors( List<Author> authors )
	{
		this.authors = authors;
	}

	public String getCitedByUrl()
	{
		return citedByUrl;
	}

	public void setCitedByUrl( String citedByUrl )
	{
		this.citedByUrl = citedByUrl;
	}

	public String getAuthorText()
	{
		return authorText;
	}

	public void setAuthorText( String authorText )
	{
		this.authorText = authorText;
	}

	public String getYear()
	{
		return year;
	}

	public void setYear( String year )
	{
		this.year = year;
	}

	public String getPublisher()
	{
		return publisher;
	}

	public void setPublisher( String publisher )
	{
		this.publisher = publisher;
	}

	public boolean isPublicationTopicEverExtractedWith( ExtractionServiceType extractionServiceType )
	{
		if ( this.publicationTopics == null || this.publicationTopics.isEmpty() )
			return false;

		for ( PublicationTopic eachPublicationTopic : this.publicationTopics )
		{
			if ( eachPublicationTopic.getExtractionServiceType().equals( extractionServiceType ) && eachPublicationTopic.isValid() )
			{
				return true;
			}
		}

		return false;
	}

	public boolean isPublicationContainSourceFrom( SourceType sourceType )
	{
		if ( this.publicationSources == null || this.publicationSources.isEmpty() )
			return false;

		for ( PublicationSource eachPublicationSource : this.publicationSources )
		{
			if ( eachPublicationSource.getSourceType().equals( sourceType ) )
			{
				return true;
			}
		}

		return false;
	}

	public Set<Circle> getCircles()
	{
		return circles;
	}

	public void setCircles( Set<Circle> circles )
	{
		this.circles = circles;
	}

	public Set<UserPublicationBookmark> getUserPublicationBookmarks()
	{
		return userPublicationBookmarks;
	}

	public void setUserPublicationBookmarks( Set<UserPublicationBookmark> userPublicationBookmarks )
	{
		this.userPublicationBookmarks = userPublicationBookmarks;
	}

	public CompletionStatus getPublicationTypeStatus()
	{
		return publicationTypeStatus;
	}

	public void setPublicationTypeStatus( CompletionStatus publicationTypeStatus )
	{
		this.publicationTypeStatus = publicationTypeStatus;
	}

}


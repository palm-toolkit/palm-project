package de.rwth.i9.palm.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "academic_event" )
@Indexed
public class Event extends PersistableResource
{
	@Column
	private Date date;

	@Column( length = 10 )
	private String dateFormat;

	@Column
	@Lob
	@Field( index = Index.YES, analyze = Analyze.YES, store = Store.YES )
	@Analyzer( definition = "eventanalyzer" )
	private String name;

	@Column
	private java.sql.Timestamp crawlDate;

	@Column( length = 4 )
	@Field( index = Index.YES, analyze = Analyze.NO, store = Store.YES )
	private String year;

	/* from dblp */
	@Column
	private String dblpUrl;

	@Column( length = 10 )
	private String volume;

	@Column( name = "position_", columnDefinition = "int default 0" )
	private int position;

	@ManyToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
	@JoinColumn( name = "academic_event_group_id" )
	@IndexedEmbedded
	@Boost( 2.0f )
	private EventGroup eventGroup;

	@OneToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
	private Location location;

	// as List, therefore it can be sorted on hibernate query
	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "event" )
	private List<Publication> publications;

	@Column( columnDefinition = "bit default 0" )
	private boolean added = false;

	@Column( columnDefinition = "int default 0" )
	private int numberParticipant;

	@Column( columnDefinition = "int default 0" )
	private int numberPaper;

	@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true )
	private Set<EventInterestProfile> eventInterestProfiles;

	@Column( columnDefinition = "bit default 0" )
	private boolean isUpdateInterest = false;

	/* store any information in json format */
	@Column
	@Lob
	private String additionalInformation;

	public Date getDate()
	{
		return date;
	}

	public void setDate( Date date )
	{
		this.date = date;
	}

	public Location getLocation()
	{
		return location;
	}

	public void setLocation( Location location )
	{
		this.location = location;
	}

	public String getYear()
	{
		return year;
	}

	public void setYear( String year )
	{
		this.year = year;
	}

	public EventGroup getEventGroup()
	{
		return eventGroup;
	}

	public void setEventGroup( EventGroup eventGroup )
	{
		this.eventGroup = eventGroup;
	}

	public String getDblpUrl()
	{
		return dblpUrl;
	}

	public void setDblpUrl( String dblpUrl )
	{
		this.dblpUrl = dblpUrl;
	}

	public String getVolume()
	{
		return volume;
	}

	public void setVolume( String volume )
	{
		this.volume = volume;
	}

	public String getDateFormat()
	{
		return dateFormat;
	}

	public void setDateFormat( String dateFormat )
	{
		this.dateFormat = dateFormat;
	}

	public List<Publication> getPublications()
	{
		return publications;
	}

	public void setPublications( List<Publication> publications )
	{
		this.publications = publications;
	}

	public Event addPublication( Publication publication )
	{
		if ( this.publications == null )
			this.publications = new ArrayList<Publication>();

		this.publications.add( publication );

		return this;
	}

	public java.sql.Timestamp getCrawlDate()
	{
		return crawlDate;
	}

	public void setCrawlDate( java.sql.Timestamp crawlDate )
	{
		this.crawlDate = crawlDate;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public int getPosition()
	{
		return position;
	}

	public void setPosition( int position )
	{
		this.position = position;
	}

	public boolean isAdded()
	{
		return added;
	}

	public void setAdded( boolean added )
	{
		this.added = added;
	}

	public int getNumberParticipant()
	{
		return numberParticipant;
	}

	public void setNumberParticipant( int numberParticipant )
	{
		this.numberParticipant = numberParticipant;
	}

	public int getNumberPaper()
	{
		return numberPaper;
	}

	public void setNumberPaper( int numberPaper )
	{
		this.numberPaper = numberPaper;
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

	public Event addOrUpdateAdditionalInformation( String objectKey, Object objectValue )
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
			informationNode.putPOJO( objectKey, '"' + objectValue.toString() + '"' );
		else
			informationNode.putPOJO( objectKey, objectValue );

		this.additionalInformation = informationNode.toString();

		return this;
	}

	public Set<EventInterestProfile> getEventInterestProfiles()
	{
		return eventInterestProfiles;
	}

	public void setEventInterestProfiles( Set<EventInterestProfile> eventInterestProfiles )
	{
		this.eventInterestProfiles = eventInterestProfiles;
	}

	public Event addEventInterestProfiles( EventInterestProfile eventInterestProfile )
	{
		if ( this.eventInterestProfiles == null )
			this.eventInterestProfiles = new LinkedHashSet<EventInterestProfile>();

		this.eventInterestProfiles.add( eventInterestProfile );

		return this;
	}

	public EventInterestProfile getSpecificEventInterestProfile( String interestProfileName )
	{
		if ( interestProfileName == null || interestProfileName.equals( "" ) )
			return null;

		if ( this.eventInterestProfiles == null || this.eventInterestProfiles.isEmpty() )
			return null;

		for ( EventInterestProfile aip : this.eventInterestProfiles )
		{
			if ( aip.getName().equals( interestProfileName ) )
			{
				return aip;
			}
		}

		return null;
	}

	public boolean isUpdateInterest()
	{
		return isUpdateInterest;
	}

	public void setUpdateInterest( boolean isUpdateInterest )
	{
		this.isUpdateInterest = isUpdateInterest;
	}

}
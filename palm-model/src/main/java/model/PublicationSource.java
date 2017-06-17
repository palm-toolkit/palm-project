package de.rwth.i9.palm.model;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "publication_source" )
public class PublicationSource extends PersistableResource
{
	@Column
	@Lob
	private String title;

	/* comma separated author list */
	@Column
	@Lob
	private String coAuthors;

	/* blank space separated author list */
	@Column
	@Lob
	private String coAuthorsUrl;

	/* comma separated coauthor affiliation list */
	@Column
	@Lob
	private String authorAffiliation;

	@Column
	@Lob
	private String abstractText;

	@Column
	@Lob
	private String contentText;

	@Column
	@Lob
	private String keyword;
	
	@Column( length = 20 )
	private String date;

	@Column
	private String venue;

	@Column
	private String venueUrl;

	@Column
	private String venueTheme;

	@Column
	private String publicationType;
	
	@Column( length = 20 )
	private String pages;
	
	@Column
	private int citedBy;

	@Column
	private String sourceUrl;

	@Enumerated( EnumType.STRING )
	@Column( length = 16 )
	private SourceType sourceType;
	
	@Enumerated( EnumType.STRING )
	@Column( length = 16 )
	private SourceMethod sourceMethod;

	@Column
	@Lob
	private String mainSource;

	@Column
	@Lob
	private String mainSourceUrl;

	/* store any information in json format */
	@Column
	@Lob
	private String additionalInformation;

	/* store any citation information in jsonformat */
	@Column
	@Lob
	private String citedByUrl;

	@ManyToOne
	@JoinColumn( name = "publication_id" )
	private Publication publication;

	public Publication getPublication()
	{
		return publication;
	}

	public void setPublication( Publication publication )
	{
		this.publication = publication;
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

	public String getAuthorAffiliation()
	{
		return authorAffiliation;
	}

	public void setAuthorAffiliation( String authorAffiliation )
	{
		this.authorAffiliation = authorAffiliation;
	}

	public String getKeyword()
	{
		return keyword;
	}

	public void setKeyword( String keyword )
	{
		this.keyword = keyword;
	}

	public String getSourceUrl()
	{
		return sourceUrl;
	}

	public void setSourceUrl( String sourceUrl )
	{
		this.sourceUrl = sourceUrl;
	}

	public String getPublicationType()
	{
		return publicationType;
	}

	public void setPublicationType( String publicationType )
	{
		this.publicationType = publicationType;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate( String date )
	{
		this.date = date;
	}

	public String getPages()
	{
		return pages;
	}

	public void setPages( String pages )
	{
		this.pages = pages;
	}

	public int getCitedBy()
	{
		return citedBy;
	}

	public void setCitedBy( int citedBy )
	{
		this.citedBy = citedBy;
	}

	public SourceMethod getSourceMethod()
	{
		return sourceMethod;
	}

	public void setSourceMethod( SourceMethod sourceMethod )
	{
		this.sourceMethod = sourceMethod;
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

	public PublicationSource addOrUpdateAdditionalInformation( String objectKey, Object objectValue )
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

	public String getVenue()
	{
		return venue;
	}

	public void setVenue( String venue )
	{
		this.venue = venue;
	}

	public String getVenueUrl()
	{
		return venueUrl;
	}

	public void setVenueUrl( String venueUrl )
	{
		this.venueUrl = venueUrl;
	}

	public SourceType getSourceType()
	{
		return sourceType;
	}

	public void setSourceType( SourceType sourceType )
	{
		this.sourceType = sourceType;
	}

	public String getMainSource()
	{
		return mainSource;
	}

	public void setMainSource( String mainSource )
	{
		this.mainSource = mainSource;
	}

	public String getMainSourceUrl()
	{
		return mainSourceUrl;
	}

	public void setMainSourceUrl( String mainSourceUrl )
	{
		this.mainSourceUrl = mainSourceUrl;
	}

	public String getCoAuthors()
	{
		return coAuthors;
	}

	public void setCoAuthors( String coAuthors )
	{
		this.coAuthors = coAuthors;
	}

	public String getCoAuthorsUrl()
	{
		return coAuthorsUrl;
	}

	public void setCoAuthorsUrl( String coAuthorsUrl )
	{
		this.coAuthorsUrl = coAuthorsUrl;
	}

	public String getVenueTheme()
	{
		return venueTheme;
	}

	public void setVenueTheme( String venueTheme )
	{
		this.venueTheme = venueTheme;
	}

	public String getCitedByUrl()
	{
		return citedByUrl;
	}

	public void setCitedByUrl( String citedByUrl )
	{
		this.citedByUrl = citedByUrl;
	}

}
package de.rwth.i9.palm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.rwth.i9.palm.persistence.PersistableResource;

@Entity
@Table( name = "publication_file" )
public class PublicationFile extends PersistableResource
{
	@Column
	@Lob
	private String url;

	@Column
	private String source;
	
	@Enumerated( EnumType.STRING )
	@Column( length = 16 )
	private SourceType sourceType;

	@Enumerated( EnumType.STRING )
	@Column( length = 5 )
	private FileType fileType;

	@Column( columnDefinition = "bit default 0" )
	private boolean checked = false;
	
	@Column( columnDefinition = "bit default 0" )
	private boolean readable = false;
	
	@Column( columnDefinition = "bit default 0" )
	private boolean correctlyExtracted = false;
	
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

	public String getUrl()
	{
		return url;
	}

	public void setUrl( String url )
	{
		this.url = url;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource( String source )
	{
		this.source = source;
	}

	public SourceType getSourceType()
	{
		return sourceType;
	}

	public void setSourceType( SourceType sourceType )
	{
		this.sourceType = sourceType;
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked( boolean checked )
	{
		this.checked = checked;
	}

	public boolean isReadable()
	{
		return readable;
	}

	public void setReadable( boolean readable )
	{
		this.readable = readable;
	}

	public boolean isCorrectlyExtracted()
	{
		return correctlyExtracted;
	}

	public void setCorrectlyExtracted( boolean correctlyExtracted )
	{
		this.correctlyExtracted = correctlyExtracted;
	}

	public FileType getFileType()
	{
		return fileType;
	}

	public void setFileType( FileType fileType )
	{
		this.fileType = fileType;
	}
	
	public boolean isPublicationFileUrlContainsUrls( String[] arrayOfUrl )
	{
		if ( arrayOfUrl.length > 0 )
			for ( String eachUrl : arrayOfUrl )
			{
				if ( eachUrl.equals( "" ) )
					continue;
				if ( this.url.contains( eachUrl ) )
					return true;
			}
		return false;
	}

}
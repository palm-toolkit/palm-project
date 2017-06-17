package de.rwth.i9.palm.pdfextraction.service;

import java.util.ArrayList;
import java.util.List;

/**
 * This code is not used
 * 
 * @author Sigit
 *
 */
public class AcademicPublicationStructure
{
	public AcademicPublicationStructure()
	{
		readingPhase = new ArrayList<String>();
		readingPhase.add( "title" );
		readingPhase.add( "author" );
		readingPhase.add( "abstract" );
		readingPhase.add( "body" );
		readingPhase.add( "references" );
		this.setCurrentReadingPhaseIndex( 0 );
		// for the first time add titile section
		AcademicPublicationSection acs = new AcademicPublicationSection();
		acs.setHeader( "title" );
		acs.setStatus( "running" );
		this.addAcademicPubllicationSection( acs );
	}

	private List<AcademicPublicationSection> academicPublicationSections;

	private int currentReadingPhaseIndex;

	private List<String> readingPhase;

	public void incrementPhaseIndex()
	{
		this.currentReadingPhaseIndex++;
	}

	public int getCurrentReadingPhaseIndex()
	{
		return currentReadingPhaseIndex;
	}

	public void setCurrentReadingPhaseIndex( int currentReadingPhaseIndex )
	{
		this.currentReadingPhaseIndex = currentReadingPhaseIndex;
	}

	public List<AcademicPublicationSection> getAcademicPublicationSections()
	{
		return academicPublicationSections;
	}

	public void setAcademicPublicationSections( List<AcademicPublicationSection> academicPublicationSections )
	{
		this.academicPublicationSections = academicPublicationSections;
	}

	public AcademicPublicationStructure addAcademicPubllicationSection( AcademicPublicationSection aps )
	{
		if ( this.academicPublicationSections == null )
			this.academicPublicationSections = new ArrayList<AcademicPublicationSection>();
		this.academicPublicationSections.add( aps );

		return this;
	}

	public AcademicPublicationSection getSectionByHeader( String header )
	{
		for ( AcademicPublicationSection aps : this.academicPublicationSections )
			if ( aps.getHeader().equals( header ) )
				return aps;

		/* if not exist create new one */
		AcademicPublicationSection aps = new AcademicPublicationSection();
		aps.setHeader( header );
		aps.setStatus( "running" );
		this.academicPublicationSections.add( aps );
		return aps;
	}

	public List<String> getReadingPhase()
	{
		return readingPhase;
	}

	public void setReadingPhase( List<String> readingPhase )
	{
		this.readingPhase = readingPhase;
	}

	public AcademicPublicationSection getCurrentRunningSection()
	{
		for ( AcademicPublicationSection aps : this.academicPublicationSections )
			if ( aps.getStatus().equals( "running" ) )
				return aps;
		return null;
	}
}

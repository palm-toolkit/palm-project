package de.rwth.i9.palm.pdfextraction.service;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;

/**
 * This code is not used
 * 
 * This class is customized iTextPdf TextExtractionStrategy, designed for
 * detecting the structure of academic publication
 * 
 * @author sigit
 *
 */
public class CustomTextExtractionStrategy implements TextExtractionStrategy
{
	// check whether pdf can be read or not
	private boolean isPdfReadable;

	// store the last font type from previous loop
	private String lastFontType;

	// store the last font height from previous loop
	private float lastFontHeight;

	// store last bounding box bottom right coordinate
	private Vector lastCoordinateBottomRight;

	// the textResultant test
	private StringBuilder textResult;

	// store characters from previous loop
	private String lastTextCharacter;

	// document page number
	private int pageNumber;
	
	// document previous page number
	private int lastPageNumber;

	// store the pageSize
	private Rectangle pageSize;

	// page margin, determine whether the margin of a page
	private float pageMargin;

	private float commonHeaderHeight;

	// Container for academic pdf structure
	AcademicPublicationStructure academicPublicationStructure;

	private String lastWord;

	private StringBuilder lastLine;

	private StringBuilder lastContentSection;
	/**
	 * Enumeration for font type, such as normal, bold, etc
	 */
	private enum TextRenderMode
	{
		FILLTEXT(0), 
		STROKETEXT(1), 
		FILLTHENSTROKETEXT(2), 
		INVISIBLE(3), 
		FILLTEXTANDADDTOPATHFORCLIPPING(4), 
		STROKETEXTANDADDTOPATHFORCLIPPING(5), 
		FILLTHENSTROKETEXTANDADDTOPATHFORCLIPPING(6), 
		ADDTEXTTOPADDFORCLIPPING(7);

		private int value;

		private TextRenderMode( int value )
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}
	}

	/**
	 * Custom constructor
	 */
	public CustomTextExtractionStrategy()
	{
		textResult = new StringBuilder();
		lastLine = new StringBuilder();
		lastContentSection = new StringBuilder();
		lastCoordinateBottomRight = new Vector( 0f, 0f, 1f );
		isPdfReadable = true;
		lastWord = "";
		academicPublicationStructure = new AcademicPublicationStructure();
	}

	@Override
	public void beginTextBlock()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void endTextBlock()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void renderImage( ImageRenderInfo arg0 )
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Customize how the pdf have to be rendered
	 */
	@Override
	public void renderText( TextRenderInfo renderInfo )
	{
		// check whether letter position inside margin
		boolean isInsidePageMargin = false;
		
		// get the font type
		String currentFontType = renderInfo.getFont().getPostscriptFontName();

		// check whether the font type is recognized by the system
		if ( currentFontType.equals( "Unspecified Font Name" ) )
			isPdfReadable = false;

		// proceed only if pdf readable
		if ( isPdfReadable && !renderInfo.getText().equals( " " ) )
		{

			// IText read pdf file based on letter/chunk coordinate in rectangle
			Vector currentCoordinateBottomLeft = renderInfo.getBaseline().getStartPoint();
			Vector currentCoordinateTopRight = renderInfo.getAscentLine().getEndPoint();
			// get bottom right vector coordinate
			Vector currentCoordinateBottomRight = new Vector( currentCoordinateTopRight.get( 0 ), currentCoordinateBottomLeft.get( 1 ), 1.0f );
		
			// check whether the text is not in margin
			// (margin > x > document.width - margin)
			// ( margin > y > document.height - margin)
			if( currentCoordinateBottomLeft.get( 0 ) > this.pageMargin && currentCoordinateBottomRight.get( 0 ) < pageSize.getWidth() - pageMargin &&
				currentCoordinateBottomLeft.get( 1 ) > this.pageMargin && currentCoordinateBottomRight.get( 1 ) < pageSize.getHeight() - pageMargin ){
				isInsidePageMargin = true;
			}

			// only process text, if it's not in the page margin
			if( isInsidePageMargin ){
				// the current character
				StringBuilder currentCharacters = new StringBuilder();

				// check whether a blank space is needed
				boolean isBlankSpaceAdded = false;
				// flag whether a line is complete
				boolean isLastLineComplete = false;
				// flag section complete
				boolean isSectionComplete = false;
				// flag whether a word is complete
				boolean isWordSplitted = false;
				// Check if the bold font type is in used
				if ( ( renderInfo.getTextRenderMode() == (int) TextRenderMode.FILLTHENSTROKETEXT.getValue() ) )
					currentFontType += "+Bold";
				
				String currentReadingPhase = this.academicPublicationStructure.getReadingPhase().get( this.academicPublicationStructure.getCurrentReadingPhaseIndex() );
				// get font height
				float currentFontHeight = currentCoordinateTopRight.get( 1 ) - currentCoordinateBottomLeft.get( 1 );
				// get font space between previous and current
				float curSpaceWidth = 0f;
				// if character previous and current on the same line
				if ( currentCoordinateBottomRight.get( 1 ) == this.lastCoordinateBottomRight.get( 1 ) )
					curSpaceWidth = currentCoordinateBottomLeft.get( 0 ) - lastCoordinateBottomRight.get( 0 );
				else{
					// y coordinate
					if ( this.lastCoordinateBottomRight.get( 1 ) > 0 )
					{

						isLastLineComplete = true;
						// check whether a word is not complete
						if ( this.lastLine.charAt( this.lastLine.length() - 1 ) == '-' )
							isWordSplitted = true;
						else
							isBlankSpaceAdded = true;

						// put paragraph end detection

						// put subscript detection
					}
				}
				

				// if there is blank line
				// if space between last coordinate x and current x, larger than
				// 2x font size
				if ( lastCoordinateBottomRight.get( 1 ) - currentCoordinateBottomRight.get( 1 ) > 2 * currentFontHeight )
				{
					currentCharacters.append( "\n" );
					isBlankSpaceAdded = false;
				}
	
				// inserting blank space only if there is significant space
				// between previous and current character
				// but not too great in distance
				if ( ( this.lastTextCharacter != null && curSpaceWidth > 1f && curSpaceWidth < currentFontHeight ) || isBlankSpaceAdded )
					currentCharacters.append( ' ' );

				// if word is splitted
				if ( isWordSplitted )
					this.lastLine.setLength( this.lastLine.length() - 1 );

				// if a line is complete
				if ( isLastLineComplete )
				{
					this.textResult.append( this.lastLine.toString() );
					this.lastContentSection.append( this.lastLine.toString() );
					this.lastLine.setLength( 0 );
				}

				// if reading on title section
				if ( currentReadingPhase.equals( "title" ) )
				{
					// if font size any type change
					if ( this.lastCoordinateBottomRight.get( 1 ) > 0 )
						if ( !currentFontType.equals( this.lastFontType ) && currentFontHeight != this.lastFontHeight )
							isSectionComplete = true;

					if ( isSectionComplete )
					{
						this.academicPublicationStructure.getSectionByHeader( "title" ).addContent( this.lastContentSection.toString() ).setStatus( "complete" ).setPageNumber( pageNumber );
						this.lastContentSection.setLength( 0 );
						// change to next phase "author"
						this.academicPublicationStructure.incrementPhaseIndex();
					}
				}
				// if reading on author section
				else if ( currentReadingPhase.equals( "author" ) )
				{
					// if font size any type change
					if ( !currentFontType.equals( this.lastFontType ) && currentFontHeight != this.lastFontHeight )
						isSectionComplete = true;

					if ( isSectionComplete )
					{
						if ( this.lastContentSection.length() == 0 )
							this.lastContentSection.append( this.lastLine.toString() );

						String tempLine = this.lastContentSection.toString().toLowerCase();
						if ( tempLine.indexOf( "abstract" ) > 0 )
						{// if abstract found set author section complete and
							// prepare section for abstract
							AcademicPublicationSection apsAuthor = this.academicPublicationStructure.getCurrentRunningSection();
							apsAuthor.setStatus( "complete" );
							this.academicPublicationStructure.incrementPhaseIndex();
							// prepare section for abstract
							AcademicPublicationSection aps = new AcademicPublicationSection();
							aps.setHeader( this.lastContentSection.toString() );
							aps.setHeaderFontType( this.lastFontType );
							aps.setHeaderFontHeight( this.lastFontHeight );
							aps.setContentFontType( currentFontType );
							aps.setContentFontHeight( currentFontHeight );
							aps.setPageNumber( this.pageNumber );
							aps.setStatus( "running" );

							this.academicPublicationStructure.addAcademicPubllicationSection( aps );
						}
						else
						{
							this.academicPublicationStructure.getSectionByHeader( "author" ).addContent( this.lastContentSection.toString() );
						}
						this.lastContentSection.setLength( 0 );
					}

				}
				// if reading on abstract section
				else if ( currentReadingPhase.equals( "abstract" ) )
				{
					boolean isFoundHeader = false;
					if ( !currentFontType.equals( this.lastFontType ) && currentFontHeight != this.lastFontHeight && isLastLineComplete )
					{
						AcademicPublicationSection currentAPS = this.academicPublicationStructure.getCurrentRunningSection();
						// the state from header to normal text
						if ( currentFontHeight < currentAPS.getContentFontHeight() + 0.5f && currentFontHeight > currentAPS.getContentFontHeight() - 0.5f )
							isFoundHeader = true;

						if ( isFoundHeader )
						{
							AcademicPublicationSection apsLastSection = this.academicPublicationStructure.getCurrentRunningSection();
							apsLastSection.setStatus( "complete" );
							// prepare new section
							AcademicPublicationSection aps = new AcademicPublicationSection();
							aps.setHeader( this.lastContentSection.toString() );
							aps.setHeaderFontType( this.lastFontType );
							aps.setHeaderFontHeight( this.lastFontHeight );
							aps.setContentFontType( currentFontType );
							aps.setContentFontHeight( currentFontHeight );
							aps.setPageNumber( this.pageNumber );
							aps.setStatus( "running" );



							this.academicPublicationStructure.addAcademicPubllicationSection( aps );

							// check introduction section
							String tempLine = this.lastContentSection.toString().toLowerCase();
							if ( tempLine.indexOf( "introduction" ) > 0 )
							{
								this.academicPublicationStructure.incrementPhaseIndex();
								commonHeaderHeight = this.lastFontHeight;
							}
						}
						else
						{
							this.academicPublicationStructure.getCurrentRunningSection().addContent( this.lastContentSection.toString() );
						}
						this.lastContentSection.setLength( 0 );
					}
					// if found large empty line then change to new section
				}
				// if reading on references section
				else if ( currentReadingPhase.equals( "references" ) )
				{
					int k = 0;
				}
				else
				{ // actually this is for body phase
					boolean isFoundHeader = false;
					if ( !currentFontType.equals( this.lastFontType ) && currentFontHeight != this.lastFontHeight && isLastLineComplete )
					{
						//AcademicPublicationSection currentAPS = this.academicPublicationStructure.getCurrentRunningSection();
						// the state from header to normal text
						//if ( currentFontHeight < currentAPS.getContentFontHeight() + 0.5f && currentFontHeight > currentAPS.getContentFontHeight() - 0.5f )
						//	isFoundHeader = true;
						
						if ( this.lastFontHeight > this.commonHeaderHeight - 0.1 )
							isFoundHeader = true;

						if ( isFoundHeader )
						{
							AcademicPublicationSection apsLastSection = this.academicPublicationStructure.getCurrentRunningSection();
							apsLastSection.setStatus( "complete" );
							// prepare new section
							AcademicPublicationSection aps = new AcademicPublicationSection();
							aps.setHeader( this.lastContentSection.toString() );
							aps.setHeaderFontType( this.lastFontType );
							aps.setHeaderFontHeight( this.lastFontHeight );
							aps.setContentFontType( currentFontType );
							aps.setContentFontHeight( currentFontHeight );
							aps.setPageNumber( this.pageNumber );
							aps.setStatus( "running" );

							this.academicPublicationStructure.addAcademicPubllicationSection( aps );

							// check introduction section
							String tempLine = this.lastContentSection.toString().toLowerCase();
							if ( tempLine.indexOf( "references" ) > 0 )
								this.academicPublicationStructure.incrementPhaseIndex();

						}
						else
						{
							this.academicPublicationStructure.getCurrentRunningSection().addContent( this.lastContentSection.toString() );
						}
						this.lastContentSection.setLength( 0 );
					}
				}

				// Append the current text
				currentCharacters.append( renderInfo.getText() );
				// this.textResult.append( currentCharacters );
				this.lastLine.append( currentCharacters );
	
				// Set currently used properties
				this.lastFontHeight = currentFontHeight;
				this.lastFontType = currentFontType;
				this.lastTextCharacter = currentCharacters.toString();
	
				this.lastCoordinateBottomRight = currentCoordinateBottomRight;
				this.lastPageNumber = pageNumber;
			}
		}
	}

	@Override
	public String getResultantText()
	{
		return textResult.toString();
	}


	public void setPageNumber( int pageNumber )
	{
		this.pageNumber = pageNumber;
		// reset some properties
		this.lastTextCharacter = null;
		this.textResult.setLength( 0 );
	}

	public void setPageSize( Rectangle pageSize )
	{
		this.pageSize = pageSize;
	}

	public float getPageMargin()
	{
		return pageMargin;
	}

	public void setPageMargin( float pageMargin )
	{
		this.pageMargin = pageMargin;
	}

	public AcademicPublicationStructure getAcademicPublicationStructure()
	{
		return academicPublicationStructure;
	}

}

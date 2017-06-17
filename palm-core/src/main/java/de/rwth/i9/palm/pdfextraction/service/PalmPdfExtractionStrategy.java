package de.rwth.i9.palm.pdfextraction.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;

import de.rwth.i9.palm.utils.NumberUtils;

/**
 * This class is customized iTextPdf TextExtractionStrategy, designed for
 * detecting the structure of academic publication
 * 
 * @author sigit
 *
 */
public class PalmPdfExtractionStrategy implements TextExtractionStrategy
{
	// extraction phase
	private List<String> readingPhase = new ArrayList<String>( Arrays.asList( "title", "author", "abstract", "keyword", "content" ) );
	// extraction phase index
	private int readingPhaseIndex;
	private int titleIndex;
	// typically largest font size is on title;
	private float largestFontHeight;
	private float contentHeaderFontHeight;
	private float contentFontHeight;
	private float contentBlockSize;
	private boolean contentSplitted;
	// check whether pdf can be read or not
	private boolean isPdfReadable;

	// store the last font type from previous loop
	private String lastFontType;

	// store the last font height from previous loop
	private float lastFontHeight;

	// store last bounding box bottom right coordinate
	private int lastPageNumber;

	// store last bounding box bottom right coordinate
	private Vector lastCoordinateBottomRight;

	// the textResultant test
	private StringBuilder textResult;

	// document page number
	private int pageNumber;

	// store the pageSize
	private Rectangle pageSize;

	// page margin, determine whether the margin of a page
	private float pageMargin;

	// Container for academic pdf structure
	List<TextSection> textSections;
	TextSection textSection;

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
	public PalmPdfExtractionStrategy()
	{
		this.textResult = new StringBuilder();
		this.lastLine = new StringBuilder();
		this.lastContentSection = new StringBuilder();
		this.lastCoordinateBottomRight = new Vector( 0f, 0f, 1f );
		this.isPdfReadable = true;
		this.textSections = new ArrayList<TextSection>();
		this.lastPageNumber = 0;
		this.readingPhaseIndex = 0;
		this.titleIndex = 0;
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
	 * Customize how the pdf have to be extracted
	 */
	@Override
	public void renderText( TextRenderInfo renderInfo )
	{
		// flag whether letter position inside margin
		boolean isInsidePageMargin = false;
		
		// get the font type
		String currentFontType = renderInfo.getFont().getPostscriptFontName();

		// starting new page, prepare new textSection
		if ( this.lastPageNumber != this.pageNumber )
			if ( textSection == null )
				textSection = new TextSection();

		// check whether the font type is recognized by the system
		if ( currentFontType.equals( "Unspecified Font Name" ) )
			isPdfReadable = false;

		// proceed only if pdf readable
		// reject blank input, since space is generated from text distance
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

			// only process text, if it's not lies in the page margin
			if( isInsidePageMargin ){
				// the current character
				StringBuilder currentCharacters = new StringBuilder();

				// flag whether a line is complete
				boolean isLastLineComplete = false;
				// flag section complete
				boolean isSectionComplete = false;
				// flag whether a word is complete
				boolean isWordSplitted = false;
				// Check if the bold font type is in used
				if ( ( renderInfo.getTextRenderMode() == (int) TextRenderMode.FILLTHENSTROKETEXT.getValue() ) )
					currentFontType += "+Bold";
				
				// get font height
				float currentFontHeight = NumberUtils.round( currentCoordinateTopRight.get( 1 ) - currentCoordinateBottomLeft.get( 1 ), 2 );
				// get font space between previous and current
				float curSpaceWidth = 0f;
				// if character previous and current on the same line
				if ( currentCoordinateBottomRight.get( 1 ) == this.lastCoordinateBottomRight.get( 1 ) )
				{
					curSpaceWidth = currentCoordinateBottomLeft.get( 0 ) - lastCoordinateBottomRight.get( 0 );
					// if detect spacing
					if ( curSpaceWidth > 0.1 * currentFontHeight && curSpaceWidth < 3 * currentFontHeight )
						currentCharacters.append( ' ' );
					// detect huge spacing on same line
					else if ( curSpaceWidth > currentFontHeight )
						isSectionComplete = true;

					// detect text differences on the beginning of section 
					// only check first 20 letter
					// only check on author and abstract phases
					if ( this.lastLine.length() < 20 && this.lastContentSection.length() == 0 && !this.readingPhase.get( this.readingPhaseIndex ).equals( "content" ) )
					{
						// check if there is significant distance with previous section
						// indication the beginning of new section
//						if ( this.lastCoordinateBottomRight.get( 1 ) - currentCoordinateBottomRight.get( 1 ) > 2 * currentFontHeight )
//						{
						if ( currentFontHeight != this.lastFontHeight || !currentFontType.equals( this.lastFontType ) )
								isSectionComplete = true;
//						}
					}
				}
				// in different line
				else{
					// y coordinate difference value
					float yPosDifference = this.lastCoordinateBottomRight.get( 1 ) - currentCoordinateBottomRight.get( 1 );
					
					// -positive value, indicating
					// -- new line or new section
					// -- superscript
					if ( Math.abs( yPosDifference ) > this.contentFontHeight )
					{
						if ( yPosDifference > 0 )
						{
							// check for new line within paragraph
							if ( yPosDifference < 2 * currentFontHeight )
							{
								if ( currentFontHeight != this.lastFontHeight )
								{// check whether 2 section on same line
									// superscript detected
									if ( currentFontHeight > this.lastFontHeight )
									{
										// if just a superscript then
										if ( this.lastFontHeight + this.lastCoordinateBottomRight.get( 1 ) > 1.5 * currentFontHeight + currentCoordinateBottomRight.get( 1 ) )
											isSectionComplete = true;
									}
									else
									{
										isSectionComplete = true;
									}
								}
								else if ( !currentFontType.equals( this.lastFontType ) )
								{
									if ( this.lastFontType.contains( "Bold" ) && !currentFontType.contains( "Bold" ) )
										isSectionComplete = true;
								}
								else
								{
									isLastLineComplete = true;
									// check whether a word is not complete
									if ( this.lastLine.charAt( this.lastLine.length() - 1 ) == '-' )
										isWordSplitted = true;
									else
									{
										currentCharacters.append( ' ' );
									}
								}
							}
							else if ( yPosDifference > 2 * currentFontHeight )
								isSectionComplete = true;
						}
						// - negative value, indicating
						// -- subscript
						// -- new column
						else
						{
							// check if it#s not a subscript
							if ( Math.abs( yPosDifference ) > currentFontHeight )
								isSectionComplete = true;
							else
								currentCharacters.append( ' ' );
						}

						// second, checking paragraph base on end indentation
						if ( !isSectionComplete && this.lastContentSection.length() > 0 && this.readingPhase.get( this.readingPhaseIndex ).equals( "content" ) )
						{
							if ( Math.abs( this.lastCoordinateBottomRight.get( 0 ) - this.textSection.getBottomRightBoundary().get( 0 ) ) > 2 * this.contentFontHeight )
								isSectionComplete = true;
						}
					}

				}

				// if word is split, remove split sign "-"
				if ( isWordSplitted )
					this.lastLine.setLength( this.lastLine.length() - 1 );

				// if a line is complete or a section is complete
				if ( isLastLineComplete || isSectionComplete )
				{
					this.textResult.append( this.lastLine.toString() );
					this.lastContentSection.append( this.lastLine.toString() );
					if ( this.lastLine.length() > 0 )
					{
						this.textSection.addContentLine( this.lastLine.toString() );

						// update right most boundary ( find highest x)
						if ( this.textSection.getBottomRightBoundary().get( 0 ) < this.lastCoordinateBottomRight.get( 0 ) )
							this.textSection.setBottomRightBoundary( this.lastCoordinateBottomRight );

						// update left most boundary( find lowest x)
						if ( !isSectionComplete && this.textSection.getTopLeftBoundary().get( 0 ) > currentCoordinateBottomLeft.get( 0 ) )
						{
							// detect paragraph indent start
//							if ( this.textSection.getTopLeftBoundary().get( 0 ) - currentCoordinateBottomLeft.get( 0 ) > 1.5 * currentFontHeight )
//								isSectionComplete = true;
							// update left most boundary
							float topBoundary = this.textSection.getTopLeftBoundary().get( 1 );
							this.textSection.setTopLeftBoundary( new Vector( currentCoordinateBottomLeft.get( 0 ), topBoundary, 1f ) );
						}
						// check for paragraph or not
						if ( this.readingPhase.get( this.readingPhaseIndex ).equals( "content" ) )
						{
							float currentSectionBlockSize = NumberUtils.round( this.textSection.getBottomRightBoundary().get( 0 ) - this.textSection.getTopLeftBoundary().get( 0 ), 0 );
							// check if block size not identical to previous
							// paragraph
							if ( currentSectionBlockSize > this.contentBlockSize + 10 || currentSectionBlockSize < this.contentBlockSize - 10 )
								isSectionComplete = true;
						}
					}
					// reset
					this.lastLine.setLength( 0 );

				}

				if ( isSectionComplete )
				{
					if ( this.lastContentSection.toString().trim().length() > 0 )
					{
						// remove section name if content too short
						if ( this.lastContentSection.length() < 7 )
							this.textSection.setName( null );

						// assign content and other properties
						this.textSection.setContent( this.lastContentSection.toString() );
						this.textSection.setIndentEnd( this.lastCoordinateBottomRight.get( 0 ) );

						// update bottom right boundary ( find lowest y)
						if ( this.textSection.getBottomRightBoundary().get( 1 ) > this.lastCoordinateBottomRight.get( 1 ) )
						{
							float rightBoundary = this.textSection.getBottomRightBoundary().get( 0 );
							this.textSection.setBottomRightBoundary( new Vector( rightBoundary, this.lastCoordinateBottomRight.get( 1 ), 1f ) );
						}

						this.textSections.add( textSection );
					}
					// create new one and assign some properties
					this.textSection = new TextSection();
					this.textSection.setPageNumber( pageNumber );
					this.textSection.setTopLeftBoundary( new Vector( currentCoordinateBottomLeft.get( 0 ), currentCoordinateTopRight.get( 1 ), 1.0f ) );
					this.textSection.setFontHeight( currentFontHeight );
					this.textSection.setFontType( currentFontType );
					this.textSection.setIndentStart( currentCoordinateBottomLeft.get( 0 ) );

					// finding introduction/content loop through keyword phase
					if ( this.readingPhase.get( this.readingPhaseIndex ).equals( "content" ) )
					{
						if ( this.textSections.get( this.textSections.size() - 1 ).getFontHeight() == this.contentFontHeight )
						{
							float prevSectionBlockSize = NumberUtils.round( this.textSections.get( this.textSections.size() - 1 ).getBottomRightBoundary().get( 0 ) - this.textSections.get( this.textSections.size() - 1 ).getTopLeftBoundary().get( 0 ), 0 );
							// check if block size identical, give 10 pixel as
							// threshold
							if ( prevSectionBlockSize < this.contentBlockSize + 10 && prevSectionBlockSize > this.contentBlockSize - 10 )
							{
								if ( this.contentSplitted )
								{
									this.textSections.get( this.textSections.size() - 1 ).setName( "content-cont" );
									this.contentSplitted = false;
								}
								else
									this.textSections.get( this.textSections.size() - 1 ).setName( "content" );

								if ( NumberUtils.round( this.textSections.get( this.textSections.size() - 1 ).getBottomRightBoundary().get( 0 ), 0 ) - NumberUtils.round( this.textSections.get( this.textSections.size() - 1 ).getIndentEnd(), 0 ) < 5 )
								{
									this.contentSplitted = true;
								}
							}
							else
							{
								if ( this.textSections.get( this.textSections.size() - 1 ).getFontType().contains( "Bold" ) )
								{
									this.textSections.get( this.textSections.size() - 1 ).setName( "content-header" );
								}
							}
						}
						else
						{
							if ( this.textSections.get( this.textSections.size() - 1 ).getFontHeight() >= this.contentHeaderFontHeight && this.textSections.get( this.textSections.size() - 1 ).getContent().length() > 6 )
							{
								this.textSections.get( this.textSections.size() - 1 ).setName( "content-header" );
							}
						}
					}

					// finding introduction/content loop through keyword phase
					else if ( this.readingPhase.get( this.readingPhaseIndex ).equals( "keyword" ) )
					{
						if ( this.textSection.getFontHeight() >= this.contentFontHeight )
						{
							this.textSection.setName( "keyword" );
							if ( this.textSection.getFontHeight() == this.contentHeaderFontHeight )
							{
								this.textSection.setName( "keyword-header" );
							}
						}

						// found large text section
						if ( this.lastContentSection.length() > 300 )
						{
							// set initial content fontHeight
							this.contentFontHeight = this.textSections.get( this.textSections.size() - 1 ).getFontHeight();


							// get column width
							this.contentBlockSize = NumberUtils.round( this.textSections.get( this.textSections.size() - 1 ).getBottomRightBoundary().get( 0 ) - this.textSections.get( this.textSections.size() - 1 ).getTopLeftBoundary().get( 0 ), 0 );
							this.readingPhaseIndex = 4;// set to content
							// check previous section
							for ( int i = this.textSections.size() - 2; i > 0; i-- )
							{
								if ( this.textSections.get( i ).getFontHeight() == this.contentFontHeight )
								{
									float prevSectionBlockSize = NumberUtils.round( this.textSections.get( i ).getBottomRightBoundary().get( 0 ) - this.textSections.get( i ).getTopLeftBoundary().get( 0 ), 0 );
									// check if block size identical, give 10 pixel as threshold
									if ( prevSectionBlockSize < this.contentBlockSize + 10 && prevSectionBlockSize > this.contentBlockSize - 10 )
									{
										if ( NumberUtils.round( this.textSections.get( i ).getBottomRightBoundary().get( 0 ), 0 ) - NumberUtils.round( this.textSections.get( i ).getIndentEnd(), 0 ) < 5 )
										{
											this.textSections.get( i ).setName( "content-cont" );
											this.contentSplitted = true;
										}
									}
								}
								else if ( this.textSections.get( i ).getFontHeight() > this.contentFontHeight )
								{
									this.textSections.get( i ).setName( "content-header" );
									this.textSections.get( i + 1 ).setName( "content" );
									break;
								}
							}

							if ( contentSplitted )
							{
								this.textSections.get( this.textSections.size() - 1 ).setName( "content-cont" );
								this.contentSplitted = false;
							}
							else
								this.textSections.get( this.textSections.size() - 1 ).setName( "content" );
						}
					}

					// finding abstract loop at author section
					else if ( this.readingPhase.get( this.readingPhaseIndex ).equals( "author" ) )
					{
						this.textSection.setName( "author" );
						// found large text section
						if ( this.lastContentSection.length() > 280 )
						{
							// set last section label
							this.textSections.get( this.textSections.size() - 1 ).setName( "abstract" );
							// set prev text section to author
//							for ( int i = 1; i < this.textSections.size() - 2; i++ )
//							{
//								if ( !this.textSections.get( i ).getName().equals( "title" ) )
//								{
//									this.textSections.get( i ).setName( "author" );
//								}
//							}
							// set initial content fontHeight
							this.contentFontHeight = this.textSections.get( this.textSections.size() - 1 ).getFontHeight();

							if ( this.textSections.size() > 2 )
							{
								// set abstract header with 2 condition
								if ( this.textSections.get( this.textSections.size() - 2 ).getFontHeight() > this.textSections.get( this.textSections.size() - 1 ).getFontHeight() )
								{
									this.textSections.get( this.textSections.size() - 2 ).setName( "abstract-header" );
									//currentHeader = this.textSections.get( this.textSections.size() - 2 ).getContent();
								}
								if ( this.textSections.get( this.textSections.size() - 2 ).getFontHeight() == this.textSections.get( this.textSections.size() - 1 ).getFontHeight() && !this.textSections.get( this.textSections.size() - 2 ).getFontType().equals( this.textSections.get( this.textSections.size() - 1 ).getFontType() ) )
								{
									this.textSections.get( this.textSections.size() - 2 ).setName( "abstract-header" );
									//currentHeader = this.textSections.get( this.textSections.size() - 2 ).getContent();
								}
								// detect another reading phase
								if ( this.textSection.getFontHeight() >= this.contentFontHeight || this.textSection.getFontType().contains( "Bold" ) )
								{
									this.textSection.setName( "keyword-header" );
									this.contentHeaderFontHeight = this.textSection.getFontHeight();
									this.readingPhaseIndex = 3;// set to keyword phase
								}
								else
									this.readingPhaseIndex = 1;// set to author phase
							}
						}
					}

					// finding the title
					else if ( pageNumber == 1 && currentFontHeight < this.lastFontHeight && this.lastContentSection.length() > 10 )
					{
						if ( this.lastFontHeight > this.largestFontHeight )
						{
							this.largestFontHeight = this.lastFontHeight;
							// reset previous title, if any
							this.textSections.get( titleIndex ).setName( null );
							// set title to section
							this.titleIndex = this.textSections.size() - 1;
							this.textSections.get( titleIndex ).setName( "title" );

							this.textSection.setName( "author" );
							this.readingPhaseIndex = 1;
						}
					}
					// reset
					this.lastContentSection.setLength( 0 );

				}
				// Append the current text
				currentCharacters.append( renderInfo.getText() );
				// this.textResult.append( currentCharacters );
				this.lastLine.append( currentCharacters );
	
				// Set currently used properties
				this.lastFontHeight = currentFontHeight;
				this.lastFontType = currentFontType;
				this.lastPageNumber = pageNumber;
	
				this.lastCoordinateBottomRight = currentCoordinateBottomRight;
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

	public List<TextSection> getTextSections()
	{
		// put last text-section
		// assign content and other properties
		this.textSection.setContent( this.lastContentSection.toString() );
		this.textSection.setIndentEnd( this.lastCoordinateBottomRight.get( 0 ) );

		// update bottom right boundary ( find lowest y)
		if ( this.textSection.getBottomRightBoundary().get( 1 ) > this.lastCoordinateBottomRight.get( 1 ) )
		{
			float rightBoundary = this.textSection.getBottomRightBoundary().get( 0 );
			this.textSection.setBottomRightBoundary( new Vector( rightBoundary, this.lastCoordinateBottomRight.get( 1 ), 1f ) );
		}

		float prevSectionBlockSize = NumberUtils.round( this.textSection.getBottomRightBoundary().get( 0 ) - this.textSection.getTopLeftBoundary().get( 0 ), 0 );
		// check if block size identical, give 10 pixel as threshold
		if ( prevSectionBlockSize < this.contentBlockSize + 10 && prevSectionBlockSize > this.contentBlockSize - 10 )
		{
			this.textSection.setName( "content" );
		}

		this.textSections.add( textSection );
		return this.textSections;
	}

	public TextSection getTextSection()
	{

		return this.textSection;
	}


}

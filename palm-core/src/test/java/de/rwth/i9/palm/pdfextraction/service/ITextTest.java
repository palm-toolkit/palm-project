package de.rwth.i9.palm.pdfextraction.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Ignore;
import org.junit.Test;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class ITextTest
{
	/** The original PDF that will be parsed. */
	public static final String PREFACE = "C:/Users/nifry/Desktop/citeme.pdf";
	/** The resulting text file. */
	public static final String RESULT = "C:/Users/nifry/Desktop/preface.txt";


	/**
	 * Parses a PDF to a plain text file.
	 * 
	 * @param pdf
	 *            the original PDF
	 * @param txt
	 *            the resulting text
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void parsePdf() throws IOException
	{
		PdfReader reader = new PdfReader( PREFACE );
		PdfReaderContentParser parser = new PdfReaderContentParser( reader );
		PrintWriter out = new PrintWriter( new FileOutputStream( RESULT ) );
		TextExtractionStrategy strategy;
		System.out.println( "===================== TEST ONE ======================" );
		for ( int i = 1; i <= reader.getNumberOfPages(); i++ )
		{
			strategy = parser.processContent( i, new SimpleTextExtractionStrategy() );
			out.println( strategy.getResultantText() );
			System.out.println( strategy.getResultantText() );
			System.out.println( "========================================================" );
			System.out.println( "========================================================" );
			System.out.println( "========================================================" );
		}
		out.flush();
		out.close();
		reader.close();
	}

	@Test
	@Ignore
	public void test1pdfExtract() throws IOException
	{
		String src = "http://dspace.learningnetworks.org/bitstream/1820/3180/1/Chatti_ETS.pdf";

		PdfReader reader = new PdfReader( src );

		CustomTextExtractionStrategy customTextExtractionStrategy = new CustomTextExtractionStrategy();
		customTextExtractionStrategy.setPageMargin( 50f );

		Rectangle pdfPageSize = reader.getPageSize( 1 );
		customTextExtractionStrategy.setPageSize( pdfPageSize );

		PrintWriter out = new PrintWriter( new FileOutputStream( "C:\\Users\\nifry\\Desktop\\test.txt" ) );
		// Rectangle rect = new Rectangle( 70, 80, 490, 580 );
		// RenderFilter filter = new RegionTextRenderFilter( rect );
		System.out.println( "===================== TEST TWO ======================" );

		for ( int i = 1; i <= reader.getNumberOfPages(); i++ )
		{
			// strategy = new FilteredTextRenderListener(new
			// LocationTextExtractionStrategy(), filter);
			// update the current page size

			customTextExtractionStrategy.setPageNumber( i );
			customTextExtractionStrategy.setPageSize( pdfPageSize );

			out.println( PdfTextExtractor.getTextFromPage( reader, i, customTextExtractionStrategy ) );
			// System.out.println( PdfTextExtractor.getTextFromPage( reader, i,
			// customTextExtractionStrategy ) );
			System.out.println( "============= End Of Page Number " + i + " ===========" );
		}
		out.flush();
		out.close();
		AcademicPublicationStructure aps = customTextExtractionStrategy.getAcademicPublicationStructure();

		for ( AcademicPublicationSection as : aps.getAcademicPublicationSections() )
		{
			System.out.println( "header : " + as.getHeader() );
			if ( as.getContents() != null )
				for ( String ctn : as.getContents() )
					System.out.println( "content : " + ctn );

			System.out.println();
			System.out.println();
		}

		int i = 0;
	}
}

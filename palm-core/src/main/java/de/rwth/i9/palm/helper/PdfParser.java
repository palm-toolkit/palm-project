package de.rwth.i9.palm.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.SAXException;

public class PdfParser
{
	private final static int MAXIMUM_TEXT_CHUNK_SIZE = 1024 * 1000;
	public static PdfDetail parsePdf( File pdfFile ) throws IOException, SAXException, TikaException
	{
		PdfDetail pdfDetail = new PdfDetail();
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream( pdfFile );
		ParseContext pcontext = new ParseContext();

		// parsing the document using PDF parser
		PDFParser pdfparser = new PDFParser();
		pdfparser.parse( inputstream, handler, metadata, pcontext );

		// getting the content of the document
		pdfDetail.setHandler( handler );

		// getting metadata of the document
		pdfDetail.setMetadata( metadata );

		return pdfDetail;
	}

	public static List<String> parsePdfPerChunks( File pdfFile ) throws IOException, SAXException, TikaException
	{
		final List<String> chunks = new ArrayList<String>();
		chunks.add( "" );
		ContentHandlerDecorator handler = new ContentHandlerDecorator() {
			@Override
			public void characters( char[] ch, int start, int length )
			{
				String lastChunk = chunks.get( chunks.size() - 1 );
				String thisStr = new String( ch, start, length );

				if ( lastChunk.length() + length > MAXIMUM_TEXT_CHUNK_SIZE )
				{
					chunks.add( thisStr );
				}
				else
				{
					chunks.set( chunks.size() - 1, lastChunk + thisStr );
				}
			}
		};

		FileInputStream inputstream = new FileInputStream( pdfFile );
		AutoDetectParser parser = new AutoDetectParser();
		Metadata metadata = new Metadata();
		parser.parse( inputstream, handler, metadata );
		return chunks;
	}
}

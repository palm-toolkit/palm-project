package de.rwth.i9.palm.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.helper.OpenNlpResultHelper;
import de.rwth.i9.palm.helper.PdfDetail;
import de.rwth.i9.palm.helper.PdfParser;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
//import de.rwth.i9.palm.analytics.api.PalmAnalytics;

@Controller
@RequestMapping( value = "/analytics" )
public class AnalyticsController
{

	// logger
	static private final Logger logger = LoggerFactory.getLogger( AnalyticsController.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private PalmAnalytics palmAnalytics;

	@RequestMapping( value = "/dialog", method = RequestMethod.GET )
	@Transactional
	public ModelAndView landing( @RequestParam( value = "sessionid", required = false ) final String sessionId, final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = new ModelAndView( "getAnalyticsView", "link", "analytics" );

		if ( sessionId != null && sessionId.equals( "0" ) )
			response.setHeader( "SESSION_INVALID", "yes" );

		return model;
	}

	@RequestMapping( value = "/run", method = RequestMethod.POST )
	@Transactional
	public ModelAndView runAnalytics( final HttpServletResponse response, @RequestParam( value = "content" ) final String stringContent )
	{
		ModelAndView model = new ModelAndView( "getAnalyticsResult", "link", "analytics" );
		model.addObject( "stringContent", stringContent );

		String[] sentences = palmAnalytics.getOpenNLPTool().detectSentences( stringContent );
		List<String> listSentences = Arrays.asList( sentences );
		List<OpenNlpResultHelper> onrhs = new ArrayList<OpenNlpResultHelper>();
		model.addObject( "listSentences", listSentences );

		for ( String sentence : sentences )
		{
			OpenNlpResultHelper onrh = new OpenNlpResultHelper();
			String[] tokenizeSentence = palmAnalytics.getOpenNLPTool().tokenize( sentence );
			String[] posTaggerSentence = palmAnalytics.getOpenNLPTool().tagPartOfSpeech( tokenizeSentence );
			List<String> nounPhraseChunked = palmAnalytics.getOpenNLPTool().nounPhraseExtractor( tokenizeSentence, posTaggerSentence );


			onrh.setSentence( sentence );
			onrh.setTokenizedSentence( Arrays.asList( tokenizeSentence ) );
			onrh.setPosTaggedSentence( Arrays.asList( posTaggerSentence ) );
			onrh.setNounPhraseChunked( nounPhraseChunked );

			onrhs.add( onrh );
		}

		model.addObject( "onrhs", onrhs );

		return model;
	}

	/**
	 * Upload article via jquery ajax file upload saved to database
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws TikaException
	 * @throws SAXException
	 */
	@RequestMapping( value = "/upload", method = RequestMethod.POST )
	public @ResponseBody String multiUpload( MultipartHttpServletRequest request, HttpServletResponse response ) throws IOException, SAXException, TikaException
	{
		return uploadTest( request, true, false );
	}

	/**
	 * Upload article via jquery ajax file upload saved to database
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws TikaException
	 * @throws SAXException
	 */
	@RequestMapping( value = "/uploadpreview", method = RequestMethod.POST )
	public @ResponseBody String multiUploadPreview( MultipartHttpServletRequest request, HttpServletResponse response ) throws IOException, SAXException, TikaException
	{
		return uploadTest( request, false, false );
	}

	private String uploadTest( MultipartHttpServletRequest request, boolean saveToDb, boolean saveToSystem ) throws IOException, SAXException, TikaException
	{
		// get full path
		String fullPath = request.getSession().getServletContext().getRealPath( "/" );

		// build an iterator
		Iterator<String> itr = request.getFileNames();
		MultipartFile mpf = null;

		Publication publication = new Publication();

		// get each file
		while ( itr.hasNext() )
		{
			// get next MultipartFile
			mpf = request.getFile( itr.next() );
			// upload file and get the file back

			File convFile = convert( mpf );

			PdfDetail pdfDetail = PdfParser.parsePdf( convFile );

			List<String> chunkedFile = PdfParser.parsePdfPerChunks( convFile );

			int i = 0;

			String patternString = "[\\r\\n]+abstract";
			Pattern pattern = Pattern.compile( patternString, Pattern.CASE_INSENSITIVE );

			String[] split = pattern.split( chunkedFile.get( 0 ) );

			System.out.println( "split.length = " + split.length );

			for ( String element : split )
			{
				System.out.println( "element = " + element );
				System.out.println( "==============================================" );
			}
			if ( saveToDb )
			{
				// persist publication here
			}
		}
		return "success";
	}

	public File convert( MultipartFile file ) throws IOException
	{
		File convFile = new File( file.getOriginalFilename() );
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream( convFile );
		fos.write( file.getBytes() );
		fos.close();
		return convFile;
	}

}
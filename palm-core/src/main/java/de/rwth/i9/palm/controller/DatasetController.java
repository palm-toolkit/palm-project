package de.rwth.i9.palm.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.rwth.i9.palm.persistence.PersistenceStrategy;
//import de.rwth.i9.palm.analytics.api.PalmAnalytics;

@Controller
@RequestMapping( value = "/dataset" )
public class DatasetController
{

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	/*@Autowired
	private PalmAnalytics analytics;*/

	@RequestMapping( method = RequestMethod.GET )
	@Transactional
	public ModelAndView landing( @RequestParam( value = "sessionid", required = false ) final String sessionId, final HttpServletResponse response ) throws InterruptedException
	{
		ModelAndView model = new ModelAndView( "dataset", "link", "dataset" );

		if ( sessionId != null && sessionId.equals( "0" ) )
			response.setHeader( "SESSION_INVALID", "yes" );

		// Publication pub = new Publication();
		// pub.setAbstractOriginal( "something" );
		// persistenceStrategy.getPublicationDAO().persist( pub );

		// System.out.println( analytics.getCValueAlgorithm().test( "cvaluetest"
		// ) );

		// PublicationOld pubOld = new PublicationOld();
		// pubOld.setAuthors( "test" );
		// persistenceStrategy.getPublicationOldDAO().persist( pubOld );
		
		//List<PublicationOld> pubOlds = persistenceStrategy.getPublicationOldDAO().getAll();

		//for ( PublicationOld pubOld : pubOlds )
		//{
			//String s = pubOld.getAbstractText();
		/*
		 * s = s.replace( "Ã¶", "ö" ) .replace( "Ã©", "é" ) .replace( "Ã¼", "ü"
		 * ) .replace( "Ã¤", "ä" ) .replace( "ÃŸ", "ß" ) .replace( "Ã­", "i" )
		 * .replace( "Ã¸", "ø" ) .replace( "Ã¡", "á" ) .replace( "Ã¯", "ï" )
		 * .replace( "Ã§", "ç" ) .replace( "Ã¥", "å" ) .replace( "Ã±", "ñ" )
		 * .replace( "Ã±", "ñ" ) .replace( "Ãº", "ú" ) .replace( "Ã«", "ë" )
		 * .replace( "Ã³", "ó" ) .replace( "Ã¨", "è" ) .replace( "Ã–", "Ö" );
		 * 
		 * 
		 * 
		 * s = s.replace( "Ã®", "î" ) .replace( "Ã?", "A" );
		 * 
		 * 
		 * s = s.replace( "Ã…", "A" ) .replace( "Ã²", "ò" ) .replace( "Ã˜", "Ø"
		 * ) .replace( "Ã‡", "Ç" ) .replace( "Ã…", "Å" ) .replace( "Ã‰", "É" );
		 * s = s.replace( "Â´", "ô" ) .replace( "Ã", "Á" );
		 */
			
		/*
		 * s = s.replace( "Abstractâ€”", "" ) .replace( "Abstract", "" );
		 */
			
			//s = s.replace( "ABSTRACT", "" );
					
			//pubOld.setAbstractText( s );
			//persistenceStrategy.getPublicationOldDAO().persist( pubOld );
			//System.out.println( s );
			
			
		//}
		
		// PublicationOld pubOld = new PublicationOld();
		// pubOld.setAbstractText( "something" );
		// persistenceStrategy.getPublicationOldDAO().persist( pubOld );

		return model;
	}

}
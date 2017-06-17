package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth.i9.palm.model.PublicationType;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceProperty;
import de.rwth.i9.palm.model.SourceType;

public class DblpEventCollection extends PublicationCollection
{
	private final static Logger log = LoggerFactory.getLogger( DblpEventCollection.class );

	public DblpEventCollection()
	{
		super();
	}

	/**
	 * get publication list on a venue
	 * 
	 * @param url
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Object> getEventDetailByVenueUrl( String url, Source source ) throws IOException
	{
		Map<String, Object> venueInformationMap = new LinkedHashMap<String, Object>();

		// Using jsoup java html parser library
		Document document = PublicationCollectionHelper.getDocumentWithJsoup( url, 5000, getDblpCookie( source ) );

		if ( document == null )
			return Collections.emptyMap();

		Element mainContainer = document.select( "#main" ).first();

		if ( mainContainer == null )
		{
			log.info( "Main container not found " );
			return Collections.emptyMap();
		}


		List<Object> publicationList = new ArrayList<Object>();

		// information taken from header
		Map<String, String> headerInformation = null;
		String mainHeaderText = null;

		for ( Element element : mainContainer.children() )
		{
			if ( element.tagName().equals( "header" ) )
			{
				// main header
				if ( element.hasClass( "headline" ) )
				{
					mainHeaderText = element.text();
				}
				// sub header
				else
				{
					Element h2Header = element.select( "h2" ).first();
					if ( h2Header != null )
					{
						if ( venueInformationMap.get( "type" ).equals( PublicationType.JOURNAL ) )
						{
							headerInformation = new LinkedHashMap<String, String>();
							// typical journal header "Volume 16, Number 4,
							// 2012"
							String[] headerSplit = h2Header.text().trim().split( "," );
							if ( headerSplit.length == 3 )
							{
								if ( headerSplit[0].length() > 9 )
									headerInformation.put( "volume", headerSplit[0].substring( 8 ) );
								if ( headerSplit[1].length() > 9 )
									headerInformation.put( "number", headerSplit[1].substring( 8 ) );

								headerInformation.put( "datePublished", standardizeDblpDate( headerSplit[2].trim() ) );
							}
						}
						else if ( venueInformationMap.get( "type" ).equals( PublicationType.CONFERENCE ) )
						{
							headerInformation.put( "conferenceTheme", h2Header.text().trim() );
						}
					}
				}
			}
			else if ( element.tagName().equals( "ul" ) )
			{
				if ( element.attr( "class" ).equals( "publ-list" ) )
				{
					for ( Element publicationElement : element.children() )
					{
						Map<String, String> publicationDetails = null;
						if ( venueInformationMap.get( "type" ).equals( PublicationType.JOURNAL ) )
						{
							// get publication list from journal
							if ( publicationElement.attr( "class" ).contains( "article" ) )
								publicationDetails = getDblpJournalPublication( publicationElement );
							else if ( publicationElement.attr( "class" ).contains( "informal" ) )
								publicationDetails = getDblpInformalPublication( publicationElement, PublicationType.JOURNAL );
						}
						else if ( venueInformationMap.get( "type" ).equals( PublicationType.CONFERENCE ) )
						{
							// get publication list from conference
							if ( publicationElement.attr( "class" ).contains( "inproceedings" ) )
								publicationDetails = getDblpConferencePublication( publicationElement );
							else if ( publicationElement.attr( "class" ).contains( "informal" ) )
								publicationDetails = getDblpInformalPublication( publicationElement, PublicationType.CONFERENCE );
							else if ( publicationElement.attr( "class" ).contains( "editor" ) ){
								publicationDetails = getDblpEditorshipPublication( publicationElement );
								//TODO: get conference theme and date from editorship title
								headerInformation = new LinkedHashMap<String, String>();
								headerInformation.put( "datePublished", getDateFromEdithorshipTitle( publicationDetails.get( "title" ), (String) venueInformationMap.get( "year" ) ) );
							}
						}

						// add header detail into publication information
						if ( headerInformation != null )
							publicationDetails.putAll( headerInformation );
						
						// check if publication title, usually only contain "Editorial"
						// there  are a lot title like this that will messed up publication result
						// just skip title which are too short
						if( publicationDetails.get( "title" ).length() < 15 )
							continue;

						// put into List
						publicationList.add( publicationDetails );
					}
				}
			}
			else if ( element.tagName().equals( "div" ) )
			{
				if ( element.attr( "id" ) != null && element.attr( "id" ).equals( "breadcrumbs" ) )
				{
					String breadCrumbsLabel = element.text();
					if ( breadCrumbsLabel.toLowerCase().contains( "journals" ) )
						venueInformationMap.put( "type", PublicationType.JOURNAL );
					else if ( breadCrumbsLabel.toLowerCase().contains( "conferences" ) )
						venueInformationMap.put( "type", PublicationType.CONFERENCE );
					
					// extract conference/journal main url
					String mainUrl = element.select( "li > span > span > a" ).first().absUrl( "href" );
					venueInformationMap.put( "main-url", mainUrl );

					// extract header information
					venueInformationMap.putAll( getDblpMainHeaderInformation( mainHeaderText, (PublicationType) venueInformationMap.get( "type" ) ) );
				}
			}
		}

		venueInformationMap.put( "publications", publicationList );

		return venueInformationMap;
	}

	private static Map<String, Object> getDblpMainHeaderInformation( String mainHeaderText, PublicationType publicationType )
	{
		Map<String, Object> mainHeaderInformation = new LinkedHashMap<String, Object>();
		if ( publicationType.equals( PublicationType.JOURNAL ) )
		{
			/// get journal name and volume
			/// e.g. Machine Learning, Volume 87
			String[] mainHeaderTextArray = mainHeaderText.split( "," );
			if ( mainHeaderTextArray.length > 1 )
			{
				mainHeaderInformation.put( "name", mainHeaderTextArray[0].trim() );
				if ( mainHeaderTextArray[1].length() > 7 )
					mainHeaderInformation.put( "volume", mainHeaderTextArray[1].substring( 7 ).trim() );
			}

		}
		else if ( publicationType.equals( PublicationType.CONFERENCE ) )
		{
			/// get conference name, year, city and country
			/// e.g. 7. CSEDU 2015: Lisbon, Portugal
			// first split between conference and location
			String[] mainHeaderTextArray = mainHeaderText.split( ":" );

			if ( mainHeaderTextArray.length == 2 )
			{
				/// get conference name and year
				/// e.g. 7. CSEDU 2015
				String venueNameAndYear = null;
				if ( mainHeaderTextArray[0].contains( "." ) )
				{
					// remove venue number e.g. 7. CSEDU 2015 to CSEDU 2015
					String[] venueNamePart = mainHeaderTextArray[0].split( "\\." );
					if ( venueNamePart.length == 2 )
						venueNameAndYear = venueNamePart[1].trim();
				}
				else
					venueNameAndYear = mainHeaderTextArray[0].trim();

				// get year and name
				if ( venueNameAndYear.length() > 6 )
				{
					mainHeaderInformation.put( "name", venueNameAndYear.substring( 0, venueNameAndYear.length() - 5 ) );
					mainHeaderInformation.put( "year", venueNameAndYear.substring( venueNameAndYear.length() - 4 ) );
				}

				/// get city, state and country
				/// e.g. Lisbon, Portugal

				// first check, if there is other unnecessary information
				// e.g. Lisbon, Portugal - Workshop
				String venueCityAndCountry = null;
				if ( mainHeaderTextArray[1].contains( "-" ) )
				{
					int dashIndex = mainHeaderTextArray[1].indexOf( " -" );
					venueCityAndCountry = mainHeaderTextArray[1].substring( 0, dashIndex ).trim();
				}
				else
					venueCityAndCountry = mainHeaderTextArray[1].trim();

				// get city and country
				String[] venueCityAndCountryArray = venueCityAndCountry.split( "," );
				if ( venueCityAndCountryArray.length > 1 )
				{
					if ( venueCityAndCountryArray.length == 2 )
					{
						mainHeaderInformation.put( "city", venueCityAndCountryArray[0].trim() );
						mainHeaderInformation.put( "country", venueCityAndCountryArray[1].trim() );
					}
					else
					{
						mainHeaderInformation.put( "city", venueCityAndCountryArray[0].trim() );
						mainHeaderInformation.put( "state", venueCityAndCountryArray[1].trim() );
						mainHeaderInformation.put( "country", venueCityAndCountryArray[2].trim() );
					}
				}
			}

		}
		return mainHeaderInformation;
	}

	/**
	 * Get publication general information from DBLP
	 * 
	 * @param publicationElement
	 * @param publicationDetails
	 */
	private static void getDblpPublicationInformationInGeneral( Element publicationElement, Map<String, String> publicationDetails )
	{
		// get original source (PDF or a webpage)
		Element sourceElementContainer = publicationElement.select( "nav.publ" ).select( "li" ).first();
		Elements sourceElements = sourceElementContainer.select( "div.body" ).first().select( "a" );
		if ( sourceElements != null && sourceElements.size() > 0 )
		{
			String docUrl = "";
			String doc = "";
			for ( Element sourceElement : sourceElements )
			{
				docUrl += sourceElement.absUrl( "href" ) + " ";
				doc += sourceElement.text().replace( ",", "" ) + ",";
			}
			publicationDetails.put( "doc", doc.substring( 0, doc.length() - 1 ) );
			publicationDetails.put( "doc_url", docUrl.substring( 0, docUrl.length() - 1 ) );
		}

		// get container, where all of information resides
		Element dataElement = publicationElement.select( "div.data" ).first();

		// get list of author in comma separated, together with author link
		Elements authorElements = dataElement.select( "[itemprop=author]" );
		String authorNames = "";
		String authorUrl = "";
		for ( Element authorElement : authorElements )
		{
			authorNames += authorElement.text().replace( ",", " " ) + ",";
			Element authorUrlElement = authorElement.select( "a" ).first();
			if ( authorUrlElement != null )
				authorUrl += authorUrlElement.absUrl( "href" ) + " ";
			else
				authorUrl += "null ";
		}
		if ( !authorNames.equals( "" ) )
		{
			publicationDetails.put( "coauthor", authorNames.substring( 0, authorNames.length() - 1 ) );
			publicationDetails.put( "coauthorUrl", authorUrl );
		}

		// other general information
		publicationDetails.put( "title", dataElement.select( "span.title" ).text() );
		publicationDetails.put( "source", SourceType.DBLP.toString() );
	}

	/**
	 * Part of code that extract Publication with type Editorship
	 * 
	 * @param publicationElement
	 * @return
	 * @throws IOException
	 */
	private static Map<String, String> getDblpEditorshipPublication( Element publicationElement ) throws IOException
	{
		Map<String, String> publicationDetails = new LinkedHashMap<String, String>();

		// first, extract general information on DBLP publication
		getDblpPublicationInformationInGeneral( publicationElement, publicationDetails );

		// second, extract specific information which is only available for
		// editorship.
		publicationDetails.put( "type", PublicationType.EDITORSHIP.toString() );
		// get container, where all of information resides
		Element dataElement = publicationElement.select( "div.data" ).first();
		
		publicationDetails.put( "book-series", dataElement.select( "a" ).select( "[itemtype=http://schema.org/BookSeries]" ).text() );
		//publicationDetails.put( "book-page", dataElement.select( "" ).text() );
		publicationDetails.put( "book-publisher", dataElement.select( "[itemprop=publisher]" ).text() );
		publicationDetails.put( "book-date-published", dataElement.select( "[itemprop=datePublished]" ).text() );
		publicationDetails.put( "book-isbn", dataElement.select( "[itemprop=isbn]" ).text() );

		return publicationDetails;
	}

	/**
	 * Part of code that extract Publication with type Conference
	 * 
	 * @param publicationElement
	 * @return
	 * @throws IOException
	 */
	private static Map<String, String> getDblpConferencePublication( Element publicationElement ) throws IOException
	{
		Map<String, String> publicationDetails = new LinkedHashMap<String, String>();

		// first, extract general information on DBLP publication
		getDblpPublicationInformationInGeneral( publicationElement, publicationDetails );

		// second, extract specific information which is only available for
		// conference.
		publicationDetails.put( "type", PublicationType.CONFERENCE.toString() );
		// get container, where all of information resides
		Element dataElement = publicationElement.select( "div.data" ).first();
		publicationDetails.put( "pages", dataElement.select( "[itemprop=pagination]" ).text() );

		return publicationDetails;
	}

	/**
	 * Part of code that extract Publication with type Informal
	 * 
	 * @param publicationElement
	 * @return
	 * @throws IOException
	 */
	private static Map<String, String> getDblpInformalPublication( Element publicationElement, PublicationType publicationType ) throws IOException
	{
		Map<String, String> publicationDetails = new LinkedHashMap<String, String>();

		// first, extract general information on DBLP publication
		getDblpPublicationInformationInGeneral( publicationElement, publicationDetails );

		// second, extract specific information which is only available for
		// conference.
		publicationDetails.put( "type", publicationType.toString() );
		// get container, where all of information resides
		Element dataElement = publicationElement.select( "div.data" ).first();
		publicationDetails.put( "pages", dataElement.select( "[itemprop=pagination]" ).text() );

		return publicationDetails;
	}

	/**
	 * Part of code that extract Publication with type Journal
	 * 
	 * @param publicationElement
	 * @return
	 * @throws IOException
	 */
	private static Map<String, String> getDblpJournalPublication( Element publicationElement ) throws IOException
	{
		Map<String, String> publicationDetails = new LinkedHashMap<String, String>();

		// first, extract general information on DBLP publication
		getDblpPublicationInformationInGeneral( publicationElement, publicationDetails );

		// second, extract specific information which is only available for
		// journal.
		publicationDetails.put( "type", PublicationType.JOURNAL.toString() );

		// get container, where all of information resides
		Element dataElement = publicationElement.select( "div.data" ).first();
		publicationDetails.put( "pages", dataElement.select( "[itemprop=pagination]" ).text() );

		return publicationDetails;
	}

	/**
	 * Convert date on DBLP into standardize format yyyy/M
	 * 
	 * @param stringDate
	 * @return
	 */
	private static String standardizeDblpDate( String stringDate )
	{
		// return Null for false input
		if ( stringDate.length() < 4 )
			return null;
		// only contain year
		if ( stringDate.length() == 4 )
			return stringDate;

		stringDate = stringDate.toLowerCase();

		String month = "1";
		String year = stringDate.substring( stringDate.length() - 4, stringDate.length() );

		// months and quarters in number
		if ( stringDate.startsWith( "feb" ) )
			month = "2";
		else if ( stringDate.startsWith( "mar" ) )
			month = "3";
		else if ( stringDate.startsWith( "apr" ) || stringDate.startsWith( "sec" ) )
			month = "4";
		else if ( stringDate.startsWith( "may" ) )
			month = "5";
		else if ( stringDate.startsWith( "jun" ) )
			month = "6";
		else if ( stringDate.startsWith( "jul" ) || stringDate.startsWith( "thi" ) )
			month = "7";
		else if ( stringDate.startsWith( "aug" ) )
			month = "8";
		else if ( stringDate.startsWith( "sep" ) )
			month = "9";
		else if ( stringDate.startsWith( "oct" ) || stringDate.startsWith( "fou" ) )
			month = "10";
		else if ( stringDate.startsWith( "nov" ) )
			month = "11";
		else if ( stringDate.startsWith( "dec" ) )
			month = "12";

		return year + "/" + month;
	}

	/**
	 * Find string month on title and return in M format
	 * 
	 * @param editorshipTitle
	 * @return
	 */
	private static String getDateFromEdithorshipTitle( String editorshipTitle, String year )
	{
		// e.g. 23-25 May, 2015
		// e.g. 31. August - 3. September 2014
		// e.g. September 1, 2015
		String month = null;
		String day = null;
		editorshipTitle = editorshipTitle.toLowerCase();

		String[] months = { "january", "february", "march", "april", "may", "june", "july", "august", " september", "october", "november", "december" };

		for ( int i = 0; i < months.length; i++ )
		{
			int monthIndex = editorshipTitle.indexOf( months[i] );
			if ( monthIndex > -1 )
			{
				if ( monthIndex < 10 )
					continue;

				// get month
				month = Integer.toString( i + 1 );
				// get day
				String date = editorshipTitle.substring( monthIndex - 6, monthIndex + months[i].length() + 3 );
				// remove any non number and multiple spaces
				date = date.replaceAll( "[^0-9]", " " ).replaceAll( " +", " " );
				String[] dateArray = date.split( " " );
				if ( dateArray.length > 0 )
				{
					for ( int j = 0; j < dateArray.length; j++ )
					{
						if ( !dateArray[j].equals( "" ) && dateArray[j].length() < 3 )
						{
							day = dateArray[j];
							break;
						}
					}
				}
			}
		}

		if ( month != null && day != null )
			return year + "/" + month + "/" + day;
		else if ( month != null && day == null )
			return year + "/" + month;

		return year;
	}

	/**
	 * DBLP cache, important for select correct DBLP page before crawling
	 * 
	 * @return
	 */
	private static Map<String, String> getDblpCookie( Source source )
	{
		Map<String, String> cookies = new HashMap<String, String>();

		if ( source != null )
			for ( SourceProperty sourceProperty : source.getSourceProperties() )
			{
				if ( sourceProperty.getMainIdentifier().equals( "cookie" ) && sourceProperty.isValid() )
					cookies.put( sourceProperty.getSecondaryIdentifier(), sourceProperty.getValue() );
			}
		return cookies;
	}

	/**
	 * Searching venues on DBLP
	 * 
	 * @param type
	 */
	public static List<Object> getEventFromDBLPSearch( String query, String type, Source source )
	{
		// the url of querying venue will be
		String url = "http://dblp.uni-trier.de/search/venue?q=" + query.replace( " ", "+" );

		// The Map will be in format[ venue name, venue url] map
		List<Object> venueListObject = new ArrayList<Object>();

		// Using jsoup java html parser library
		Document document = PublicationCollectionHelper.getDocumentWithJsoup( url, 5000, getDblpCookie( source ) );

		if ( document == null )
			return Collections.emptyList();

		// find out page is author page or search page
		if ( document.baseUri().equals( url ) )
		{

			Element venueListUl = document.select( "#completesearch-venues>div" ).first().select( "ul" ).first();

			if ( venueListUl == null )
				return Collections.emptyList();

			for ( Element venueLi : venueListUl.children() )
			{
				Element venueAHref = venueLi.select( "a" ).first();

				if ( venueAHref == null )
					continue;

				Map<String, String> venueMap = new LinkedHashMap<String, String>();
				String venueUrl = venueAHref.absUrl( "href" );
				String venueName = venueAHref.text();
				String venueType = "journal";
				String venueShortName = null;

				if ( venueUrl.startsWith( "http://dblp.uni-trier.de/db/conf" ) )
					venueType = "conference";

				// filter
				if ( type.equals( "journal" ) && ( venueType.equals( "conference" ) || venueType.equals( "workshop" ) ) )
					continue;
				if ( venueType.equals( "journal" ) && ( type.equals( "conference" ) || type.equals( "workshop" ) ) )
					continue;

				int shortNameIndex = venueName.indexOf( "(" );
				if ( shortNameIndex > 5 )
				{
					venueShortName = venueName.substring( shortNameIndex + 1, venueName.length() - 1 );
					venueName = venueName.substring( 0, shortNameIndex - 1 );
				}

				venueMap.put( "name", venueName );
				if ( venueShortName != null )
					venueMap.put( "abbr", venueShortName );
				else
				{
					int cutIndex = 0;
					if ( venueType.equals( "conference" ) )
						cutIndex = 33;
					else
						cutIndex = 37;
					String venueShortFromUrl = venueUrl.substring( cutIndex ).replaceAll( "\\/", "" ).toUpperCase();
					if ( venueShortFromUrl.length() < 9 )
						venueMap.put( "abbr", venueShortFromUrl );
				}
				venueMap.put( "url", venueUrl );
				venueMap.put( "type", venueType );

				venueListObject.add( venueMap );
			}
		}
		else
		{
			String venueUrl = document.baseUri();
			int cutToIndex = venueUrl.indexOf( "?" );
			if ( cutToIndex != -1 )
				venueUrl = venueUrl.substring( 0, cutToIndex );

			String venueName = document.select( "#headline" ).first().text();

			if ( venueUrl != null && venueName != null )
			{

				Map<String, String> venueMap = new LinkedHashMap<String, String>();
				String venueType = "journal";
				String venueShortName = null;

				if ( venueUrl.startsWith( "http://dblp.uni-trier.de/db/conf" ) )
					venueType = "conference";

				int shortNameIndex = venueName.indexOf( "(" );
				if ( shortNameIndex > 5 )
				{
					venueShortName = venueName.substring( shortNameIndex + 1, venueName.length() - 1 );
					venueName = venueName.substring( 0, shortNameIndex - 1 );
				}

				venueMap.put( "name", venueName );
				if ( venueShortName != null )
					venueMap.put( "abbr", venueShortName );
				else
				{
					int cutIndex = 0;
					if ( venueType.equals( "conference" ) )
						cutIndex = 33;
					else
						cutIndex = 37;
					String venueShortFromUrl = venueUrl.substring( cutIndex ).replaceAll( "\\/", "" ).toUpperCase();
					if ( venueShortFromUrl.length() < 9 )
						venueMap.put( "abbr", venueShortFromUrl );
				}
				venueMap.put( "url", venueUrl );
				venueMap.put( "type", venueType );

				venueListObject.add( venueMap );
			}
		}

		return venueListObject;
	}

	/**
	 * Get list of venue
	 */
	@SuppressWarnings( "unchecked" )
	public static Map<String, Object> getEventListFromDBLP( String url, Source source )
	{
		Map<String, Object> mainEventMap = new LinkedHashMap<String, Object>();

		// Using jsoup java html parser library
		Document document = PublicationCollectionHelper.getDocumentWithJsoup( url, 5000, getDblpCookie( source ) );

		if ( document == null )
			return Collections.emptyMap();

		Element mainContainer = document.select( "#main" ).first();

		if ( mainContainer == null )
		{
			log.info( "Main container not found " );
			return Collections.emptyMap();
		}

		// contain list of conference / journal in specific year
		List<Object> eventList = new ArrayList<Object>();

		// contains header information only for conference type
		Map<String, Object> conferenceOnSpecificYear = null;

		for ( Element element : mainContainer.children() )
		{

			if ( element.tagName().equals( "header" ) )
			{
				if ( mainEventMap.get( "type" ) != null )
				{
					if ( mainEventMap.get( "type" ).equals( PublicationType.CONFERENCE ) )
					{
						// put conference based on year to list
						if ( conferenceOnSpecificYear != null )
						{
							if ( conferenceOnSpecificYear.get( "volume" ) != null )
								eventList.add( conferenceOnSpecificYear );
						}

						// get header information from conference
						String headerText = element.text();
						String[] headerTextSplit = headerText.split( ":" );

						if ( headerTextSplit.length == 0 )
							continue;

						// create new conference per year object
						conferenceOnSpecificYear = new LinkedHashMap<String, Object>();

						if ( headerTextSplit[0].length() > 8 )
							conferenceOnSpecificYear.put( "year", headerTextSplit[0].substring( headerTextSplit[0].length() - 4 ) );

						int dotIndex = headerTextSplit[0].indexOf( "." );
						if ( dotIndex > 0 )
						{
							conferenceOnSpecificYear.put( "number", headerTextSplit[0].substring( 0, dotIndex ).trim() );
							conferenceOnSpecificYear.put( "abbr", headerTextSplit[0].substring( dotIndex + 1, headerTextSplit[0].length() - 4 ).trim() );
						}

						if ( headerTextSplit.length > 1 )
						{
							String[] conferenceLocation = headerTextSplit[1].split( "," );
							if ( conferenceLocation.length == 3 )
							{
								conferenceOnSpecificYear.put( "city", conferenceLocation[0].trim() );
								conferenceOnSpecificYear.put( "state", conferenceLocation[1].trim() );
								conferenceOnSpecificYear.put( "country", conferenceLocation[2].replace( "The ", "" ).trim() );
							}
							else if ( conferenceLocation.length == 2 )
							{
								conferenceOnSpecificYear.put( "city", conferenceLocation[0].trim() );
								conferenceOnSpecificYear.put( "country", conferenceLocation[1].replace( "The ", "" ).trim() );
							}
							else
							{
								conferenceOnSpecificYear.put( "country", conferenceLocation[0].replace( "The", "" ).trim() );
							}
						}


					}
				}
				else
				{
					if ( element.attr( "id" ) != null && element.attr( "id" ).equals( "headline" ) )
						mainEventMap.put( "title", element.text() );
				}
			}

			else if ( element.tagName().equals( "ul" ) )
			{

				for ( Element eventLiElement : element.children() )
				{
					if ( mainEventMap.get( "type" ).equals( PublicationType.JOURNAL ) )
					{
						Map<String, Object> journalOnSpecificYear = getDblpJournal( eventLiElement );
						// add journal on specific year
						if ( !journalOnSpecificYear.isEmpty() )
							eventList.add( getDblpJournal( eventLiElement ) );
					}
					else if ( mainEventMap.get( "type" ).equals( PublicationType.CONFERENCE ) )
					{
						Map<String, String> conferenceVolumeUrl = getDblpConferenceUrl( eventLiElement );
						// add journal on specific year
						if ( !conferenceVolumeUrl.isEmpty() )
						{
							Map<String, String> volumeMap = null;
							if ( conferenceOnSpecificYear.get( "volume" ) == null )
							{
								// create volume new map
								volumeMap = new LinkedHashMap<String, String>();
								conferenceOnSpecificYear.put( "volume", volumeMap );
							}
							else
							{
								volumeMap = (Map<String, String>) conferenceOnSpecificYear.get( "volume" );
							}
							volumeMap.putAll( conferenceVolumeUrl );

						}
					}
				}
			}

			else if ( element.tagName().equals( "div" ) )
			{
				if ( element.attr( "id" ) != null && element.attr( "id" ).equals( "breadcrumbs" ) )
				{
					String breadCrumbsLabel = element.text();
					if ( breadCrumbsLabel.toLowerCase().contains( "journals" ) )
						mainEventMap.put( "type", PublicationType.JOURNAL );
					else if ( breadCrumbsLabel.toLowerCase().contains( "conferences" ) )
						mainEventMap.put( "type", PublicationType.CONFERENCE );
				}
			}
		}

		// only on conference, insert last object
		if ( mainEventMap.get( "type" ).equals( PublicationType.CONFERENCE ) )
		{
			if ( conferenceOnSpecificYear != null )
				eventList.add( conferenceOnSpecificYear );
		}

		// put event list
		mainEventMap.put( "events", eventList );

		return mainEventMap;
	}

	/**
	 * Get DBLP journal
	 * 
	 * @param eventLiElement
	 * @return
	 */
	private static Map<String, Object> getDblpJournal( Element eventLiElement )
	{
		Map<String, Object> dblpJournal = new LinkedHashMap<String, Object>();

		String eventLiText = eventLiElement.text();

		// find type of format
		// Type 1: Volume 8: 2015 or Volume 8, 2015
		// Type 2: 2011: Volume 175
		int type = 1;
		if ( !eventLiText.toLowerCase().startsWith( "volume" ) )
			type = 2;

		Elements eventLiChildren = eventLiElement.select( "a" );

		if ( eventLiChildren == null )
			return Collections.emptyMap();

		// put list of event volume object here
		Map<String, String> volumeMap = new LinkedHashMap<String, String>();

		String[] eventLiTextSplit = eventLiText.split( ":" );

		// it turns out dblp has many format e.g.Volume 1, 1998
		if ( eventLiTextSplit.length != 2 )
		{
			// try to split with comma
			eventLiTextSplit = eventLiText.split( "," );
			if ( eventLiTextSplit.length != 2 )
			{
				return Collections.emptyMap();
			}
		}

		if ( type == 1 )
		{
			String volume = "0";
			if ( eventLiTextSplit[0].length() > 6 )
			{
				volume = eventLiTextSplit[0].substring( 7 );
			}

			// volume , here only one volume
			volumeMap.put( volume.trim(), eventLiChildren.get( 0 ).absUrl( "href" ) );

			// put year and volume
			String year = eventLiTextSplit[1].trim();
			if ( year.length() > 4 )
				year = year.substring( 0, 4 );
			dblpJournal.put( "year", year );
			dblpJournal.put( "volume", volumeMap );
		}
		else if ( type == 2 )
		{
			// put year
			String year = eventLiTextSplit[0].trim();
			if ( year.length() > 4 )
				year = year.substring( 0, 4 );
			dblpJournal.put( "year", year );

			// put volume
			for ( Element volumeElement : eventLiChildren )
			{
				volumeMap.put( volumeElement.text().trim(), volumeElement.absUrl( "href" ) );
			}
			dblpJournal.put( "volume", volumeMap );
		}

		return dblpJournal;
	}

	/**
	 * Get DBLP conference
	 * 
	 * @param conferenceOnSpecificYear
	 */
	private static Map<String, String> getDblpConferenceUrl( Element eventLiElement )
	{
		Map<String, String> dblpConference = new LinkedHashMap<String, String>();
	
		String linklUrl = eventLiElement.select( ".publ" ).select( ".head>a" ).first().absUrl( "href" );
		String title =  eventLiElement.select( ".data" ).select( ".title" ).first().text();

		if ( linklUrl== null ||  title == null )
			return Collections.emptyMap();
		
		dblpConference.put( title, linklUrl );

		return dblpConference;
	}

	/**
	 * There is an API available, but not really useful for getting the complete
	 * information of author and venue
	 * http://www.dblp.org/search/api/?q=ulrik%20schroeder&h=1000&c=4&f=0&format
	 * =json
	 */
}

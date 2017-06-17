package de.rwth.i9.palm.interestmining.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.rwth.i9.palm.model.ExtractionServiceType;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationTopic;
import de.rwth.i9.palm.utils.Inflector;

/**
 * Create cluster of publication based on year and language
 * 
 * @author sigit
 *
 */
public class PublicationClusterHelper
{
	// cluster identifier
	String language;
	int year;

	// list of publications on cluster
	List<Publication> publications;

	int numberOfWordsOnTitle;
	String concatenatedTitle = "";

	int numberOfPublicationWithKeyword;
	int numberOfWordsOnKeyword;
	String concatenatedKeyword = "";

	int numberOfWordsOnAbstract;
	String concatenatedAbstract = "";

	Map<String, TermDetail> termMap;

	public List<Publication> getPublications()
	{
		return publications;
	}

	public void setPublications( List<Publication> publications )
	{
		for ( Publication publication : publications )
		{
			this.addPublicationAndUpdate( publication );
		}
	}

	public PublicationClusterHelper addPublicationAndUpdate( Publication publication )
	{
		if ( this.publications == null )
			this.publications = new ArrayList<Publication>();
		this.publications.add( publication );

		// update other properties
		this.updateConcatenatedTitle( publication.getTitle() );
		if ( publication.getKeywordText() != null )
		{
			this.updateConcatenatedKeyword( publication.getKeywordText() );
			// increment number of publications contain keywords
			incrementNumberOfPublicationWithKeyword();
		}
		if ( publication.getAbstractText() != null )
			this.updateConcatenatedAbstract( publication.getAbstractText() );

		return this;
	}

	public int getYear()
	{
		return this.year;
	}

	public void setYear( int year )
	{
		this.year = year;
	}

	public String getLanguage()
	{
		return this.language;
	}

	public void setLangauge( String language )
	{
		if ( language.length() > 20 )
			language = language.substring( 0, 20 );
		this.language = language;
	}

	public int getNumberOfWordsOnTitle()
	{
		return numberOfWordsOnTitle;
	}

	private int updateNumberOfWordsOnTitle( String titleText )
	{
		this.numberOfWordsOnTitle += countWords( titleText );
		return this.numberOfWordsOnTitle;
	}

	public String getConcatenatedTitle()
	{
		return concatenatedTitle;
	}

	private String updateConcatenatedTitle( String titleText )
	{
		// filter and normalize text
		titleText = normalizeText( titleText );

		// concatenated
		this.concatenatedTitle += titleText + " ";

		// update word count
		updateNumberOfWordsOnTitle( titleText );

		return this.concatenatedTitle;
	}

	public int getNumberOfPublicationWithKeyword()
	{
		return numberOfPublicationWithKeyword;
	}

	private void incrementNumberOfPublicationWithKeyword()
	{
		this.numberOfPublicationWithKeyword++;
	}

	public int getNumberOfWordsOnKeyword()
	{
		return numberOfWordsOnKeyword;
	}

	private int updateNumberOfWordsOnKeyword( String keywordText )
	{
		this.numberOfWordsOnKeyword += countWords( keywordText );
		return this.numberOfWordsOnKeyword;
	}

	public String getConcatenatedKeyword()
	{
		return concatenatedKeyword;
	}

	private String updateConcatenatedKeyword( String keywordText )
	{
		// filter and normalize text
		keywordText = normalizeText( keywordText );

		// concatenated
		this.concatenatedKeyword += keywordText;

		// update word count
		updateNumberOfWordsOnKeyword( keywordText );

		return this.concatenatedKeyword;
	}

	public int getNumberOfWordsOnAbstract()
	{
		return numberOfWordsOnAbstract;
	}

	private int updateNumberOfWordsOnAbstract( String abstractText )
	{
		this.numberOfWordsOnAbstract += countWords( abstractText );
		return this.numberOfWordsOnAbstract;
	}

	public String getConcatenatedAbstract()
	{
		return concatenatedAbstract;
	}

	private String updateConcatenatedAbstract( String abstractText )
	{
		// filter and normalize text
		abstractText = normalizeText( abstractText );

		// concatenated
		this.concatenatedAbstract += abstractText;

		// update word count
		updateNumberOfWordsOnAbstract( abstractText );

		return this.concatenatedAbstract;
	}

	// utility methods

	// counting the number of words on text
	private int countWords( String text )
	{
		return text.split( "\\s+" ).length;
	}

	// do normalization and filtering on text
	// changed plural form into singular form
	private String normalizeText( String text )
	{
		// to lower case
		// remove all number
		// remove -_()
		// remove s on word end
		text = text.toLowerCase().replaceAll( "[^\\w\\s-]", "" );
		if ( this.getLanguage().equals( "english" ) )
			text = singularizeString( text );
		return text;
	}

	private String singularizeString( String text )
	{
		Inflector inflector = new Inflector();
		return inflector.singularize( text );
	}

	private boolean isText1ContainaMoreUpercaseTahnText2( String text1, String text2 )
	{
		int text1UpperCaseCount = 0;
		int text2UpperCaseCount = 0;
		for ( int k = 0; k < text1.length(); k++ )
			if ( Character.isUpperCase( text1.charAt( k ) ) )
				text1UpperCaseCount++;

		for ( int k = 0; k < text2.length(); k++ )
			if ( Character.isUpperCase( text2.charAt( k ) ) )
				text2UpperCaseCount++;

		if ( text1UpperCaseCount > text2UpperCaseCount )
			return true;

		return false;
	}

	/**
	 * Only run this method after all publication has clustered This method,
	 * calculate frequencies occurred
	 */
	public Map<String, TermDetail> calculateTermProperties()
	{
		if ( this.publications == null )
			return Collections.emptyMap();

		// init termMap
		termMap = new HashMap<String, TermDetail>();

		for ( Publication publication : this.publications )
		{
			// calculate frequencies of occurred term based on publication topic
			if ( publication.getPublicationTopics() != null )
			{
				for ( PublicationTopic publicationTopic : publication.getPublicationTopics() )
				{

					if ( publicationTopic.getTermValues() == null || publicationTopic.getTermValues().isEmpty() )
						continue;

					// get properties needed
					ExtractionServiceType extractionServiceType = publicationTopic.getExtractionServiceType();
					Map<String, Double> termValues = publicationTopic.getTermValues();
					
					// prepare termDetail
					TermDetail termDetail = null;
							
					for ( Map.Entry<String, Double> termValuesEntry : termValues.entrySet() )
					{
						String term = normalizeText( termValuesEntry.getKey() );
						// check if termMap has already contain term
						// if term already exist "more than one term extractor
						// services, produced same terms"
						if ( termMap.get( term ) != null )
						{
							termDetail = termMap.get( term );
							// update extraction service list
							termDetail.addExtractionServiceType( extractionServiceType );

							// prefer one that have uppercase letter
							//if ( isText1ContainaMoreUpercaseTahnText2( termValuesEntry.getKey(), termDetail.getTermLabel() ) )
							//	termDetail.setTermLabel( termValuesEntry.getKey() );
						}
						// term not exist on map
						else
						{
							// create new termDetail object
							termDetail = new TermDetail();

							// add extraction service
							termDetail.addExtractionServiceType( extractionServiceType );
							termDetail.setTermLabel( term );
							termDetail.setTermLength( this.countWords( term ) );

							// calculate frequencies
							termDetail.setFrequencyOnTitle( StringUtils.countMatches( this.getConcatenatedTitle(), term ) );
							termDetail.setFrequencyOnAbstract( StringUtils.countMatches( this.getConcatenatedAbstract(), term ) );
							termDetail.setFrequencyOnKeyword( StringUtils.countMatches( this.getConcatenatedKeyword(), term ) );

							// put into map
							termMap.put( term, termDetail );
						}
					}

				}
			}

			// TODO : add this keyword configuration to administration
			// calculate frequencies of occurred term based on publication
			// keyword
			if ( publication.getKeywordText() != null && !publication.getKeywordText().isEmpty() )
			{
				for ( String keyword : publication.getKeywordText().split( "," ) )
				{

					// get properties needed
					ExtractionServiceType extractionServiceType = ExtractionServiceType.KEYWORDBASED;
					
					String term = normalizeText( keyword );

					// check if termMap has already contain term
					// if term already exist "more than one term extractor
					// services, produced same terms"

					// prepare termDetail
					TermDetail termDetail = null;
					if ( termMap.get( term ) != null )
					{
						termDetail = termMap.get( term );
						// update extraction service list
						termDetail.addExtractionServiceType( extractionServiceType );
						// prefer one that have uppercase letter
						//if ( isText1ContainaMoreUpercaseTahnText2( keyword, termDetail.getTermLabel() ) )
						//	termDetail.setTermLabel( keyword );
					}
					// term not exist on map
					else
					{
						// create new termDetail object
						termDetail = new TermDetail();

						// add extraction service
						termDetail.addExtractionServiceType( extractionServiceType );
						termDetail.setTermLabel( term );
						termDetail.setTermLength( this.countWords( term ) );

						// calculate frequencies
						termDetail.setFrequencyOnTitle( StringUtils.countMatches( this.getConcatenatedTitle(), term ) );
						termDetail.setFrequencyOnAbstract( StringUtils.countMatches( this.getConcatenatedAbstract(), term ) );
						termDetail.setFrequencyOnKeyword( StringUtils.countMatches( this.getConcatenatedKeyword(), term ) );

						// put into map
						termMap.put( term, termDetail );
					}
				}

			}

		}

		return termMap;
	}

	public Map<String, TermDetail> getTermMap()
	{
		return termMap;
	}

	/**
	 * Get TermMap as ArrayList, sorted based on term length
	 * 
	 * @return
	 */
	public List<TermDetail> getTermMapAsList()
	{
		if ( this.termMap == null )
			return Collections.emptyList();

		List<TermDetail> termDetails = new ArrayList<TermDetail>();

		// put list member on specific index ( sorting )
		for ( Map.Entry<String, TermDetail> termDetailEntryMap : this.termMap.entrySet() )
		{
			TermDetail termDetail = termDetailEntryMap.getValue();
			if ( termDetails.isEmpty() )
				termDetails.add( termDetail );
			else
			{
				// searching index position based on term length
				int indexPosition = 0;
				int termlength = termDetail.getTermLength();
				for ( int i = 0; i < termDetails.size(); i++ )
				{
					indexPosition = i;
					if ( termlength >= termDetails.get( i ).getTermLength() )
						break;
				}
				// add termmap on specific position
				termDetails.add( indexPosition, termDetail );
			}
		}

		return termDetails;
	}

	/**
	 * Class contains term properties
	 * 
	 * @author Sigit
	 *
	 */
	class TermDetail
	{

		private String termLabel;
		private int termLength;
		private List<ExtractionServiceType> extractionServiceTypes;
		private int frequencyOnTitle;
		private int frequencyOnKeyword;
		private int frequencyOnAbstract;

		public String getTermLabel()
		{
			return termLabel;
		}

		public void setTermLabel( String termLabel )
		{
			this.termLabel = termLabel;
		}

		public int getTermLength()
		{
			return termLength;
		}

		public void setTermLength( int termLength )
		{
			this.termLength = termLength;
		}

		public List<ExtractionServiceType> getExtractionServiceTypes()
		{
			return extractionServiceTypes;
		}

		public TermDetail addExtractionServiceType( ExtractionServiceType extractionServiceType )
		{
			if ( this.extractionServiceTypes == null )
				this.extractionServiceTypes = new ArrayList<ExtractionServiceType>();

			if ( !this.extractionServiceTypes.contains( extractionServiceType ) )
				this.extractionServiceTypes.add( extractionServiceType );

			return this;
		}

		public void setExtractionServiceTypes( List<ExtractionServiceType> extractionServiceTypes )
		{
			this.extractionServiceTypes = extractionServiceTypes;
		}

		public int getFrequencyOnTitle()
		{
			return frequencyOnTitle;
		}

		public void setFrequencyOnTitle( int frequencyOnTitle )
		{
			this.frequencyOnTitle = frequencyOnTitle;
		}

		public int getFrequencyOnKeyword()
		{
			return frequencyOnKeyword;
		}

		public void setFrequencyOnKeyword( int frequencyOnKeyword )
		{
			this.frequencyOnKeyword = frequencyOnKeyword;
		}

		public int getFrequencyOnAbstract()
		{
			return frequencyOnAbstract;
		}

		public void setFrequencyOnAbstract( int frequencyOnAbstract )
		{
			this.frequencyOnAbstract = frequencyOnAbstract;
		}

	}

}

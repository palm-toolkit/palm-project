package de.rwth.i9.palm.interestmining.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.rwth.i9.palm.interestmining.service.PublicationClusterHelper.TermDetail;
import de.rwth.i9.palm.model.AuthorInterest;
import de.rwth.i9.palm.model.ExtractionServiceType;
import de.rwth.i9.palm.model.Interest;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Service
public class CorePhraseInterestProfile
{
	private final static Logger log = LoggerFactory.getLogger( CorePhraseInterestProfile.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	public void doCorePhraseCalculation( AuthorInterest authorInterest, Set<Interest> newInterests, PublicationClusterHelper publicationCluster, Double yearFactor, Double totalYearFactor, int numberOfExtractionService )
	{
		// assign authorInterest properties
		authorInterest.setLanguage( publicationCluster.getLanguage() );

		DateFormat dateFormat = new SimpleDateFormat( "yyyy", Locale.ENGLISH );
		try
		{
			authorInterest.setYear( dateFormat.parse( Integer.toString( publicationCluster.getYear() ) ) );
		}
		catch ( ParseException e )
		{
			e.printStackTrace();
		}

		
		Map<String, TermDetail> termDetailsMap = publicationCluster.getTermMap();
		
		// ordered map as helper
		Map<String, Integer> wordOccurrenceMap = new HashMap<String, Integer>();
		Map<String, Double> termWeightHelperMap = new HashMap<String, Double>();
		double maxWeightValue = 0.0;

		for ( Map.Entry<String, TermDetail> termDetailEntryMap : termDetailsMap.entrySet() )
		{
			String term = termDetailEntryMap.getKey();

			// just skip terms which are too long
			if ( term.length() > 50 )
				continue;

			// terms which are too short is not good either
			if ( term.length() < 4 )
				continue;

			TermDetail termDetail = termDetailEntryMap.getValue();
			int totalOccurrenceOnEachTerm = 0;
			Double intersectionFactor = 0.0;

			// only proceed for term that intersect with other topic extractor
			if ( ( termDetail.getExtractionServiceTypes().size() >= numberOfExtractionService - 1 ) || numberOfExtractionService == 1 )
				// calculate the weight based on frequency and factor on each
				// cluster
				totalOccurrenceOnEachTerm = termDetail.getFrequencyOnTitle() + termDetail.getFrequencyOnKeyword() + termDetail.getFrequencyOnAbstract();
			else
				continue;

			// calculate occurrence on each term words
			if ( termDetail.getTermLength() > 1 )
			{
				String[] termWords = termDetail.getTermLabel().split( "\\s+" );
				for ( int i = 0; i < termWords.length; i++ )
				{
					assignWordOccurrenceMap( termWords[i], totalOccurrenceOnEachTerm, wordOccurrenceMap );
				}
			}
			else
			{
				assignWordOccurrenceMap( termDetail.getTermLabel(), totalOccurrenceOnEachTerm, wordOccurrenceMap );
			}

			// calculate intersection factor
			intersectionFactor = (double) termDetail.getExtractionServiceTypes().size();

			// Extraction from yahoo content analysis have higher precision
			if ( termDetail.getExtractionServiceTypes().contains( ExtractionServiceType.YAHOOCONTENTANALYSIS ) )
				intersectionFactor += 0.5;
			
			// then multiple by year factor
			// intersectionFactor = intersectionFactor * yearFactor;
			
			// Finally, put calculation into authorInterest object
			// only if intersectionFactor > 1.0
			if ( intersectionFactor <= 1.0 )
				continue;
			
			termWeightHelperMap.put( term, intersectionFactor );
		}

		// calculate score
		for ( Map.Entry<String, Double> termWeightHelperEntry : termWeightHelperMap.entrySet() )
		{
			String term = termWeightHelperEntry.getKey();
			int score = getTermScore( term, wordOccurrenceMap );

			if ( score == 0 )
				continue;

			double newWeight = score * termWeightHelperEntry.getValue();
			// update value
			termWeightHelperEntry.setValue( newWeight );

			// update max value for normalization
			if ( newWeight > maxWeightValue )
				maxWeightValue = newWeight;
		}

		// normalize value between 0 - 1
		for ( Map.Entry<String, Double> termWeightHelperEntry : termWeightHelperMap.entrySet() )
		{
			String term = termWeightHelperEntry.getKey();

			if ( maxWeightValue == 0 )
				continue;

			double normalizedWeighting = termWeightHelperEntry.getValue() / maxWeightValue;

			// proceed to interest object
			Interest interest = persistenceStrategy.getInterestDAO().getInterestByTerm( term );

			if ( interest == null )
			{
				for ( Interest newInterest : newInterests )
				{
					if ( newInterest.getTerm().equals( term ) )
					{
						interest = newInterest;
						break;
					}
				}
			}
			if ( interest == null )
			{
				interest = new Interest();
				interest.setTerm( term );
				newInterests.add( interest );
				persistenceStrategy.getInterestDAO().persist( interest );
			}
			authorInterest.addTermWeight( interest, normalizedWeighting );
		}
		
	}

	private void assignWordOccurrenceMap( String word, int value, Map<String, Integer> wordOccurrenceMap )
	{
		if ( wordOccurrenceMap.get( word ) == null )
		{
			wordOccurrenceMap.put( word, value );
		}
		else
		{
			wordOccurrenceMap.put( word, wordOccurrenceMap.get( word ) + value );
		}
	}

	private int getTermScore( String term, Map<String, Integer> wordOccurrenceMap )
	{
		int score = 0;
		String[] termWords = term.split( "\\s+" );
		for ( int i = 0; i < termWords.length; i++ )
		{
			if ( wordOccurrenceMap.get( termWords[i] ) != null )
				score += wordOccurrenceMap.get( termWords[i] );
		}
		return score;
	}

}

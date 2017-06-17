package de.rwth.i9.palm.interestmining.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.interestmining.service.PublicationClusterHelper.TermDetail;
import de.rwth.i9.palm.model.EventInterest;
import de.rwth.i9.palm.model.Interest;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Service
public class WordFreqEventInterestProfile
{
	private final static Logger log = LoggerFactory.getLogger( WordFreqEventInterestProfile.class );
	// final private String PROFILENAME = "cvalue";

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private PalmAnalytics palmAnalytics;

	public void doWordFreqCalculation( EventInterest eventInterest, PublicationClusterHelper publicationCluster, Double yearFactor, Double totalYearFactor, int numberOfExtractionService )
	{
		// assign eventInterest properties
		eventInterest.setLanguage( publicationCluster.getLanguage() );

		DateFormat dateFormat = new SimpleDateFormat( "yyyy", Locale.ENGLISH );
		try
		{
			eventInterest.setYear( dateFormat.parse( Integer.toString( publicationCluster.getYear() ) ) );
		}
		catch ( ParseException e )
		{
			e.printStackTrace();
		}

		// Prepare the distinct factors per cluster
		int maxLength = Math.max( publicationCluster.getNumberOfWordsOnTitle(), publicationCluster.getNumberOfWordsOnAbstract() );
		double titleWordFactor = 0.0,
				abstractWordFactor = 0.0,
				keywordWordFactor = 0.0;
		// assign distinct factors
		if( publicationCluster.getNumberOfWordsOnTitle() > 0)
			titleWordFactor = maxLength / (double) publicationCluster.getNumberOfWordsOnTitle();
		if( publicationCluster.getNumberOfWordsOnAbstract() > 0)
			abstractWordFactor = maxLength / (double) publicationCluster.getNumberOfWordsOnAbstract();
		if( publicationCluster.getNumberOfWordsOnKeyword() > 0)
			keywordWordFactor = (maxLength / (double) publicationCluster.getNumberOfWordsOnKeyword()) * ( publicationCluster.getNumberOfPublicationWithKeyword() / (double) publicationCluster.getPublications().size()); 
		
		// get sorted term details
		List<TermDetail> termDetails = publicationCluster.getTermMapAsList();
		
		// ordered map as helper for calculating nested term
		Map<String, Double> termWeightHelperMap = new LinkedHashMap<String, Double>();
		double maxWeightValue = 0.0;

		for ( TermDetail termDetail : termDetails )
		{
			String term = termDetail.getTermLabel();

			// just skip term which are too long
			if ( term.length() > 50 )
				continue;

			// terms which are too short is not good either
			if ( term.length() < 4 )
				continue;

			Double termWeight = 0.0;

			// only proceed for term that intersect with other topic extractor
			if ( ( termDetail.getExtractionServiceTypes().size() >= numberOfExtractionService - 1 ) || numberOfExtractionService == 1 )
				// calculate the weight based on frequency and factor on each cluster
				termWeight = ( termDetail.getFrequencyOnTitle() * titleWordFactor ) + 
						( termDetail.getFrequencyOnKeyword() * abstractWordFactor ) + 
						( termDetail.getFrequencyOnAbstract() * keywordWordFactor );
			else
				continue;

			// check for nested terms on multi-words term
			if ( termDetail.getTermLength() > 1 )
			{
				for ( Map.Entry<String, Double> termWeightHelperEntry : termWeightHelperMap.entrySet() )
				{
					String shorterTerm = termWeightHelperEntry.getKey();

					// only check term with fewer words count than current term
					if ( shorterTerm.split( "\\s+" ).length >= termDetail.getTermLength() )
						break;

					// check for nested term , if nested then add current
					// weighting with nested
					if ( term.contains( shorterTerm ) )
						termWeight += termWeightHelperEntry.getValue();
				}
			}

			if ( termWeight == 0.0 )
				continue;

			// put current term and weighting into ordered hashmap helper
			termWeightHelperMap.put( term, termWeight );

			// update maxWeightValue if there is weight that larger with current
			// maxWeightValue
			if ( termWeight > maxWeightValue )
				maxWeightValue = termWeight;
		}

		// normalize value between 0 - 1
		for ( Map.Entry<String, Double> termWeightHelperEntry : termWeightHelperMap.entrySet() )
		{
			String term = termWeightHelperEntry.getKey();

			if ( maxWeightValue == 0 )
				continue;

			double normalizedWeighting = termWeightHelperEntry.getValue() / maxWeightValue;

			// process term to event interest map
			Interest interest = persistenceStrategy.getInterestDAO().getInterestByTerm( term );

			if ( interest == null )
			{
				interest = new Interest();
				interest.setTerm( term );

				persistenceStrategy.getInterestDAO().persist( interest );
			}
			eventInterest.addTermWeight( interest, normalizedWeighting );
		}
	}

}
